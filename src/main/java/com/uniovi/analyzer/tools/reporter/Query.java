package com.uniovi.analyzer.tools.reporter;

public class Query {

	private String query;
	private String level;
	private String descrition;

	public Query(String query, String level, String descrition) {
		this.query = query;
		this.level = level;
		this.descrition = descrition;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDescrition() {
		return descrition;
	}

	public void setDescrition(String descrition) {
		this.descrition = descrition;
	}

}
