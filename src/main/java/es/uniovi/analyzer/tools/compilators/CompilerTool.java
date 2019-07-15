package es.uniovi.analyzer.tools.compilators;

import es.uniovi.analyzer.exceptions.CompilerException;

public interface CompilerTool {

	/**
	 * Compiles a single file
	 * @param basePath path to the folder
	 * @param programID the id of the program to compile
	 * @param filename the file to compile
	 * @param arguments extra arguments for javac
	 * @throws CompilerException
	 */
	public void compileFile(String basePath, String programID, String filename, String arguments) throws CompilerException;
	
	/**
	 * Compiles a project inside the folder
	 * @param basePath path to the folder
	 * @param programID the id of the program to compile
	 * @param arguments extra arguments for javac
	 *
	 * @throws CompilerException 
	 */
	public void compileFolder(String basePath, String programID, String arguments) throws CompilerException;
	
}
