package es.uniovi.analyzer.tasks.github;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.tasks.AbstractAnalyzerCallable;

public class GithubCodeAnalyzerCallable extends AbstractAnalyzerCallable {

	private String url;
	
	public GithubCodeAnalyzerCallable(String classpath, String url) {
		super(classpath);
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
			throw new EnviromentException("error.enviroment.create.github");
		} finally {
			if (result != null)
				result.close();
		}
	}

	@Override
	protected void compile() throws CompilerException {
		super.compile();
		compiler.compileFolder(basePath, programID, classpath);
	}
	
}
