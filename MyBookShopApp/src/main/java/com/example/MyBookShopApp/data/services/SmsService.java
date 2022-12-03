package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.SmsCode;
import com.example.MyBookShopApp.data.repositories.SmsCodeRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.logging.Logger;

@Service
public class SmsService
{
    @Value("${sms.ru.api.id}")
    private String apiId;

    private final String URL_SMS_SERVICE;

    private final SmsCodeRepository smsCodeRepository;

    private Logger logger = Logger.getLogger(this.getClass().getName());


    @Autowired
    public SmsService(SmsCodeRepository smsCodeRepository) {
        URL_SMS_SERVICE = "https://sms.ru/sms/send" + apiId;
        this.smsCodeRepository = smsCodeRepository;

    }

    public String sendSecretCodeSms(String contact)
    {
        String formattedContact = contact.replaceAll("[( )-]", "");
        String generatedCode = generatedCode();
        createRequestForGetSmsCode(formattedContact, generatedCode);
        return generatedCode;
    }

    public String generatedCode()
    {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6){
            sb.append(random.nextInt(9));
        }
        sb.insert(3, " ");
        return sb.toString();
    }

    public void saveNewCode(SmsCode smsCode)
    {
        if (smsCodeRepository.findByCode(smsCode.getCode()) == null)
        {
            smsCodeRepository.save(smsCode);

        }
    }

    public Boolean verifyCode(String code)
    {
        SmsCode smsCode = smsCodeRepository.findByCode(code);
        return (smsCode != null && !smsCode.isExpired());
    }

    private void createRequestForGetSmsCode(String phoneNumber, String generatedCode)
    {
        generatedCode = generatedCode.replaceAll(" ", "+");
        String url = URL_SMS_SERVICE + "?api_id=" + apiId + "&to=" + phoneNumber + "&msg=Send+your+password:+" + generatedCode + "&json=1";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        logger.info("response: " + response);
    }
}
