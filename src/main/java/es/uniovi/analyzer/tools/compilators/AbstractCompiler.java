package es.uniovi.analyzer.tools.compilators;

public abstract class AbstractCompiler implements CompilerTool {

	private final static String PLUGIN_ARG_TEMPLATE = "-Xplugin:ProgQueryPlugin %s S %s;%s;%s";
	
	protected String getPluginArg(String programId) {
		return String.format(PLUGIN_ARG_TEMPLATE, 
				programId,
				System.getProperty("neo4j.user", "neo4j"),
				System.getProperty("neo4j.password", "neo4j"),
				System.getProperty("neo4j.url", "bolt://localhost:7687"));
	}

}
