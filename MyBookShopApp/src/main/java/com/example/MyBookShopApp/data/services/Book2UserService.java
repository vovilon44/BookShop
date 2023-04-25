package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.CountCartKeptMainDto;
import com.example.MyBookShopApp.data.TransactionEntity;
import com.example.MyBookShopApp.data.TransactionsDto;
import com.example.MyBookShopApp.data.repositories.Book2UserRepository;
import com.example.MyBookShopApp.data.repositories.TransactionRepository;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.telegram.BotBooksResponse;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

@Service
public class Book2UserService {
    private final Book2UserRepository book2UserRepository;

    private final BookService bookService;

    private final BookstoreUserRegister userRegister;

    private final TransactionRepository transactionRepository;
    private final BookstoreUserRepository bookstoreUserRepository;

    @Autowired
    public Book2UserService(Book2UserRepository book2UserRepository, BookService bookService, BookstoreUserRegister userRegister, TransactionRepository transactionRepository, BookstoreUserRepository bookstoreUserRepository) {
        this.book2UserRepository = book2UserRepository;
        this.bookService = bookService;
        this.userRegister = userRegister;
        this.transactionRepository = transactionRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean addBook2User(List<String> slugs, String status) {
        List<Book> books = bookService.getBooksInSlugs(slugs);
        if (slugs.size() == books.size()) {
            List<Book2UserEntity> book2UserList = book2UserRepository.findBook2UserEntityByBook_SlugInAndUser_Email(slugs, userRegister.getCurrentUser().getEmail());
            switch (status) {
                case "CART":
                    if (book2UserList.stream().allMatch(e -> e.getTypeId() != 3 && e.getTypeId() != 4)) {
                        return saveBook2User(books, book2UserList, 2);
                    } else {
                        return false;
                    }
                case "UNLINK":
                    if (book2UserList.stream().allMatch(e -> e.getTypeId() != 3 && e.getTypeId() != 4)) {
                        book2UserRepository.deleteAll(book2UserList);
                        return true;
                    } else {
                        return false;
                    }
                case "KEPT":
                    if (book2UserList.stream().allMatch(e -> e.getTypeId() != 3 && e.getTypeId() != 4)) {
                        return saveBook2User(books, book2UserList, 1);
                    } else {
                        return false;
                    }
                case "ARCHIVED":
                    if (book2UserList.size() > 0 && book2UserList.stream().allMatch(e -> e.getTypeId() == 3)) {
                        return saveBook2User(books, book2UserList, 4);
                    } else {
                        return false;
                    }
                case "PAID":
                    if (book2UserList.stream().allMatch(e -> e.getTypeId() == 4 || e.getTypeId() == 2)) {
                        return saveBook2User(books, book2UserList, 3);
                    } else {
                        return false;
                    }
            }

        }
        return false;
    }

    private boolean saveBook2User(List<Book> books, List<Book2UserEntity> book2UserEntityList, Integer type){
        for (Book2UserEntity book2User : book2UserEntityList){
            book2User.setTime(LocalDate.now());
            book2User.setTypeId(type);
            books.remove(book2User.getBook());
            book2UserRepository.save(book2User);
        }
        for (Book book : books){
            Book2UserEntity book2User = new Book2UserEntity();
            book2User.setTime(LocalDate.now());
            book2User.setTypeId(type);
            book2User.setBook(book);
            book2User.setUser(userRegister.getCurrentUser());
            book2UserRepository.save(book2User);
        }
        return true;
    }


    public Book2UserEntity getBook2User(String slug){
        return book2UserRepository.findBook2UserEntitiesByBook_SlugAndUser(slug, userRegister.getCurrentUser());
    }



    public boolean changeStatusForCartBooksWithCartToPaid() {
        List<Book2UserEntity> book2UserEntities = book2UserRepository.findBook2UserEntitiesByUser_EmailAndTypeIdIs(userRegister.getCurrentUser().getEmail(), 2);
        if (book2UserEntities != null) {
            book2UserEntities.forEach(e -> {
                e.setTypeId(3);
                book2UserRepository.save(e);
            });
            return true;
        }
        return false;
    }


    public TransactionsDto getTransactions(Integer offset, Integer limit, String local)
    {

        Pageable nextPage = PageRequest.of(offset,limit);
        DateTimeFormatter df;
        if (local.equals("ru")){
            df = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", new Locale("ru"));
        } else {
            df = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH);
        }
        Page<TransactionEntity> transactionsPage = transactionRepository.findAllByUser_IdOrderByTimeDesc(userRegister.getCurrentUser().getId(), nextPage);
        List<TransactionEntity> transactions = transactionsPage.getContent();
        transactions.forEach(e->e.setDateTimeToString(e.getTime().format(df)));
        return new TransactionsDto(transactions,transactions.size(), offset * limit + transactions.size() >= transactionsPage.getTotalElements());
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean payBooks() {
        List<Book> books = bookService.getBooksInMyType(2);
        if (books != null) {
            Double paymentSumTotal = books.stream().mapToDouble(e -> e.getPrice() - (e.getPrice() * e.getDiscount())).sum();
            if (paymentSumTotal <= userRegister.getBalance()) {
                if (changeStatusForCartBooksWithCartToPaid() && userRegister.correctBalanceAfterPay(paymentSumTotal)) {
                    books.forEach(e -> {
                        TransactionEntity transaction = new TransactionEntity();
                        transaction.setBook(e);
                        transaction.setUser(userRegister.getCurrentUser());
                        transaction.setTime(LocalDateTime.now());
                        transaction.setDescription("Buy book \'" + e.getTitle() + "\'");
                        transaction.setValue(e.getPrice() - (e.getPrice() * e.getDiscount()));
                        transactionRepository.save(transaction);
                    });
                    return true;
                }
            }
        }
        return false;
    }

    public String removeBookInCookie(String contents, String slug) {
        if (contents!= null && contents.contains(slug)){
            StringJoiner sj = new StringJoiner("/");
            for (String slugBook: contents.split("/")){
                if (!slugBook.equals(slug)){
                    sj.add(slugBook);
                }
            }
            return sj.toString();
        } else {
            return contents;
        }
    }

    public String addBookInCookie(String contents, String slug) {
        if (contents == null || contents.equals("")){
            return slug;
        } else {
            return (new StringJoiner("/").add(contents).add(slug).toString());
        }
    }

    private Integer getBookCountForTypeAndCurrentUser(Integer type) {
        return book2UserRepository.countAllByUserAndTypeIdIs(userRegister.getCurrentUser(), type);
    }

    public CountCartKeptMainDto getHeaderInfoAuthUser(){
        return new CountCartKeptMainDto(
                getBookCountForTypeAndCurrentUser(3),
                getBookCountForTypeAndCurrentUser(2),
                getBookCountForTypeAndCurrentUser(1),
                Math.round(userRegister.getBalance() * 100.0) / 100.0,
                userRegister.getCurrentUser().getName());

    }

    public Double getSummInCart(){
        return book2UserRepository.getSummCustom(userRegister.getCurrentUser().getId());
    }

    public boolean checkAccessForBook(String hash) {
        Book2UserEntity book2User = book2UserRepository.findBook2UserEntityByBook_BookFileList_HashIsAndUser_IdIsAndTypeIdIs(hash, userRegister.getCurrentUser().getId(), 3);
        if (book2User != null){
            return true;
        } else {
            return false;
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BotBooksResponse botChangeStatusBookForUser(BookstoreUser user, Integer bookId, Integer bookTypeId) {
        Book2UserEntity book2User = book2UserRepository.findBook2UserEntitiesByBookIdAndUser(bookId, user);
        if (book2User != null && !(bookTypeId == 3 && book2User.getTypeId() == 2)){
            book2User.setTypeId(bookTypeId);
            book2UserRepository.save(book2User);
            return new BotBooksResponse(true, "Статус книги изменен");
        } else if (bookTypeId == 3 && book2User.getTypeId() == 2){
            if (botPayBookInCard(book2User)){
                return new BotBooksResponse(true, "Книга успешно куплена");
            } else {
                return new BotBooksResponse(false, "На балансе недостаточно средств");
            }
        }
        return new BotBooksResponse(false, "Статус книги не изменился");
    }

    public BotBooksResponse botDeleteBookStatus(BookstoreUser user, Integer bookId) {
        Book2UserEntity book2User = book2UserRepository.findBook2UserEntitiesByBookIdAndUser(bookId, user);
        if (book2User != null){
            book2UserRepository.delete(book2User);
            return new BotBooksResponse(true, "Статус книги изменен");
        }
        return new BotBooksResponse(true, "Статус книги не изменился");
    }

    public BotBooksResponse botAddBookInCard(BookstoreUser user, Integer bookId, Integer bookTypeId) {
        if (book2UserRepository.findBook2UserEntitiesByBookIdAndUser(bookId, user) == null){
            book2UserRepository.save(new Book2UserEntity(bookTypeId, bookService.getBookById(bookId), user));
            return new BotBooksResponse(true, "Статус книги изменен");
        }
        return new BotBooksResponse(true, "Статус книги не изменился");
    }

    private boolean botPayBookInCard(Book2UserEntity book2User) {
        Double priceBook = book2User.getBook().getPrice() - book2User.getBook().getPrice() * book2User.getBook().getDiscount();
        BookstoreUser user = book2User.getUser();
        if (user.getBalance() > priceBook) {
            user.setBalance(user.getBalance() - priceBook);
            book2User.setTypeId(3);
            bookstoreUserRepository.save(user);
            book2UserRepository.save(book2User);
            return true;
        }
        return false;
    }
    public Book2UserEntity botGetBook2User(BookstoreUser user, Integer bookId) {
        return book2UserRepository.findBook2UserEntitiesByBookIdAndUser(bookId, user);
    }

//    public String getChangeCookie(String slug, String cartContents, String keptContents)
//    {
//
//    }
}
