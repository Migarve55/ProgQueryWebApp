package es.uniovi.analyzer.tasks.zip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;
import es.uniovi.analyzer.tasks.AbstractAnalyzerCallable;

public class ZipAnalizerCallable extends AbstractAnalyzerCallable {

	private InputStream fileIs;
	private String extraCP;

	public ZipAnalizerCallable(String args, String extraCP, InputStream fileIs) {
		super(args);
		this.fileIs = fileIs;
		this.extraCP = extraCP;
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
		compiler.compileFolder(basePath, extraCP, args);
	}

}
