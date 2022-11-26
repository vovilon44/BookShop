package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class AuthUserControllerTest {


    private final String regData;
    private final MockMvc mockMvc;
    private final BookstoreUserRepository bookstoreUserRepository;
    private ContactConfirmationPayload emailPayload;
    private ContactConfirmationPayload phonePayload;
    private final BookstoreUserRegister bookstoreUserRegister;
    private final ObjectMapper objectMapper;

    @Autowired
    AuthUserControllerTest(MockMvc mockMvc, BookstoreUserRepository bookstoreUserRepository, BookstoreUserRegister bookstoreUserRegister, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.bookstoreUserRegister = bookstoreUserRegister;
        this.objectMapper = objectMapper;
        phonePayload = new ContactConfirmationPayload();
        emailPayload = new ContactConfirmationPayload();
        regData = "name=NameTestUser&phone=%2B7+%28234%29+532-23-43&phoneCode=333+333&email=exampleEmail%40test.ru&mailCode=333+333&pass=332233";
    }

    @BeforeEach
    void setUp()
    {

        emailPayload.setContact("lcodner1@email.ru");
        emailPayload.setCode("332233");

        phonePayload.setContact("+7 (777) 777-77-77");
        phonePayload.setCode("332233");
    }

    @AfterEach
    void tearDown()
    {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail("exampleEmail@test.ru");
        if (bookstoreUser != null){
            bookstoreUserRepository.delete(bookstoreUser);
        }
    }

    @Test
    void handleUserRegistration() throws Exception {
        mockMvc.perform(post("/reg").content(regData)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label/span").string(containsString("Регистрация прошла успешно")))
                .andExpect(status().isOk());

        assertTrue(bookstoreUserRepository.findBookstoreUserByEmail("exampleEmail@test.ru") != null);
    }

    @Test
    void LoginByEmail() throws Exception {
        mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(emailPayload))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(cookie().exists("token"))
                .andExpect(status().isOk());
    }

    @Test
    void LoginByEmailFailInvalidPass() throws Exception {
        emailPayload.setCode("332244");
        mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(emailPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(cookie().doesNotExist("token"))
                .andExpect(status().is(302));
    }

    @Test
    void LoginByEmailFailInvalidEmail() throws Exception {
        emailPayload.setContact("incorrect@email.ru");
        mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(emailPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(cookie().doesNotExist("token"))
                .andExpect(status().is(302));
    }
    @Test
    void LoginByPhone() throws Exception {
        mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(phonePayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(cookie().exists("token"))
                .andExpect(status().isOk());
    }

    @Test
    void LoginByPhoneFailInvalidPass() throws Exception {
        phonePayload.setCode("332244");
        mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(phonePayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(cookie().doesNotExist("token"))
                .andExpect(status().is(302));
    }

    @Test
    void LoginByPhoneFailInvalidEmail() throws Exception {
        phonePayload.setContact("+7 (888) 888-88-88");
        mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(phonePayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(cookie().doesNotExist("token"))
                .andExpect(status().is(302));
    }

    @Test
    void Logout() throws Exception {

        mockMvc.perform(get("/logout").cookie(new Cookie("token", bookstoreUserRegister.jwtLogin(emailPayload).getResult())))
                .andDo(print())
                .andExpect(cookie().value("token", (String) null))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }
}