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

	private final static String PLUGIN_CLASSPATH_UNIX = ".:plugin/ProgQuery.jar";
	private final static String PLUGIN_CLASSPATH_WIN = "./plugin/ProgQuery.jar";
	
	private final static String ENCODING = "ISO-8859-1";
	private final static String PLUGIN_ARG = "-Xplugin:ProgQueryPlugin %s S %s";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public void compileFile(String basePath, String filename, String arguments) throws CompilerException {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, "test");
		// Extra arguments
		addArgumentsFromString(args, arguments);
		// All files
		args.add(filename);
		// Compilation
		compile(compiler, args);
	}

	public void compileFolder(String basePath, String programID, String arguments) throws CompilerException {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, programID);
		// Extra arguments
		if (arguments != null)
			addArgumentsFromString(args, arguments);
		// All files
		generateSourcesFile(basePath);
		// Compilation
		compile(compiler, args);
	}
	
	/**
	 * Launch the compiler
	 * @param compiler
	 * @param args
	 * @throws CompilerException
	 */
	private void compile(JavaCompiler compiler,List<String> args) throws CompilerException {
		sanitizeArguments(args);
		String argsArray[] = new String[args.size()];
		args.toArray(argsArray);
		logger.info("Executing compilation command: javac {}", String.join(" ", args));
		if(compiler.run(null, null, null, argsArray) != 0) {
			throw new CompilerException();
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
			System.setProperty("java.home", System.getenv("JAVA_HOME"));
			return ToolProvider.getSystemJavaCompiler();
		}
		return compiler;
	}

	private List<String> basicArgs(String basePath, String programID) {
		String pluginClasspath = 
				System.getProperty("os.name").matches("Win.*") ? 
						PLUGIN_CLASSPATH_WIN : 
						PLUGIN_CLASSPATH_UNIX;
		return new ArrayList<>(
				Arrays.asList("-cp", pluginClasspath, 
						"-encoding", ENCODING,
						String.format(PLUGIN_ARG, programID, System.getProperty("neo4j.url")), 
						"-d", basePath, "-nowarn", "-g:none", 
						"-Xlint:none", "@" + basePath + "sources.txt"));
	}

	private void addArgumentsFromString(List<String> args, String extraArgs) {
		Arrays.asList(extraArgs.split(" ")).stream()
			.map((arg) -> arg.trim())
			.filter((arg) -> !arg.isEmpty())
			.forEach((arg) -> args.add(arg));
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
