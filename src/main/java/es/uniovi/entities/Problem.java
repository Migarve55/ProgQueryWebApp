package es.uniovi.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Problem {
	
	public final static int MSG_LENGTH = 1024 * 3;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(length = MSG_LENGTH)
	private String msg;
	
	@ManyToOne
	@JoinColumn(name = "analysis_id")
	@JsonIgnore
	private Analysis analysis;
	
	@ManyToOne
	@JoinColumn(name = "result_id")
	@JsonIgnore
	private Result result;
	
	public Problem() {
		
	}
	
	public Problem(String msg) {
		this.msg = msg;
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

	public Analysis getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
