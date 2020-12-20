package es.uniovi.analyzer.tools.reporter.dto;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;


public class DriverTest {

	public static void main(String[] args) {
		Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "s3cr3t0." ) );
		String query = "CREATE DATABASE $param";
		
		try ( Session session = driver.session() )
        {
            Map<String, Object> params = new HashMap<>();
            params.put("param", "test");
            StatementResult result = session.run(query, params);
            printResult(result);
        }
	}
	
	private static void printResult(StatementResult result) {
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
