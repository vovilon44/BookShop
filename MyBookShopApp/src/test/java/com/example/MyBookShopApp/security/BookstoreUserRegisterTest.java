package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTUtil;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookstoreUserRegisterTest {

    private final BookstoreUserRegister userRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;
    private ContactConfirmationPayload payloadEmail;
    private ContactConfirmationPayload payloadPhone;
    private BookstoreUser bookstoreUser;
    private final JWTUtil jwtUtil;

    private final BookstoreUserRepository bookstoreUserRepository;


    @MockBean
    private BookstoreUserRepository bookstoreUserRepositoryMock;

    @MockBean
    private BookstoreUserDetailsService bookstoreUserDetailsServiceMock;

    @Autowired
    public BookstoreUserRegisterTest(BookstoreUserRegister userRegister, PasswordEncoder passwordEncoder, JWTUtil jwtUtil, BookstoreUserRepository bookstoreUserRepository) {
        this.userRegister = userRegister;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.bookstoreUserRepository = bookstoreUserRepository;
        bookstoreUser = new BookstoreUser();
        bookstoreUser.setEmail("lcodner1@email.ru");
        bookstoreUser.setPassword("$2a$10$aB8/id9W6v4BlI0GN/P0Keo3Nh/S67EJ.hVlb4eCgu2yiA0cyYtGy");
        bookstoreUser.setName("Ericka");
        bookstoreUser.setPhone("+7 (999) 999-99-99");
        payloadEmail = new ContactConfirmationPayload();
        payloadPhone = new ContactConfirmationPayload();




    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@email.org");
        registrationForm.setName("Tester");
        registrationForm.setPass("332238");
        registrationForm.setPhone("9031222323");

        payloadEmail.setContact("lcodner1@email.ru");
        payloadEmail.setCode("332233");

        payloadPhone.setContact("+7 (999) 999-99-99");
        payloadPhone.setCode("332233");

    }

    @AfterEach
    void tearDown()
    {
        registrationForm = null;
    }


//    @Test
//    void registerNewUser() {
//        BookstoreUser user = userRegister.registerNewUser(registrationForm);
//        assertNotNull(user);
//        assertTrue(passwordEncoder.matches(registrationForm.getPass(), user.getPassword()));
//        assertTrue(CoreMatchers.is(user.getPhone()).matches(registrationForm.getPhone()));
//        assertTrue(CoreMatchers.is(user.getName()).matches(registrationForm.getName()));
//        assertTrue(CoreMatchers.is(user.getEmail()).matches(registrationForm.getEmail()));
//
//        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1)).
//                save(Mockito.any(BookstoreUser.class));
//    }

//    @Test
//    void registerNewUserFail(){
//        Mockito.doReturn(new BookstoreUser())
//                .when(bookstoreUserRepositoryMock)
//                .findBookstoreUserByEmail(registrationForm.getEmail());
//        BookstoreUser user = userRegister.registerNewUser(registrationForm);
//        assertNull(user);
//    }

//    @Test
//    void validLoginWithEmail(){
//        Mockito.doReturn(new BookstoreUserDetails(bookstoreUser))
//                .when(bookstoreUserDetailsServiceMock)
//                .loadUserByUsername(payloadEmail.getContact());
//        ContactConfirmationResponse response = userRegister.jwtLogin(payloadEmail);
//        assertTrue(jwtUtil.validateToken(response.getResult(), new BookstoreUserDetails(bookstoreUser)));
//    }

//    @Test
//    void loginWithEmailFailedPass(){
//        payloadEmail.setCode("332244");
//        Mockito.doReturn(new BookstoreUserDetails(bookstoreUser))
//                .when(bookstoreUserDetailsServiceMock)
//                .loadUserByUsername(payloadEmail.getContact());
//        try {
//            Object response = userRegister.jwtLogin(payloadEmail);
//            assertTrue(false);
//        } catch (BadCredentialsException e){
//            assertTrue(e.getLocalizedMessage().equals("Bad credentials"));
//        }
//    }

//    @Test
//    void validLoginWithPhone(){
//        Mockito.doReturn(new BookstoreUserDetails(bookstoreUser))
//                .when(bookstoreUserDetailsServiceMock)
//                .loadUserByUsername(payloadPhone.getContact());
//        ContactConfirmationResponse response = userRegister.jwtLogin(payloadPhone);
//        assertTrue(jwtUtil.validateToken(response.getResult(), new BookstoreUserDetails(bookstoreUser)));
//    }


    @Test
    void loginWithPhoneFailedPass(){
        payloadPhone.setCode("332244");
        Mockito.doReturn(new BookstoreUserDetails(bookstoreUser))
                .when(bookstoreUserDetailsServiceMock)
                .loadUserByUsername(payloadPhone.getContact());
        try {
            Object response = userRegister.jwtLogin(payloadPhone);
            assertTrue(false);
        } catch (BadCredentialsException e){
            assertTrue(e.getLocalizedMessage().equals("Bad credentials"));
        }
    }


}