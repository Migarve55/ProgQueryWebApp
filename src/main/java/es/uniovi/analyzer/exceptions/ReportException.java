package es.uniovi.analyzer.exceptions;

public class ReportException extends AnalyzerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReportException() {
		super("error.report");
	}
	
	public ReportException(String errorCode) {
		super(errorCode);
	}

}
