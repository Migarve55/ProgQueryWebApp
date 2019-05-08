package com.uniovi.analyzer.tools.reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ReportTool {
	
	private String dbPath;
	private List<String> queries = new ArrayList<String>();
	
	public ReportTool(String dbPath) {
		this.dbPath = dbPath;
	}

	public void addQuery(String query) {
		queries.add(query);
	}
	
	public void addAllQueries(List<String> queries) {
		queries.addAll(queries);
	}
	
	public void loadQueriesFromFile(String filepath) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(filepath))) {
			stream.forEach((line) -> {
				queries.add(line);
			});
		}
	}

	public List<CodeError> generateReport() {
		List<CodeError> errors = new ArrayList<CodeError>();
		Neo4jQueryRunner queryRunner = new Neo4jQueryRunner(dbPath);
		try {
			for (String query : queries) {
				queryRunner.runQuery(query).forEach((result) -> {
					errors.add(getCodeErrorFromResult(result));
				});
			}
		} finally {
			queryRunner.close();
		}
		return errors;
	}
	
	private CodeError getCodeErrorFromResult(Map<String,Object> result) {
		CodeError error = new CodeError();
		error.setFile(result.get("file").toString());
		Long line = (Long) result.get("line");
		if (line != null)
			error.setLine(line);
		Long column = (Long) result.get("column");
		if (column != null)
			error.setColumn(column);
		return error;
	}
	
}
