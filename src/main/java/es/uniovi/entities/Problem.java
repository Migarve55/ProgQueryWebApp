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
	
	private int line;
	private int col;
	
	private String compilationUnit;
	
	@ManyToOne
	@JoinColumn(name = "query_id")
	private Query query;
	
	@ManyToOne
	@JoinColumn(name = "result_id")
	private Result result;
	
	public Problem() {
		
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
