package com.uniovi.tasks.callables;

import java.util.List;

import com.uniovi.entities.CodeError;

public class GithubCodeAnalyzerCallable extends AbstractAnalyzerCallable {

	private String url;
	
	public GithubCodeAnalyzerCallable(String url, String args) {
		super(args);
		this.url = url;
	}

	@Override
	public List<CodeError> call() throws Exception {
		waitLoop();
		task.incrementProgress(30);
		task.setStatus(String.format("Downloading repo from %s...", url));
		waitLoop();
		task.incrementProgress(30);
		task.setStatus("Compiling...");
		waitLoop();
		task.incrementProgress(30);
		task.setStatus("Analyzing...");
		waitLoop();
		return super.call();
	}
	
}
