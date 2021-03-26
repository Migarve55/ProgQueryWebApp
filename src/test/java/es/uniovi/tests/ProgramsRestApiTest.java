package es.uniovi.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;

import es.uniovi.tests.util.AbstractRestApiTest;

public class ProgramsRestApiTest extends AbstractRestApiTest {
	
	@Test
	public void CP_PGR_01() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .params("user", "miguel@email.com")
	        .when()
	        	.get("/api/programs")
	        .then()
	        	.statusCode(200)
	        	.body("[0].id", is(12))
	        	.body("[0].name", is("program1"))
	        	.body("[0].results", is(1))
	        	.body("size()", is(1));
	}
	
	@Test
	public void CP_PGR_02() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .when()
	        	.delete("/api/programs/12")
	        .then()
	        	.statusCode(200);
		
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .params("user", "miguel@email.com")
	        .when()
	        	.get("/api/programs")
	        .then()
	        	.statusCode(200)
	        	.body("size()", is(0));
	}


}
