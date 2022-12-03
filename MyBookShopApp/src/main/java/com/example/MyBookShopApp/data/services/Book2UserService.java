package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.TransactionEntity;
import com.example.MyBookShopApp.data.repositories.Book2UserRepository;
import com.example.MyBookShopApp.data.repositories.TransactionRepository;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class Book2UserService {
    private final Book2UserRepository book2UserRepository;

    private BookService bookService;

    private final BookstoreUserRegister userRegister;

    private final TransactionRepository transactionRepository;

    @Autowired
    public Book2UserService(Book2UserRepository book2UserRepository, BookService bookService, BookstoreUserRegister userRegister, TransactionRepository transactionRepository) {
        this.book2UserRepository = book2UserRepository;
        this.bookService = bookService;
        this.userRegister = userRegister;
        this.transactionRepository = transactionRepository;
    }

    public void addBook2User(String slug, Integer type) throws BookstoreApiWrongParameterException {
        Book2UserEntity book2User = book2UserRepository.findBook2UserEntityByBook_SlugAndUser_EmailAndTypeId(slug, userRegister.getCurrentUser().getEmail(), type);
        if (book2User == null) {
            book2User = new Book2UserEntity();
            book2User.setBook(bookService.getBookFromSlug(slug));
            book2User.setUser(userRegister.getCurrentUser());
            book2User.setTime(LocalDate.now());
            book2User.setTypeId(type);
            book2UserRepository.save(book2User);
        }

    }

    public void removeBook2User(String slug, Integer type) throws BookstoreApiWrongParameterException {
        Book2UserEntity book2User = book2UserRepository.findBook2UserEntityByBook_SlugAndUser_EmailAndTypeId(slug, userRegister.getCurrentUser().getEmail(), type);
        if (book2User != null) {
            book2UserRepository.delete(book2User);
        }
    }

    public Book2UserEntity getBook2UserCart(String slug, String email) throws BookstoreApiWrongParameterException {
        return book2UserRepository.findBook2UserEntitiesByBook_SlugAndUser_EmailAndTypeIdIs(slug, email, 2);

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


    public List<TransactionEntity> getTransactions()
    {
        return transactionRepository.findAllByUser_EmailIs(userRegister.getCurrentUser().getEmail());
    }


    public boolean payBooks() {
        List<Book> books = bookService.getBooksInCart(2);
        if (books != null) {
            Double paymentSumTotal = books.stream().mapToDouble(e -> e.getPrice() - (e.getPrice() * e.getDiscount())).sum();
            if (paymentSumTotal <= userRegister.getBalance()) {
                if (changeStatusForCartBooksWithCartToPaid() && userRegister.correctBalanceAfterPay(paymentSumTotal)) {
                    books.forEach(e -> {
                        TransactionEntity transaction = new TransactionEntity();
                        transaction.setBook(e);
                        transaction.setUser(userRegister.getCurrentUser());
                        transaction.setTime(LocalDate.now());
                        transaction.setDescription("Buy book " + e.getTitle());
                        transaction.setValue(e.getPrice() - (e.getPrice() * e.getDiscount()));
                        transactionRepository.save(transaction);
                    });
                    return true;
                }
            }
        }
        return false;
    }
}
