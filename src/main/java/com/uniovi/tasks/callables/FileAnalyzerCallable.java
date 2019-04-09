package com.uniovi.tasks.callables;

import java.io.File;
import java.util.List;

import com.uniovi.analyzer.DatabaseManager;
import com.uniovi.analyzer.JavaCompilerUtil;
import com.uniovi.analyzer.ReportFactory;
import com.uniovi.entities.CodeError;

public class FileAnalyzerCallable extends AbstractAnalyzerCallable {

	private File file;
	
	//Utilities
	private JavaCompilerUtil compiler = new JavaCompilerUtil();
	private DatabaseManager dbManager = new DatabaseManager();
	private ReportFactory reportFactory = new ReportFactory();
	
	public FileAnalyzerCallable(String args, File file) {
		super(args);
		this.file = file;
	}

	@Override
	public List<CodeError> call() throws Exception {
		String dbName = "testDb";
		task.setStatus(String.format("Creating neo4j database (%s)...", dbName));
		dbManager.createDb(dbName);
		task.incrementProgress(15);
		task.setStatus(String.format("Compiling %s...", file.getName()));
		compiler.compile(file.getAbsolutePath(), getArgs(), dbName);
		task.incrementProgress(50);
		task.setStatus("Creating report...");
		List<CodeError> report = reportFactory.generateReport(dbName);
		task.incrementProgress(90);
		task.setStatus(String.format("Deleting neo4j database (%s)...", dbName));
		return report;
	}

}
