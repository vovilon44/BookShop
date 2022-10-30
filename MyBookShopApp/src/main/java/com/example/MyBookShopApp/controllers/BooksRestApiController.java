package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BookService;
import com.example.MyBookShopApp.data.BooksPageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BooksRestApiController {

    private final BookService bookService;

    @Autowired
    public BooksRestApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books/recommended")
    public ResponseEntity<BooksPageDto> getRecommendedBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit)
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit)));
    }

    @GetMapping("/books/recent")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getRecentBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @RequestParam("from") Optional<String> from, @RequestParam("to") Optional<String> to) {
        if (from.isPresent() || to.isPresent()) {
            return ResponseEntity.ok(new BooksPageDto(bookService.getListOfRecentBooks(offset, limit, from.get(), to.get())));
        } else {
            return ResponseEntity.ok(new BooksPageDto(bookService.getListOfRecentBooksWithoutDate(offset, limit)));
        }
    }

    @GetMapping("/books/popular")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getPopularBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit)
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getListOfPopularBooks(offset, limit)));
    }

    @GetMapping("/books/genre/{slugGenre}")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getBooksForGenre(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String slugGenre)
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getBookFromGenre(slugGenre,offset, limit)));
    }

    @GetMapping("/books/author/{slugAuthor}")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getBooksForAuthor(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String slugAuthor)
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getBooksFromAuthor(slugAuthor,offset, limit)));
    }

    @GetMapping("/books/tag/{slugTag}")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getBooksForTag(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String slugTag)
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getBookFromTag(slugTag,offset, limit)));
    }


    @GetMapping("/search/{searchText}")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getSearchBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String searchText)
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getBooksBySearch(searchText,offset, limit)));
    }
}
