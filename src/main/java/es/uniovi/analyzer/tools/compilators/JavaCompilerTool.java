package es.uniovi.analyzer.tools.compilators;

import java.io.File;
import java.io.IOException;
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

public class JavaCompilerTool implements CompilerTool {

	private final static String PLUGIN_CLASSPATH = "./src/main/resources/plugin/ProgQuery.jar;./src/main/resources/plugin/neo4jLibs/*;";
	private final static String ENCODING = "ISO-8859-1";
	private final static String PLUGIN_ARG = "-Xplugin:ProgQueryPlugin %s";
	public final static String DB_PATH = "neo4j/data/ProgQuery.db";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 * @param basePath
	 * @param extraClassPath
	 * @param filename
	 * @param arguments
	 * @return if it has compiled everything ok
	 * @throws CompilerException 
	 */
	public void compileFile(String basePath, String extraClassPath, String filename, String arguments) throws CompilerException {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, extraClassPath);
		// Extra arguments
		addArgumentsFromString(args, arguments);
		// All files
		args.add(filename);
		// Compilation
		compile(compiler, args);
	}

	public void compileFolder(String basePath, String extraClassPath, String arguments) throws CompilerException {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, extraClassPath);
		// Extra arguments
		if (arguments != null)
			addArgumentsFromString(args, arguments);
		// All files
		generateSourcesFile(basePath);
		// Compilation
		compile(compiler, args);
	}
	
	private void compile(JavaCompiler compiler,List<String> args) throws CompilerException {
		String argsArray[] = new String[args.size()];
		args.toArray(argsArray);
		logger.info("Executing compilation command: javac {}", String.join(" ", args));
		if(compiler.run(null, null, null, argsArray) != 0) {
			throw new CompilerException();
		}
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
		String ecp = extraClassPath.trim();
		String extraClassPathLocal = "";
		if (!ecp.isEmpty()) {
			extraClassPathLocal = "./" + basePath + ecp + ";";
		}
		return new ArrayList<>(
				Arrays.asList("-cp", PLUGIN_CLASSPATH + extraClassPathLocal, 
						"-encoding", ENCODING,
						String.format(PLUGIN_ARG, basePath + DB_PATH), 
						"-d", basePath, "-nowarn", "-g:none", 
						"-Xlint:none", "@" + basePath + "sources.txt"));
	}

	private void addArgumentsFromString(List<String> args, String extraArgs) {
		Arrays.asList(extraArgs.split(" ")).stream()
			.map((arg) -> arg.trim())
			.filter((arg) -> !arg.isEmpty())
			.forEach((arg) -> args.add(arg));
	}

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
