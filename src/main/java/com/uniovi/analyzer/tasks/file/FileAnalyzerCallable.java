package com.uniovi.analyzer.tasks.file;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.uniovi.analyzer.exceptions.CompilerException;
import com.uniovi.analyzer.exceptions.EnviromentException;
import com.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import com.uniovi.analyzer.tools.ToolFactory;

public class FileAnalyzerCallable extends AbstractAnalyzerCallable {

	private InputStream fileIs;
	private String fileName;

	public FileAnalyzerCallable(String args, String fileName, InputStream fileIs) {
		super(args);
		this.fileName = fileName;
		this.fileIs = fileIs;
	}

	@Override
	protected void prepareEnviroment() throws EnviromentException {
		super.prepareEnviroment();
		// Coping required files
		String path = basePath + fileName;
		try (BufferedInputStream is = new BufferedInputStream(fileIs)) {
			Files.copy(is, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			throw new EnviromentException("error.enviroment.io");
		}
	}

	@Override
	protected void compile() throws CompilerException {
		nextStep(String.format("Compiling %s...", fileName), 25);
		ToolFactory.getJavaCompilerTool().compileFile(basePath, "", fileName, args);
	}

}
