package es.uniovi.analyzer.tools.reporter;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

public class Neo4jFacade implements AutoCloseable {

	private final Driver driver;
	
	private final static String DELETE_PROGRAM_QUERY = 
			"MATCH (p:PROGRAM)-[*]-(connected)" + 
			"WHERE p.ID = $programID" + 
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
    public StatementResult runQuery(String query, String programID) {
    	StatementResult result = null;
    	try ( Session session = driver.session() )
        {
            result = session.writeTransaction( new TransactionWork<StatementResult>()
            {
                @Override
                public StatementResult execute( Transaction tx ) {
                	Map<String, Object> params = new HashMap<>();
                	params.put("programID", programID);
                	return tx.run(query, params);
                }

            });
        }
		return result;
    }
    
    /**
     * Deletes a program from the database
     * @param programID
     */
    public void removeProgram(String programID) {
    	try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<StatementResult>()
            {
                @Override
                public StatementResult execute( Transaction tx ) {
                	Map<String, Object> params = new HashMap<>();
                	params.put("programID", programID);
                	return tx.run(DELETE_PROGRAM_QUERY, params);
                }
            });
        }
    }
    
	@Override
    public void close() {
    	driver.close();
    }
    
}
