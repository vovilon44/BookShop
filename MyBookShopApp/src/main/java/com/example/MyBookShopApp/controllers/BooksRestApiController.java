package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import com.example.MyBookShopApp.data.services.*;
import com.example.MyBookShopApp.data.telegram.Bot;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

@RestController
@RequestMapping("/api")
public class BooksRestApiController {

    private final BookService bookService;
    private final BookLike2UserService bookLike2UserService;
    private final BookReviewLikeService bookReviewLikeService;
    private final BookstoreUserRegister userRegister;
    private final SessionService sessionService;
    private final Book2UserService book2UserService;
    private final DepositService depositService;
    private final Bot bot;

    @Autowired
    public BooksRestApiController(BookService bookService, BookLike2UserService bookLike2UserService, BookReviewLikeService bookReviewLikeService, BookstoreUserRegister userRegister, SessionService sessionService, Book2UserService book2UserService, DepositService depositService, Bot bot) {
        this.bookService = bookService;
        this.bookLike2UserService = bookLike2UserService;
        this.bookReviewLikeService = bookReviewLikeService;
        this.userRegister = userRegister;
        this.sessionService = sessionService;
        this.book2UserService = book2UserService;
        this.depositService = depositService;
        this.bot = bot;
    }

    @GetMapping("/books/recommended")
    public ResponseEntity<ApiResponse<BooksPageDto>> getRecommendedBooks(HttpServletRequest request,
                                                                         @RequestParam("offset") Integer offset,
                                                                         @RequestParam("limit") Integer limit,
                                                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                                                         @CookieValue(name = "keptContents", required = false) String keptContents,
                                                                         @CookieValue(name = "historyVisit", required = false) String historyVisit)
    {
            if (request.isUserInRole("ROLE_USER")) {
                return ResponseEntity.ok(bookService.getResponseBooks(bookService.getPageOfRecommendedBooksAuthorized(offset, limit)));
            } else {
                return ResponseEntity.ok(bookService.getResponseBooks(bookService.getPageOfRecommendedBooksNotAuthorized(offset, limit, cartContents, keptContents, historyVisit)));
            }

    }

    @GetMapping("/books/recent")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getRecentBooks(HttpServletRequest request,
                                                                    @RequestParam("offset") Integer offset,
                                                                    @RequestParam("limit") Integer limit,
                                                                    @RequestParam(name = "from", required = false) String from,
                                                                    @RequestParam(name = "to", required = false) String to,
                                                                    @CookieValue(name = "cartContents", required = false) String cartContents,
                                                                    @CookieValue(name = "keptContents", required = false) String keptContents) throws ParseException {
        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getListOfRecentBooksAuthorized(offset, limit, from, to)));
        } else {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getListOfRecentBooksNotAuthorized(offset, limit, from, to, cartContents, keptContents)));
        }
    }

    @GetMapping("/books/popular")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getPopularBooks(HttpServletRequest request,
                                                                     @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit,
                                                                     @CookieValue(name = "cartContents", required = false) String cartContents,
                                                                     @CookieValue(name = "keptContents", required = false) String keptContents)
    {

        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getListOfPopularBooksAuthorized(offset, limit)));
        } else {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getListOfPopularBooksNotAuthorized(offset, limit, cartContents, keptContents)));
        }
    }

    @GetMapping("/books/genre/{slugGenre}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getBooksForGenre(HttpServletRequest request, @RequestParam("offset") Integer offset,
                                                                      @RequestParam("limit") Integer limit, @PathVariable String slugGenre,
                                                                      @CookieValue(name = "cartContents", required = false) String cartContents,
                                                                      @CookieValue(name = "keptContents", required = false) String keptContents)
    {
        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBookFromGenreAuthorized(slugGenre, offset, limit)));
        } else {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBookFromGenreNotAuthorized(slugGenre, offset, limit, cartContents, keptContents)));
        }
    }

    @GetMapping("/books/author/{slugAuthor}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getBooksForAuthor(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit,
                                                          @PathVariable String slugAuthor, HttpServletRequest request,
                                                          @CookieValue(name = "cartContents", required = false) String cartContents,
                                                          @CookieValue(name = "keptContents", required = false) String keptContents)
    {
        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBooksFromAuthorAuthorized(slugAuthor, offset, limit)));
        } else {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBooksFromAuthorNotAuthorized(slugAuthor, offset, limit, cartContents, keptContents)));
        }
    }

    @GetMapping("/books/tag/{tagSlug}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getBooksForTag(HttpServletRequest request, @RequestParam("offset") Integer offset,
                                                                    @RequestParam("limit") Integer limit, @PathVariable String tagSlug,
                                                                    @CookieValue(name = "cartContents", required = false) String cartContents,
                                                                    @CookieValue(name = "keptContents", required = false) String keptContents) throws BookstoreApiWrongParameterException {
        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBookFromTagAuthorized(tagSlug, offset, limit)));
        } else {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBookFromTagNotAuthorized(tagSlug, offset, limit, cartContents, keptContents)));
        }
    }

    @PostMapping("/rateBook/{slug}")
    public ResponseEntity<ApiResponse> handleChangeBookRating(@PathVariable("slug") String slug,
                                                                  @CookieValue(name = "booksRating", required = false) String booksRating,
                                                                  @RequestBody BookRatingDto bookRatingDto, HttpServletResponse response, HttpServletRequest request) throws BookstoreApiWrongParameterException {
        ApiResponse apiResponse = new ApiResponse<>();
        if (bookService.checkBookPresence(slug) && bookRatingDto.getValue() != null && bookRatingDto.getValue() > 0 && bookRatingDto.getValue() <= 5) {
            if (request.isUserInRole("ROLE_USER")) {
                if (!bookLike2UserService.updateBookLike2UserLikeValue(slug, bookRatingDto.getValue())) {
                    apiResponse.setResult(false);
                    apiResponse.setMessage("book not found");
                    return ResponseEntity.ok(apiResponse);
                }
            } else {
                Cookie cookie = new Cookie("booksRating", bookService.getCookieForRatingBook(booksRating, slug, bookRatingDto.getValue()));
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            apiResponse.setResult(true);
            apiResponse.setStatus(HttpStatus.OK);
            return ResponseEntity.ok(apiResponse);
        } else {
            apiResponse.setResult(false);
            apiResponse.setMessage("book not found");
            return ResponseEntity.ok(apiResponse);
        }
    }

    @PostMapping("/changeBookStatus")
    public ResponseEntity<ApiResponse> handleChangeBookStatus(
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "keptContents", required = false) String keptContents,
                                         @CookieValue(name = "token", required = false) String token,
                                         @RequestBody BookStatusDto bookStatusDto, HttpServletRequest request, HttpServletResponse response, Model model) throws BookstoreApiWrongParameterException {
        ApiResponse apiResponse = new ApiResponse<>();
        if (bookStatusDto.getStatus() != null && !bookStatusDto.getStatus().equals("") && bookStatusDto.getBooksIds().size() > 0) {
            if (request.isUserInRole("ROLE_USER")) {
                    if (!book2UserService.addBook2User(bookStatusDto.getBooksIds(), bookStatusDto.getStatus())){
                        apiResponse.setResult(false);
                        apiResponse.setMessage("Книга куплена или в архиве");
                        apiResponse.setStatus(HttpStatus.OK);
                        return ResponseEntity.ok(apiResponse);
                    };
            } else {
                for (String slug : bookStatusDto.getBooksIds()) {
                    if (bookStatusDto.getStatus().equals("KEPT")) {
                        cartContents = book2UserService.removeBookInCookie(cartContents, slug);
                        keptContents = book2UserService.addBookInCookie(keptContents, slug);
                    } else if (bookStatusDto.getStatus().equals("CART")) {
                        cartContents = book2UserService.addBookInCookie(cartContents, slug);
                        keptContents = book2UserService.removeBookInCookie(keptContents, slug);
                    } else if (bookStatusDto.getStatus().equals("UNLINK")) {
                        cartContents = book2UserService.removeBookInCookie(cartContents, slug);
                        keptContents = book2UserService.removeBookInCookie(keptContents, slug);
                    }
                }
                Cookie cartContentsCookie = new Cookie("cartContents", cartContents);
                cartContentsCookie.setPath("/");
                Cookie keptContentsCookie = new Cookie("keptContents", keptContents);
                keptContentsCookie.setPath("/");
                response.addCookie(cartContentsCookie);
                response.addCookie(keptContentsCookie);
        }
        }

        apiResponse.setResult(true);
        apiResponse.setStatus(HttpStatus.OK);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/getBooksCountMainKeptCart")
    public CountCartKeptMainDto getCountMainKeptCart(HttpServletRequest request){
        if (request.isUserInRole("ROLE_USER")) {
            return book2UserService.getHeaderInfoAuthUser();
        } else {
            return new CountCartKeptMainDto(false);
        }
    }
    @GetMapping("/transactions")
    @ResponseBody
    public ResponseEntity<ApiResponse<TransactionsDto>> getBooksForTag(HttpServletRequest request, @RequestParam("offset") Integer offset,
                                                                    @RequestParam("limit") Integer limit, Model model) throws BookstoreApiWrongParameterException {
            return ResponseEntity.ok(bookService.getResponseTransactions(book2UserService.getTransactions(offset, limit, model.getAttribute("currentLocale").toString())));
    }

    @GetMapping("/search/{searchText}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getSearchBooks(HttpServletRequest request,
                                                                    @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit,
                                                                    @CookieValue(name = "cartContents", required = false) String cartContents,
                                                                    @CookieValue(name = "keptContents", required = false) String keptContents,
                                                                    @PathVariable String searchText) throws BookstoreApiWrongParameterException {
        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBooksBySearchAuthorized(searchText, offset, limit)));
        } else {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getBooksBySearchNotAuthorized(searchText, offset, limit, cartContents, keptContents)));
        }


    }

    @PostMapping("/rateBookReview")
    public ResponseEntity<ApiResponse> handleRatingReviewRequest(@RequestBody RateReviewDto rateReviewDto, @CookieValue(value = "token", required = false) String token, HttpServletRequest request) {
        ApiResponse response1 = new ApiResponse<>();
        if (rateReviewDto.getReviewId() != null &&
                rateReviewDto.getValue() != null &&
                rateReviewDto.getReviewId() > 0 &&
                (rateReviewDto.getValue() == 1 || rateReviewDto.getValue() == -1)&&
                request.isUserInRole("ROLE_USER")){
            bookReviewLikeService.addLikeReview(rateReviewDto.getReviewId(), rateReviewDto.getValue());
            response1.setResult(true);
            response1.setStatus(HttpStatus.OK);
            return ResponseEntity.ok(response1);
        } else {
            response1.setResult(false);
            response1.setMessage("invalid request param or not authorized");
            return ResponseEntity.ok(response1);
        }

    }

    @GetMapping("/books/my")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getMyBook(HttpServletRequest request, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit)
    {
        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getMyBooks(offset,limit)));
        } else {
            return null;
        }
    }

    @GetMapping("/books/my/archive")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getMyArchiveBook(HttpServletRequest request, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit)
    {
        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getMyBooksInArchive(offset,limit)));
        } else {
            return null;
        }
    }

    @GetMapping("/books/my/history")
    @ResponseBody
    public ResponseEntity<ApiResponse<BooksPageDto>> getMyHistoryBook(HttpServletRequest request, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit)
    {
        if (request.isUserInRole("ROLE_USER")) {
            return ResponseEntity.ok(bookService.getResponseBooks(bookService.getMyHistoryBooks(offset,limit)));
        } else {
            return null;
        }
    }

    @PostMapping("/session/remove")
    @ResponseBody
    public ResponseEntity<ApiResponse> removeSession(Integer id)
    {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.ACCEPTED, "", sessionService.removeSession(id)));
    }

    @PostMapping("/sessions/remove")
    @ResponseBody
    public ResponseEntity<ApiResponse> removeAllSessions(Integer[] ids)
    {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.ACCEPTED, "", sessionService.removeAllSessions(ids)));
    }

    @GetMapping("/pay/result")
    @ResponseBody
    public ResponseEntity<String> handleProfileResult(@RequestParam(value = "SignatureValue", required = false) String signature,
                                      @RequestParam(value = "OutSum", required = false) Double sum,
                                      @RequestParam(value = "shp_bot_chat", required = false) String chatId)
    {
        DepositTransactionEntity transaction = new DepositTransactionEntity();
        if (signature != null && sum != null){
            transaction = depositService.checkDepositTransaction(signature);
            if (transaction != null) {
                userRegister.correctBalanceAfterAdd(sum, transaction.getUser());
                if (chatId != null){
                    bot.sendDepositResult(chatId);
                }
            }

        }
        return ResponseEntity.ok("OK" + transaction.getInvId());
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
