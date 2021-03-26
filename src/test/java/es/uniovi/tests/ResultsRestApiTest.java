package es.uniovi.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import es.uniovi.tests.util.AbstractRestApiTest;

public class ResultsRestApiTest extends AbstractRestApiTest {
	
	@Test
	public void CP_RSL_01() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        	.params("programId", "12")
	        .when()
	        	.get("/api/results")
	        .then()
	        	.statusCode(200)
	        	.body("[0].program", equalTo("program1"));
	}
	
	@Test
	public void CP_RSL_02() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        	.params("analysisId", "9")
	        .when()
	        	.get("/api/results")
	        .then()
	        	.statusCode(200)
	        	.body("[0].program", equalTo("program1"));
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
	        	.body("[0].program", equalTo("program1"));
	}
	
	@Test
	public void CP_RSL_04() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        	.params("programId", "12")
	        	.params("analysisId", "9")
	        .when()
	        	.get("/api/results")
	        .then()
	        	.statusCode(200)
	        	.body("[0].program", equalTo("program1"));
	}
	
	@Test
	public void CP_RSL_05() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .when()
	        	.delete("/api/results/12")
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
