package com.uniovi.analyzer.tools;

import com.uniovi.analyzer.tools.compilators.JavaCompilerTool;
import com.uniovi.analyzer.tools.compilators.MavenCompilerTool;
import com.uniovi.analyzer.tools.enviroment.EnviromentManagerTool;
import com.uniovi.analyzer.tools.reporter.ReportTool;

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
	
	public static ReportTool getReportTool(String dbPath) {
		return new ReportTool(dbPath);
	}
	
}
