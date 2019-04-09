package com.uniovi.analyzer;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerUtil {
	
	/**
	 * Compiles
	 * 
	 * @param classPath
	 * @param arguments
	 * @param dbPath
	 */
	public void compile(String classPath, String arguments, String dbPath) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String finalArguments = String.format("%s -Xplugin:ProgQueryPlugin %s", classPath, arguments);
		compiler.run(null, null, null, finalArguments);
	}

}
