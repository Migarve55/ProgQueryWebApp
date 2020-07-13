package es.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_GitAnalyzerView extends PO_AnalyzerView {
	
	public static void goToPage(WebDriver driver) {
		WebElement menuOpt = driver.findElement(By.xpath("//*/a[contains(@href,'/')]"));
		menuOpt.click();
		WebElement queryMenu = driver.findElement(By.id("git"));
		queryMenu.click();
	}

}
