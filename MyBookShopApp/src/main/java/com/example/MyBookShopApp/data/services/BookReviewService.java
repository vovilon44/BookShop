package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.BookRepository;
import com.example.MyBookShopApp.data.repositories.BookReviewLikeRepository;
import com.example.MyBookShopApp.data.repositories.BookReviewRepository;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BookReviewService
{
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookRepository bookRepository;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private  final BookstoreUserRegister userRegister;

    @Autowired
    public BookReviewService(BookReviewRepository bookReviewRepository, BookReviewLikeRepository bookReviewLikeRepository, BookRepository bookRepository, BookstoreUserRepository bookstoreUserRepository, NamedParameterJdbcTemplate jdbcTemplate, BookstoreUserRegister userRegister) {
        this.bookReviewRepository = bookReviewRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.bookRepository = bookRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.userRegister = userRegister;
    }

    public List<BookReviewEntity> getBookReview(String slug){
        if (slug == null){
            return new ArrayList<>();
        } else {
            List<BookReviewEntity> bookReviewEntityList = jdbcTemplate.query("select *, COALESCE((select sum(value) from book_review_like as brl where brl.review_id=br.id group by review_id), 0)  as rang from book_review as br inner join books on br.book_id = books.id where books.slug = :slug order by rang desc ", Map.of("slug", slug), (ResultSet rs, int rowNum) -> {
                BookReviewEntity bookReviewEntity = new BookReviewEntity();
                bookReviewEntity.setId(rs.getInt("id"));
                bookReviewEntity.setBook(bookRepository.findBookById(rs.getInt("book_id")));
                bookReviewEntity.setUser(bookstoreUserRepository.findBookstoreUserById(rs.getInt("user_id")));
                bookReviewEntity.setTime(rs.getDate("time"));
                bookReviewEntity.setText(rs.getString("text"));
                bookReviewEntity.setBookReviewLikeEntityList(bookReviewLikeRepository.findByReview_Id(rs.getInt("id")));
                return bookReviewEntity;
            });
            if (bookReviewEntityList != null) {
                return bookReviewEntityList;
            } else {
                return new ArrayList<>();
            }
        }
    }

    public void addReview(String slug, String text) {
        BookReviewEntity bookReview = new BookReviewEntity();
        bookReview.setUser(userRegister.getCurrentUser());
        bookReview.setBook(bookRepository.findBookBySlug(slug));
        bookReview.setTime(new Date());
        bookReview.setText(text);
        bookReview.setBookReviewLikeEntityList(new ArrayList<>());
        bookReviewRepository.save(bookReview);
    }
}
