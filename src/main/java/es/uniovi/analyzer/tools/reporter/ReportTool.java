package es.uniovi.analyzer.tools.reporter;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.exceptions.Neo4jException;
import org.neo4j.driver.v1.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.QueryDto;

public class ReportTool {
	
	private String url;
	private String programID;
	private List<QueryDto> queries = new ArrayList<QueryDto>();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ReportTool(String url, String programID) {
		this.url = url;
		this.programID = programID;
	}
	
	public void setQueries(List<QueryDto> queries) {
		this.queries = queries;
	}

	public List<ProblemDto> generateReport() {
		List<ProblemDto> errors = new ArrayList<ProblemDto>();
		logger.info("Creating report for program {}", programID);
		try (Neo4jFacade queryRunner = new Neo4jFacade(url)) {
			for (QueryDto query : queries) {
				if (isQuerySafe(query.getQueryText())) {
					try {
						logger.info("Running query {}", query.getName());
						queryRunner.runQuery(query.getQueryText(), programID).forEachRemaining((result) -> {
							ProblemDto problem = getProblemDtoFromResult(result);
							problem.setQueryName(query.getName());
							errors.add(problem);
						});
					} catch (Neo4jException qee) {
						logger.info("Could not execute query {}, error: {}", query.getName(), qee.getMessage());
					}
				} else {
					logger.info("Query {} was not safe. Avoiding execution...", query.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return errors;
	}
	
	/**
	 * Checks if the query modifies in any way the data base
	 * Statments that remove, add or modify nodes or relationsa are not allowed
	 * @param query
	 * @return
	 */
	public static boolean isQuerySafe(String query) {
		String lcQuery = query.toLowerCase();
		return !lcQuery.contains("delete") 
			&& !lcQuery.contains("create") 
			&& !lcQuery.contains("set") 
			&& !lcQuery.contains("remove")
			&& !lcQuery.contains("merge");
	}
	
	private ProblemDto getProblemDtoFromResult(Record record) {
		ProblemDto error = new ProblemDto();
		StringBuilder sb = new StringBuilder();
		if (record.fields().isEmpty())
			return error;
		for (int i = 0; i < record.fields().size() - 1; i++) {
			Pair<String, ?> pair = record.fields().get(i);
			sb.append(String.format("%s: %s, ", pair.key(), pair.value()));
		}
		Pair<String, ?> pair = record.fields().get(record.fields().size() - 1);
		sb.append(String.format("%s: %s", pair.key(), pair.value()));
		error.setMsg(sb.toString());
		return error;
	}
	
}
