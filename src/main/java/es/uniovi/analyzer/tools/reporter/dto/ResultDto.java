package es.uniovi.analyzer.tools.reporter.dto;

import java.util.ArrayList;
import java.util.List;

public class ResultDto {
	
	private List<ProblemDto> problems;
	
	private List<AnalysisExecutionProblemDto> analysisExecutionProblems;
	
	public ResultDto() {
		this.problems = new ArrayList<ProblemDto>();
		this.analysisExecutionProblems = new ArrayList<AnalysisExecutionProblemDto>();
	}
	
	public ResultDto(List<ProblemDto> problems, List<AnalysisExecutionProblemDto> analysisExecutionProblems) {
		this.problems = problems;
		this.analysisExecutionProblems = analysisExecutionProblems;
	}

	public List<ProblemDto> getProblems() {
		return problems;
	}

	public List<AnalysisExecutionProblemDto> getAnalysisExecutionProblems() {
		return analysisExecutionProblems;
	}

	public String getTextSummary() {
		StringBuilder sb = new StringBuilder();
		for (AnalysisExecutionProblemDto aep : analysisExecutionProblems) {
			sb.append(String.format("%s%n", aep.getTextSummary()));
		}
		for (ProblemDto p : problems) {
			sb.append(String.format("%s%n", p.getMsg()));
		}
		return sb.toString();
	}

}
