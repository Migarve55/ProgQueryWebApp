package es.uniovi.analyzer.tools.compilators;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uniovi.analyzer.exceptions.CompilerException;

public class JavaCompilerTool extends AbstractCompiler {
	
	private final static String ENCODING = "ISO-8859-1";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void compileFile(String basePath, String programID, String userId, String filename, OutputStream errStream) throws CompilerException {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, programID, userId, "");
		// Add file
		args.add(basePath + filename);
		// Compilation
		logger.info("Compiling program file {} using java", programID);
		compile(compiler, args, errStream);
	}

	@Override
	public void compileFolder(String basePath, String programID, String userId, String classpath, OutputStream errStream) throws CompilerException {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, programID, userId, classpath);
		if (shouldShowDebugOutput())
			args.add("-g");
		// Add files
		generateSourcesFile(basePath);
		args.add("@" + basePath + "sources.txt");
		// Compilation
		logger.info("Compiling program folder {} using java", programID);
		compile(compiler, args, errStream);
	}
	
	/**
	 * Launch the compiler
	 * @param compiler
	 * @param args
	 * @throws CompilerException
	 */
	private void compile(JavaCompiler compiler,List<String> args, OutputStream errStream) throws CompilerException {
		sanitizeArguments(args);
		String argsArray[] = new String[args.size()];
		args.toArray(argsArray);
		logger.info("Executing compilation command: javac {}", String.join(" ", args));
		if(compiler.run(null, System.out, errStream, argsArray) != 0) {
			throw new CompilerException("error.compiler.java");
		}
	}
	
	/**
	 * Removes from the list anything that can cause a command injection
	 * @param args the list of arguments
	 */
	private void sanitizeArguments(List<String> args) {
		args.remove(";");
		args.remove("&&");
	}

	private JavaCompiler getCompiler() {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			String javaHome = System.getenv("JAVA_HOME");
			logger.info("java.home property was not set, now its: " + javaHome);
			System.setProperty("java.home", javaHome);
			return ToolProvider.getSystemJavaCompiler();
		}
		return compiler;
	}

	private List<String> basicArgs(String basePath, String programID, String userId, String classpath) {
		return new ArrayList<>(
				Arrays.asList(
						"-classpath", getClassPath(basePath, classpath),
						"-sourcepath", basePath,
						"-d", basePath,
						"-encoding", ENCODING,
						String.format(getPluginArg(programID, userId), programID, System.getProperty("neo4j.url")), 
						"-nowarn", "-g"));
	}

	/**
	 * Transforms a classpath into a proper modifing the jars base path
	 * Example: libs/A.jar -> basePath/libs/A.jar
	 * @param basePath
	 * @param classpath
	 * @return
	 */
	private String getClassPath(String basePath, String classpath) {
		String[] parts = classpath.split(";");
		StringBuilder sb = new StringBuilder();
		for (String p : parts) {
			if (p.trim().isEmpty())
				continue;
			sb.append(String.format("%s%s;", basePath, p));
		}
		String pluginClasspath = System.getenv("PLUGIN_CLASSPATH");
		String additionalClasspath = sb.toString();
		if (additionalClasspath.isEmpty()) {
			return pluginClasspath;
		} else {
			return pluginClasspath + ";" + additionalClasspath;
		}
	}

	/**
	 * Generates a java source file for the project
	 * @param folder
	 */
	private void generateSourcesFile(String folder) {
		File baseFolder = new File(folder);
		List<String> files = new ArrayList<String>();
		addJavaFiles(files, baseFolder);
		File sourcesFile = new File(folder + "sources.txt");
		try {
			sourcesFile.createNewFile();
			Files.write(sourcesFile.toPath(), files, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
