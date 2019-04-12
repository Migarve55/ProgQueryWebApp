package com.uniovi.analyzer.tasks;

import java.util.List;
import java.util.concurrent.FutureTask;

import com.uniovi.analyzer.exceptions.EnviromentException;
import com.uniovi.analyzer.tools.reporter.CodeError;

public class AnalyzerTask extends FutureTask<List<CodeError>> {
	
	private int progress = 0;
	private String status = "In progress...";

	private AbstractAnalyzerCallable callable;
	
	public AnalyzerTask(AbstractAnalyzerCallable callable) {
		super(callable);
		callable.setTask(this);
		this.callable = callable;
	}
	
	@Override
	protected void done() {
		super.done();
		progress = 100;
		status = "Done";
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		progress = 100;
		status = "Cancelled";
		try {
			callable.cleanEnviroment();
		} catch (EnviromentException e) {
			e.printStackTrace();
		}
		return super.cancel(mayInterruptIfRunning);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public void incrementProgress(int increment) {
		this.progress = Math.min(this.progress + increment, 100);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
