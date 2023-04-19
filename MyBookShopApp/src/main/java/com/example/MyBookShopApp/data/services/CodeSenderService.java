package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.ChangeUserEntity;
import com.example.MyBookShopApp.data.CodesFromUsersEntity;
import com.example.MyBookShopApp.data.repositories.SmsCodeRepository;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.ContactConfirmationResponse;
import com.example.MyBookShopApp.security.RegistrationFormEditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class CodeSenderService
{
    @Value("${sms.ru.api.id}")
    private String apiId;
    @Value("${appEmail.email}")
    private String mailSender;
    @Value("${login.maxCount}")
    private Integer loginMaxCount;

    @Value("${sentCode.length}")
    private Integer lengthOfSentCode;

    private final String URL_SMS_SERVICE;
    private final JavaMailSender javaMailSender;
    private final BookstoreUserRegister userRegister;
    private final SmsCodeRepository smsCodeRepository;

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public CodeSenderService(JavaMailSender javaMailSender, BookstoreUserRegister userRegister, SmsCodeRepository smsCodeRepository) {
        this.javaMailSender = javaMailSender;
        this.userRegister = userRegister;
        URL_SMS_SERVICE = "https://sms.ru/sms/send" + apiId;
        this.smsCodeRepository = smsCodeRepository;

    }

    public ContactConfirmationResponse sendSecretCodeSms(String contact, String locale, Boolean login)
    {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        String generatedCode = saveNewCode(new CodesFromUsersEntity(contact, generatedCode().replace(" ", ""), 60 * 5, 0));
        if (login && !userRegister.checkUserByContact(contact)){
            response.setResult(false);
            response.setError(locale.equals("ru") ? "Пользователь не найден" : "User is not found");
        } else if (!login && userRegister.checkUserByContact(contact)){
            response.setResult(false);
            response.setError(locale.equals("ru") ? "Пользователь с такими данными уже зарегистрирован" : "User with such data is already registered");
        } else if (generatedCode.equals("false")){
            response.setResult(false);
            response.setError(locale.equals("ru") ? "Данный контакт приостановлен, попробуйте повторить через 5 минут" : "This contact has been suspended, please try again in 5 minutes");
        } else if (contact.contains("@")){
            response.setResult(true);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailSender);
            message.setTo(contact);
            message.setSubject("Bookstore email verification");
            message.setText("Verification code is: " + generatedCode);
            try {
                javaMailSender.send(message);
            } catch (MailException e) {
                response.setResult(false);
                logger.warning(e.getLocalizedMessage());
                response.setError(locale.equals("ru") ? "Введен некорректный Email" : "Incorrect email entered");
            }
        } else {
            response.setResult(true);
            String formattedContact = contact.replaceAll("[( )-]", "");
//            createRequestForGetSmsCode(formattedContact, generatedCode);
            response.setMessage("На ваш телефон отправлено сообщение с кодом для входа. Введите его");
        }
        return response;
    }



    public void sendEmailForEditUser(RegistrationFormEditDto registrationForm, String token)
    {
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
        message.setFrom(mailSender);
        message.setTo(changeUser.getEmail());

        message.setSubject("Bookstore email verification");
        message.setText("For change account direct: " + urlForMessage);
        javaMailSender.send(message);
    }

    public String generatedCode()
    {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < lengthOfSentCode){
            sb.append(random.nextInt(9));
        }
        sb.insert(lengthOfSentCode / 2, " ");
        return sb.toString();
    }

    public String getMaskForCode()
    {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < lengthOfSentCode){
            sb.append(9 + "");
        }
        sb.insert(lengthOfSentCode / 2, " ");
        return sb.toString();
    }

    private String saveNewCode(CodesFromUsersEntity codesFromUsersEntity) {
        CodesFromUsersEntity code = smsCodeRepository.findByContact(codesFromUsersEntity.getContact());
        if (code == null) {
            smsCodeRepository.save(codesFromUsersEntity);
            return codesFromUsersEntity.getCode();
        } else if (code.isExpired()) {
            code.setCode(codesFromUsersEntity.getCode());
            code.setExpireTime(LocalDateTime.now().plusSeconds(60 * 5));
            code.setTryCount(0);
            smsCodeRepository.save(code);
            return codesFromUsersEntity.getCode();
        } else if (code.getTryCount() >= 5){
            return "false";
        } else {
        return code.getCode();
    }

}

    public ContactConfirmationResponse verifyCode(String contact, String code, String locale, Boolean login)
    {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        CodesFromUsersEntity codesFromUsersEntity = smsCodeRepository.findByContact(contact);
        if (codesFromUsersEntity != null){
            if (codesFromUsersEntity.getCode().equals(code) && !codesFromUsersEntity.isExpired() && codesFromUsersEntity.getTryCount() < loginMaxCount) {
                response.setResult(true);
                response.setMessage(locale.equals("ru") ? "Код успешно подтвержден" : "Code successfully verified");
            } else {
                codesFromUsersEntity.setTryCount(codesFromUsersEntity.getTryCount() + 1);
                smsCodeRepository.save(codesFromUsersEntity);
                if (codesFromUsersEntity.getTryCount() >= loginMaxCount) {
                        if (contact.contains("@")){
                            response.setError(locale.equals("ru") ?
                                    "Количество попыток входа по e-mail исчерпано, попробуйте войти по телефону или повторить вход по e-mail через 5 минут" :
                                    "The number of attempts to login by e-mail has been exhausted, try logging in by phone or try logging in again by e-mail in 5 minutes");
                        } else {
                            response.setError(locale.equals("ru") ?
                                    "Количество попыток входа по телефону исчерпано, попробуйте войти по e-mail или повторить вход по телефону через 5 минут" :
                                    "The number of attempts to log in by phone has been exhausted, please try to log in by e-mail or try to log in by phone again in 5 minutes");
                        }
                } else if (codesFromUsersEntity.getCode().equals(code) && codesFromUsersEntity.isExpired()) {
                    response.setResult(false);
                    response.setError(locale.equals("ru") ?
                            "У кода истек срок действия" :
                            "Code is expired");
                } else {
                    response.setResult(false);
                    response.setError(locale.equals("ru") ?
                            "Код подтверждения введён неверно. У вас осталось " + (loginMaxCount - codesFromUsersEntity.getTryCount()) + " попыток" :
                            "The confirmation code was entered incorrectly. You have " + (loginMaxCount- codesFromUsersEntity.getTryCount()) + " tries left");
                }
            }
        } else {
            response.setResult(false);
            response.setError(locale.equals("ru") ?
                    "Произошла непредвиденная ошибка, попробуйте повторить авторизацию еще раз" :
                    "An unexpected error occurred, please try again");
            return null;
        }
        return response;
    }

    private void createRequestForGetSmsCode(String phoneNumber, String generatedCode)
    {
        generatedCode = generatedCode.replaceAll(" ", "+");
        String url = URL_SMS_SERVICE + "?api_id=" + apiId + "&to=" + phoneNumber + "&msg=Send+your+password:+" + generatedCode + "&json=1";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    }
}
