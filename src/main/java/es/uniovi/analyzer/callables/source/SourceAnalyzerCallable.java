package es.uniovi.analyzer.callables.source;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;
import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.tools.ToolFactory;

public class SourceAnalyzerCallable extends AbstractAnalyzerCallable {
	
	private String source;
	private String javaSourceFilename;
	
	public SourceAnalyzerCallable(String source, String programId, String userId) {
		super(null, programId, userId);
		this.source = source;
		this.javaSourceFilename = getJavaFilename(source);
	}
	
	protected void prepareEnviroment() throws EnviromentException {
		super.prepareEnviroment();
		// Create source file
		ToolFactory.getEnviromentTool().createSourceFile(basePath + "/" + javaSourceFilename, source);
	}

	protected void compile() throws CompilerException {
		super.compile();
		compiler.compileFile(basePath, programID, userId, javaSourceFilename, getOutputStremRecorder());
	}
	
	private String getJavaFilename(String source) {
		String name = getClassNameFromCode(source);
		return name + ".java";
	}
	
	private String getClassNameFromCode(String code) {
	    String[] splitted = code.split("(\\s+|[{])");
	    for (int i = 0; i < splitted.length - 3; i++)
	        if (splitted[i].contentEquals("public")) {
	            if (splitted[i + 1].contentEquals("class"))
	                return splitted[i + 2];
	            if (splitted[i + 2].contentEquals("class"))
	                return splitted[i + 3];
	        }
	    return "source";
	}
	
}
