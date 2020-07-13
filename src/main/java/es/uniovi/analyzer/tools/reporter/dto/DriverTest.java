package es.uniovi.analyzer.tools.reporter.dto;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

public class DriverTest {

	public static void main(String[] args) {
		Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "s3cr3t0." ) );
	
		
		String database = "system";
		String query = "CREATE DATABASE $param";
		
		try ( Session session = driver.session( SessionConfig.builder().withDatabase(database).build() ) )
        {
            Map<String, Object> params = new HashMap<>();
            params.put("param", "test");
            Result result = session.run(query, params);
            printResult(result);
        }
	}
	
	private static void printResult(Result result) {
		result.forEachRemaining((record) -> {
			StringBuilder sb = new StringBuilder();
			record.fields().forEach((field) -> { 
				sb.append(field);
				sb.append(" ");
			});
			System.out.println(sb);
		});
	}

}
