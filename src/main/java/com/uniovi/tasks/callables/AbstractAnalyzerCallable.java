package com.uniovi.tasks.callables;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.uniovi.entities.CodeError;
import com.uniovi.tasks.AnalyzerTask;

public abstract class AbstractAnalyzerCallable implements Callable<List<CodeError>> {
	
	private String args;
	protected AnalyzerTask task;
	
	public  AbstractAnalyzerCallable(String args) {
		this.args = args;
	}

	@Override
	public List<CodeError> call() throws Exception {
		return Arrays.asList(
				new CodeError("test.java", 1, 3, "HIGH", "This was generated as a mere test")
			);
	}
	
	protected void waitLoop() {
		long pow = (long) Math.pow(10, 10);
		for(long i = 0;i < pow;i++);
	}

	public void setTask(AnalyzerTask task) {
		this.task = task;
	}

	public String getArgs() {
		return args;
	}

}
