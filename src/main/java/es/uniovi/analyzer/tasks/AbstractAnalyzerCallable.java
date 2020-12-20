package es.uniovi.analyzer.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.exceptions.ReportException;
import es.uniovi.analyzer.tools.ToolFactory;
import es.uniovi.analyzer.tools.compilators.CompilerTool;
import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.QueryDto;

public abstract class AbstractAnalyzerCallable implements Callable<List<ProblemDto>> {
	
	protected String args;
	protected String basePath;
	protected String programID;
	protected AnalyzerTask task;
	protected CompilerTool compiler;
	protected List<QueryDto> queries = new ArrayList<QueryDto>();
	
	private Consumer<List<ProblemDto>> callback;

	public AbstractAnalyzerCallable(String args) {
		this.args = args;
		this.programID = UUID.randomUUID().toString();
	}

	@Override
	public final List<ProblemDto> call() throws EnviromentException, ReportException, CompilerException {
		List<ProblemDto> result = new LinkedList<ProblemDto>();
		try {
			prepareEnviroment();
			compile();
			createReport(result);
			executeCallback(result);
		} finally {
			cleanEnviroment();
		}
		return result;
	}
	
	private void executeCallback(List<ProblemDto> result) throws ReportException {
		try {
			if (callback != null)
				callback.accept(result);
		} catch (Exception e) {
			throw new ReportException();
		}
	}
	
	public void nextStep(String step, int increment) {
		task.setStatus(step);
		task.incrementProgress(increment);
	}

	// Template methods

	protected void prepareEnviroment() throws EnviromentException {
		nextStep("Preparing enviroment...", 25);
		basePath = ToolFactory.getEnviromentTool().createEnviroment();
	}

	protected void compile() throws CompilerException {
		nextStep("Compiling...", 25);
	}

	protected void createReport(List<ProblemDto> result) throws ReportException {
		// If there are 
		if (!queries.isEmpty()) {
			nextStep("Creating report", 25);
			result.addAll(
				ToolFactory
				.getReportTool(System.getProperty("neo4j.url"), programID, queries)
				.generateReport()
			);
		} else {
			nextStep("No report will be created", 25);
		}
	}

	protected void cleanEnviroment() throws EnviromentException {
		nextStep("Cleaning enviroment", 25);
		ToolFactory.getEnviromentTool().deleteEnviroment(basePath);
	}

	// Setters

	public void setCallback(Consumer<List<ProblemDto>> callback) {
		this.callback = callback;
	}

	public void setQueries(List<QueryDto> queries) {
		this.queries = queries;
	}
	
	public void setTask(AnalyzerTask task) {
		this.task = task;
	}

	public void setCompiler(CompilerTool compiler) {
		this.compiler = compiler;
	}

	// Getters

	public String getProgramID() {
		return programID;
	}
	
}