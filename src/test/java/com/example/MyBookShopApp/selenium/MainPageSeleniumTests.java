package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MainPageSeleniumTests {
    private static ChromeDriver driver;

    @BeforeAll
    static void setup(){
        System.setProperty("webdriver.chrome.driver", "/Users/Admin/Desktop/spring/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown(){
        driver.quit();
    }

    @Test
    public void testMainPageAccess() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage("http://localhost:8085/")
                .pause();
        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    public void testMainPageSearchByQuery() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage("http://localhost:8085/")
                .pause()
                .setUpSearchToken("Fear")
                .pause()
                .submit()
                .pause();
        assertTrue(driver.getPageSource().contains("Fear City"));
    }

//    @Test
//    public void testMainToGenresPageAccess() throws InterruptedException {
//        MainPage mainPage = new MainPage(driver);
//        mainPage
//                .callPage()
//                .pause()
//                .goToGenresPage()
//                .pause();
//        assertTrue(driver.getPageSource().contains("Жанры"));
//    }
//
//    @Test
//    public void testMainToRecentPageAccess() throws InterruptedException {
//        MainPage mainPage = new MainPage(driver);
//        mainPage
//                .callPage()
//                .pause()
//                .goToRecentPage()
//                .pause();
//        assertTrue(driver.getPageSource().contains("Новинки"));
//    }
//
//    @Test
//    public void testMainToPopularPageAccess() throws InterruptedException {
//        MainPage mainPage = new MainPage(driver);
//        mainPage
//                .callPage()
//                .pause()
//                .goToPopularPage()
//                .pause();
//        assertTrue(driver.getPageSource().contains("Популярное"));
//    }
//
//    @Test
//    public void testMainToAuthorsPageAccess() throws InterruptedException {
//        MainPage mainPage = new MainPage(driver);
//        mainPage
//                .callPage()
//                .pause()
//                .goToAuthorsPage()
//                .pause();
//
//        assertTrue(driver.getPageSource().contains("Авторы"));
//    }

    @Test
    public void testMainNavigateAccess() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage("http://localhost:8085/")
                .pause()
                .mainPageNavigate()
                .pause()
                .callPage("http://localhost:8085/genres")
                .pause()
                .genrePageNavigate()
                .callPage("http://localhost:8085/books/recent")
                .pause()
                .recentPageNavigate()
                .pause()
                .callPage("http://localhost:8085/books/popular")
                .popularPageNavigate()
                .pause()
                .callPage("http://localhost:8085/authors")
                .authorsPageNavigate()
                .pause();

        assertTrue(driver.getPageSource().contains("Авторы"));
    }

}