package com.example.MyBookShopApp.security;

public class PhoneNumberUserDetails extends BookstoreUserDetails
{
    public PhoneNumberUserDetails(BookstoreUser bookstoreUser) {
        super(bookstoreUser);
    }

    @Override
    public String getUsername() {
        return getBookstoreUser().getPhone();
    }
}
