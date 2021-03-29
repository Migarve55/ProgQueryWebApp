package es.uniovi.analyzer.tools.compilators;

public abstract class AbstractCompiler implements CompilerTool {

	private final static String PLUGIN_ARG_TEMPLATE = "-Xplugin:ProgQueryPlugin %s;%s S %s;%s;%s";
	
	protected String getPluginArg(String programId, String userId) {
		return String.format(PLUGIN_ARG_TEMPLATE, 
				programId,
				userId,
				System.getProperty("neo4j.user", "neo4j"),
				System.getProperty("neo4j.password", "neo4j"),
				System.getProperty("neo4j.url", "bolt://localhost:7687"));
	}
	
	// SHOW_DEBUG_OUTPUT
	protected boolean shouldShowDebugOutput() {
		return System.getenv("SHOW_DEBUG_OUTPUT") != null;
	}

}
