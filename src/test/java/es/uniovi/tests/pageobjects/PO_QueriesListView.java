package es.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_QueriesListView extends PO_NavView {

	public static void goToPage(WebDriver driver) {
		WebElement queryMenu = driver.findElement(By.id("queries-menu"));
		queryMenu.click();
		WebElement menuOpt = driver.findElement(By.xpath("//*/a[contains(@href,'/query/list')]"));
		menuOpt.click();
	}
	
	public static void search(WebDriver driver, String queryName) {
		WebElement searchText = driver.findElement(By.name("searchText"));
		searchText.click();
		searchText.clear();
		searchText.sendKeys(queryName);
		driver.findElement(By.xpath("//*/button[contains(@type,'submit')]")).click();
	}
	
	public static void checkExists(WebDriver driver, String queryName) {
		checkElement(driver, "text", queryName);
	}

}
