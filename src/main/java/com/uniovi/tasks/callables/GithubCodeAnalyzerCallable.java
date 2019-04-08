package com.uniovi.tasks.callables;

import java.util.List;

import com.uniovi.entities.CodeError;

public class GithubCodeAnalyzerCallable extends AbstractAnalyzerCallable {

	private String url;
	private String args;
	
	public GithubCodeAnalyzerCallable(String url, String args) {
		super();
		this.url = url;
		this.args = args;
	}

	@Override
	public List<CodeError> call() throws Exception {
		waitLoop();
		task.incrementProgress(25);
		task.setStatus("Downloading repo...");
		waitLoop();
		task.incrementProgress(50);
		task.setStatus("Compiling...");
		waitLoop();
		task.incrementProgress(80);
		task.setStatus("Analyzing...");
		waitLoop();
		return super.call();
	}
	
	private void waitLoop() {
		long pow = (long) Math.pow(10, 10);
		for(long i = 0;i < pow;i++);
	}
	
}
