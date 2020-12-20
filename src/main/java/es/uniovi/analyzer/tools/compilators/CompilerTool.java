package es.uniovi.analyzer.tools.compilators;

import es.uniovi.analyzer.exceptions.CompilerException;

public interface CompilerTool {

	/**
	 * Compiles a single file
	 * @param basePath to the folder
	 * @param programID of the program to compile
	 * @param filename of the file to compile
	 * @param arguments for the java compiler
	 * @throws CompilerException
	 */
	public void compileFile(String basePath, String programID, String filename, String arguments) throws CompilerException;
	
	/**
	 * Compiles a project inside the folder
	 * @param basePath to the folder
	 * @param programID of the program to compile
	 * @param arguments for the java compiler
	 *
	 * @throws CompilerException 
	 */
	public void compileFolder(String basePath, String programID, String arguments) throws CompilerException;
	
}
