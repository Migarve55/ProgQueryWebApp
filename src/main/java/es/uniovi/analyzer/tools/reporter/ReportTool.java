package es.uniovi.analyzer.tools.reporter;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.QueryDto;

public class ReportTool {
	
	private String url;
	private String database;
	private String programID;
	private List<QueryDto> queries = new ArrayList<QueryDto>();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ReportTool(String url, String database, String programID) {
		this.url = url;
		this.database = database;
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
				try {
					logger.info("Running query {}", query.getName());
					queryRunner.runQuery(database, query.getQueryText(), programID).forEachRemaining((result) -> {
						ProblemDto problem = getProblemDtoFromResult(result);
						problem.setQueryName(query.getName());
						errors.add(problem);
					});
				} catch (Neo4jException qee) {
					logger.info("Could not execute query {}, error: {}", query.getName(), qee.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return errors;
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
