package es.uniovi.analyzer.tools.reporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.QueryExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.QueryDto;

public class ReportTool {
	
	private String dbPath;
	private List<QueryDto> queries = new ArrayList<QueryDto>();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ReportTool(String dbPath) {
		this.dbPath = dbPath;
	}
	
	public void setQueries(List<QueryDto> queries) {
		this.queries = queries;
	}

	public List<ProblemDto> generateReport() {
		List<ProblemDto> errors = new ArrayList<ProblemDto>();
		logger.info("Creating report...");
		try (Neo4jQueryRunner queryRunner = new Neo4jQueryRunner(dbPath)) {
			for (QueryDto query : queries) {
				try {
					queryRunner.runQuery(query.getQueryText()).forEach((result) -> {
						ProblemDto problem = getProblemDtoFromResult(result);
						problem.setQueryName(query.getName());
						errors.add(problem);
					});
				} catch (QueryExecutionException qee) {
					logger.error("Could not compile query {}, error: {}", query.getName(), qee.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return errors;
	}
	
	private ProblemDto getProblemDtoFromResult(Map<String,Object> result) {
		ProblemDto error = new ProblemDto();
		Object file = result.get("file");
		error.setFile(file == null ? "???" : prettifyFilename(file.toString()));
		Long line = (Long) result.get("line");
		if (line != null)
			error.setLine(line);
		Long column = (Long) result.get("column");
		if (column != null)
			error.setColumn(column);
		return error;
	}
	
	private String prettifyFilename(String filename) {
		int i = filename.indexOf("env_");
		return filename.substring(i + 41, filename.length());
	}
	
}
