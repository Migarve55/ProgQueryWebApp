package es.uniovi.analyzer.callables.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * @author www.codejava.net
 *
 */
public class UnzipUtility {

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 * @param is
	 * @param destDirectory
	 * @throws IOException
	 */
	public static void unzip(InputStream is, String destDirectory) throws IOException {
		try (ZipInputStream zipIn = new ZipInputStream(is)) {
			Path targetDir = Paths.get(destDirectory);
			for (ZipEntry ze; (ze = zipIn.getNextEntry()) != null; ) {
	            Path resolvedPath = targetDir.resolve(ze.getName());
	            if (ze.isDirectory()) {
	                Files.createDirectories(resolvedPath);
	            } else {
	                Files.createDirectories(resolvedPath.getParent());
	                Files.copy(zipIn, resolvedPath);
	            }
	        }
		}
	}
	
	

}
