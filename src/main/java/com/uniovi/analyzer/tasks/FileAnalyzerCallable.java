package com.uniovi.analyzer.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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
	protected void prepareEnviroment() throws IOException {
		super.prepareEnviroment();
		basePath = enviromentManager.createEnviroment();
		if (basePath == null) {
			throw new RuntimeException("The enviroment was not created");
		}

		// Coping required files
		String path = basePath + fileName;
		try (BufferedInputStream is = new BufferedInputStream(fileIs)) {
			Files.copy(is, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			task.setStatus("Cleaning enviroment...");
			enviromentManager.deleteEnviroment(basePath);
			throw ioe;
		}
	}

	@Override
	protected void compile() {
		nextStep(String.format("Compiling %s...", fileName), 25);
		compiler.compileFile(basePath, fileName, getArgs());
	}

	@Override
	protected List<CodeError> createReport() {
		super.createReport();
		ReportTool reportFactory = setupReportTool(basePath + "/neo4j/data/ProgQuery.db");
		return reportFactory.generateReport();
	}

	@Override
	protected void cleanEnviroment() throws IOException {
		super.cleanEnviroment();
		enviromentManager.deleteEnviroment(basePath);
	}

}
