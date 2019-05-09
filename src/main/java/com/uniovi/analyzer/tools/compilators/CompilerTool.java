package com.uniovi.analyzer.tools.compilators;

import com.uniovi.analyzer.exceptions.CompilerException;

public interface CompilerTool {

	/**
	 * 
	 * @param basePath
	 * @param extraClassPath
	 * @param arguments
	 * @return if it has compiled everything ok
	 * @throws CompilerException 
	 */
	public void compileFolder(String basePath, String extraClassPath, String arguments) throws CompilerException;
	
}
