package es.uniovi.tasks;

import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.tools.reporter.dto.ResultDto;

public abstract class AbstractTask extends FutureTask<ResultDto> {

	private AbstractAnalyzerCallable callable;
	
	private Long resultId;
	
	private Long programId;
	
	public AbstractTask(AbstractAnalyzerCallable callable) {
		super(callable);
		this.callable = callable;
	}
	
	public abstract String getOkUrl();
	
	public abstract String getKoUrl();
	
	@Override
	protected void done() {
		super.done();
		callable.setProgress(100);
		callable.setStatus("Done");
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		callable.setProgress(100);
		callable.setStatus("Cancelled");
		try {
			callable.cleanEnviroment();
		} catch (EnviromentException e) {
			e.printStackTrace();
		}
		return super.cancel(mayInterruptIfRunning);
	}
	
	public String getRecordedOutput() {
		return this.callable.getRecordedOutput();
	}

	public int getProgress() {
		return callable.getProgress();
	}

	public String getStatus() {
		return callable.getStatus();
	}

	public void setCallback(Consumer<ResultDto> callback) {
		this.callable.setCallback(callback);
	}

	@Override
	public String toString() {
		return "Task-" + this.hashCode();
	}

	public Long getResultId() {
		return resultId;
	}

	public void setResultId(Long resultId) {
		this.resultId = resultId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

}

