package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPage {
    private String url = "http://localhost:8085/";
    private ChromeDriver driver;

    public MainPage(ChromeDriver driver) {
        this.driver = driver;
    }

    public MainPage callPage(String url) {
        driver.get(url);
        return this;
    }

    public MainPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public MainPage setUpSearchToken(String token) {
        WebElement element = driver.findElement(By.id("query"));
        element.sendKeys(token);
        return this;
    }

    public MainPage submit() {
        WebElement element = driver.findElement(By.id("search"));
        element.submit();
        return this;
    }

    public MainPage goToPageAndBack(String xpath) {
        WebElement element = driver.findElement(By.xpath(xpath));
        element.click();
        driver.navigate().back();
        return this;
    }

    public MainPage goToRecentPage() {
        WebElement element = driver.findElement(By.xpath("//*[@id=\"navigate\"]/ul/li[3]/a"));
        element.click();
        return this;
    }

    public MainPage goToPopularPage() {
        WebElement element = driver.findElement(By.xpath("//*[@id=\"navigate\"]/ul/li[4]/a"));
        element.click();
        return this;
    }

    public MainPage goToAuthorsPage() {
        WebElement element = driver.findElement(By.xpath("//*[@id=\"navigate\"]/ul/li[5]/a"));
        element.click();
        return this;
    }

    public MainPage mainPageNavigate() {
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[2]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[3]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[4]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[5]/a");
        return this;
    }

    public MainPage genrePageNavigate() {
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[1]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[3]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[4]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[5]/a");
        return this;
    }

    public MainPage recentPageNavigate() {
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[1]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[2]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[4]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[5]/a");
        return this;
    }

    public MainPage popularPageNavigate() {
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[1]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[2]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[3]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[5]/a");
        return this;
    }

    public MainPage authorsPageNavigate() {
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[1]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[2]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[3]/a");
        goToPageAndBack("//*[@id=\"navigate\"]/ul/li[4]/a");
        return this;
    }
}
