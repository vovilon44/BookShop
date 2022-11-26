package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPage
{

    private String url = "http://localhost:8085/";
    private ChromeDriver driver;

    public MainPage(ChromeDriver driver)
    {
        this.driver = driver;
    }

    public MainPage callPage()
    {
        driver.get(url);
        return this;
    }


    public MainPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return  this;
    }

    public MainPage setUpSearchToken(String token)
    {
        WebElement element = driver.findElement(By.id("query"));
        element.sendKeys(token);
        return this;
    }

    public MainPage submit()
    {
        WebElement element = driver.findElement(By.id("search"));
        element.submit();
        return this;
    }

    public MainPage clickButtonNextRecommended()
    {
        WebElement element = driver.findElement(By.xpath("/html/body/div/div/main/div[1]/div[1]/div[2]/div[2]/div/button[2]"));
        element.click();
        return this;
    }

    public MainPage clickButtonNextNews()
    {
        WebElement element = driver.findElement(By.xpath("/html/body/div/div/main/div[1]/div[2]/div[2]/div[2]/div/button[2]"));
        element.click();
        return this;
    }

    public MainPage clickButtonNextPopular()
    {
        WebElement element = driver.findElement(By.xpath("/html/body/div/div/main/div[1]/div[3]/div[2]/div[2]/div/button[2]"));
        element.click();
        return this;
    }

    public MainPage clickTag()
    {
        WebElement element = driver.findElement(By.xpath("//*[@id=\"tags\"]/div[23]/a"));
        element.click();
        return this;
    }

    public MainPage clickBook()
    {
        WebElement element = driver.findElement(By.xpath("//strong[@class='Card-title']/a"));
        element.click();
        return this;
    }

    public MainPage clickAuthor()
    {
        WebElement element = driver.findElement(By.xpath("//div[@class='Authors-item']/a"));
        element.click();
        return this;
    }

    public MainPage clickMenuGenres()
    {
        WebElement element = driver.findElement(By.partialLinkText("Genres"));
        element.click();
        return this;
    }

    public MainPage clickMenuNews()
    {
        WebElement element = driver.findElement(By.partialLinkText("News"));
        element.click();
        return this;
    }

    public MainPage clickMenuPopular()
    {
        WebElement element = driver.findElement(By.partialLinkText("Popular"));
        element.click();
        return this;
    }

    public MainPage clickMenuAuthor()
    {
        WebElement element = driver.findElement(By.partialLinkText("Author"));
        element.click();
        return this;
    }


    public MainPage clickGenreItem()
    {
        WebElement element = driver.findElement(By.partialLinkText("Internet business"));
        element.click();
        return this;
    }

    public MainPage submitFateFromInNews(String date)
    {
        WebElement element = driver.findElement(By.id("fromdaterecent"));
        element.clear();
        element.sendKeys(date);
        return this;
    }
}
