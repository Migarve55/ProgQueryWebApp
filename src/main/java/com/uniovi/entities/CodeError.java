package com.uniovi.entities;

public class CodeError {

	private String file;
	private long line = 0;
	private long column = 0;
	private String level;
	private String description;
	
	public CodeError() {
		
	}

	public CodeError(String file, int line, int column, String level, String description) {
		super();
		this.file = file;
		this.line = line;
		this.column = column;
		this.level = level;
		this.description = description;
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
