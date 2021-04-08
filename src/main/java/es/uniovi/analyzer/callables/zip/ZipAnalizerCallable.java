package es.uniovi.analyzer.callables.zip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;
import es.uniovi.analyzer.exceptions.CompilerException;
import es.uniovi.analyzer.exceptions.EnviromentException;

public class ZipAnalizerCallable extends AbstractAnalyzerCallable {

	private InputStream fileIs;

	public ZipAnalizerCallable(String classpath, String programId, String userId, InputStream fileIs) {
		super(classpath, programId, userId);
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
		compiler.compileFolder(basePath, programID, userId, classpath, getOutputStremRecorder());
	}

}
