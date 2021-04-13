package es.uniovi.analyzer.tools.reporter.dto;

import java.util.List;

public class ResultDto {
	
	private List<ProblemDto> problems;
	
	private List<QueryExecutionProblemDto> queryExecutionProblems;
	
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

}
