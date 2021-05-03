package es.uniovi.analyzer.tools.reporter.dto;

import java.util.ArrayList;
import java.util.List;

public class ResultDto {
	
	private List<ProblemDto> problems;
	
	private List<QueryExecutionProblemDto> queryExecutionProblems;
	
	public ResultDto() {
		this.problems = new ArrayList<ProblemDto>();
		this.queryExecutionProblems = new ArrayList<QueryExecutionProblemDto>();
	}
	
	public ResultDto(List<ProblemDto> problems, List<QueryExecutionProblemDto> queryExecutionProblems) {
		this.problems = problems;
		this.queryExecutionProblems = queryExecutionProblems;
	}

	public List<ProblemDto> getProblems() {
		return problems;
	}

	public List<QueryExecutionProblemDto> getQueryExecutionProblems() {
		return queryExecutionProblems;
	}

	public String getTextSummary() {
		StringBuilder sb = new StringBuilder();
		for (QueryExecutionProblemDto qep : queryExecutionProblems) {
			sb.append(String.format("%s%n", qep.getTextSummary()));
		}
		for (ProblemDto p : problems) {
			sb.append(String.format("%s%n", p.getMsg()));
		}
		return sb.toString();
	}

}
