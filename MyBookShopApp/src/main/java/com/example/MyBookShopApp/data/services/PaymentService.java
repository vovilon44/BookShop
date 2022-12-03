package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class PaymentService
{

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;
    public String getPaymentUrl(Integer sum) throws NoSuchAlgorithmException {
//        Double paymentSumTotal = books.stream().mapToDouble(e->e.getPrice() - (e.getPrice() * e.getDiscount()) ).sum();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String invId = "5";
        md.update((merchantLogin+":"+sum.toString()+":"+ invId + ":" +firstTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + invId +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + sum.toString() +
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&IsTest=1";
    }
}
