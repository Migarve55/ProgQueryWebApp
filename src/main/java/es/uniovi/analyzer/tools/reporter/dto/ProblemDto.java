package es.uniovi.analyzer.tools.reporter.dto;

public class ProblemDto {

	private String msg;
	
	private String analysisName;
	
	public ProblemDto() {
		
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getAnalysisName() {
		return analysisName;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}

}
