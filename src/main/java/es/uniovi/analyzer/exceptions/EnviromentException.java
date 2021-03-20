package es.uniovi.analyzer.exceptions;

public class EnviromentException extends AnalyzerException {

	private static final long serialVersionUID = 1L;

	public EnviromentException() {
		super("error.enviroment");
	}
	
	public EnviromentException(String errorCode) {
		super(errorCode);
	}
	

}
