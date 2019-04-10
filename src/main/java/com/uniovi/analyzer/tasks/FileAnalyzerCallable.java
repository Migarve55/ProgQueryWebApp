package com.uniovi.analyzer.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.uniovi.analyzer.exceptions.EnviromentException;
import com.uniovi.analyzer.exceptions.ReportException;
import com.uniovi.analyzer.tools.EnviromentManagerTool;
import com.uniovi.analyzer.tools.JavaCompilerTool;
import com.uniovi.analyzer.tools.reporter.ReportTool;
import com.uniovi.entities.CodeError;

public class FileAnalyzerCallable extends AbstractAnalyzerCallable {

	private InputStream fileIs;
	private String fileName;
	private String basePath;

	// Utilities
	private JavaCompilerTool compiler = new JavaCompilerTool();
	private EnviromentManagerTool enviromentManager = new EnviromentManagerTool();

	public FileAnalyzerCallable(String args, String fileName, InputStream fileIs) {
		super(args);
		this.fileName = fileName;
		this.fileIs = fileIs;
	}

	@Override
	protected void prepareEnviroment() throws EnviromentException {
		super.prepareEnviroment();
		basePath = enviromentManager.createEnviroment();
		if (basePath == null) {
			throw new EnviromentException();
		}

		// Coping required files
		String path = basePath + fileName;
		try (BufferedInputStream is = new BufferedInputStream(fileIs)) {
			Files.copy(is, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			task.setStatus("Cleaning enviroment...");
			enviromentManager.deleteEnviroment(basePath);
			throw new EnviromentException(ioe);
		}
	}

	@Override
	protected void compile() {
		nextStep(String.format("Compiling %s...", fileName), 25);
		if(!compiler.compileFile(basePath, "", fileName, getArgs())) {
			throw new RuntimeException("Could not compile");
		}
	}

	@Override
	protected List<CodeError> createReport() throws ReportException {
		super.createReport();
		ReportTool reportFactory = setupReportTool(basePath + JavaCompilerTool.DB_PATH);
		return reportFactory.generateReport();
	}

	@Override
	protected void cleanEnviroment() throws EnviromentException {
		super.cleanEnviroment();
		enviromentManager.deleteEnviroment(basePath);
	}

}
