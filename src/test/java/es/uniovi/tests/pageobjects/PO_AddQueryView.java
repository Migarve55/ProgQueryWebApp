package es.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_AddQueryView extends PO_NavView {

	public static void goToPage(WebDriver driver) {
		WebElement queryMenu = driver.findElement(By.id("queries-menu"));
		queryMenu.click();
		WebElement menuOpt = driver.findElement(By.xpath("//*/a[contains(@href,'/query/add')]"));
		menuOpt.click();
	}
	
	public static void fillForm(WebDriver driver, String namep, String descriptionp, String queryp, boolean isPublic, int... publicTo) {
		WebElement name = driver.findElement(By.name("name"));
		name.click();
		name.clear();
		name.sendKeys(namep);
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
		//Add public to
		for (int n : publicTo) {
			driver.findElement(By.xpath(String.format("(//option)[%d]", n))).click();
		}
		WebElement btnAdd = driver.findElement(By.id("sendBtn"));
		btnAdd.click();
	}

}
