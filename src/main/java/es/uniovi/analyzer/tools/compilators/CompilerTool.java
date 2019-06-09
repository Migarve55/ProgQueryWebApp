package es.uniovi.analyzer.tools.compilators;

import es.uniovi.analyzer.exceptions.CompilerException;

public interface CompilerTool {

	/**
	 * Compiles a project inside the folder
	 * @param basePath path the the folder
	 * @param arguments extra arguments for javac
	 *
	 * @throws CompilerException 
	 */
	public void compileFolder(String basePath, String arguments) throws CompilerException;
	
}
