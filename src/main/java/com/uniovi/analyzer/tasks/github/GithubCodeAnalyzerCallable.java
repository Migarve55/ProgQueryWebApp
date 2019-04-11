package com.uniovi.analyzer.tasks.github;

import java.io.File;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.uniovi.analyzer.exceptions.CompilerException;
import com.uniovi.analyzer.exceptions.EnviromentException;
import com.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import com.uniovi.analyzer.tools.ToolFactory;

public class GithubCodeAnalyzerCallable extends AbstractAnalyzerCallable {

	private String url;
	
	public GithubCodeAnalyzerCallable(String url, String args) {
		super(args);
		this.url = url;
	}
	
	@Override
	protected void prepareEnviroment() throws EnviromentException {
		super.prepareEnviroment();
		// Download URL
		nextStep(String.format("Downloading repository from %s...", url), 0);
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
		if(!ToolFactory.getMavenCompilerTool().compileFolder(basePath, "", getArgs())) {
			throw new CompilerException();
		}
	}
	
}
