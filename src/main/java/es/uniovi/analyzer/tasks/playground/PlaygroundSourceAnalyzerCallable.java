package es.uniovi.analyzer.tasks.playground;

import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import es.uniovi.analyzer.tools.ToolFactory;

public class PlaygroundSourceAnalyzerCallable extends AbstractAnalyzerCallable {
	
	private String source;
	private String javaSourceFilename;
	
	public PlaygroundSourceAnalyzerCallable(String source, String userId) {
		super(null, userId);
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
		compiler.compileFile(basePath, programID, userId, javaSourceFilename);
	}
	
	@Override
	public boolean isPlayground() {
		return true;
	}
	
	private String getJavaFilename(String source) {
		String src = source.trim();
		if (!src.startsWith("public"))
			return "source.java";
		// Encontrar nombre de la clase
		int startIndex = src.indexOf("class");
		int endIndex = src.indexOf("{");
		String name = src.substring(startIndex + 5, endIndex).trim();
		return name + ".java";
	}
	
}
