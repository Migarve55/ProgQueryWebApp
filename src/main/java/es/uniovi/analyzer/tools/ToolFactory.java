package es.uniovi.analyzer.tools;

import java.util.List;

import es.uniovi.analyzer.tools.compilators.JavaCompilerTool;
import es.uniovi.analyzer.tools.compilators.MavenCompilerTool;
import es.uniovi.analyzer.tools.enviroment.EnviromentManagerTool;
import es.uniovi.analyzer.tools.reporter.ReportTool;
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

	public static ReportTool getReportTool(String dbPath, String programID, List<QueryDto> queries) {
		ReportTool tool = new ReportTool(dbPath, programID);
		tool.setQueries(queries);
		return tool;
	}
	
}
