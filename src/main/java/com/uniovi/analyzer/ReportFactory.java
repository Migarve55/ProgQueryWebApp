package com.uniovi.analyzer;

import java.util.Arrays;
import java.util.List;

import com.uniovi.entities.CodeError;

public class ReportFactory {

	public List<CodeError> generateReport(String dbName) {
		return Arrays.asList(
				new CodeError("test.java", 1, 3, "HIGH", "This was generated as a mere test")
			);
	}
	
}
