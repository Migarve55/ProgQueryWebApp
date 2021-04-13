package es.uniovi.analyzer.callables;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.exceptions.ReportException;
import es.uniovi.analyzer.tools.ToolFactory;
import es.uniovi.analyzer.tools.compilators.CompilerTool;
import es.uniovi.analyzer.tools.reporter.dto.QueryDto;
import es.uniovi.analyzer.tools.reporter.dto.ResultDto;

public abstract class AbstractAnalyzerCallable implements Callable<ResultDto> {
	
	private int progress = 0;
	private String status = "In progress...";
	
	protected String classpath;
	protected String basePath;
	protected String programID;
	protected String userId;
	protected CompilerTool compiler;
	protected List<QueryDto> queries = new ArrayList<QueryDto>();
	
	protected ResultDto result;
	
	private Consumer<ResultDto> callback;

	public AbstractAnalyzerCallable(String classpath, String programId, String userId) {
		this.classpath = classpath;
		this.programID = programId;
		this.userId = userId;
	}

	@Override
	public final ResultDto call() throws EnviromentException, ReportException, CompilerException {
		ResultDto result = null;
		try {
			prepareEnviroment();
			compile();
			createReport();
			executeCallback();
		} finally {
			cleanEnviroment();
		}
		return result;
	}
	
	private void executeCallback() throws ReportException {
		try {
			if (callback != null)
				callback.accept(this.result);
		} catch (Exception e) {
			throw new ReportException();
		}
	}
	
	public void nextStep(String step, int increment) {
		setStatus(step);
		incrementProgress(increment);
	}

	// Template methods

	protected void prepareEnviroment() throws EnviromentException {
		nextStep("Preparing enviroment...", 25);
		basePath = ToolFactory.getEnviromentTool().createEnviroment();
	}

	protected void compile() throws CompilerException {
		nextStep("Compiling...", 25);
	}

	protected void createReport() throws ReportException {
		if (!queries.isEmpty()) {
			nextStep("Creating report", 25);
			this.result = ToolFactory
				.getNeo4jTool(System.getProperty("neo4j.url"), programID, queries)
				.generateReport();
		} else {
			nextStep("No report will be created", 25);
		}
	}

	public void cleanEnviroment() throws EnviromentException {
		nextStep("Cleaning enviroment", 25);
		ToolFactory.getEnviromentTool().deleteEnviroment(basePath);
	}
	
	private ByteArrayOutputStream errStream = new ByteArrayOutputStream();
	
	public String getRecordedOutput() {
		return errStream.toString(Charset.defaultCharset());
	}
	
	protected OutputStream getOutputStremRecorder() {
		return errStream;
	}

	// Getters and Setters

	public void setCallback(Consumer<ResultDto> callback) {
		this.callback = callback;
	}

	public void setQueries(List<QueryDto> queries) {
		this.queries = queries;
	}

	public void setCompiler(CompilerTool compiler) {
		this.compiler = compiler;
	}

	public String getProgramID() {
		return programID;
	}
	
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public void incrementProgress(int increment) {
		this.progress = Math.min(this.progress + increment, 100);
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
}