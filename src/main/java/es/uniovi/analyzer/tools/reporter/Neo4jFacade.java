package es.uniovi.analyzer.tools.reporter;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

public class Neo4jFacade implements AutoCloseable {

	private final Driver driver;
	
	private final static String DELETE_PROGRAM_QUERY = 
			"MATCH (p:PROGRAM)-[*]->(connected) " + 
			"WHERE p.ID = $programID " + 
			"DETACH DELETE p, connected";
	
	private final static String CREATE_DATABASE = "CREATE DATABASE ";
	private final static String DELETE_DATABASE = "DROP DATABASE ";

    public Neo4jFacade(String url) {
    	driver = GraphDatabase.driver( url, AuthTokens.basic( System.getProperty("neo4j.user"), System.getProperty("neo4j.password") ) );
    }

    /**
     * Run a query
     * @param query
     * @param programID the ID of the program to analize
     * @return
     */
    public Result runQuery(String database, String query, String programID) {
		try ( Session session = driver.session( SessionConfig.builder().withDatabase(database).build() ) )
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
    	try ( Session session = driver.session( SessionConfig.builder().withDatabase(database).build() ) )
        {
            Map<String, Object> params = new HashMap<>();
        	params.put("programID", programID);
            session.writeTransaction(tx -> tx.run(DELETE_PROGRAM_QUERY, params));
        }
    }
    
    /**
     * Creates a new database
     * @param database
     */
    public void addDataBase(String database) {
    	try ( Session session = driver.session( SessionConfig.builder().withDatabase("system").build() ) )
        {
            session.run(CREATE_DATABASE + database);
        }
    }
    
    /**
     * Deletes a database
     * @param database
     */
    public void removeDataBase(String database) {
    	try ( Session session = driver.session( SessionConfig.builder().withDatabase("system").build() ) )
        {
            session.run(DELETE_DATABASE + database);
        }
    }
    
	@Override
    public void close() {
    	driver.close();
    }
    
}
