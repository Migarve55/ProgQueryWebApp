package com.uniovi.tasks.callables;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.uniovi.entities.CodeError;
import com.uniovi.tasks.AnalyzerTask;

public abstract class AbstractAnalyzerCallable implements Callable<List<CodeError>> {
	
	protected AnalyzerTask task;

	@Override
	public List<CodeError> call() throws Exception {
		return Arrays.asList(
				new CodeError("test.java", 1, 3, "HIGH", "This was generated as a mere test")
			);
	}

	public void setTask(AnalyzerTask task) {
		this.task = task;
	}

}
