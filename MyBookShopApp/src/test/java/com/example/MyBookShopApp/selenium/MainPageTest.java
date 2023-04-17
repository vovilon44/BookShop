package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MainPageTest {

    private static ChromeDriver driver;

    @BeforeAll
    static void setup()
    {
        System.setProperty("webdriver.chrome.driver","/home/vladimir/Documents/IdeaProjects/selenium/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown()
    {
        driver.quit();
    }

    @Test
    public void testMainPageAccess() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();
        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    public void testMainPageSearchByQuery() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .setUpSearchToken("lly")
                .pause()
                .submit()
                .pause();
        assertTrue(driver.getPageSource().contains("Gumball Rally, The"));
    }

    @Test
    public void testMainPageRecommended() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        assertTrue(driver.findElementsByXPath("/html/body/div/div/main/div[1]/div[1]/div[2]/div[1]/div/div/div").size() == 6);

        mainPage
                .clickButtonNextRecommended()
                .pause();

        assertTrue(driver.findElementsByXPath("/html/body/div/div/main/div[1]/div[1]/div[2]/div[1]/div/div/div").size() == 12);
    }

    @Test
    public void testMainPageNews() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        assertTrue(driver.findElementsByXPath("/html/body/div/div/main/div[1]/div[2]/div[2]/div[1]/div/div/div").size() == 6);

        mainPage
                .clickButtonNextNews()
                .pause();

        assertTrue(driver.findElementsByXPath("/html/body/div/div/main/div[1]/div[2]/div[2]/div[1]/div/div/div").size() == 12);
    }

    @Test
    public void testMainPagePopular() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        assertTrue(driver.findElementsByXPath("/html/body/div/div/main/div[1]/div[3]/div[2]/div[1]/div/div/div").size() == 6);

        mainPage
                .clickButtonNextPopular()
                .pause();

        assertTrue(driver.findElementsByXPath("/html/body/div/div/main/div[1]/div[3]/div[2]/div[1]/div/div/div").size() == 12);
    }

    @Test
    public void testMainPageTags() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickTag()
                .pause();

        assertTrue(driver.findElementByClassName("Section-title").getText().equals("LIFE"));
    }

    @Test
    public void testMainPageFirstBook() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickBook()
                .pause();
        assertTrue(driver.getCurrentUrl().contains("http://localhost:8085/books/book-"));
    }

    @Test
    public void testMainPageGenresMenu() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickMenuGenres()
                .pause();
        assertTrue(driver.getCurrentUrl().equals("http://localhost:8085/genres"));

        mainPage.clickGenreItem()
                .pause();
        assertTrue(driver.getCurrentUrl().equals("http://localhost:8085/genres/genre-trn-256"));
        assertTrue(driver.getPageSource().contains("Internet business"));
    }

    @Test
    public void testMainPageNewsMenu() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickMenuNews()
                .pause();
        assertTrue(driver.getCurrentUrl().equals("http://localhost:8085/recent"));

        mainPage.clickBook()
                .pause();
        assertTrue(driver.getCurrentUrl().contains("http://localhost:8085/books/book-"));

        driver.get("http://localhost:8085/recent");
        mainPage
                .pause()
                .submitFateFromInNews("07.02.2010")
                .pause()
                .clickBook()
                .pause();
        assertTrue(driver.getCurrentUrl().contains("http://localhost:8085/books/book-"));
    }

    @Test
    public void testMainPagePopularMenu() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickMenuPopular()
                .pause();
        assertTrue(driver.getCurrentUrl().equals("http://localhost:8085/popular"));

        mainPage.clickBook()
                .pause();
        assertTrue(driver.getCurrentUrl().contains("http://localhost:8085/books/book-"));

    }

    @Test
    public void testMainPageAuthorMenu() throws InterruptedException
    {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickMenuAuthor()
                .pause();
        assertTrue(driver.getCurrentUrl().equals("http://localhost:8085/authors"));

        mainPage.clickAuthor()
                .pause();
        assertTrue(driver.getCurrentUrl().contains("http://localhost:8085/authors/author-"));

    }


}