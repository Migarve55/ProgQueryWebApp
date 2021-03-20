package es.uniovi.analyzer.exceptions;

public class AnalyzerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String errorCode = "error.compiler";

	public AnalyzerException() {
		super();
	}
	
	public AnalyzerException(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
