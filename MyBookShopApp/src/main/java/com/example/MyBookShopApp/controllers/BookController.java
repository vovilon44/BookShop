package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.RateReviewDto;
import com.example.MyBookShopApp.data.ReviewMessageDto;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.services.BookReviewLikeService;
import com.example.MyBookShopApp.data.services.BookReviewService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.TagService;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final TagService tagService;
    private final BookReviewService bookReviewService;
    private final BookReviewLikeService bookReviewLikeService;

    @Autowired
    public BookController(BookService bookService, TagService tagService, BookReviewService bookReviewService, BookReviewLikeService bookReviewLikeService) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.bookReviewService = bookReviewService;
        this.bookReviewLikeService = bookReviewLikeService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }


    @GetMapping("/{slugBook}")
    public String booksPage(@PathVariable String slugBook, @CookieValue(name = "booksRating", required = false) String booksRating, @CookieValue(value = "token", required = false) String token, Model model) throws BookstoreApiWrongParameterException {
        if (token != null){
            model.addAttribute("bookRating", bookService.getBookFromSlug(slugBook).getBookRating());
            model.addAttribute("isBookRatingEmpty", false);
        }
        else {
            if (booksRating != null && booksRating.contains(slugBook)) {
                List<String> booksRatingList = List.of(booksRating.split("/"));
                booksRatingList.forEach(e -> {
                    if (e.contains(slugBook)) {
                        Integer value = Integer.parseInt(e.split("=")[1]);
                        model.addAttribute("bookRating", value);
                        model.addAttribute("isBookRatingEmpty", false);
                    }
                });
            } else {
                model.addAttribute("isBookRatingEmpty", true);
            }

        }
        List<BookReviewEntity> bookReviewsEntity = bookReviewService.getBookReview(slugBook);
        if (bookReviewsEntity.size() > 0){
            model.addAttribute("isReviewEmpty", false);
            model.addAttribute("reviewsBook", bookReviewsEntity);
        } else {
            model.addAttribute("isReviewEmpty", true);
        }
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
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {
        byte[] data = bookService.getBookFile(hash);
        Path path = bookService.getPathFile(hash);
        MediaType mediaType = bookService.getBookFileMime(hash);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
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

}
