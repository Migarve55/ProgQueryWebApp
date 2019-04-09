package com.uniovi.analyzer.reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	
	public void loadQueriesFromFile(String filepath) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(filepath))) {
			stream.forEach((line) -> {
				String[] tokens = line.split("\t");
				if (tokens.length == 3)
					queries.add(new Query(tokens[0], tokens[1], tokens[2]));
			});
		}
	}

	public List<CodeError> generateReport() {
		return queries.stream()
				.map((query) -> getCodeErrorFromQuery(query))
				.collect(Collectors.toList());
	}
	
	private CodeError getCodeErrorFromQuery(Query query) {
		CodeError error = new CodeError();
		error.setDescription(query.getDescrition());
		error.setLevel(query.getLevel());
		return null;
	}
	
}
