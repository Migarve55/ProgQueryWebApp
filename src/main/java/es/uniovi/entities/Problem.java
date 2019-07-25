package es.uniovi.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Problem {
	
	@Id
	@GeneratedValue
	private long id;
	
	private String msg;
	
	private int line = -1;
	private int col = -1;
	
	private String compilationUnit;
	
	@ManyToOne
	@JoinColumn(name = "query_id")
	private Query query;
	
	@ManyToOne
	@JoinColumn(name = "result_id")
	private Result result;
	
	public Problem() {
		
	}
	
	public Problem(String msg) {
		this.msg = msg;
	}

	public Problem(String file, int line, int col) {
		this.compilationUnit = file;
		this.line = line;
		this.col = col;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(String compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
