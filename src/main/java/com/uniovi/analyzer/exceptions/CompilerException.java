package com.uniovi.analyzer.exceptions;

public class CompilerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getLocalizedMessage() {
		return "error.compiler";
	}
	
}
