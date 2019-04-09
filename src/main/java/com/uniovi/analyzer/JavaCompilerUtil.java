package com.uniovi.analyzer;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerUtil {
	
	private final static String PLUGIN_CLASSPATH = "-cp /src/main/resources/plugin/ProgQuery.jar;/src/main/resources/plugin/neo4jLibs/*;";
	private final static String PLUGIN_ARG = "-Xplugin:ProgQueryPlugin";
	
	/**
	 * Compiles everything inside the enviroment
	 * 
	 * @param basePath
	 * @param arguments
	 */
	public void compile(String basePath, String arguments) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String outputPathArg = String.format("-d %sout/", basePath); 
		compiler.run(null, null, null, PLUGIN_CLASSPATH, PLUGIN_ARG, outputPathArg, basePath);
	}

}
