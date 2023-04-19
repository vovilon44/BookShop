package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.security.BookstoreUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@Service
public class PaymentService
{

    Logger logger = Logger.getLogger(this.getClass().getName());

    private final DepositService depositService;

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;
    @Value("${robokassa.pass.second.test}")
    private String secondTestPass;

    @Autowired
    public PaymentService(DepositService depositService) {
        this.depositService = depositService;
    }

    public String getPaymentUrl(Double sum){
        String changeSum = sum.toString();
        if ((sum * 100) % 100 == 0){
            changeSum = changeSum.substring(0, changeSum.length() - 2);
        } else if ((sum * 10) % 10 == 0){
            changeSum = changeSum.substring(0, changeSum.length() - 1);
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            int invId = (int) (Math.random() * 999998 + 1);
            md.update((merchantLogin+":"+sum+":"+ invId + ":" +firstTestPass).getBytes());
            String signature = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
            md.update((changeSum +":"+ invId + ":" +secondTestPass).getBytes());
            String signatureSecond = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
            depositService.addTransaction(signature, signatureSecond, sum, invId);
            return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                    "?MerchantLogin=" + merchantLogin +
                    "&InvId=" + invId +
                    "&Culture=ru" +
                    "&Encoding=utf-8" +
                    "&OutSum=" + sum +
                    "&SignatureValue=" + signature +
                    "&IsTest=1";
        } catch (NoSuchAlgorithmException e){
            logger.warning(e.getMessage());
            return null;
        }

    }

    public String botGetPaymentUrl(Double sum, long chatId, BookstoreUser user){
        String changeSum = sum.toString();
        if ((sum * 100) % 100 == 0){
            changeSum = changeSum.substring(0, changeSum.length() - 2);
        } else if ((sum * 10) % 10 == 0){
            changeSum = changeSum.substring(0, changeSum.length() - 1);
        }
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            int invId = (int) (Math.random() * 999998 + 1);
            md.update((merchantLogin + ":" + sum + ":" + invId + ":" + firstTestPass +":shp_bot_chat=" + chatId).getBytes());
            String signature = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
            md.update((changeSum + ":" + invId + ":" + secondTestPass + ":" + chatId).getBytes());
            String signatureSecond = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
            depositService.addTransaction(signature, signatureSecond, sum, user, invId);
            return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                    "?MerchantLogin=" + merchantLogin +
                    "&InvId=" + invId +
                    "&Culture=ru" +
                    "&Encoding=utf-8" +
                    "&OutSum=" + sum +
                    "&SignatureValue=" + signature +
                    "&IsTest=1" +
                    "&shp_bot_chat=" + chatId;
        } catch (NoSuchAlgorithmException e){
            logger.warning(e.getMessage());
            return null;
        }

    }
}
