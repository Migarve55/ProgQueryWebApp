package es.uniovi.analyzer.tools.reporter.dto;

public class ProblemDto {

	private String msg;
	
	private String queryName;
	
	public ProblemDto() {
		
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

}
