package com.uniovi.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.tomcat.util.http.fileupload.FileUtils;

public class EnviromentManager {
	
	private final static String PATH = "src/main/resources/uploads/";

	/**
	 * Creates the eniroment folder folder
	 * @return the base path to the created folder, null if it was not created
	 */
	public String createEnviroment() {
		String folder = String.format("%senv_%s/", PATH, UUID.randomUUID());
		if (!new File(folder).mkdirs())
			return null;
		return folder;
	}
	
	/**
	 * 
	 * @param basePath
	 * @throws IOException 
	 */
	public void deleteEnviroment(String basePath) throws IOException {
		FileUtils.deleteDirectory(new File(basePath));
	}

}
