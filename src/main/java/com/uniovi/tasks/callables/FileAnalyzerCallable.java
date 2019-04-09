package com.uniovi.tasks.callables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uniovi.analyzer.EnviromentManager;
import com.uniovi.analyzer.JavaCompilerUtil;
import com.uniovi.analyzer.reporter.ReportFactory;
import com.uniovi.entities.CodeError;

public class FileAnalyzerCallable extends AbstractAnalyzerCallable {
	
	private MultipartFile uploaded;
	
	//Utilities
	private JavaCompilerUtil compiler = new JavaCompilerUtil();
	private EnviromentManager enviromentManager = new EnviromentManager();
	
	public FileAnalyzerCallable(String args, MultipartFile uploaded) {
		super(args);
		this.uploaded = uploaded;
	}

	@Override
	public List<CodeError> call() throws IOException {
		//Creating enviroment
		task.setStatus("Creating enviroment...");
		String basePath = enviromentManager.createEnviroment();
		if (basePath == null) {
			task.cancel(false);
		}
		
		//Coping required files
		String fileName = uploaded.getOriginalFilename();
		String path = basePath + fileName;
		try (InputStream is = uploaded.getInputStream()) {
			Files.copy(is, 
					Paths.get(path),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			task.setStatus("Cleaning enviroment...");
			enviromentManager.deleteEnviroment(basePath);
			throw ioe;
		}
		
		File file = new File(path);
		
		//Compilation
		task.incrementProgress(25);
		task.setStatus(String.format("Compiling %s...", file.getName()));
		compiler.compileFile(basePath, file.getName(), getArgs());
		
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
