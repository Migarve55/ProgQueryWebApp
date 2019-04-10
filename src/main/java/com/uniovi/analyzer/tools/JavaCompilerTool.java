package com.uniovi.analyzer.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerTool {

	private final static String PLUGIN_CLASSPATH = "src/main/resources/plugin/ProgQuery.jar;src/main/resources/plugin/neo4jLibs/*;";
	private final static String ENCODING = "utf8";
	private final static String PLUGIN_ARG = "-Xplugin:ProgQueryPlugin %s";
	public final static String DB_PATH = "neo4j/data/ProgQuery.db";

	/**
	 * 
	 * @param basePath
	 * @param extraClassPath
	 * @param filename
	 * @param arguments
	 * @return if it has compiled everything ok
	 */
	public boolean compileFile(String basePath, String extraClassPath, String filename, String arguments) {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, extraClassPath);
		// Extra arguments
		addArgumentsFromString(args, arguments);
		// All files
		args.add(filename);
		// Compilation
		String argsArray[] = new String[args.size()];
		args.toArray(argsArray);
		return compiler.run(null, null, null, argsArray) == 0;
	}

	/**
	 * 
	 * @param basePath
	 * @param extraClassPath
	 * @param arguments
	 * @return if it has compiled everything ok
	 */
	public boolean compileFolder(String basePath, String extraClassPath, String arguments) {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, extraClassPath);
		// Extra arguments
		addArgumentsFromString(args, arguments);
		// All files
		args.addAll(getAllJavaSourceFilesInFolder(basePath));
		// Compilation
		String argsArray[] = new String[args.size()];
		args.toArray(argsArray);
		return compiler.run(null, null, null, argsArray) == 0;
	}

	private JavaCompiler getCompiler() {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			System.setProperty("java.home", System.getenv("JAVA_HOME"));
			return ToolProvider.getSystemJavaCompiler();
		}
		return compiler;
	}

	private List<String> basicArgs(String basePath, String extraClassPath) {
		return new ArrayList<>(
				Arrays.asList("-cp", PLUGIN_CLASSPATH + extraClassPath.trim(), 
						"-encoding", ENCODING,
						String.format(PLUGIN_ARG, basePath + DB_PATH), 
						"-d", basePath, "-nowarn", "-g:none", "-Xlint:none"));
	}

	private void addArgumentsFromString(List<String> args, String extraArgs) {
		Arrays.asList(extraArgs.split(" ")).stream().map((arg) -> arg.trim()).filter((arg) -> !arg.isEmpty())
				.forEach((arg) -> args.add(arg));
	}

	private List<String> getAllJavaSourceFilesInFolder(String folder) {
		File baseFolder = new File(folder);
		List<String> files = new ArrayList<String>();
		addJavaFiles(files, baseFolder);
		return files;
	}

	private void addJavaFiles(List<String> files, File folder) {
		for (File file : folder.listFiles()) {
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
