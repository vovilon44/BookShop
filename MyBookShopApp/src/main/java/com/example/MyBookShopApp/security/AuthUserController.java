package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.ChangeUserEntity;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.SmsCode;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.PaymentService;
import com.example.MyBookShopApp.data.services.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Random;

@Controller
public class AuthUserController
{
    private  final  BookstoreUserRegister userRegister;

    private final JavaMailSender javaMailSender;
    private final SmsService smsService;
    private final BookService bookService;

    private final Book2UserService book2UserService;
    private final PaymentService paymentService;


    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister, JavaMailSender javaMailSender, SmsService smsService, BookService bookService, Book2UserService book2UserService, PaymentService paymentService) {
        this.userRegister = userRegister;
        this.javaMailSender = javaMailSender;
        this.smsService = smsService;
        this.bookService = bookService;
        this.book2UserService = book2UserService;
        this.paymentService = paymentService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @GetMapping("/signin")
    public String handleSignIn(){
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model)
    {
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload)
    {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");

        if (payload.getContact().contains("@")){
            return response;
        } else {
            String smsCodeString = smsService.sendSecretCodeSms(payload.getContact());
            smsService.saveNewCode(new SmsCode(smsCodeString, 60 * 5));
            return response;
        }
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload)
    {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("belialpw2@bk.ru");
        message.setTo(payload.getContact());
        SmsCode smsCode =new SmsCode(smsService.generatedCode(), 300);
        smsService.saveNewCode(smsCode);
        message.setSubject("Bookstore email verification");
        message.setText("Verification code is: " + smsCode.getCode());
        javaMailSender.send(message);
        response.setResult("true");
        return response;

    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload){
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        if (smsService.verifyCode(payload.getCode())){
            response.setResult("true");
        }
        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model)
    {
        if (userRegister.registerNewUser(registrationForm) != null){
            model.addAttribute("regOk", true);
        } else {
            model.addAttribute("regOk", false);
        }

        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload, HttpServletResponse httpServletResponse) {
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ContactConfirmationResponse handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload, HttpServletResponse httpServletResponse) {
        if (smsService.verifyCode(payload.getCode())) {
            ContactConfirmationResponse loginResponse = userRegister.jwtLoginByPhoneNumber(payload);
            Cookie cookie = new Cookie("token", loginResponse.getResult());
            httpServletResponse.addCookie(cookie);
            return loginResponse;
        } else {
            return null;
        }
    }

    @GetMapping("books/my")
    public String handleMy(){
        return "my";
    }

    @GetMapping("/profile")
    public String handleProfile(Model model)
    {
        model.addAttribute("transactions", book2UserService.getTransactions());
        model.addAttribute("curUsr", userRegister.getCurrentUser());
        return "profile";
    }

    @PostMapping("/profile/addMoney")
    public RedirectView handlePay(AddMoneyDto addMoneyDto) throws NoSuchAlgorithmException {
        String paymentUrl = paymentService.getPaymentUrl(addMoneyDto.getSum());
        return new RedirectView(paymentUrl);
    }

//    @GetMapping("/logout")
//    public String handleLogut(HttpServletRequest request){
//        HttpSession session = request.getSession();
//        SecurityContextHolder.clearContext();
//        if(session != null){
//            session.invalidate();
//        }
//        for (Cookie cookie : request.getCookies()){
//            cookie.setMaxAge(0);
//        }
//        return "redirect:/";
//    }

    @PostMapping("/profile/edit")
    public String handleUserEdit(RegistrationFormEditDto registrationForm, Model model, @CookieValue(name = "token", required = false) String token)
    {
        if (token != null) {
            if (registrationForm.getPassword().equals(registrationForm.getPasswordReply()) &&
                    registrationForm.getMail().equals(userRegister.getCurrentUser().getEmail()) &&
                    registrationForm.getName().equals(userRegister.getCurrentUser().getName()) &&
                    registrationForm.getPhone().equals(userRegister.getCurrentUser().getPhone())) {
                if (userRegister.changePassForUser(registrationForm.getPassword())) {
                    model.addAttribute("transactions", book2UserService.getTransactions());
                    model.addAttribute("curUsr", userRegister.getCurrentUser());
                    model.addAttribute("passwordChanged", true);
                    return "/profile";
                }
                model.addAttribute("transactions", book2UserService.getTransactions());
                model.addAttribute("curUsr", userRegister.getCurrentUser());
                model.addAttribute("passwordChangedFailed", true);
                return "/profile";
            } else {
                Random random = new Random();
                StringBuilder sb = new StringBuilder();
                while (sb.length() < 3){
                    sb.append(random.nextInt(9));
                }
                String urlForMessage = "http://localhost:8085/profile/" + sb + token;

                ChangeUserEntity changeUser =new ChangeUserEntity();
                changeUser.setCode(sb.toString());
                changeUser.setEmail(registrationForm.getMail() != null ? registrationForm.getMail() : userRegister.getCurrentUser().getEmail());
                changeUser.setName(registrationForm.getName() != null ? registrationForm.getName() : userRegister.getCurrentUser().getName());
                changeUser.setPhone(registrationForm.getPhone() != null ? registrationForm.getPhone() : userRegister.getCurrentUser().getPhone());
                changeUser.setExpairedTime(LocalDateTime.now().plusSeconds(300));
                userRegister.saveChangedUser(changeUser);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("belialpw2@bk.ru");
                message.setTo(changeUser.getEmail());

                message.setSubject("Bookstore email verification");
                message.setText("For change account direct: " + urlForMessage);
                javaMailSender.send(message);
                model.addAttribute("transactions", book2UserService.getTransactions());
                model.addAttribute("curUsr", userRegister.getCurrentUser());
                model.addAttribute("userWait", true);
                return "/profile";
            }
        }
        return "redirect:/profile";
    }

    @GetMapping("/profile/{tokenApprove}")
    public String handleUserEditConfirm(@PathVariable String tokenApprove, @CookieValue(name = "token", required = false) String token, Model model)
    {
        if (token != null) {
            if (tokenApprove.substring(3).equals(token)) {
                if (userRegister.changeUser(tokenApprove.substring(0, 3))) {
                    model.addAttribute("userEdit", true);
                    return "/signin";
                }
            }
            model.addAttribute("transactions", book2UserService.getTransactions());
            model.addAttribute("curUsr", userRegister.getCurrentUser());
            model.addAttribute("userEditFailed", true);
            return "/profile";
        } else {
            return "redirect:/signin";
        }
    }

}
