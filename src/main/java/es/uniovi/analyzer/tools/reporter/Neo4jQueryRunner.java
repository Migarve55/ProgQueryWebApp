package es.uniovi.analyzer.tools.reporter;

import java.io.Closeable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jQueryRunner implements Closeable {

	GraphDatabaseService graphDb; 

    public Neo4jQueryRunner(String path) {
    	GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
    	graphDb = graphDbFactory.newEmbeddedDatabase(new File(path));
    }

    public Stream<Map<String, Object>> runQuery(String query, String programID) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("programID", programID);
		return graphDb.execute(query, params).stream();
    }
    
    public void close() {
    	graphDb.shutdown();
    }
    
}
