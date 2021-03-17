package es.uniovi.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import es.uniovi.entities.Query;
import es.uniovi.tests.util.AbstractRestApiTest;

public class QueryRestApiTest extends AbstractRestApiTest {

	@Test
	public void getQueries() throws Exception {
		 given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .when()
	        	.get("/api/analyses")
	        .then()
	        	.statusCode(200)
	        	.body("", equalTo(""));
		 
	}

}
