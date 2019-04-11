package com.uniovi.analyzer.tasks.zip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.uniovi.analyzer.exceptions.CompilerException;
import com.uniovi.analyzer.exceptions.EnviromentException;
import com.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import com.uniovi.analyzer.tools.ToolFactory;

public class ZipAnalizerCallable extends AbstractAnalyzerCallable {

	private InputStream fileIs;

	public ZipAnalizerCallable(String args, InputStream fileIs) {
		super(args);
		this.fileIs = fileIs;
	}

	@Override
	protected void prepareEnviroment() throws EnviromentException {
		super.prepareEnviroment();
		// Coping required files
		try (BufferedInputStream is = new BufferedInputStream(fileIs)) {
			UnzipUtility.unzip(is, basePath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new EnviromentException("error.enviroment.io");
		}
	}

	@Override
	protected void compile() throws CompilerException {
		super.compile();
		ToolFactory.getJavaCompilerTool().compileFolder(basePath, "", getArgs());
	}

}
