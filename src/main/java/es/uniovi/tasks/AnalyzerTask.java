package es.uniovi.tasks;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;

public class AnalyzerTask extends AbstractTask {
	
	private Long resultId;
	
	private Long programId;

	public AnalyzerTask(AbstractAnalyzerCallable callable) {
		super(callable);
	}

	@Override
	public String getOkUrl() {
		if (getResultId() != null) {
			return "/result/" + this.getResultId();
		} else {
			return "/program/detail/" + this.getProgramId();
		}
	}

	@Override
	public String getKoUrl() {
		return "/";
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
