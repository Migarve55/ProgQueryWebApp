package es.uniovi.tests.util;

import static io.restassured.RestAssured.given;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import es.uniovi.security.JwtSecurityConstants;
import es.uniovi.security.JwtUser;
import es.uniovi.services.InsertSampleDataService;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractRestApiTest {

	private RequestSpecification specification;
	
	@Autowired 
	private InsertSampleDataService insertSampleDataService;

	@LocalServerPort
	private int portNumber;

	@Before
	public void setUp() {
		this.insertSampleDataService.resetDB();
		this.insertSampleDataService.createAllData();
		this.buildJWTSpecification("miguel@email.com", "123456");
	}

	protected void buildJWTSpecification(String username, String password) {
		
		// Ignore if the token is ready
		if (specification != null)
			return;
		
		JwtUser user = new JwtUser();
		user.setUsername(username);
		user.setPassword(password);

		String token = given()
						.relaxedHTTPSValidation()
						.port(portNumber)
						.baseUri("https://localhost")
						.contentType("application/json")
						.body(user)
					.when()
						.post(JwtSecurityConstants.SIGN_UP_URL)
					.then().statusCode(200)
					.extract().header(JwtSecurityConstants.HEADER);

		specification = new RequestSpecBuilder()
				.addHeader(JwtSecurityConstants.HEADER, token)
				.setBaseUri("https://localhost")
				.setPort(portNumber)
				.addFilter(new RequestLoggingFilter(LogDetail.PARAMS))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.setRelaxedHTTPSValidation()
				.build();
	}
	
	protected RequestSpecification getRequestSpecification() {
		return this.specification;
	}

}