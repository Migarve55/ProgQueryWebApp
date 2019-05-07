package com.uniovi.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Result {

	@Id
	@GeneratedValue
	private long id;
	
	@OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Problem> problems;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public Result() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Set<Problem> getProblems() {
		return problems;
	}

	public void setProblems(Set<Problem> problems) {
		this.problems = problems;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
