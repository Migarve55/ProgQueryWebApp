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
	public void compileFile(String basePath, String programID, String filename, String arguments) throws CompilerException {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, programID);
		// Extra arguments
		if (arguments != null)
			addArgumentsFromString(args, arguments);
		// Add file
		args.add(basePath + filename);
		// Compilation
		compile(compiler, args);
	}

	@Override
	public void compileFolder(String basePath, String programID, String arguments) throws CompilerException {
		JavaCompiler compiler = getCompiler();
		// Basic config
		List<String> args = basicArgs(basePath, programID);
		if (shouldShowDebugOutput())
			args.add("-g");
		// Extra arguments
		if (arguments != null)
			addArgumentsFromString(args, arguments);
		// Add files
		generateSourcesFile(basePath);
		args.add("@" + basePath + "sources.txt");
		// Compilation
		logger.info("Compiling program {} using java", programID);
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
		OutputStream stream = System.out;
		if (shouldHideCompilerOutput()) {
			stream = new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					//Do nothing
				}
			};
		}
		logger.info("Executing compilation command: javac {}", String.join(" ", args));
		if(compiler.run(null, stream, stream, argsArray) != 0) {
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
		return new ArrayList<>(
				Arrays.asList(
						"-cp", System.getenv("PLUGIN_CLASSPATH"),
						"-encoding", ENCODING,
						String.format(getPluginArg(programID), programID, System.getProperty("neo4j.url")), 
						"-d", basePath, "-nowarn", "-g:none", "-Xlint:none"));
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
