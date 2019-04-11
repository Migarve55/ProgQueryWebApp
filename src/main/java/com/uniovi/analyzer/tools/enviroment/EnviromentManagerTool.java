package com.uniovi.analyzer.tools.enviroment;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.analyzer.exceptions.EnviromentException;

public class EnviromentManagerTool {
	
	private final static String PATH = "src/main/resources/uploads/";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates the eniroment folder folder
	 * @return the base path to the created folder, null if it was not created
	 */
	public String createEnviroment() {
		String folder = String.format("%senv_%s/", PATH, UUID.randomUUID());
		if (!new File(folder).mkdirs())
			return null;
		logger.info("Created enviroment in {}", folder);
		return folder;
	}
	
	/**
	 * 
	 * @param basePath
	 * @throws IOException 
	 */
	public void deleteEnviroment(String basePath) throws EnviromentException {
		try {
			FileUtils.deleteDirectory(new File(basePath));
			logger.info("Deleted enviroment in {}", basePath);
		} catch (IOException e) {
			throw new EnviromentException(e);
		}
	}

}
