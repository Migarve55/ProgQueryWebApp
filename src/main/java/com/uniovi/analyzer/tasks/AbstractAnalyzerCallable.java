package com.uniovi.analyzer.tasks;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import com.uniovi.analyzer.exceptions.CompilerException;
import com.uniovi.analyzer.exceptions.EnviromentException;
import com.uniovi.analyzer.exceptions.ReportException;
import com.uniovi.analyzer.tools.ToolFactory;
import com.uniovi.analyzer.tools.reporter.CodeError;
import com.uniovi.analyzer.tools.reporter.ReportTool;

public abstract class AbstractAnalyzerCallable implements Callable<List<CodeError>> {

	private String args;
	protected String basePath;
	protected AnalyzerTask task;

	public AbstractAnalyzerCallable(String args) {
		this.args = args;
	}

	@Override
	public final List<CodeError> call() throws EnviromentException, ReportException, CompilerException {
		List<CodeError> result = null;
		try {
			prepareEnviroment();
			compile();
			result = createReport();
		} finally {
			cleanEnviroment();
		}
		return result;
	}
	
	public void nextStep(String step, int increment) {
		task.setStatus(step);
		task.incrementProgress(increment);
	}

	// Template methods

	protected void prepareEnviroment() throws EnviromentException {
		nextStep("Preparing enviroment...", 25);
		basePath = ToolFactory.getEnviromentTool().createEnviroment();
		if (basePath == null) {
			throw new EnviromentException();
		}
	}

	protected void compile() throws CompilerException {
		nextStep("Compiling...", 25);
		// This needs to be overrided
	}

	protected List<CodeError> createReport() throws ReportException {
		nextStep("Creating report", 25);
		String dbPath = basePath + "/neo4j/data/ProgQuery.db";
		return ToolFactory.getReportTool(dbPath).generateReport();
	}

	protected void cleanEnviroment() throws EnviromentException {
		nextStep("Cleaning enviroment", 25);
		ToolFactory.getEnviromentTool().deleteEnviroment(basePath);
	}

	protected ReportTool setupReportTool(String dbPath) {
		ReportTool reportFactory = new ReportTool(dbPath);
		try {
			reportFactory.loadQueriesFromFile("src/main/resources/queries.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reportFactory;
	}

	// Setters

	public void setTask(AnalyzerTask task) {
		this.task = task;
	}

	public String getArgs() {
		return args;
	}

}