package es.uniovi.analyzer.tools.compilators;

import es.uniovi.analyzer.exceptions.CompilerException;

public interface CompilerTool {

	/**
	 * Compiles a single file
	 * @param basePath to the folder
	 * @param programID of the program to compile
	 * @param userId user universal identifier
	 * @param filename of the file to compile
	 * @throws CompilerException
	 */
	public void compileFile(String basePath, String programID, String userId, String filename) throws CompilerException;
	
	/**
	 * Compiles a project inside the folder
	 * @param basePath to the folder
	 * @param programID of the program to compile
	 * @param userId user universal identifier
	 * @param classpath Classpath (optional)
	 *
	 * @throws CompilerException 
	 */
	public void compileFolder(String basePath, String programID, String userId, String classpath) throws CompilerException;
	
}
