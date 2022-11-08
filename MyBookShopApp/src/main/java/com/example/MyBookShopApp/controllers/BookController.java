package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BookService;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.TagService;
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class BookController
{
    private final BookService bookService;
    private final TagService tagService;

    @Autowired
    public BookController(BookService bookService, TagService tagService) {
        this.bookService = bookService;
        this.tagService = tagService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @GetMapping("/{slugBook}")
    public String booksPage(@PathVariable String slugBook, @CookieValue(name = "booksRating", required = false) String booksRating, Model model) throws BookstoreApiWrongParameterException {
        Logger.getLogger("aaaaaaaa").info("booksRating: " + booksRating);
        if (booksRating != null && booksRating.contains(slugBook)){
            List<String> booksRatingList = List.of(booksRating.split("/"));
            booksRatingList.forEach(e->{
                if (e.contains(slugBook)){
                   Integer value = Integer.parseInt(e.split("=")[1]);
                   model.addAttribute("bookRating", value);
                    model.addAttribute("isBookRatingEmpty", false);
                }
            });
        } else {
            model.addAttribute("isBookRatingEmpty", true);
        }
        model.addAttribute("book", bookService.getBookFromSlug(slugBook));
        model.addAttribute("tags", tagService.getTagsForBookBySlug(slugBook));
        return "/books/slug";
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file")MultipartFile file, @PathVariable ("slug") String slug) throws IOException, BookstoreApiWrongParameterException {
        bookService.saveFile(file, slug);
        return ("redirect:/books/" + slug);
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable ("hash") String hash) throws IOException {
        byte[] data = bookService.getBookFile(hash);
        Path path = bookService.getPathFile(hash);
        MediaType mediaType = bookService.getBookFileMime(hash);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }




}
