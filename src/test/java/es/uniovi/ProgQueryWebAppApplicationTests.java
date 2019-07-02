package es.uniovi;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import es.uniovi.entities.Query;
import es.uniovi.entities.User;
import es.uniovi.services.QueryService;
import es.uniovi.services.UsersService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProgQueryWebAppApplicationTests {

	@Autowired
	private UsersService usersService;

	@Autowired
	private QueryService queryService;
	
	final static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	final static String Geckdriver024 = "F:\\DriversNavegadores\\geckodriver024win64.exe";
	final static String URL = "http://localhost:8080";
	
	final static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}
	
	@Before
	public void setup() {
		//Reset db
		queryService.deleteAll();
		usersService.deleteAll();
		//Insert data
		User user1 = new User("miguel@email.com", "Miguel", "Garnacho Vélez");
		user1.setPassword("123456");
		User user2 = new User("oscar@email.com", "Oscar", "Prieto");
		user2.setPassword("123456");
		User user3 = new User("paco@email.com", "Paco", "Salvador Vega");
		user3.setPassword("123456");
		User user4 = new User("maria@hotmail.es", "Maria Luisa", "Jorganes Hernandez");
		user4.setPassword("123456");
		User user5 = new User("alvaro@email.com", "Álvaro", "Sanchez Dragó");
		user5.setPassword("123456");

		usersService.addUser(user1);
		usersService.addUser(user2);
		usersService.addUser(user3);
		usersService.addUser(user4);
		usersService.addUser(user5);

		Query query2 = new Query("es.uniovi.query1", "Test 1", "...");
		query2.setUser(user1);
		query2.setPublicForAll(false);
		Set<User> publicTo = new HashSet<User>();
		publicTo.add(user2);
		publicTo.add(user3);
		publicTo.add(user4);
		query2.setPublicTo(publicTo);

		Query query3 = new Query("es.uniovi.query2", "Test 2", "...");
		query3.setUser(user2);

		Query query4 = new Query("es.uniovi.query3", "Test 3", "...");
		query4.setUser(user3);

		Query query5 = new Query("es.uniovi.query4", "Test 4", "...");
		query5.setUser(user4);
		query5.setPublicForAll(true);

		queryService.saveQuery(query2);
		queryService.saveQuery(query3);
		queryService.saveQuery(query4);
		queryService.saveQuery(query5);
		//Go to URL
		driver.navigate().to(URL);
	}
	
	@After
	public void clean() {
		driver.manage().deleteAllCookies();
	}

	@AfterClass
	static public void end() {
		driver.quit();
	}
	
	/**
	 * Succesful login
	 */
	@Test
	public void testCase01() {
		
	}
	
	/**
	 * Login with incorrect credentials
	 */
	@Test
	public void testCase02() {
		
	}
	
	/**
	 * Succesful sing up
	 */
	@Test
	public void testCase03() {
		
	}
	
	/**
	 * Sing up with invalid data
	 */
	@Test
	public void testCase04() {
		
	}
	
	/**
	 * Users creates a private query and it shows up in the private query list
	 */
	@Test
	public void testCase05() {
		
	}
	
	/**
	 * Users creates a public query and it shows up in the private query list 
	 * and in others users queries list when choosing queries to analyze
	 */
	@Test
	public void testCase06() {
		
	}
	
	/**
	 * Users creates a private query with private access to another user 
	 * and it shows up in the private query list 
	 * and in the other user queries list when choosing queries to analyze
	 */
	@Test
	public void testCase07() {
		
	}
	
	/**
	 * Users creates a query with invalid data
	 */
	@Test
	public void testCase08() {
		
	}
	
	/**
	 * Analyze a zip file
	 */
	@Test
	public void testCase09() {
		
	}
	
	/**
	 * Analyze a git repo
	 */
	@Test
	public void testCase10() {
		
	}
	
	/**
	 * Analyze a program
	 */
	@Test
	public void testCase11() {
		
	}
	
	/**
	 * User deletes a query
	 * It should check that there are problems without that query description
	 */
	@Test
	public void testCase12() {
		
	}

	/**
	 * User deletes a program
	 * The related results should dissapear
	 */
	@Test
	public void testCase13() {
		
	}
	
	/**
	 * User deletes a result
	 */
	@Test
	public void testCase14() {
		
	}

	/**
	 * User edits a query
	 * Check the data is updated
	 */
	@Test
	public void testCase15() {
		
	}

	/**
	 * User edits a query with invalid data
	 * The query should not have changed
	 */
	@Test
	public void testCase16() {
		
	}


}
