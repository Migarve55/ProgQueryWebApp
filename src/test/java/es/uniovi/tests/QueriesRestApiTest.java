package es.uniovi.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import es.uniovi.entities.Query;
import es.uniovi.tests.util.AbstractRestApiTest;

public class QueriesRestApiTest extends AbstractRestApiTest {

	@Test
	public void CP_ANL_01() throws Exception {
		Query newQuery = new Query();
		newQuery.setName("created");
		newQuery.setDescription("...");
		newQuery.setQueryText("MATCH (n) RETURN n");
		newQuery.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newQuery)
	        .when()
	        	.post("/api/analyses")
	        .then()
	        	.statusCode(200)
	        	.body("name", equalTo("created"));
		
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .params("user", "miguel@email.com")
	        .params("owner", "true")
	        .when()
	        	.get("/api/analyses")
	        .then()
	        	.statusCode(200)
	        	.body("[0].name", equalTo("created"))
	        	.body("[1].name", equalTo("test1"))
	        	.body("[2].name", equalTo("test2"));
	}
	
	@Test
	public void CP_ANL_02() throws Exception {
		Query newQuery = new Query();
		newQuery.setName("");
		newQuery.setDescription("...");
		newQuery.setQueryText("...");
		newQuery.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newQuery)
	        .when()
	        	.post("/api/analyses")
	        .then()
	        	.statusCode(400);
		
		newQuery = new Query();
		newQuery.setName("test..name");
		newQuery.setDescription("...");
		newQuery.setQueryText("...");
		newQuery.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newQuery)
	        .when()
	        	.post("/api/analyses")
	        .then()
	        	.statusCode(400);
		
		newQuery = new Query();
		newQuery.setName("..testname");
		newQuery.setDescription("...");
		newQuery.setQueryText("...");
		newQuery.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newQuery)
	        .when()
	        	.post("/api/analyses")
	        .then()
	        	.statusCode(400);
		
		newQuery = new Query();
		newQuery.setName("test.name");
		newQuery.setDescription("");
		newQuery.setQueryText("...");
		newQuery.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newQuery)
	        .when()
	        	.post("/api/analyses")
	        .then()
	        	.statusCode(400);
		
		newQuery = new Query();
		newQuery.setName("test.name");
		newQuery.setDescription("...");
		newQuery.setQueryText("");
		newQuery.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newQuery)
	        .when()
	        	.post("/api/analyses")
	        .then()
	        	.statusCode(400);
	}
	
	@Test
	public void CP_ANL_03() throws Exception {
		Query query = new Query();
		query.setDescription("new description");
		query.setQueryText("MATCH (n) RETURN n as msg");
		query.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(query)
	        .when()
	        	.put("/api/analyses/test1")
	        .then()
	        	.statusCode(200);
		
		given()
        .spec(getRequestSpecification())
        .contentType("application/json")
        .params("user", "miguel@email.com")
        .when()
        	.get("/api/analyses/test1")
        .then()
        	.statusCode(200)
        	.body("name", equalTo("test1"))
        	.body("description", equalTo("new description"))
        	.body("query", equalTo("MATCH (n) RETURN n as msg"));
	}
	
	@Test
	public void CP_ANL_04() throws Exception {
		Query query = new Query();
		query.setDescription("");
		query.setQueryText("new query text");
		query.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(query)
	        .when()
	        	.put("/api/analyses/test1")
	        .then()
	        	.statusCode(400);
		
		query = new Query();
		query.setDescription("new query description");
		query.setQueryText("MATCH (n RETURN n");
		query.setPublicForAll(false);
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(query)
	        .when()
	        	.put("/api/analyses/test1")
	        .then()
	        	.statusCode(400);
	}
	
	@Test
	public void CP_ANL_05() throws Exception {
		 given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .params("user", "miguel@email.com")
	        .when()
	        	.get("/api/analyses")
	        .then()
	        	.statusCode(200)
	        	.body("[0].name", equalTo("test1"))
	        	.body("[1].name", equalTo("test2"))
	        	.body("[2].name", equalTo("test3"));
		 
	}
	
	@Test
	public void CP_ANL_06() throws Exception {
		 given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .params("user", "miguel@email.com")
	        .params("owner", "true")
	        .when()
	        	.get("/api/analyses")
	        .then()
	        	.statusCode(200)
	        	.body("[0].name", equalTo("test1"))
	        	.body("[1].name", equalTo("test2"));
	}
	
	@Test
	public void CP_ANL_07() throws Exception {
		given()
        .spec(getRequestSpecification())
        .contentType("application/json")
        .when()
        	.delete("/api/analyses/test1")
        .then()
        	.statusCode(200);
		
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .params("user", "miguel@email.com")
	        .params("owner", "true")
	        .when()
	        	.get("/api/analyses")
	        .then()
	        	.statusCode(200)
	        	.body("[0].name", equalTo("test2"));
	}

}
