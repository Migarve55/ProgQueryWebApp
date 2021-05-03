package es.uniovi.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Result {

	@Id
	@GeneratedValue
	private long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	
	@OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Problem> problems;
	
	@OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<QueryExecutionProblem> queryExecutionProblems;
	
	@ManyToOne
	@JoinColumn(name = "program_id")
	@JsonIgnore
	private Program program;
	
	public Result() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Set<Problem> getProblems() {
		return problems;
	}

	public void setProblems(Set<Problem> problems) {
		this.problems = problems;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Set<QueryExecutionProblem> getQueryExecutionProblems() {
		return queryExecutionProblems;
	}

	public void setQueryExecutionProblems(Set<QueryExecutionProblem> queryExecutionProblems) {
		this.queryExecutionProblems = queryExecutionProblems;
	}
	
}
