package com.uniovi.analyzer.tasks;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.uniovi.analyzer.exceptions.CompilerException;
import com.uniovi.analyzer.exceptions.EnviromentException;
import com.uniovi.analyzer.exceptions.ReportException;
import com.uniovi.analyzer.tools.EnviromentManagerTool;
import com.uniovi.analyzer.tools.JavaCompilerTool;
import com.uniovi.analyzer.tools.reporter.ReportTool;
import com.uniovi.entities.CodeError;

public class GithubCodeAnalyzerCallable extends AbstractAnalyzerCallable {

	private String url;
	private String basePath;

	// Utilities
	private JavaCompilerTool compiler = new JavaCompilerTool();
	private EnviromentManagerTool enviromentManager = new EnviromentManagerTool();
	
	public GithubCodeAnalyzerCallable(String url, String args) {
		super(args);
		this.url = url;
	}
	
	@Override
	protected void prepareEnviroment() throws EnviromentException {
		nextStep("Creating enviroment", 10);
		basePath = enviromentManager.createEnviroment();
		if (basePath == null) {
			throw new RuntimeException("The enviroment was not created");
		}

		// Download URL
		nextStep(String.format("Downloading repository from %s...", url), 15);
		try {
			Git.cloneRepository()
			  .setURI(url)
			  .setDirectory(new File(basePath))
			  .call();
		} catch (GitAPIException e) {
			throw new EnviromentException(e);
		}
		
	}

	@Override
	protected void compile() throws CompilerException {
		super.compile();
		if(!compiler.compileFolder(basePath, "", getArgs())) {
			throw new RuntimeException("Could not compile");
		}
	}

	@Override
	protected List<CodeError> createReport() throws ReportException {
		super.createReport();
		ReportTool reportFactory = setupReportTool(basePath + "/neo4j/data/ProgQuery.db");
		return reportFactory.generateReport();
	}

	@Override
	protected void cleanEnviroment() throws EnviromentException {
		super.cleanEnviroment();
		enviromentManager.deleteEnviroment(basePath);
	}
	
}
