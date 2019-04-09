package com.uniovi.analyzer.reporter;

import java.util.Arrays;
import java.util.List;

import com.uniovi.entities.CodeError;

public class ReportFactory {
	
	private String dbPath;
	private List<Query> queries;
	
	public ReportFactory(String dbPath) {
		this.dbPath = dbPath;
	}

	public void addQuery(Query query) {
		queries.add(query);
	}
	
	public void loadQueriesFromFile(String filepath) {
		
	}

	public List<CodeError> generateReport() {
		return Arrays.asList(
				new CodeError("test.java", 1, 3, "HIGH", "This was generated as a mere test")
			);
	}
	
}
