package es.uniovi.tests.pageobjects;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import es.uniovi.tests.util.SeleniumUtils;

public class PO_NavView extends PO_View {
	
	public static void checkIsLogged(WebDriver driver) {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "queries-menu", getTimeout());
		assertTrue(elementos.size() == 1);
	}

	public static void logout(WebDriver driver) {
		WebElement loginBtn = driver.findElement(By.id("logout"));
		loginBtn.click();
	}
 	
	public static void changeLanguage(WebDriver driver, String textLanguage) {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "btnLanguage", getTimeout());
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "languageDropdownMenuButton", getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", textLanguage, getTimeout());
		elementos.get(0).click();
	}

}
