package com.uniovi.analyzer.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.uniovi.analyzer.tools.EnviromentManagerTool;
import com.uniovi.analyzer.tools.JavaCompilerTool;
import com.uniovi.analyzer.tools.reporter.ReportTool;
import com.uniovi.entities.CodeError;

public class ZipAnalizerCallable extends AbstractAnalyzerCallable {

	private InputStream fileIs;
	private String basePath;

	// Utilities
	private JavaCompilerTool compiler = new JavaCompilerTool();
	private EnviromentManagerTool enviromentManager = new EnviromentManagerTool();

	public ZipAnalizerCallable(String args, InputStream fileIs) {
		super(args);
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
		try (BufferedInputStream is = new BufferedInputStream(fileIs)) {
			UnzipUtility.unzip(is, basePath);
		} catch (IOException ioe) {
			task.setStatus("Cleaning enviroment...");
			enviromentManager.deleteEnviroment(basePath);
			throw ioe;
		}
	}

	@Override
	protected void compile() {
		super.compile();
		if(!compiler.compileFolder(basePath, "", getArgs())) {
			throw new RuntimeException("Could not compile");
		}
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
