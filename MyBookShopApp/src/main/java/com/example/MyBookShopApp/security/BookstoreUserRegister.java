package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.ChangeUserEntity;
import com.example.MyBookShopApp.data.repositories.Book2UserHistoryRepository;
import com.example.MyBookShopApp.data.repositories.Book2UserRepository;
import com.example.MyBookShopApp.data.repositories.BookRepository;
import com.example.MyBookShopApp.data.repositories.ChangeUserRepository;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserHistory;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class BookstoreUserRegister {
    private final BookstoreUserRepository bookstoreUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;

    private final ChangeUserRepository changeUserRepository;
    private final BookRepository bookRepository;
    private final Book2UserHistoryRepository book2UserHistoryRepository;
    private final Book2UserRepository book2UserRepository;

    private final JWTUtil jwtUtil;

    @Autowired
    public BookstoreUserRegister(BookstoreUserRepository bookstoreUserRepository, PasswordEncoder passwordEncoder, BookstoreUserDetailsService bookstoreUserDetailsService, ChangeUserRepository changeUserRepository, BookRepository bookRepository, Book2UserHistoryRepository book2UserHistoryRepository, Book2UserRepository book2UserRepository, JWTUtil jwtUtil) {
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.changeUserRepository = changeUserRepository;
        this.bookRepository = bookRepository;
        this.book2UserHistoryRepository = book2UserHistoryRepository;
        this.book2UserRepository = book2UserRepository;
        this.jwtUtil = jwtUtil;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookstoreUserDetails registerNewUser(RegistrationForm registrationForm, String cartContents, String keptContents, String historyVisit) {
        BookstoreUser userByEmail = bookstoreUserRepository.findBookstoreUserByEmail(registrationForm.getEmail());
        BookstoreUser userByPhone = bookstoreUserRepository.findBookstoreUserByPhone(registrationForm.getPhone());

        if (userByEmail == null && userByPhone == null) {
            BookstoreUser user = new BookstoreUser();
            user.setName(registrationForm.getName());
            user.setEmail(registrationForm.getEmail());
            user.setPhone(registrationForm.getPhone());
            user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            user.setRegTime(new Date());
            BookstoreUser savedUser = bookstoreUserRepository.save(user);
            if (cartContents != null && !cartContents.equals("")) {
                book2UserRepository.saveAll(
                        bookRepository.findBooksBySlugIn(Arrays.asList(cartContents.split("/")))
                                .stream().map(e -> {
                                    Book2UserEntity book2User = new Book2UserEntity();
                                    book2User.setBook(e);
                                    book2User.setUser(savedUser);
                                    book2User.setTime(LocalDate.now());
                                    book2User.setTypeId(2);
                                    return book2User;
                                }).collect(Collectors.toList()));
            }
            if (keptContents != null && !keptContents.equals("")) {
                book2UserRepository.saveAll(
                        bookRepository.findBooksBySlugIn(Arrays.asList(keptContents.split("/")))
                                .stream().map(e -> {
                                    Book2UserEntity book2User = new Book2UserEntity();
                                    book2User.setBook(e);
                                    book2User.setUser(savedUser);
                                    book2User.setTime(LocalDate.now());
                                    book2User.setTypeId(1);
                                    return book2User;
                                }).collect(Collectors.toList()));
            }
            if (historyVisit != null && !historyVisit.equals("")) {
                book2UserHistoryRepository.saveAll(
                        bookRepository.findBooksBySlugIn(Arrays.asList(historyVisit.split("/")))
                                .stream().map(e -> {
                                    Book2UserHistory book2UserHistory = new Book2UserHistory();
                                    book2UserHistory.setBook(e);
                                    book2UserHistory.setUser(savedUser);
                                    book2UserHistory.setTime(LocalDate.now());
                                    return book2UserHistory;
                                }).collect(Collectors.toList()));
            }
        return new BookstoreUserDetails(savedUser);
        } else {
            return null;
        }

    }

    public String login(Integer userId) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserById(userId);
        if (user != null){
            String jwtToken = jwtUtil.generateToken(new BookstoreUserDetails(user));
            return jwtToken;
        } else {
            return "";
        }

    }

    public String jwtLogin(ContactConfirmationPayload payload) {
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        BookstoreUserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        return jwtToken;
    }


    public BookstoreUser getCurrentUser() {
        BookstoreUserDetails userDetails = (BookstoreUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getBookstoreUser();
    }

    public boolean changePassForUser(String newPass) {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail(getCurrentUser().getEmail());
        if (bookstoreUser != null) {
            bookstoreUser.setPassword(passwordEncoder.encode(newPass));
            bookstoreUserRepository.save(bookstoreUser);
            return true;
        }
        return false;
    }

    public Double getBalance() {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail(getCurrentUser().getEmail());
        if (bookstoreUser != null) {
            return bookstoreUser.getBalance();
        } else {
            return 0.0;
        }
    }

    public boolean correctBalanceAfterPay(Double value) {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail(getCurrentUser().getEmail());
        if (bookstoreUser != null) {
            bookstoreUser.setBalance(bookstoreUser.getBalance() - value);
            bookstoreUserRepository.save(bookstoreUser);
            return true;
        } else {
            return false;
        }
    }

    public boolean correctBalanceAfterAdd(Double value, BookstoreUser user) {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail(user.getEmail());
        if (bookstoreUser != null) {
            bookstoreUser.setBalance(bookstoreUser.getBalance() + value);
            bookstoreUserRepository.save(bookstoreUser);
            return true;
        } else {
            return false;
        }
    }

    public void saveChangedUser(ChangeUserEntity changeUser) {
        changeUserRepository.save(changeUser);
    }

    public boolean changeUser(String code) {
        ChangeUserEntity changeUser = changeUserRepository.findByCodeIs(code);
        if (changeUser != null && changeUser.getExpairedTime().isAfter(LocalDateTime.now())) {
            BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail(getCurrentUser().getEmail());
            bookstoreUser.setEmail(changeUser.getEmail());
            bookstoreUser.setName(changeUser.getName());
            bookstoreUser.setPhone(changeUser.getPhone());
            bookstoreUserRepository.save(bookstoreUser);
            return true;
        } else {
            return false;
        }

    }


    public boolean checkUserByContact(String contact) {
        return bookstoreUserRepository.findBookstoreUserByEmailOrPhone(contact, contact) != null;
    }

    public BookstoreUser getBookstoreUserById(Integer userId){
        return bookstoreUserRepository.findBookstoreUserById(userId);
    }
}
