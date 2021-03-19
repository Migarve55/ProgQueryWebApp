package es.uniovi.analyzer.tools;

import java.util.List;

import es.uniovi.analyzer.tools.compilators.JavaCompilerTool;
import es.uniovi.analyzer.tools.compilators.MavenCompilerTool;
import es.uniovi.analyzer.tools.enviroment.EnviromentManagerTool;
import es.uniovi.analyzer.tools.reporter.Neo4jTool;
import es.uniovi.analyzer.tools.reporter.dto.QueryDto;

public class ToolFactory {
	
	public static EnviromentManagerTool getEnviromentTool() {
		return new EnviromentManagerTool();
	}
	
	public static JavaCompilerTool getJavaCompilerTool() {
		return new JavaCompilerTool();
	}
	
	public static MavenCompilerTool getMavenCompilerTool() {
		return new MavenCompilerTool();
	}

	public static Neo4jTool getNeo4jTool(String url, String programID, List<QueryDto> queries) {
		Neo4jTool tool = new Neo4jTool(url, programID);
		tool.setQueries(queries);
		return tool;
	}
	
}
