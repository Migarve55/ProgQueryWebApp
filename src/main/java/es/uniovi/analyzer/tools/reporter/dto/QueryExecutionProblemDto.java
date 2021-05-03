package es.uniovi.analyzer.tools.reporter.dto;

public class QueryExecutionProblemDto {
	
	private String name;
	
	private String msg;
	
	public QueryExecutionProblemDto(String name, String msg) {
		this.name = name;
		this.msg = msg;
	}

	public String getName() {
		return name;
	}

	public String getMsg() {
		return msg;
	}

	public String getTextSummary() {
		if (name.isBlank()) {
			return this.getMsg();
		}
		return String.format("[%s]: %s", name, getMsg());
	}

}
