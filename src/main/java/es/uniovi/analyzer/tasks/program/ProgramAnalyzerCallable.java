package es.uniovi.analyzer.tasks.program;

import java.util.List;

import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.exceptions.ReportException;
import es.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import es.uniovi.analyzer.tools.ToolFactory;
import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;

public class ProgramAnalyzerCallable extends AbstractAnalyzerCallable {
	
	public ProgramAnalyzerCallable(String programID) {
		super(null);
		this.programID = programID;
	}

	protected void prepareEnviroment() throws EnviromentException {
		// Nothing to do here
	}

	protected void compile() throws CompilerException {
		// Nothing to do here
	}

	protected List<ProblemDto> createReport() throws ReportException {
		nextStep("Creating report", 50);
		String dbConn = "";
		return ToolFactory.getReportTool(dbConn, programID, queries).generateReport();
	}

	protected void cleanEnviroment() throws EnviromentException {
		// Nothing to do here
	}

}
