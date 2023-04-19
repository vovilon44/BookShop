package com.example.MyBookShopApp.data.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${telegamm.bot.name}")
    private String botUsername;

    @Value("${telegamm.bot.token}")
    private String botToken;

    private final BotService botService;


    @Value("${file.path}")
    private String filePath;

    private final static String BUTTON_MOVE_ARCHIVE = "buttonMoveArchive";
    private final static String BUTTON_MOVE_MY = "buttonMoveMy";
    private final static String BUTTON_MOVE_CART = "buttonMoveCart";
    private final static String BUTTON_MOVE_KEPT = "buttonMoveKept";
    private final static String BUTTON_OUT = "ButtonOut";
    private final static String BUTTON_BUY = "buttonBuy";
    private final static String BUTTON_IN_CART = "buttonInCart";
    private final static String BUTTON_IN_KEPT = "buttonInKept";
    private final static String BUTTON_DOWNLOAD = "buttonDownload";
    private final static String BUTTON_DOWNLOAD_HERE = "buttonDownloadHere";
    private final static String SHOW_DESCRIPTION = "showDescription";
    private final static String HIDE_DESCRIPTION = "hideDescription";

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public Bot(BotService botService) throws TelegramApiException {
        this.botService = botService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String text = update.getCallbackQuery().getMessage().getCaption();
            String callbackQueryId = update.getCallbackQuery().getId();
            BotUser botUser = botService.getBotUser(update.getCallbackQuery().getFrom().getId());
            int bookId = Integer.parseInt((text.split(" ")[1]).replace("\n", ""));
            BotBook bookData = botService.getBook(botUser, (bookId - botUser.getId() % 100)).getData().get(0);
            int statusBookForUserType = 1;
            InlineKeyboardMarkup inlineKeyboardMarkup = update.getCallbackQuery().getMessage().getReplyMarkup();
            AnswerCallbackQuery answerCallbackQuery =new AnswerCallbackQuery(callbackQueryId);
            answerCallbackQuery.setShowAlert(true);
            BotBooksResponse result = new BotBooksResponse();
            switch (callbackData) {
                case (SHOW_DESCRIPTION):
                    inlineKeyboardMarkup.getKeyboard().get(0).get(0).setText("Свернуть");
                    inlineKeyboardMarkup.getKeyboard().get(0).get(0).setCallbackData(HIDE_DESCRIPTION);
                    sendEditMessage(inlineKeyboardMarkup, chatId, messageId, getFullTextDescriptionBotBook(bookData, text));
                    return;
                case (HIDE_DESCRIPTION):
                    inlineKeyboardMarkup.getKeyboard().get(0).get(0).setText("Развернуть");
                    inlineKeyboardMarkup.getKeyboard().get(0).get(0).setCallbackData(SHOW_DESCRIPTION);
                    sendEditMessage(inlineKeyboardMarkup, chatId, messageId, text.substring(0, text.indexOf("Тэги:")));
                    return;
                case BUTTON_IN_CART:
                    result = botService.botAddStatusBookForUser(botUser.getUser(), (int) (bookId - botUser.getId() % 100), 2);
                    break;
                case BUTTON_OUT:
                    result = botService.botRemoveStatusBookForUser(botUser.getUser(), (int) (bookId - botUser.getId() % 100));
                    break;
                case BUTTON_MOVE_ARCHIVE: statusBookForUserType++;
                case BUTTON_BUY:
                case BUTTON_MOVE_MY: statusBookForUserType++;
                case BUTTON_MOVE_CART: statusBookForUserType++;
                case BUTTON_MOVE_KEPT:
                    result = botService.botChangeStatusBookForUser(botUser.getUser(), (int) (bookId - botUser.getId() % 100), statusBookForUserType);
                    break;
                case BUTTON_DOWNLOAD_HERE:
                    result = botService.getBookPath(botUser.getUser(), (int) (bookId - botUser.getId() % 100));
                    if (result.getResult()) {
                        sendBookFile(chatId, messageId, result.getMessage());
                        return;
                    }
                    break;
                case BUTTON_IN_KEPT:
                    result = botService.botAddStatusBookForUser(botUser.getUser(), (int) (bookId - botUser.getId() % 100), 1);
                    break;
            }
            DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), messageId);
            answerCallbackQuery.setText(result.getMessage());
            try {
                execute(answerCallbackQuery);
                if (result.getResult()) {
                    execute(deleteMessage);
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            sendMessage(update.getMessage().getChatId(), botService.sendSecretCodeSms(update.getMessage().getFrom().getId(), update.getMessage().getContact().getPhoneNumber()), null);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            BotUser botUser = botService.getBotUser(update.getMessage().getFrom().getId());
            String textFromUser = update.getMessage().getText().toLowerCase();
            int commandStatusValue = 1;
            if (botUser != null) {
                switch (textFromUser) {
                    case ("/archive"): commandStatusValue++;
                    case ("/my"):commandStatusValue++;
                    case ("/cart"):commandStatusValue++;
                    case ("/kept"):commandStatusValue++;
                    case ("/search_all"):commandStatusValue++;
                    case ("/search_tag"):commandStatusValue++;
                    case ("/search_title"):commandStatusValue++;
                    case ("/search_author"):commandStatusValue++;
                    case ("/search_genre"):
                            beforeSendMessage(chatId, botService.changeBotUserStatus(botUser, BotUserStatus.of(commandStatusValue)), botUser); return;
                    case ("/status"):
                        sendMessage(chatId, botService.getStatus(botUser), botUser);
                        return;
                    case ("/help"):
                        sendMessage(chatId, botService.getHelp(), botUser);
                        return;
                    default:
                        if (textFromUser.matches("[\\d ]+")) {
                            beforeSendMessage(chatId, botService.getPageBooks(botUser, textFromUser.replace(" ", "")), botUser);
                        } else {
                            beforeSendMessage(chatId, botService.getPageBooksWithCommand(botUser, textFromUser), botUser);
                        }
                }
            } else {
                beforeSendMessage(update.getMessage().getChatId(), new BotBooksResponse((update.getMessage().getText().equals("/start") ?
                        "Здравствуйте, вас приветствует бот книжного магазина Bookstore!\n" : "") +
                        "Для аутентификации пожалуйста предоставьте свой контакт"), null);
            }
        }
    }

    private void sendBookFile(long chatId, int messageId, String bookPath) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setDocument(new InputFile(new File(filePath + "/sourceFileUpload" + bookPath)));
        sendDocument.setChatId(chatId);
        sendDocument.setReplyToMessageId(messageId);
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEditMessage(InlineKeyboardMarkup inlineKeyboardMarkup, long chatId, int messageId, String text) {
        EditMessageCaption message = new EditMessageCaption();
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setCaption(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboard getReplyMarkup(BotUser botUser) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        keyboardRows.add(row);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        if (botUser == null) {
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText("Предоставить контакт >>");
            keyboardButton.setRequestContact(true);
            row.add(keyboardButton);
        }
        else if (botUser.getWordForSearch() != null || botUser.getStatus().getValue() > 5) {
            row.add("Назад");
            row.add("Далее");
        }
        else {
            return new ReplyKeyboardRemove(true);
        }
        return replyKeyboardMarkup;
    }

    private void beforeSendMessage(long chatId, BotBooksResponse pageBooks, BotUser botUser) {
        if (pageBooks.getData().size() > 0) {
            for (BotBook book : pageBooks.getData()) {
                sendBookShort(chatId, book, botUser);
            }
        }
        sendMessage(chatId, pageBooks.getMessage(), botUser);
    }

    private void sendMessage(Long chatId, String text, BotUser botUser) {
        SendMessage message = new SendMessage();
        message.setReplyMarkup(getReplyMarkup(botUser));
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.warning(e.getMessage());
        }
    }

    private void sendBookShort(long chatId, BotBook book, BotUser botUser) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setDisableNotification(true);
        sendPhoto.setReplyMarkup(getInlineKeyboardMarkup(botUser, book, chatId));
        sendPhoto.setPhoto(new InputFile(new File(filePath + book.getBook().getImage())));
        sendPhoto.setCaption(getShortTextDescriptionBotBook(botUser, book));

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            logger.warning(e.getMessage());
        }
    }

    private ReplyKeyboard getInlineKeyboardMarkup(BotUser botUser, BotBook book, long chatId) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineSecond = new ArrayList<>();
        rowsInline.add(rowInline);
        rowsInline.add(rowInlineSecond);
        InlineKeyboardButton showDescription = new InlineKeyboardButton();
        showDescription.setText("развернуть");
        showDescription.setCallbackData(SHOW_DESCRIPTION);
        rowInline.add(showDescription);
        markupInLine.setKeyboard(rowsInline);

        switch (botUser.getStatus()){
            case KEPT:
                rowInline.add(createButton("В корзину", BUTTON_MOVE_CART));
                rowInline.add(createButton("Убрать", BUTTON_OUT));
                return markupInLine;

            case CART:
                rowInline.add(createButton("В отложенные", BUTTON_MOVE_KEPT));
                rowInlineSecond.add(createButton("Убрать", BUTTON_OUT));
                rowInlineSecond.add(createButton("Купить", BUTTON_BUY, botService.getLinkForPayment(botUser, book.getCurrentPrice() - botUser.getUser().getBalance(), chatId)));
                break;
            case MY:
                rowInline.add(createButton("В архив", BUTTON_MOVE_ARCHIVE));
                rowInlineSecond.add(createButton("Скачать на сайте", BUTTON_DOWNLOAD, botService.getLinkForDownload(botUser, book.getBook().getId())));
                rowInlineSecond.add(createButton("Скачать тут в fb2", BUTTON_DOWNLOAD_HERE));
                break;
            case ARCHIVE:
                rowInline.add(createButton("В мои книги", BUTTON_MOVE_MY));
                break;
            default:
                rowInline.add(createButton("В корзину", BUTTON_IN_CART));
                rowInline.add(createButton("В отложенные", BUTTON_IN_KEPT));
        }
        return markupInLine;
    }

    private InlineKeyboardButton createButton(String text, String name){
        return createButton(text, name, null);
    }

    private InlineKeyboardButton createButton(String text, String name, String url){
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(name);
        button.setUrl(url);
        return button;
    }

    private String getShortTextDescriptionBotBook(BotUser botUser, BotBook book){
        StringBuilder sb = new StringBuilder();
        sb.append(" Идентификатор: ").append(book.getBook().getId() + botUser.getId() % 100).append("\n");
        sb.append("\t\t\t\tНазвание: ").append(book.getBook().getTitle()).append("\n");
        sb.append("\t\t\t\tАвторы: ").append(book.getAuthors()).append("\n");
        sb.append("\t\t\t\tЖанры: ").append(book.getGenres()).append("\n");
        sb.append("\t\t\t\tРэйтинг: ").append(book.getRating().toString()).append("\n");
        return sb.toString();
    }

    private String getFullTextDescriptionBotBook(BotBook book, String text){
        StringBuilder sb = new StringBuilder(text);
        sb.append("\n\t\t\t\tТэги: ").append(book.getTags()).append("\n");
        if (book.getBook().getIsBestseller() == 1) {
            sb.append("\tЯвляется бестселлером").append("\n");
        }
        sb.append("\t\t\t\tОписание: ").append(book.getBook().getDescription()).append("\n");
        sb.append("\t\t\t\t\t\tЦена: ").append(book.getBook().getPrice());
        if (book.getBook().getDiscount() > 0) {
            sb.append("\t\t\t\tЦена со скидкой: ").append(Math.round((book.getBook().getPrice() - book.getBook().getPrice() * book.getBook().getDiscount()) * 100) / 100);
        }
        return sb.toString();
    }

    public void sendDepositResult(String chatId){
        try {
            execute(new SendMessage(chatId, "Пополнение баланса прошло успешно"));
        } catch (TelegramApiException e) {
            logger.warning(e.getMessage());
        }
    }

}