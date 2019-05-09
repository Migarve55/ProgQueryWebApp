package com.uniovi.analyzer.tools.reporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import com.uniovi.analyzer.tools.reporter.dto.QueryDto;

public class ReportTool {
	
	private String dbPath;
	private List<QueryDto> queries = new ArrayList<QueryDto>();
	
	public ReportTool(String dbPath) {
		this.dbPath = dbPath;
	}
	
	public void setQueries(List<QueryDto> queries) {
		this.queries = queries;
	}

	public List<ProblemDto> generateReport() {
		List<ProblemDto> errors = new ArrayList<ProblemDto>();
		Neo4jQueryRunner queryRunner = new Neo4jQueryRunner(dbPath);
		try {
			for (QueryDto query : queries) {
				queryRunner.runQuery(query.getQueryText()).forEach((result) -> {
					ProblemDto problem = getProblemDtoFromResult(result);
					problem.setQueryName(query.getName());
					errors.add(problem);
				});
			}
		} finally {
			queryRunner.close();
		}
		return errors;
	}
	
	private ProblemDto getProblemDtoFromResult(Map<String,Object> result) {
		ProblemDto error = new ProblemDto();
		error.setFile(result.get("file").toString());
		Long line = (Long) result.get("line");
		if (line != null)
			error.setLine(line);
		Long column = (Long) result.get("column");
		if (column != null)
			error.setColumn(column);
		return error;
	}
	
}
