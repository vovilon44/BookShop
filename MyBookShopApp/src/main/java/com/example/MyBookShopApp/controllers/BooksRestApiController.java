package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.ApiResponse;
import com.example.MyBookShopApp.data.services.BookService;
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
    public ResponseEntity<ApiResponse<BooksPageDto>> getRecommendedBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit)
    {
        return ResponseEntity.ok(bookService.getResponseBooks(bookService.getPageOfRecommendedBooks(offset, limit).getContent()));
    }

    @GetMapping("/books/recent")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getRecentBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @RequestParam(name = "from", required = false) String from, @RequestParam(name = "to", required = false) String to) throws ParseException {
        if (from != null && !from.equals("") && to != null && !to.equals("")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getListOfRecentBooks(offset, limit, from, to)));
        } else {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getListOfRecentBooksWithoutDate(offset, limit)));
        }
    }

    @GetMapping("/books/popular")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getPopularBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit)
    {
        return ResponseEntity.ok(bookService.getResponseBooks(bookService.getListOfPopularBooks(offset, limit)));
    }

    @GetMapping("/books/genre/{slugGenre}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getBooksForGenre(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String slugGenre)
    {
        return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBookFromGenre(slugGenre,offset, limit).getContent()));
    }

    @GetMapping("/books/author/{slugAuthor}")
    @ResponseBody
    public ResponseEntity<BooksPageDto> getBooksForAuthor(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String slugAuthor)
    {
        return ResponseEntity.ok(new BooksPageDto(bookService.getBooksFromAuthor(slugAuthor,offset, limit).getContent()));
    }

    @GetMapping("/books/tag/{slugTag}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getBooksForTag(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String slugTag) throws BookstoreApiWrongParameterException {
        ApiResponse<BooksPageDto> response = new ApiResponse<>();
        BooksPageDto data = new BooksPageDto(bookService.getBookFromTag(slugTag,offset, limit).getContent());
        response.setDebugMessage("successful request");
        response.setMessage("data size: " + data.getCount() + " elements");
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(data);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search/{searchText}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getSearchBooks(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit, @PathVariable String searchText) throws BookstoreApiWrongParameterException {
        return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBooksBySearch(searchText,offset, limit).getContent()));
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
