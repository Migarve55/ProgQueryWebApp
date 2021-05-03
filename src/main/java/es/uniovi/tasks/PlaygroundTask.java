package es.uniovi.tasks;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;

public class PlaygroundTask extends AbstractTask {
	
	private String querySource;
	
	private String programSource;
	
	private String resultMsg;

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
		try {
			if (this.getResultMsg() != null) {
				return getBaseUrl() + "&resultMsg=" + URLEncoder.encode(getResultMsg(), "UTF-8");
			} else {
				return getBaseUrl() + "&noResult";
			}
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException(uee);
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
		if (programSource == null)
			programSource = "";
		if (querySource == null)
			querySource = "";
		try {
			return String.format("/analyzer/playground?programSource=%s&querySource=%s", 
					URLEncoder.encode(programSource, "UTF-8"),
					URLEncoder.encode(querySource,   "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "/analyzer/playground";
		}
	}

	public String getQuerySource() {
		return querySource;
	}


	public String getProgramSource() {
		return programSource;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

}
