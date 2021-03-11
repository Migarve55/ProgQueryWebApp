package es.uniovi.analyzer.tasks.playground;

import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import es.uniovi.analyzer.tools.ToolFactory;

public class PlaygroundSourceAnalyzerCallable extends AbstractAnalyzerCallable {

	private final static String JAVA_SOURCE_FILENAME = "source.java";
	
	private String source;
	
	public PlaygroundSourceAnalyzerCallable(String source) {
		super(null);
		this.source = source;
	}
	
	protected void prepareEnviroment() throws EnviromentException {
		super.prepareEnviroment();
		// Create source file
		ToolFactory.getEnviromentTool().createSourceFile(basePath + "/" + JAVA_SOURCE_FILENAME, source);
	}

	protected void compile() throws CompilerException {
		super.compile();
		compiler.compileFile(basePath, programID, JAVA_SOURCE_FILENAME);
	}
	
}
