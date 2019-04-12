package com.uniovi.analyzer.exceptions;

public class CompilerException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String localizedMessage = "error.compiler";

	public CompilerException() {
		
	}
	
	public CompilerException(String localizedMessage) {
		this.localizedMessage = localizedMessage;
	}

	@Override
	public String getLocalizedMessage() {
		return localizedMessage;
	}
	
}
