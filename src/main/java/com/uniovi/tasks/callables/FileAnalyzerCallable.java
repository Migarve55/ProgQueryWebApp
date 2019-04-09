package com.uniovi.tasks.callables;

import java.io.File;
import java.util.List;

import com.uniovi.analyzer.DatabaseManager;
import com.uniovi.analyzer.JavaCompilerUtil;
import com.uniovi.analyzer.reporter.ReportFactory;
import com.uniovi.entities.CodeError;

public class FileAnalyzerCallable extends AbstractAnalyzerCallable {

	private File file;
	
	//Utilities
	private JavaCompilerUtil compiler = new JavaCompilerUtil();
	private DatabaseManager dbManager = new DatabaseManager();
	
	public FileAnalyzerCallable(String args, File file) {
		super(args);
		this.file = file;
	}

	@Override
	public List<CodeError> call() throws Exception {
		//Execution
		task.setStatus("Creating neo4j database...");
		String dbPath = dbManager.createDb("/src/resources/dbs/");
		task.incrementProgress(15);
		task.setStatus(String.format("Compiling %s...", file.getName()));
		compiler.compile(file.getAbsolutePath(), getArgs(), dbPath);
		task.incrementProgress(50);
		task.setStatus("Creating report...");
		ReportFactory reportFactory = setupReportFactory(dbPath);
		List<CodeError> report = reportFactory.generateReport();
		return report;
	}


}
