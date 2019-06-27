package es.uniovi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProgQueryWebAppApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ProgQueryWebAppApplication.class, args);
	}
	
	@Autowired
	public ProgQueryWebAppApplication(Neo4jConnectionProperties neo4jProperties) {
		System.setProperty("neo4j.url", neo4jProperties.getUrl());
		System.setProperty("neo4j.user", neo4jProperties.getUser());
		System.setProperty("neo4j.password", neo4jProperties.getPassword());
	}

}
