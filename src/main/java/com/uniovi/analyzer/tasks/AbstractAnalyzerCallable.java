package com.uniovi.analyzer.tasks;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import com.uniovi.analyzer.tools.reporter.ReportTool;
import com.uniovi.entities.CodeError;

public abstract class AbstractAnalyzerCallable implements Callable<List<CodeError>> {

	private String args;
	protected AnalyzerTask task;

	public AbstractAnalyzerCallable(String args) {
		this.args = args;
	}

	@Override
	public final List<CodeError> call() throws Exception {
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

	protected void prepareEnviroment() throws IOException {
		nextStep("Preparing enviroment...", 25);
	}

	protected void compile() {
		nextStep("Compiling...", 25);
	}

	protected List<CodeError> createReport() {
		nextStep("Creating report", 25);
		return Collections.emptyList();
	}

	protected void cleanEnviroment() throws IOException {
		nextStep("Cleaning enviroment", 25);
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
