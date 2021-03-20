package es.uniovi.analyzer.exceptions;

public class CompilerException extends AnalyzerException {

	private static final long serialVersionUID = 1L;

	public CompilerException() {
		super("error.compiler");
	}
	
	public CompilerException(String errorCode) {
		super(errorCode);
	}
	
}
