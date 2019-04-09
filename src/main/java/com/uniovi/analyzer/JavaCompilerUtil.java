package com.uniovi.analyzer;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerUtil {
	
	private final static String PLUGIN_CLASSPATH = "-cp \\src\\main\\resources\\plugin\\ProgQuery.jar;\\src\\main\\resources\\plugin\\neo4jLibs\\*;";
	private final static String PLUGIN_ARG = "-Xplugin:ProgQueryPlugin";
	
	/**
	 * Compiles everything inside the enviroment
	 * 
	 * @param basePath
	 * @param arguments
	 */
	public void compile(String basePath, String arguments) {
		JavaCompiler compiler = getCompiler();
		String outputPathArg = String.format("-d %sout/", basePath); 
		compiler.run(null, null, null, PLUGIN_CLASSPATH, PLUGIN_ARG, 
				outputPathArg, basePath, 
				arguments == null ? "" : arguments);
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
