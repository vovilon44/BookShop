package com.example.MyBookShopApp.data.telegram;

import com.example.MyBookShopApp.data.repositories.BookFileRepository;
import com.example.MyBookShopApp.data.services.*;
import com.example.MyBookShopApp.data.struct.book.file.BookFile;
import com.example.MyBookShopApp.data.struct.book.file.BookFileType;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserDetailsService;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.ContactConfirmationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BotService {

    private final BotUserStatusRepository botUserStatusRepository;
    private final PaymentService paymentService;
    private final BookService bookService;
    private final BookFileRepository bookFileRepository;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final Book2UserService book2UserService;
    private final CodeSenderService codeSenderService;

    @Value("${bot.books.limit}")
    private Integer botBookLimit;
    @Value("${bot.books.offset}")
    private Integer botBookOffset;

    @Value("${my.ip}")
    private String myIp;

    @Value("${server.port}")
    private String myPort;
    @Autowired
    public BotService(BotUserStatusRepository botUserStatusRepository, PaymentService paymentService, BookService bookService, BookFileRepository bookFileRepository, BookstoreUserDetailsService bookstoreUserDetailsService, Book2UserService book2UserService, CodeSenderService codeSenderService) {
        this.botUserStatusRepository = botUserStatusRepository;
        this.paymentService = paymentService;
        this.bookService = bookService;
        this.bookFileRepository = bookFileRepository;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.book2UserService = book2UserService;
        this.codeSenderService = codeSenderService;
    }


    public BotUser getBotUser(Long botUserId) {
        return botUserStatusRepository.findBotUserById(botUserId);

    }

    public String verifyCode(BotUser botUser, String code) {

          return  changeBotUserStatus(botUser, BotUserStatus.SEARCH_TITLE, codeSenderService.verifyCode(botUser.getPhoneNumber(), code, "ru", true));
    }

    public String sendSecretCodeSms(Long botUserId, String phoneNumber) {
        String phoneNumberEdit = phoneNumber.replace("+", "").replaceFirst("([87]?)(\\d{3})(\\d{3})(\\d{2})(\\d{2})", "+7 ($2) $3-$4-$5");
       return changeBotUserStatus(new BotUser(botUserId, phoneNumberEdit), BotUserStatus.LOGIN, codeSenderService.sendSecretCodeSms(phoneNumberEdit, "ru", true));

    }


    public BotBooksResponse getBook(BotUser botUser, long number){
        return bookService.getBotBookById((int) number);
    }



    public BotBooksResponse getPageBooks(BotUser botUser, String number){
        botUser.setPageNumber(Integer.parseInt(number) - 1);
        botUserStatusRepository.save(botUser);
        switch (botUser.getStatus()) {
            case LOGIN:
                return new BotBooksResponse(verifyCode(botUser, number));
            case KEPT:
                return bookService.getBotBooksMain(botUser, botBookLimit, 1);
            case CART:
                return bookService.getBotBooksMain(botUser, botBookLimit, 2);
            case MY:
                return bookService.getBotBooksMain(botUser, botBookLimit, 3);
            case ARCHIVE:
                return bookService.getBotBooksMain(botUser, botBookLimit, 4);
            case SEARCH_AUTHOR:
                return bookService.getBotSearchBooksByAuthors(botUser, botBookLimit);
            case SEARCH_GENRE:
                return bookService.getBotSearchBooksByGenre(botUser, botBookLimit);
            case SEARCH_TAG:
                return bookService.getBotSearchBooksByTag(botUser, botBookLimit);
            case SEARCH_TITLE:
                return bookService.getBotSearchBooksByTitle(botUser, botBookLimit);
            case SEARCH_ALL:
                return bookService.getBotSearchBooksByAll(botUser, botBookLimit);
            default:
                return new BotBooksResponse("Не верный запрос");
        }
    }

    public String getLinkForDownload(BotUser botUser, Integer bookId) {
        Random random = new Random();

        if (checkAccessDownload(botUser, bookId)){
        StringBuilder sb = new StringBuilder();
        int randomValue;
        long currentTime = System.currentTimeMillis();
            randomValue = random.nextInt(1000);
            sb.append("http://").append(myIp).append(":").append(myPort).append("/books/download/bot/");
            sb.append((char) (Math.random() * (122 - 97) + 97 )).append(String.valueOf(randomValue).length());
            sb.append(randomValue).append(String.valueOf(botUser.getUser().getId() + randomValue).length()).append(botUser.getUser().getId() + randomValue);
            randomValue = random.nextInt(1000);
            sb.append(String.valueOf(randomValue).length()).append(randomValue).append(String.valueOf(randomValue + bookId).length()).append(randomValue + bookId);
            sb.append(currentTime);

        System.out.println(sb);
        System.out.println(bookId);
        return  sb.toString();
        } else {
            return null;
        }
    }

    private boolean checkAccessDownload(BotUser botUser, Integer bookId){
        Book2UserEntity book2User  = book2UserService.botGetBook2User(botUser.getUser(), bookId);
        if (book2User != null && book2User.getTypeId() == 3){
            return true;
        }
        return false;
    }

    public BotBooksResponse getPageBooksWithCommand(BotUser botUser, String textFromUser) {
        if (botUser.getStatus() == BotUserStatus.LOGIN){
            return new BotBooksResponse(verifyCode(botUser, "incorrect"));
        }
        switch (textFromUser) {
            case "далее":
                botUser.setPageNumber(botUser.getPageNumber() + 1);
                break;
            case "назад":
                botUser.setPageNumber(botUser.getPageNumber() > 0 ? botUser.getPageNumber() - 1 : -1);
                break;
            default:
                botUser.setWordForSearch(textFromUser);
                botUser.setPageNumber(-1);
                break;
        }

        botUserStatusRepository.save(botUser);
        return getPageBooks(botUser, String.valueOf(botUser.getPageNumber() + 1));


    }



    public BotBooksResponse changeBotUserStatus(BotUser botUser, BotUserStatus botUserStatus){
        if (botUser.getStatus() == BotUserStatus.LOGIN){
           return new BotBooksResponse(verifyCode(botUser, "incorect"));
        }
        botUser.setWordForSearch(null);
        switch (botUserStatus){
            case KEPT:
            case CART:
            case MY:
            case ARCHIVE:
                botUser.setStatus(botUserStatus);
                BotBooksResponse booksResponse = getPageBooks(botUser, "0");
                booksResponse.setMessage("Выбран раздел '" + botUserStatus + "'\n" + booksResponse.getMessage());
                return booksResponse;
            default:
                botUser.setStatus(botUserStatus);
                botUserStatusRepository.save(botUser);
                return new BotBooksResponse("Выбран " + botUser.getStatus() + ". Введите слово для поиска");
        }
    }

    public String changeBotUserStatus(BotUser botUser, BotUserStatus botUserStatus, ContactConfirmationResponse response){
        switch (botUserStatus){
            case LOGIN:
                if (response != null && response.getResult()){
                    botUser.setUser(bookstoreUserDetailsService.loadUserByUsername(botUser.getPhoneNumber()).getBookstoreUser());
                    botUser.setStatus(BotUserStatus.LOGIN);
                    botUserStatusRepository.save(botUser);
                    return response.getMessage();
                } else {
                    return response.getError();
                }
            case SEARCH_TITLE:
                if (response != null && response.getResult()){
                    botUser.setStatus(BotUserStatus.SEARCH_TITLE);
                    return response.getMessage() + "\nПоздравляем " + botUser.getUser().getName() + " с успешной авторизацией!\n" + changeBotUserStatus(botUser, botUserStatus);
                } else if (response != null){
                    return response.getError().replace(" войти по e-mail или", "").replace(" по телефону", "");
                }
        }
        return "Введены невалидные данные";
    }


    public String getStatus(BotUser botUser) {
        StringBuilder sb = new StringBuilder();
        sb.append("Вы находитесь в разделе: ").append(botUser.getStatus().toString());
                switch (botUser.getStatus()){
                    case SEARCH_GENRE:
                    case SEARCH_AUTHOR:
                    case SEARCH_TITLE:
                    case SEARCH_TAG:
                    case SEARCH_ALL:
                        sb.append("\nПоисковое слово: ").append(botUser.getWordForSearch());
                        sb.append("\nСтраница: ").append(botUser.getPageNumber() + 1);
                        break;
                    case CART:
                    case MY:
                        if (botUser.getPageNumber() >= 0) {
                            sb.append("\nСтраница: ").append(botUser.getPageNumber() + 1);
                        }
                        break;
                }
        sb.append("\nДанные о пользователе:");
        sb.append("\nимя: " + botUser.getUser().getName());
        sb.append("\nemail: ").append(botUser.getUser().getEmail());
        sb.append("\nзарегистрирован: ").append(botUser.getUser().getRegTime());
        sb.append("\nВаш номер телефона: ").append(botUser.getPhoneNumber());
        sb.append("\nВаш баланс: ").append(botUser.getUser().getBalance());
        return sb.toString();
    }

    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Вас приветствует бот Bookstore\n\n");
        sb.append("Поиск:\n\n");
        sb.append("\tПоиск книг возможен по различным критериям: \n");
        sb.append("\t\t- По названию \n");
        sb.append("\t\t- Тэгу \n");
        sb.append("\t\t- Жанру \n");
        sb.append("\t\t- Автору \n");
        sb.append("\t\t- Общий поиск \n");
        sb.append("Для поиска перейдите в соответствующий раздел воспользовавшись меню в левом нижнем углу.\n");
        sb.append("Далее необходимо ввести часть интересующего вас слова, после чего в ответном сообщении придет информация о найденых книгах\n\n");
        sb.append("Просмотр и взаимодействие с книгами:\n\n");
        sb.append("\tПосле того, как книги были найдены в поиске либо после перехода в разделы с книгами бот уведомит о количестве книг и доступных страницах\n");
        sb.append("Для навигации между страницами отправьте номер страницы или используйте кнопки 'далее' и 'назад'\n");
        sb.append("На каждой странице находится по " + botBookLimit +" книг\n");
        sb.append("Под каждой книгой расположены кнопки для взаимодействия\n\n");
        sb.append("Отложенные и корзина:\n\n");
        sb.append("\tКниги найденные в поиске можно отложить или положить в корзину.\n");
        sb.append("Для перемещения книг между этими категориями перейдите в соответствующие разделы\n");
        sb.append("Чтобы купить книгу перейдите в корзину и нажмите кнопку 'купить'.\n");
        sb.append("Если на балансе денег достаточно, будет совершена покупка, в противном случая появится ссылка для пополнения счета.\n\n");
        sb.append("Мои книги и архив:\n\n");
        sb.append("\tПриобретенные книги появятся в разделе мои книги.\n");
        sb.append("Для скачивания книги есть 2 варианта.\n");
        sb.append("- В первом случае выполнится переход на сайт с последующей автоматической авторизации для дальнейшего скачивания.\n");
        sb.append("Кнопка работает лишь 5 минут после генерации!\n\n");
        sb.append("- Во втором же варианте бот пришлет файл.\n\n");
        sb.append("Так же напомним, доступно лишь 5 скачиваний на книгу, в случае превышения лимита свяжитесь с нашей администрацией.\n");
        sb.append("Уже прочитанные книги можно положить в архив.\n\n");
        sb.append("Для получения информации о пользователе и текущем статусе используйте пункт 'статус' в меню.\n\n");
        sb.append("Желаем приятного чтения!!!\n\n");
        sb.append("С уважением команда Bookstore");
        return sb.toString();
    }


    public BotBooksResponse botChangeStatusBookForUser(BookstoreUser user, Integer bookId, int bookStatusId) {
            return book2UserService.botChangeStatusBookForUser(user, bookId, bookStatusId);
    }

    public BotBooksResponse botAddStatusBookForUser(BookstoreUser user, Integer bookId, int bookStatusId) {
        return book2UserService.botAddBookInCard(user, bookId, bookStatusId);
    }

    public BotBooksResponse botRemoveStatusBookForUser(BookstoreUser user, Integer bookId) {
        return book2UserService.botDeleteBookStatus(user, bookId);
    }

    public String getLinkForPayment(BotUser botUser, Double sum) {
        if (sum > 0) {
            return paymentService.getPaymentUrl(sum);
        } else {
            return null;
        }
    }

    public BotBooksResponse getBookPath(BookstoreUser user, Integer bookId) {
       if (bookService.checkBotkDownloadExceedance(user.getId(), bookId)){
           BookFile bookFile = bookFileRepository.findBookFileByBook_IdAndTypeIdIs(bookId, BookFileType.FB2.getNumber());
           if (bookFile != null){
               bookService.botIncDownloadCount(bookFile.getHash(), user);
               return new BotBooksResponse(true, bookFile.getPath());
           }
       }
       return new BotBooksResponse(false, "превышен лимит скачивания");
    }

}
