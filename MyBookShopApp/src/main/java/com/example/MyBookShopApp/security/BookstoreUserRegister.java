package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.ChangeUserEntity;
import com.example.MyBookShopApp.data.repositories.ChangeUserRepository;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookstoreUserRegister
{
    private final BookstoreUserRepository bookstoreUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final BookstoreUserDetailsService bookstoreUserDetailsService;

    private final ChangeUserRepository changeUserRepository;

    private final JWTUtil jwtUtil;

    @Autowired
    public BookstoreUserRegister(BookstoreUserRepository bookstoreUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, BookstoreUserDetailsService bookstoreUserDetailsService, ChangeUserRepository changeUserRepository, JWTUtil jwtUtil) {
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.changeUserRepository = changeUserRepository;
        this.jwtUtil = jwtUtil;
    }




    public BookstoreUser registerNewUser(RegistrationForm registrationForm)
    {
        BookstoreUser userByEmail = bookstoreUserRepository.findBookstoreUserByEmail(registrationForm.getEmail());
        BookstoreUser userByPhone = bookstoreUserRepository.findBookstoreUserByPhone(registrationForm.getPhone());

        if(userByEmail == null && userByPhone == null){
            BookstoreUser user = new BookstoreUser();
            user.setName(registrationForm.getName());
            user.setEmail(registrationForm.getEmail());
            user.setPhone(registrationForm.getPhone());
            user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            bookstoreUserRepository.save(user);
            return user;
        } else {
            return userByPhone;
        }
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload)
    {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public  ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        BookstoreUserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public ContactConfirmationResponse jwtLoginByPhoneNumber(ContactConfirmationPayload payload)
    {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setPhone(payload.getContact());
        registrationForm.setPass(payload.getCode());
        registerNewUser(registrationForm);
        UserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public BookstoreUser getCurrentUser() {
        BookstoreUserDetails userDetails = (BookstoreUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getBookstoreUser();
    }

    public boolean changePassForUser(String newPass)
    {
        BookstoreUser bookstoreUser  = bookstoreUserRepository.findBookstoreUserByEmail(getCurrentUser().getEmail());
        if (bookstoreUser != null){
            bookstoreUser.setPassword(passwordEncoder.encode(newPass));
            bookstoreUserRepository.save(bookstoreUser);
            return true;
        }
        return false;
    }

    public Double getBalance()
    {
        BookstoreUser bookstoreUser  = bookstoreUserRepository.findBookstoreUserByEmail(getCurrentUser().getEmail());
        if (bookstoreUser != null){
            return bookstoreUser.getBalance();
        } else {
            return 0.0;
        }
    }


    public boolean correctBalanceAfterPay(Double value)
    {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail(getCurrentUser().getEmail());
        if (bookstoreUser != null) {
            bookstoreUser.setBalance(bookstoreUser.getBalance() - value);
            bookstoreUserRepository.save(bookstoreUser);
            return true;
        } else {
            return false;
        }
    }

    public void saveChangedUser(ChangeUserEntity changeUser)
    {
        changeUserRepository.save(changeUser);
    }

    public boolean changeUser(String code)
    {
        ChangeUserEntity changeUser = changeUserRepository.findByCodeIs(code);
        if (changeUser != null && changeUser.getExpairedTime().isAfter(LocalDateTime.now())){
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


}
