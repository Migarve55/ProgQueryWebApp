package es.uniovi.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import es.uniovi.entities.User;
import es.uniovi.tests.util.AbstractRestApiTest;

public class UsersRestApiTest extends AbstractRestApiTest {
	
	@Test
	public void CP_USR_01() throws Exception {
		User newUser = new User();
		newUser.setEmail("new@email.com");
		newUser.setName("New");
		newUser.setLastName("New");
		newUser.setPassword("12345678");
		newUser.setPasswordConfirm("12345678");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.post("/api/users")
	        .then()
	        	.statusCode(200)
	        	.body("email", equalTo("new@email.com"));
	}
	
	@Test
	public void CP_USR_02() throws Exception {
		User newUser = new User();
		newUser.setEmail("@email.com");
		newUser.setName("New");
		newUser.setLastName("New");
		newUser.setPassword("12345678");
		newUser.setPasswordConfirm("12345678");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.post("/api/users")
	        .then()
	        	.statusCode(400);
		
		newUser = new User();
		newUser.setEmail("new@email.com");
		newUser.setName("Neeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeew");
		newUser.setLastName("New");
		newUser.setPassword("12345678");
		newUser.setPasswordConfirm("12345678");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.post("/api/users")
	        .then()
	        	.statusCode(400);
		
		newUser = new User();
		newUser.setEmail("new@email.com");
		newUser.setName("New");
		newUser.setLastName("Neeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeew");
		newUser.setPassword("12345678");
		newUser.setPasswordConfirm("12345678");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.post("/api/users")
	        .then()
	        	.statusCode(400);
		
		newUser = new User();
		newUser.setEmail("new@email.com");
		newUser.setName("New");
		newUser.setLastName("New");
		newUser.setPassword("1234567");
		newUser.setPasswordConfirm("1234567");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.post("/api/users")
	        .then()
	        	.statusCode(400);
		
		newUser = new User();
		newUser.setEmail("new@email.com");
		newUser.setName("New");
		newUser.setLastName("New");
		newUser.setPassword("12345678");
		newUser.setPasswordConfirm("12345679");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.post("/api/users")
	        .then()
	        	.statusCode(400);
	}
	
	@Test
	public void CP_USR_03() throws Exception {
		User newUser = new User();
		newUser.setEmail("miguel@email.com");
		newUser.setName("New");
		newUser.setLastName("New");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.put("/api/users/miguel@email.com")
	        .then()
	        	.statusCode(200)
	        	.body("email", equalTo("miguel@email.com"));
	}
	
	@Test
	public void CP_USR_04() throws Exception {
		User newUser = new User();
		newUser.setName("Neeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeew");
		newUser.setLastName("New");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.put("/api/users/miguel@email.com")
	        .then()
	        	.statusCode(400);
		
		newUser = new User();
		newUser.setName("New");
		newUser.setLastName("Neeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeew");
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .body(newUser)
	        .when()
	        	.put("/api/users/miguel@email.com")
	        .then()
	        	.statusCode(400);
	}
	
	@Test
	public void CP_USR_05() throws Exception {
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .when()
	        	.delete("/api/users/miguel@email.com")
	        .then()
	        	.statusCode(200);
		
		given()
	        .spec(getRequestSpecification())
	        .contentType("application/json")
	        .when()
	        	.get("/api/users/miguel@email.com")
	        .then()
	        	.statusCode(404);
	}

}
