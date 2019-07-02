package es.uniovi.tests.pageobjects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import es.uniovi.tests.util.SeleniumUtils;

public class PO_HomeView extends PO_NavView {
	
	public static void goToPage(WebDriver driver) {
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, '/')]");
		elementos.get(0).click();
	}
	
	public static void analizeFile(WebDriver driver) {
		List<WebElement> btnSearch = SeleniumUtils.EsperaCargaPagina(driver, "id", "file", getTimeout());
		btnSearch.get(0).click();
	}
	
	public static void analizeZip(WebDriver driver) {
		List<WebElement> btnSearch = SeleniumUtils.EsperaCargaPagina(driver, "id", "zip", getTimeout());
		btnSearch.get(0).click();
	}
	
	public static void analizeGit(WebDriver driver) {
		List<WebElement> btnSearch = SeleniumUtils.EsperaCargaPagina(driver, "id", "git", getTimeout());
		btnSearch.get(0).click();
	}

}
