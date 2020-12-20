package es.uniovi.analyzer.tools.reporter;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

public class Neo4jFacade implements AutoCloseable {

	private final Driver driver;
	
	private final static String DELETE_PROGRAM_QUERY = 
			"MATCH (p:PROGRAM)-[*]->(connected) " + 
			"WHERE p.ID = $programID " + 
			"DETACH DELETE p, connected";

    public Neo4jFacade(String url) {
    	driver = GraphDatabase.driver( url, AuthTokens.basic( System.getProperty("neo4j.user"), System.getProperty("neo4j.password") ) );
    }

    /**
     * Run a query
     * @param query
     * @param programID the ID of the program to analize
     * @return
     */
    public StatementResult runQuery(String database, String query, String programID) {
		try ( Session session = driver.session() )
        {
            Map<String, Object> params = new HashMap<>();
        	params.put("programID", programID);
            return session.writeTransaction(tx -> tx.run(query, params));
        }
    }
    
    /**
     * Deletes a program from the database
     * @param programID
     */
    public void removeProgram(String database, String programID) {
    	try ( Session session = driver.session() )
        {
            Map<String, Object> params = new HashMap<>();
        	params.put("programID", programID);
            session.writeTransaction(tx -> tx.run(DELETE_PROGRAM_QUERY, params));
        }
    }
    
	@Override
    public void close() {
    	driver.close();
    }
    
}
