package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BookRatingDto;
import com.example.MyBookShopApp.data.RateReviewDto;
import com.example.MyBookShopApp.data.ReviewMessageDto;
import com.example.MyBookShopApp.data.services.*;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.links.BookLike2UserEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final TagService tagService;
    private final BookReviewService bookReviewService;
    private final BookReviewLikeService bookReviewLikeService;
    private final BookLike2UserService bookLike2UserService;
    private final Book2UserService book2UserService;
    private final Book2UserHistoryService book2UserHistoryService;
    private final BookstoreUserRegister userRegister;
    @Value("${books.pool.limit.size}")
    private Integer limit;
    @Value("${books.pool.offset.size}")
    private Integer offset;

    @Autowired
    public BookController(BookService bookService, TagService tagService, BookReviewService bookReviewService, BookReviewLikeService bookReviewLikeService, BookLike2UserService bookLike2UserService, Book2UserService book2UserService, Book2UserHistoryService book2UserHistoryService, BookstoreUserRegister userRegister) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.bookReviewService = bookReviewService;
        this.bookReviewLikeService = bookReviewLikeService;
        this.bookLike2UserService = bookLike2UserService;
        this.book2UserService = book2UserService;
        this.book2UserHistoryService = book2UserHistoryService;
        this.userRegister = userRegister;
    }




    @GetMapping("/{slugBook}")
    public String booksPage(@PathVariable String slugBook,
                            @CookieValue(name = "historyVisit", required = false) String historyVisit,
                            @CookieValue(name = "booksRating", required = false) String booksRating,
                            @CookieValue(name = "cartContents", required = false) String cartContents,
                            @CookieValue(name = "keptContents", required = false) String keptContents,
                            HttpServletRequest request, HttpServletResponse response, Model model) throws BookstoreApiWrongParameterException {
        if (request.isUserInRole("ROLE_USER")){
            book2UserHistoryService.updateBook2UserHistory(slugBook);
            BookLike2UserEntity bookRatingForUser = bookLike2UserService.getRatingBookForUser(slugBook);
            if (bookRatingForUser != null){
                model.addAttribute("isBookRatingFromUserEmpty", false);
                model.addAttribute("bookRatingFromUser", bookRatingForUser.getLikeValue());
            } else {
                model.addAttribute("isBookRatingFromUserEmpty", true);
            }
            Book2UserEntity book2User = book2UserService.getBook2User(slugBook);
            if (book2User != null) {
                switch (book2User.getTypeId()) {
                    case 1:
                        model.addAttribute("bookKept", true);
                        break;
                    case 2:
                        model.addAttribute("bookCart", true);
                        break;
                    case 4:
                        model.addAttribute("bookArch", true);
                        break;
                }
            }
            model.addAttribute("downloadExceedance", bookService.checkDownloadExceedance(slugBook));

        }
        else {
            if (booksRating != null && booksRating.contains(slugBook)) {
                List<String> booksRatingList = List.of(booksRating.split("/"));
                booksRatingList.forEach(e -> {
                    if (e.contains(slugBook)) {
                        Integer value = Integer.parseInt(e.split("=")[1]);
                        model.addAttribute("bookRatingFromUser", value);
                        model.addAttribute("isBookRatingFromUserEmpty", false);
                    }
                });
            } else {
                model.addAttribute("isBookRatingFromUserEmpty", true);
            }
            if (historyVisit == null) {
                Cookie cookieWithHistory = new Cookie("historyVisit", slugBook);
                cookieWithHistory.setPath("/");
                response.addCookie(cookieWithHistory);
            } else if (!historyVisit.contains(slugBook)) {
                StringJoiner visits = new StringJoiner("/");
                visits.add(historyVisit).add(slugBook);
                Cookie cookieWithHistory = new Cookie("historyVisit", visits.toString());
                cookieWithHistory.setPath("/");
                response.addCookie(cookieWithHistory);
            }
            if (cartContents != null && cartContents.contains(slugBook)){
                model.addAttribute("bookCart", true);
            } else if (keptContents != null && keptContents.contains(slugBook)) {
                model.addAttribute("bookKept", true);
            }
            model.addAttribute("downloadExceedance", false);
        }
        bookService.getBookFromSlug(slugBook).getBookFileList().forEach(e->e.setSize(bookService.getBookFileSize(e.getPath())));

        model.addAttribute("book", bookService.getBookFromSlug(slugBook));

        model.addAttribute("tags", tagService.getTagsForBookBySlug(slugBook));
        return "/books/slug";
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String slug) throws IOException, BookstoreApiWrongParameterException {
        bookService.saveFile(file, slug);
        return ("redirect:/books/" + slug);
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash, HttpServletRequest request) throws IOException {
        if (request.isUserInRole("ROLE_USER") && book2UserService.checkAccessForBook(hash)) {
            byte[] data = bookService.getBookFile(hash);
            Path path = bookService.getPathFile(hash);
            MediaType mediaType = bookService.getBookFileMime(hash);
            bookService.incDownloadCount(hash);

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                    .contentType(mediaType)
                    .contentLength(data.length)
                    .body(new ByteArrayResource(data));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/download/bot/{hash}")
    public String botBookFile(@PathVariable("hash") String hash, HttpServletResponse response) throws IOException {
        HashMap<String, String> slugBookAndUserId = bookService.parseBotHash(hash);
        if (slugBookAndUserId != null){

            response.setHeader("Set-Cookie", "token=" + userRegister.login(Integer.parseInt(slugBookAndUserId.get("userId"))) + "; Path=/;");
            return "redirect:/books/" + slugBookAndUserId.get("slug");
        } else {
            return "redirect:/";
        }



    }

    @PostMapping("/rateBookReview")
    public String handleRatingReviewRequest(@RequestBody RateReviewDto rateReviewDto, @CookieValue(value = "token", required = false) String token, HttpServletRequest request) {
        if (token != null){
            bookReviewLikeService.addLikeReview(rateReviewDto.getReviewId(), rateReviewDto.getValue());
        }
        return "redirect:" + request.getRequestURI();
    }

    @PostMapping("/bookReview")
    public String handleReviewMessageRequest(@RequestBody ReviewMessageDto reviewMessageDto, @CookieValue(value = "token", required = false) String token, HttpServletRequest request) {
        if (token != null) {
            bookReviewService.addReview(reviewMessageDto.getBookId(), reviewMessageDto.getText());
        }
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping("/changeBookStatus/rating/{slug}")
    public String handleChangeBookRating(@PathVariable("slug") String slug,
                                         @CookieValue(name = "booksRating", required = false) String booksRating,
                                         @CookieValue(name = "token", required = false) String token,
                                         @RequestBody BookRatingDto bookRatingDto, HttpServletResponse response, HttpServletRequest request, Model model) throws BookstoreApiWrongParameterException {
        if (request.isUserInRole("ROLE_USER")) {
            if (bookRatingDto.getValue() != null) {
                bookLike2UserService.updateBookLike2UserLikeValue(slug, bookRatingDto.getValue());
            }
        } else {
            if (bookRatingDto.getValue() != null) {
                if (booksRating != null && booksRating.contains(slug)) {
                    StringJoiner stringJoiner = new StringJoiner("/");
                    for (String rating : booksRating.split("/")) {
                        if (rating.contains(slug)) {
                            stringJoiner.add(slug + "=" + bookRatingDto.getValue());
                        } else {
                            stringJoiner.add(rating);
                        }
                    }
                    Cookie cookie = new Cookie("booksRating", stringJoiner.toString());
                    cookie.setPath("/books");
                    response.addCookie(cookie);

                } else {
                    if (booksRating == null || booksRating.equals("")){
                        Cookie cookie = new Cookie("booksRating", slug + "=" + bookRatingDto.getValue());
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    } else if (!booksRating.contains(slug)) {
                        StringJoiner stringJoiner = new StringJoiner("/");
                        stringJoiner.add(booksRating).add(slug + "=" + bookRatingDto.getValue());
                        Cookie cookie = new Cookie("booksRating", stringJoiner.toString());
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    }
                }
            }
        }
        return "redirect:/books/" + slug;
    }
    @GetMapping("/my")
    public String handleMy(Model model){
        model.addAttribute("books", bookService.getMyBooks(offset,limit));
        model.addAttribute("category", "my");
        return "my";
    }

    @GetMapping("/my/archive")
    public String handleMyArchive(Model model){
        model.addAttribute("books", bookService.getMyBooksInArchive(offset,limit));
        model.addAttribute("category", "archive");
        return "my";
    }

    @GetMapping("/my/history")
    public String handleMyHistory(Model model){
        model.addAttribute("books", bookService.getMyHistoryBooks(offset,limit));
        model.addAttribute("category", "history");
        return "my";
    }


//    @GetMapping("/myarchive")
//    public String handleMyarchive(){
//        bookService
//        return "myarchive";
//    }

}
