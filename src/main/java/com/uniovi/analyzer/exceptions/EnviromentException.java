package com.uniovi.analyzer.exceptions;

public class EnviromentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EnviromentException(Exception e) {
		super(e);
	}

	@Override
	public String getLocalizedMessage() {
		return "error.enviroment";
	}
	

}
