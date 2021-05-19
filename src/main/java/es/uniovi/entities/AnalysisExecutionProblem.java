package es.uniovi.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class AnalysisExecutionProblem {

	public final static int MSG_LENGTH = 1024 * 2;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(length = MSG_LENGTH)
	private String msg;
	
	@Column(length = Analysis.NAME_LENGTH)
	private String analysisName;
	
	@ManyToOne
	@JoinColumn(name = "result_id")
	@JsonIgnore
	private Result result;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getAnalysisName() {
		return analysisName;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getTextSummary() {
		if (getAnalysisName().isBlank()) {
			return this.getMsg();
		}
		return String.format("[%s]: %s", getAnalysisName(), getMsg());
	}
	
}
