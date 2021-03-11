package es.uniovi.analyzer.tools.reporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import es.uniovi.reflection.processing.CypherAdapter;

public class Neo4jFacade implements AutoCloseable {

	private final Driver driver;
	
	private final static String DELETE_PROGRAM_QUERY = "MATCH (p:PROGRAM) WHERE p.ID=$programID CALL apoc.path.subgraphNodes(p,{minLevel:0}) YIELD node DETACH DELETE node";

    public Neo4jFacade(String url) {
    	url = "neo4j://" + url;
    	driver = GraphDatabase.driver( url, AuthTokens.basic( System.getProperty("neo4j.user"), System.getProperty("neo4j.password") ) );
    }

    /**
     * Run a query
     * @param query
     * @param programID the ID of the program to analize
     * @return
     */
    public List<Record> runQuery(String database, String query, String programID) {
		try ( Session session = driver.session() )
        {
            Map<String, Object> params = new HashMap<>();
        	params.put("programID", programID);
        	String modifiedQuery = limitQuery(query, programID);
            return session.writeTransaction(new TransactionWork<List<Record>>() {
            	@Override
            	public List<Record> execute(Transaction tx) {
	            	 Result result = tx.run(modifiedQuery, params);
	            	 return result.list();
            	}
			});
        }
    }
    
    /**
     * Deletes a program from the database
     * @param programID
     */
    public void removeProgram(String programID) {
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
	
	private String limitQuery(String query, String programId) {
		return CypherAdapter.limitQuery(query, programId);
	}
    
}
