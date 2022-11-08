package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.ApiResponse;
import com.example.MyBookShopApp.data.BookService;
import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDateTime;

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
        return ResponseEntity.ok(new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent()));
    }

    @GetMapping("/books/recent")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getRecentBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @RequestParam("from") String from, @RequestParam("to") String to) throws ParseException {
        if (from != null && !from.equals("") && to != null && !to.equals("")) {
            return ResponseEntity.ok(new BooksPageDto(bookService.getListOfRecentBooks(offset, limit, from, to).getContent()));
        } else {
            return ResponseEntity.ok(new BooksPageDto(bookService.getListOfRecentBooksWithoutDate(offset, limit).getContent()));
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
        return ResponseEntity.ok(new BooksPageDto(bookService.getBookFromGenre(slugGenre,offset, limit).getContent()));
    }

    @GetMapping("/books/author/{slugAuthor}")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getBooksForAuthor(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String slugAuthor)
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getBooksFromAuthor(slugAuthor,offset, limit).getContent()));
    }

    @GetMapping("/books/tag/{slugTag}")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getBooksForTag(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String slugTag) throws BookstoreApiWrongParameterException {
        return ResponseEntity.ok(new BooksPageDto(bookService.getBookFromTag(slugTag,offset, limit).getContent()));
    }


    @GetMapping("/search/{searchText}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getSearchBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String searchText) throws BookstoreApiWrongParameterException {
        ApiResponse<BooksPageDto> response = new ApiResponse<>();
        BooksPageDto data = new BooksPageDto(bookService.getBooksBySearch(searchText,offset, limit).getContent());
        response.setDebugMessage("successful request");
        response.setMessage("data size: " + data.getCount() + " elements");
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/fromto")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getCustomBooks()
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getBooksBySearch()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<BooksPageDto>> MissingServletRequestParameterException(Exception exception){
        return new ResponseEntity<>(new ApiResponse<BooksPageDto>(HttpStatus.BAD_REQUEST, "Missing required parameters", exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookstoreApiWrongParameterException.class)
    public ResponseEntity<ApiResponse<BooksPageDto>> handleBookstoreApiWrongParameterException(Exception exception){
        return new ResponseEntity<>(new ApiResponse<BooksPageDto>(HttpStatus.BAD_REQUEST, "Bad parameter value...", exception), HttpStatus.BAD_REQUEST);
    }


}
