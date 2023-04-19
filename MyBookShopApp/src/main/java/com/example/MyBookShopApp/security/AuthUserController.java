package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.CodeSenderService;
import com.example.MyBookShopApp.data.services.PaymentService;
import com.example.MyBookShopApp.data.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthUserController
{
    private  final  BookstoreUserRegister userRegister;
    private final CodeSenderService codeSenderService;
    private final SessionService sessionService;
    private final Book2UserService book2UserService;
    private final PaymentService paymentService;

    @Value("${sentCode.length}")
    private Integer lengthOfSentCode;

    @Value("${transactions.limit.size}")
    private Integer limit;
    @Value("${transactions.offset.size}")
    private Integer offset;


    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister, CodeSenderService codeSenderService, SessionService sessionService, Book2UserService book2UserService, PaymentService paymentService) {
        this.userRegister = userRegister;
        this.codeSenderService = codeSenderService;
        this.sessionService = sessionService;
        this.book2UserService = book2UserService;
        this.paymentService = paymentService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @GetMapping("/signin")
    public String handleSignIn(Model model, HttpServletRequest request){
        if (request.getHeader("Referer") != null && !request.getHeader("Referer").contains("/signin") && !request.getHeader("Referer").contains("/signup")) {
            request.getSession().setAttribute("urlBeforeAutentification", request.getHeader("Referer"));
        }
        model.addAttribute("lengthCode", lengthOfSentCode);
        model.addAttribute("maskForCode", codeSenderService.getMaskForCode());
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model)
    {
        model.addAttribute("regForm", new RegistrationForm());
        model.addAttribute("lengthCode", lengthOfSentCode);
        model.addAttribute("maskForCode", codeSenderService.getMaskForCode());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload, Model model) throws MailException {

            return codeSenderService.sendSecretCodeSms(payload.getContact(), model.getAttribute("currentLocale").toString(), payload.isLogin());
    }

//    @PostMapping("/requestEmailConfirmation")
//    @ResponseBody
//    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload)
//    {
//        ContactConfirmationResponse response = new ContactConfirmationResponse();
//
//        response.setResult("true");
//        return response;
//    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload, Model model){
        return codeSenderService.verifyCode(payload.getContact(), payload.getCode().replaceAll(" ", ""), model.getAttribute("currentLocale").toString(), false);
    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "keptContents", required = false) String keptContents,
                                         @CookieValue(name = "historyVisit", required = false) String historyVisit)
    {
        if (userRegister.registerNewUser(registrationForm, cartContents, keptContents, historyVisit) != null){
            model.addAttribute("regOk", true);
        } else {
            model.addAttribute("regOk", false);
        }

        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload, HttpServletResponse httpServletResponse, Model model, HttpServletRequest request) {
        ContactConfirmationResponse responseLogin = codeSenderService.verifyCode(payload.getContact(), payload.getCode().replaceAll(" ", ""), model.getAttribute("currentLocale").toString(), true);
        if (responseLogin != null && responseLogin.getResult() != null && responseLogin.getResult()) {
            String token = userRegister.jwtLogin(payload);
            Cookie cookie = new Cookie("token", token);
            httpServletResponse.addCookie(cookie);
            responseLogin.setMessage(request.getSession().getAttribute("urlBeforeAutentification") == null ? "/books/my" : request.getSession().getAttribute("urlBeforeAutentification").toString());
//            sessionService.newSession(userRegister.getCurrentUser(), token);

        }
            return responseLogin;

    }

    @GetMapping("/profile/pay/failed")
    public String handleProfileFalse()
    {

        return "redirect:/profile?result=false";
    }
    @GetMapping("/profile/pay/success")
    public String handleProfileTrue()
    {
        return "redirect:/profile?result=true";
    }



    @GetMapping("/profile")
    public String handleProfile(Model model, @RequestParam(value = "result", required = false) String result, @CookieValue(value = "token", required = false) String token)
    {
        if (result != null && result.equals("false")){
            model.addAttribute("paymentSumTotal", book2UserService.getSummInCart());
        }
        model.addAttribute("transactions", book2UserService.getTransactions(offset, limit, model.getAttribute("currentLocale").toString()));
        model.addAttribute("curUsr", userRegister.getCurrentUser());
        model.addAttribute("sessions", sessionService.getSessions(userRegister.getCurrentUser()));
        model.addAttribute("token", token);

        return "profile";
    }

    @PostMapping("/profile/addMoney")
    public RedirectView handlePay(AddMoneyDto addMoneyDto) {
        String paymentUrl = paymentService.getPaymentUrl(addMoneyDto.getSum());
        if (paymentUrl != null) {
            return new RedirectView(paymentUrl);
        } else {
            return null;
        }

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
                    model.addAttribute("transactions", book2UserService.getTransactions(offset, limit, model.getAttribute("currentLocale").toString()));
                    model.addAttribute("curUsr", userRegister.getCurrentUser());
                    model.addAttribute("passwordChanged", true);
                    return "/profile";
                }
                model.addAttribute("transactions", book2UserService.getTransactions(offset, limit, model.getAttribute("currentLocale").toString()));
                model.addAttribute("curUsr", userRegister.getCurrentUser());
                model.addAttribute("passwordChangedFailed", true);
                return "/profile";
            } else {
                codeSenderService.sendEmailForEditUser(registrationForm, token);
                model.addAttribute("transactions", book2UserService.getTransactions(offset, limit, model.getAttribute("currentLocale").toString()));
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
            model.addAttribute("transactions", book2UserService.getTransactions(offset, limit, model.getAttribute("currentLocale").toString()));
            model.addAttribute("curUsr", userRegister.getCurrentUser());
            model.addAttribute("userEditFailed", true);
            return "/profile";
        } else {
            return "redirect:/signin";
        }
    }

}
