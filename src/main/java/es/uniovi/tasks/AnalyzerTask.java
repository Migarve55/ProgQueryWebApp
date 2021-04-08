package es.uniovi.tasks;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;

public class AnalyzerTask extends AbstractTask {

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
	

}
