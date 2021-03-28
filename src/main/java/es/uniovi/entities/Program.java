package es.uniovi.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
public class Program {
	
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	
	@Column(unique = true, length = 36)
	@JsonIgnore
	private String programIdentifier;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;
	
	@JsonIgnore
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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
	
	public String getDisplayName() {
		return String.format("%s (%s)", this.name, this.timestamp);
	}

}
