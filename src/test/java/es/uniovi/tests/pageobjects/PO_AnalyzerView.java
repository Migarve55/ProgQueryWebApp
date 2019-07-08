package es.uniovi.tests.pageobjects;

import org.openqa.selenium.WebDriver;

public class PO_AnalyzerView extends PO_NavView {
	
	public static void checkForQuery(WebDriver driver, String queryName) {
		checkElement(driver, "text", queryName);
	}

}
