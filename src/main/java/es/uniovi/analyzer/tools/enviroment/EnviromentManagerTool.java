package es.uniovi.analyzer.tools.enviroment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uniovi.analyzer.exceptions.EnviromentException;

public class EnviromentManagerTool {
	
	private final static String PATH = "uploads/";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates the enviroment folder folder
	 * @return the base path to the created folder, null if it was not created
	 */
	public String createEnviroment() throws EnviromentException {
		String folder = String.format("%senv_%s/", PATH, UUID.randomUUID());
		if (!new File(folder).mkdirs())
				throw new EnviromentException("error.enviroment.create");
		logger.info("Created enviroment in {}", folder);
		return folder;
	}
	
	/**
	 * 
	 * @param filePath
	 * @param source
	 * @throws IOException 
	 */
	public void createSourceFile(String filePath, String source) throws EnviromentException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));) {
			writer.write(source);
		} catch (IOException ioe) {
			throw new EnviromentException("error.enviroment.create");
		}
	}
	
	/**
	 * Deletes the enviroment in a folder
	 * @param basePath
	 * @throws IOException 
	 */
	public void deleteEnviroment(String basePath) throws EnviromentException {
		try {
			FileUtils.deleteDirectory(new File(basePath));
			logger.info("Deleted enviroment in {}", basePath);
		} catch (IllegalArgumentException e) {
			logger.info("Enviroment in {} was already deleted", basePath);
		} catch (IOException e) {
			logger.error("Could not delete enviroment in {}, error: {}", basePath, e.getMessage());
			throw new EnviromentException("error.enviroment.delete");
		}
	}

}
