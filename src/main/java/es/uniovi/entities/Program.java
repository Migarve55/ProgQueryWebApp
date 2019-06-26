package es.uniovi.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Program {
	
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	
	private String programIdentifier;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Result> results;
	
	public Program() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getProgramIdentifier() {
		return programIdentifier;
	}

	public void setProgramIdentifier(String programIdentifier) {
		this.programIdentifier = programIdentifier;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Result> getResults() {
		return results;
	}

	public void setResults(Set<Result> results) {
		this.results = results;
	}

}
