package es.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_LoginView extends PO_NavView {

	public static void goToPage(WebDriver driver) {
		WebElement loginBtn = driver.findElement(By.id("login"));
		if (loginBtn == null) {
			driver.findElement(By.id("logout")).click();
		}
		loginBtn = driver.findElement(By.id("login"));
		loginBtn.click();
	}
	
	public static void fillForm(WebDriver driver, String usernamep, String passwordp) {
		WebElement username = driver.findElement(By.name("username"));
		username.click();
		username.clear();
		username.sendKeys(usernamep);
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys(passwordp);
		// Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}

}
