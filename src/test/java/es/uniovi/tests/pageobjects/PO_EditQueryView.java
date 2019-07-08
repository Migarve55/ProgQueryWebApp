package es.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_EditQueryView extends PO_NavView {
	
	public static void fillForm(WebDriver driver, String descriptionp, String queryp, boolean isPublic) {
		WebElement description = driver.findElement(By.name("description"));
		description.click();
		description.clear();
		description.sendKeys(descriptionp);
		WebElement queryText = driver.findElement(By.name("queryText"));
		queryText.click();
		queryText.clear();
		queryText.sendKeys(queryp);
		if (isPublic) {
			WebElement publicToggle = driver.findElement(By.name("publicForAll"));
			publicToggle.click();
		}
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}

}
