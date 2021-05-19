package es.uniovi.analyzer.tools.reporter;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.AnalysisDto;
import es.uniovi.analyzer.tools.reporter.dto.AnalysisExecutionProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.ResultDto;

public class Neo4jTool {
	
	private String url;
	private String database;
	private String programID;
	private List<AnalysisDto> queries = new ArrayList<AnalysisDto>();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Neo4jTool(String url, String programID) { 
		this.url = url;
		this.programID = programID;
	}
	
	public void setQueries(List<AnalysisDto> queries) {
		this.queries = queries;
	}

	public ResultDto generateReport() {
		List<ProblemDto> errors = new ArrayList<ProblemDto>();
		List<AnalysisExecutionProblemDto> queryExecutionProblems = new ArrayList<AnalysisExecutionProblemDto>();
		logger.info("Creating report for program {}", programID);
		try (Neo4jFacade queryRunner = new Neo4jFacade(url)) {
			for (AnalysisDto query : queries) {
				try {
					logger.info("Running query {}", query.getName());
					queryRunner.runQuery(database, query.getQueryText(), programID).forEach((record) -> {
						ProblemDto problem = getProblemDtoFromResult(record);
						problem.setAnalysisName(query.getName());
						errors.add(problem);
					});
				} catch (Neo4jException qee) {
					logger.info("Could not execute query {}, error: {}", query.getName(), qee.getMessage());
					queryExecutionProblems.add(new AnalysisExecutionProblemDto(query.getName(), qee.getMessage()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return new ResultDto(errors, queryExecutionProblems);
	}
	
	// Auxiliar
	
	private ProblemDto getProblemDtoFromResult(Record record) {
		ProblemDto error = new ProblemDto();
		StringBuilder sb = new StringBuilder();
		int maxLength = 10;
		if (record.fields().isEmpty())
			return error;
		for (int i = 0; i < record.fields().size() - 1; i++) {
			Pair<String, ?> pair = record.fields().get(i);
			String key = pair.key();
			sb.append(String.format("%s: %s, ", key.length() <= maxLength ? key : "result" + i, pair.value()));
		}
		Pair<String, ?> pair = record.fields().get(record.fields().size() - 1);
		String key = pair.key();
		sb.append(String.format("%s: %s", key.length() <= maxLength ? key : "result", pair.value()));
		error.setMsg(sb.toString());
		return error;
	}
	
}
