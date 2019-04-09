package com.uniovi.tasks.callables;

import java.io.File;
import java.util.List;

import com.uniovi.entities.CodeError;

public class FileAnalyzerCallable extends AbstractAnalyzerCallable {

	private File file;
	
	public FileAnalyzerCallable(String args, File file) {
		super(args);
		this.file = file;
	}

	@Override
	public List<CodeError> call() throws Exception {
		waitLoop();
		task.incrementProgress(30);
		task.setStatus(String.format("Downloading file from %s...", file.getAbsolutePath()));
		waitLoop();
		task.incrementProgress(30);
		task.setStatus("Compiling...");
		waitLoop();
		task.incrementProgress(30);
		task.setStatus("Analyzing...");
		waitLoop();
		return super.call();
	}

}
