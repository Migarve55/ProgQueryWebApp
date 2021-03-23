package es.uniovi.analyzer.tools.compilators;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractCompiler implements CompilerTool {

	private final static String PLUGIN_ARG_TEMPLATE = "-Xplugin:ProgQueryPlugin %s;%s S %s;%s;%s";
	
	public StringBuilder sb = new StringBuilder("");
	
	public String getRecordedOutput() {
		return sb.toString();
	}
	
	protected String getPluginArg(String programId, String userId) {
		return String.format(PLUGIN_ARG_TEMPLATE, 
				programId,
				userId,
				System.getProperty("neo4j.user", "neo4j"),
				System.getProperty("neo4j.password", "neo4j"),
				System.getProperty("neo4j.url", "bolt://localhost:7687"));
	}
	
	// HIDE_COMPILER_OUTPUT
	protected boolean shouldHideCompilerOutput() {
		return System.getenv("HIDE_COMPILER_OUTPUT") != null;
	}
	
	// SHOW_DEBUG_OUTPUT
	protected boolean shouldShowDebugOutput() {
		return System.getenv("SHOW_DEBUG_OUTPUT") != null;
	}
	
	protected OutputStream getOutputStremRecorder() {
		sb = new StringBuilder("");
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				System.out.print(b);
				sb.append(b);
			}
		};
	}

}
