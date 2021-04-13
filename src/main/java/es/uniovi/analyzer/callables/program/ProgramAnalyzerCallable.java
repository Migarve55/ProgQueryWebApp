package es.uniovi.analyzer.callables.program;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;
import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.exceptions.ReportException;

public class ProgramAnalyzerCallable extends AbstractAnalyzerCallable {

	public ProgramAnalyzerCallable(String programId, String userId) {
		super(null, programId, userId);
	}

	@Override
	protected void prepareEnviroment() throws EnviromentException {
		// Nothing to do here
	}

	@Override
	protected void compile() throws CompilerException {
		// Nothing to do here
	}
	
	@Override
	protected void createReport() throws ReportException {
		nextStep("Creating report from program " + programID, 25);
		super.createReport();
	}

	@Override
	public void cleanEnviroment() throws EnviromentException {
		// Nothing to do here
	}
	
}
