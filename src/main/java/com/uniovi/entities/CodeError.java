package com.uniovi.entities;

public class CodeError {

	private String file;
	private int line;
	private int column;
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

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
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
