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

public class Neo4jQueryRunner implements AutoCloseable {

	private final Driver driver;

    public Neo4jQueryRunner(String url) {
    	driver = GraphDatabase.driver( url, AuthTokens.basic( System.getProperty("neo4j.user"), System.getProperty("neo4j.password") ) );
    }

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
    
	@Override
    public void close() {
    	driver.close();
    }
    
}
