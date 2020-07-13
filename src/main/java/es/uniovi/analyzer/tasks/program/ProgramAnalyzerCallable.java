package es.uniovi.analyzer.tasks.program;

import java.util.List;

import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.exceptions.ReportException;
import es.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;

public class ProgramAnalyzerCallable extends AbstractAnalyzerCallable {
	
	public ProgramAnalyzerCallable(String database, String programID) {
		super(null, database);
		this.programID = programID;
	}

	protected void prepareEnviroment() throws EnviromentException {
		// Nothing to do here
	}

	protected void compile() throws CompilerException {
		// Nothing to do here
	}

	protected void createReport(List<ProblemDto> result) throws ReportException {
		nextStep("Creating report from program " + programID, 25);
		super.createReport(result);
	}

	protected void cleanEnviroment() throws EnviromentException {
		// Nothing to do here
	}

}
