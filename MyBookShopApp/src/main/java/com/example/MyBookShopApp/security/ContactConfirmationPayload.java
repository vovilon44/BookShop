package com.example.MyBookShopApp.security;

public class ContactConfirmationPayload
{
    private String contact;
    private String code;
    private Boolean login;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "ContactConfirmationPayload{" +
                "contact='" + contact + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
