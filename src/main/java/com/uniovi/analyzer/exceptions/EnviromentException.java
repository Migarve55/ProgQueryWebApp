package com.uniovi.analyzer.exceptions;

public class EnviromentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EnviromentException(Exception e) {
		super(e);
	}

	public EnviromentException() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLocalizedMessage() {
		return "error.enviroment";
	}
	

}
