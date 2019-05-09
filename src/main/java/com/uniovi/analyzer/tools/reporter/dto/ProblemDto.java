package com.uniovi.analyzer.tools.reporter.dto;

public class ProblemDto {

	private String file;
	private long line = 0;
	private long column = 0;
	
	private String queryName;
	
	public ProblemDto() {
		
	}

	public ProblemDto(String file, int line, int column) {
		super();
		this.file = file;
		this.line = line;
		this.column = column;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public long getLine() {
		return line;
	}

	public void setLine(long line) {
		this.line = line;
	}

	public long getColumn() {
		return column;
	}

	public void setColumn(long column) {
		this.column = column;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

}
