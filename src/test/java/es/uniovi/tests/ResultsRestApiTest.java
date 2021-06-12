package es.uniovi.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;

import es.uniovi.tests.util.AbstractRestApiTest;

public class ResultsRestApiTest extends AbstractRestApiTest {
	
	@Test
	public void CP_RSL_01() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        	.params("programName", "program1")
	        .when()
	        	.get("/api/results")
	        .then()
	        	.statusCode(200)
	        	.body("size()", is(1));
	}
	
	@Test
	public void CP_RSL_02() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        	.params("analysisName", "test1")
	        .when()
	        	.get("/api/results")
	        .then()
	        	.statusCode(200)
	        	.body("size()", is(2));
	}
	
	@Test
	public void CP_RSL_03() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        	.params("user", "miguel@email.com")
	        .when()
	        	.get("/api/results")
	        .then()
	        	.statusCode(200)
	        	.body("size()", is(1));
	}
	
	@Test
	public void CP_RSL_04() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        	.params("programName", "program1")
	        	.params("analysisName", "test1")
	        .when()
	        	.get("/api/results")
	        .then()
	        	.statusCode(200)
	        	.body("size()", is(1));
	}
	
	@Test
	public void CP_RSL_05() throws Exception {
		long resultId = given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        	.params("programName", "program1")
	        .when()
	        	.get("/api/results")
	        .then()
	        	.statusCode(200)
	        	.extract()
	        	.jsonPath()
	        	.getLong("[0].id");
				
		
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .when()
	        	.delete("/api/results/" + resultId)
	        .then()
	        	.statusCode(200);
		
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .when()
	        	.get("/api/results/12")
	        .then()
	        	.statusCode(404);
	}

}
