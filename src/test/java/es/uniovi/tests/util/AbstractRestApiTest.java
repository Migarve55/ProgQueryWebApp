package es.uniovi.tests.util;

import static io.restassured.RestAssured.given;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import es.uniovi.security.JwtSecurityConstants;
import es.uniovi.security.JwtUser;
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

	@LocalServerPort
	private int portNumber;

	@Before
	public void setUp() {
		this.buildJWTSpecification("miguel@email.com", "123456");
	}

	protected void buildJWTSpecification(String username, String password) {
		JwtUser user = new JwtUser();
		user.setUsername(username);
		user.setPassword(password);

		String token = given()
						.port(portNumber)
						.contentType("application/json")
						.body(user)
					.when()
						.post(JwtSecurityConstants.SIGN_UP_URL)
					.then().statusCode(200)
					.extract().header(JwtSecurityConstants.HEADER);

		specification = new RequestSpecBuilder()
				.addHeader(JwtSecurityConstants.HEADER, token)
				.setPort(portNumber)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	protected RequestSpecification getRequestSpecification() {
		return this.specification;
	}

}