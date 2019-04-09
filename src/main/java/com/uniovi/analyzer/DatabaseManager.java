package com.uniovi.analyzer;

import java.util.UUID;

public class DatabaseManager {

	/**
	 * 
	 * @param basePath
	 * @return
	 */
	public String createDb(String basePath) {
		return String.format("%sddbb_%s", basePath, UUID.randomUUID());
	}

}
