package es.uniovi.analyzer.tools.reporter;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.Record;
import org.neo4j.graphdb.QueryExecutionException;
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
				try {
					queryRunner.runQuery(query.getQueryText(), programID).forEachRemaining((result) -> {
						ProblemDto problem = getProblemDtoFromResult(result);
						problem.setQueryName(query.getName());
						errors.add(problem);
					});
				} catch (QueryExecutionException qee) {
					logger.error("Could not execute query {}, error: {}", query.getName(), qee.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return errors;
	}
	
	private ProblemDto getProblemDtoFromResult(Record record) {
		ProblemDto error = new ProblemDto();
		String file = record.get("file").asString();
		error.setFile(file == null ? "???" : prettifyFilename(file));
		error.setLine(record.get("line", 0));
		error.setColumn(record.get("column", 0));
		return error;
	}
	
	private String prettifyFilename(String filename) {
		int i = filename.indexOf("env_");
		return filename.substring(i + 41, filename.length());
	}
	
}
