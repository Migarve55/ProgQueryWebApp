package es.uniovi.tasks;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;

public class PlaygroundTask extends AbstractTask {
	
	private String querySource;
	
	private String programSource;

	public PlaygroundTask(AbstractAnalyzerCallable callable, String querySource) {
		super(callable);
		this.querySource = querySource;
	}
	
	public PlaygroundTask(AbstractAnalyzerCallable callable, String querySource, String programSource) {
		this(callable, querySource);
		this.programSource = programSource;
	}

	@Override
	public String getOkUrl() {
		if (this.getResultId() != null) {
			return getBaseUrl() + "&resultId=" + this.getResultId();
		} else {
			return getBaseUrl() + "&noResult";
		}
	}

	@Override
	public String getKoUrl() {
		return getBaseUrl();
	}
	
	private String getBaseUrl() {
		return getBaseUrl(getProgramSource(), getQuerySource());
	}
	
	public static String getBaseUrl(String programSource, String querySource) {
		try {
			return String.format("/program/playground?programSource=%s&querySource=%s", 
					URLEncoder.encode(programSource, "UTF-8"),
					URLEncoder.encode(querySource,   "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getQuerySource() {
		return querySource;
	}


	public String getProgramSource() {
		return programSource;
	}

}
