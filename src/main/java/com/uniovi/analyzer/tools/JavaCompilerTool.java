package com.uniovi.analyzer.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerTool {

	private final static String PLUGIN_CLASSPATH = "src/main/resources/plugin/ProgQuery.jar;src/main/resources/plugin/neo4jLibs/*;";
	private final static String PLUGIN_ARG = "-Xplugin:\"ProgQueryPlugin %s\"";
	public final static String DB_PATH = "neo4j/ProgQuery.db";

	/**
	 * Compiles everything a file
	 * 
	 * @param basePath
	 * @param fileName
	 * @param arguments
	 */
	public void compileFile(String basePath, String filename, String arguments) {
		JavaCompiler compiler = getCompiler();
		//Basic config
		List<String> args = Arrays.asList(
				"-cp", PLUGIN_CLASSPATH, 
				String.format(PLUGIN_ARG, basePath + DB_PATH), 
				"-d", basePath);
		//Extra arguments
		addArgumentsFromString(args, arguments);
		//Add file
		args.add(basePath + filename);
		//Compilation
		compiler.run(null, null, null, (String[]) args.toArray());
	}

	/**
	 * Compiles everything a file
	 * 
	 * @param basePath
	 * @param arguments
	 */
	public void compileFolder(String basePath, String arguments) {
		JavaCompiler compiler = getCompiler();
		//Basic config
		List<String> args = new ArrayList<>(Arrays.asList(
				"-cp", PLUGIN_CLASSPATH, 
				String.format(PLUGIN_ARG, basePath + DB_PATH), 
				"-d", basePath));
		//Extra arguments
		addArgumentsFromString(args, arguments);
		//All files
		args.addAll(getAllJavaSourceFilesInFolder(basePath));
		//Compilation
		String argsArray[] = new String[args.size()];
		args.toArray(argsArray);
		compiler.run(null, null, null, argsArray);
	}

	private JavaCompiler getCompiler() {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			System.setProperty("java.home", System.getenv("JAVA_HOME"));
			return ToolProvider.getSystemJavaCompiler();
		}
		return compiler;
	}
	
	private void addArgumentsFromString(List<String> args, String extraArgs) {
		Arrays.asList(extraArgs.split(" ")).stream()
			.map((arg) -> arg.trim())
			.filter((arg) -> !arg.isEmpty())
			.forEach((arg) -> args.add(arg));
	}
	
	private List<String> getAllJavaSourceFilesInFolder(String folder) {
		File baseFolder = new File(folder);
		List<String> files = new ArrayList<String>();
		addJavaFiles(files, baseFolder);
		return files;
	}
	
	private void addJavaFiles(List<String> files, File folder) {
		for (File file: folder.listFiles()) {
			if (file.isDirectory()) {
				addJavaFiles(files, file);
			} else {
				if (file.getName().matches(".*\\.java")) {
					files.add(file.getAbsolutePath());
				}
			}
		}
	}

}
