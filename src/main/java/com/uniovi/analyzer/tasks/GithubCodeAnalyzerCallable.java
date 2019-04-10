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
import com.uniovi.analyzer.tools.reporter.CodeError;
import com.uniovi.analyzer.tools.reporter.ReportTool;

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
			throw new EnviromentException();
		}

		// Download URL
		nextStep(String.format("Downloading repository from %s...", url), 15);
		
		Git result = null;
		try {
			result = Git.cloneRepository()
			  .setURI(url)
			  .setDirectory(new File(basePath))
			  .call();
		} catch (GitAPIException e) {
			throw new EnviromentException(e);
		} finally {
			result.getRepository().close();
			result.close();
		}
		
	}

	@Override
	protected void compile() throws CompilerException {
		super.compile();
		if(!compiler.compileFolder(basePath, "", getArgs())) {
			throw new CompilerException();
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
