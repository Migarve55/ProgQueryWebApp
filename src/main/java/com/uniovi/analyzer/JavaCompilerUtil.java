package com.uniovi.analyzer;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerUtil {
	
	private final static String PLUGIN_CLASSPATH = "src/main/resources/plugin/ProgQuery.jar;src/main/resources/plugin/neo4jLibs/*;";
	private final static String PLUGIN_ARG = "-Xplugin:ProgQueryPlugin";
	
	/**
	 * Compiles everything inside the enviroment
	 * 
	 * @param basePath
	 * @param fileName
	 * @param arguments
	 */
	public void compileFile(String basePath, String filename, String arguments) {
		JavaCompiler compiler = getCompiler();
		compiler.run(null, null, null, 
				"-cp", PLUGIN_CLASSPATH, 
				PLUGIN_ARG, 
				"-d", basePath,
				basePath + filename);
	}
	
	private JavaCompiler getCompiler() {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_201");
			return ToolProvider.getSystemJavaCompiler();
		}
		return compiler;
	}

}
