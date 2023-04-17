package com.example.MyBookShopApp.data.telegram;

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class BotBooksResponse {
    private List<BotBook> data = new ArrayList<>();
    private Integer page;

    private Integer totalBooks;

    @Value("${bot.books.limit}")
    private Integer botBookLimit;
    private String message;

    private Boolean result;

    public BotBooksResponse() {
    }

    public BotBooksResponse(String message) {
        this();
        this.message = message;
    }

    public BotBooksResponse(boolean result, String message) {
        this();
        this.message = message;
        this.result = result;
    }

    public BotBooksResponse(BotBook book) {
        this();
        this.data.add(book);
    }

    @Override
    public String toString() {
        return message;
    }

    public BotBooksResponse(List<BotBook> data, Integer totalBooks, BotUser botUser, Integer limit) {
        this();
        this.data = data;
        this.page = botUser.getPageNumber();
        this.totalBooks = totalBooks;
        switch (botUser.getStatus()){
            case SEARCH_ALL:
            case SEARCH_TAG:
            case SEARCH_GENRE:
            case SEARCH_TITLE:
            case SEARCH_AUTHOR:
                if (totalBooks > 0){
                    this.message = "По вашему запросу '" + botUser.getWordForSearch() + "' \nнайдено книг: " + totalBooks;
                } else {
                    this.message = "По вашему запросу '" + botUser.getWordForSearch() + "' \nне найдено книг ";
                    return;
                }
                break;
            case KEPT:
                if (totalBooks > 0) {
                    this.message = "Книг отложено: " + totalBooks;
                } else {
                    this.message = "Отложенных книг нет";
                    return;
                }
                break;
            case CART:
                if (totalBooks > 0) {
                    this.message = "Книг в корзине: " + totalBooks;
                } else {
                    this.message = "Корзина пуста";
                    return;
                }
                break;
            case MY:
                if (totalBooks > 0) {
                    this.message = "Моих книг: " + totalBooks;
                } else {
                    this.message = "Моих книг нет";
                    return;
                }
            case ARCHIVE:
                if (totalBooks > 0) {
                    this.message = "Книг в архиве: " + totalBooks;
                } else {
                    this.message = "Архив пуст";
                    return;
                }
                break;
        }
        if (data.size() > 0){
            this.message +=  "\nТекущая страница: " + (botUser.getPageNumber() + 1) + " из " + (int)((totalBooks - 0.0001) / limit + 1);
        } else if (botUser.getPageNumber() > 0 ){
            this.message +=  "\nСтраниц всего: " +((int) Math.ceil(totalBooks / limit)) +" , а не: " + (botUser.getPageNumber() + 1);
        } else {
            this.message += "\nДля навигации отправьте номер нужной страницы или используйте команды 'вперед' и 'назад' либо воспользуйтесь соответствующими кнопками на клавиатуре";
        }




    }

    public Integer getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(Integer totalBooks) {
        this.totalBooks = totalBooks;
    }

    public Integer getBotBookLimit() {
        return botBookLimit;
    }

    public void setBotBookLimit(Integer botBookLimit) {
        this.botBookLimit = botBookLimit;
    }

    public List<BotBook> getData() {
        return data;
    }

    public void setData(List<BotBook> data) {
        this.data = data;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
    public String getMessage() {
        return message;
    }



    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
