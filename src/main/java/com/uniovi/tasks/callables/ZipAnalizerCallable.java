package com.uniovi.tasks.callables;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uniovi.analyzer.EnviromentManager;
import com.uniovi.analyzer.JavaCompilerUtil;
import com.uniovi.analyzer.reporter.ReportFactory;
import com.uniovi.entities.CodeError;
import com.uniovi.tasks.util.UnzipUtility;

public class ZipAnalizerCallable extends AbstractAnalyzerCallable {

	private MultipartFile uploaded;

	// Utilities
	private JavaCompilerUtil compiler = new JavaCompilerUtil();
	private EnviromentManager enviromentManager = new EnviromentManager();

	public ZipAnalizerCallable(String args, MultipartFile uploaded) {
		super(args);
		this.uploaded = uploaded;
	}

	@Override
	public List<CodeError> call() throws Exception {
		// Creating enviroment
		task.setStatus("Creating enviroment...");
		String basePath = enviromentManager.createEnviroment();
		if (basePath == null) {
			task.cancel(false);
		}

		// Coping required files
		try (InputStream is = uploaded.getInputStream()) {
			UnzipUtility.unzip(is, basePath);
		} catch (IOException ioe) {
			task.setStatus("Cleaning enviroment...");
			enviromentManager.deleteEnviroment(basePath);
			throw ioe;
		}

		// Compilation
		task.incrementProgress(25);
		task.setStatus("Compiling project...");
		compiler.compileFolder(basePath, getArgs());

		// Report creation
		task.incrementProgress(25);
		task.setStatus("Creating report...");
		ReportFactory reportFactory = setupReportFactory(basePath + "/neo4j/data/ProgQuery.db");
		List<CodeError> report = reportFactory.generateReport();

		// Cleaning enviroment
		task.incrementProgress(25);
		task.setStatus("Cleaning enviroment...");
		enviromentManager.deleteEnviroment(basePath);

		// End
		return report;
	}

}
