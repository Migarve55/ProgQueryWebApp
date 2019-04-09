package com.uniovi.tasks.callables;

import java.io.File;
import java.util.List;

import com.uniovi.analyzer.EnviromentManager;
import com.uniovi.analyzer.JavaCompilerUtil;
import com.uniovi.analyzer.reporter.ReportFactory;
import com.uniovi.entities.CodeError;

public class FileAnalyzerCallable extends AbstractAnalyzerCallable {

	private File file;
	
	//Utilities
	private JavaCompilerUtil compiler = new JavaCompilerUtil();
	private EnviromentManager enviromentManager = new EnviromentManager();
	
	public FileAnalyzerCallable(String args, File file) {
		super(args);
		this.file = file;
	}

	@Override
	public List<CodeError> call() throws Exception {
		//Creating enviroment
		task.setStatus("Creating enviroment...");
		String basePath = enviromentManager.createEnviroment();
		
		//Compilation
		task.incrementProgress(25);
		task.setStatus(String.format("Compiling %s...", file.getName()));
		compiler.compile(basePath, getArgs());
		
		//Report creation
		task.incrementProgress(25);
		task.setStatus("Creating report...");
		ReportFactory reportFactory = setupReportFactory(basePath + "/neo4j/data/ProgQuery.db");
		List<CodeError> report = reportFactory.generateReport();
		
		//Cleaning enviroment
		task.incrementProgress(25);
		task.setStatus("Cleaning enviroment...");
		enviromentManager.deleteEnviroment(basePath);
		
		//End
		return report;
	}


}
