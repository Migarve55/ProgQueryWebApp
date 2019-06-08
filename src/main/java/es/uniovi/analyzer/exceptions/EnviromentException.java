package es.uniovi.analyzer.exceptions;

public class EnviromentException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String localizedMessage = "error.enviroment";

	public EnviromentException() {
		super();
	}
	
	public EnviromentException(String localizedMessage) {
		this.localizedMessage = localizedMessage;
	}

	@Override
	public String getLocalizedMessage() {
		return localizedMessage;
	}
	

}
