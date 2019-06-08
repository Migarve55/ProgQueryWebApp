package es.uniovi.analyzer.tasks;

import java.util.List;
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
	protected AnalyzerTask task;
	protected CompilerTool compiler;
	
	private Consumer<List<ProblemDto>> callback;
	private List<QueryDto> queries;

	public AbstractAnalyzerCallable(String args) {
		this.args = args;
	}
	
	public AbstractAnalyzerCallable(String args, Consumer<List<ProblemDto>> callback) {
		this(args);
		this.callback = callback;
	}

	@Override
	public final List<ProblemDto> call() throws EnviromentException, ReportException, CompilerException {
		List<ProblemDto> result = null;
		try {
			prepareEnviroment();
			compile();
			result = createReport();
			if (callback != null)
				callback.accept(result);
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
	}

	protected void compile() throws CompilerException {
		nextStep("Compiling...", 25);
	}

	protected List<ProblemDto> createReport() throws ReportException {
		nextStep("Creating report", 25);
		String dbPath = basePath + "/neo4j/data/ProgQuery.db";
		return ToolFactory.getReportTool(dbPath, queries).generateReport();
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

}
