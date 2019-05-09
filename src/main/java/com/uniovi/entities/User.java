package com.uniovi.entities;

import javax.persistence.*;
import java.util.Set; //A collection that contains no duplicate elements

@Entity
public class User {
	
	public static final String ADMIN_ROLE = "ADMIN";
	public static final String USER_ROLE = "USER";
	
	@Id
	@GeneratedValue
	private long id;
	@Column(unique = true)
	private String email;
	private String name;
	private String lastName;
	private String password;
	@Transient 
	private String passwordConfirm;
	private String role;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Query> queries;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Result> results;
	
	@ManyToMany(cascade = {CascadeType.MERGE})
	@JoinTable(
			name="CAN_EDIT",
	        joinColumns={@JoinColumn(name="USER_ID")},
	        inverseJoinColumns={@JoinColumn(name="QUERY_ID")})
	private Set<Query> canModify;
	
	public User(String email, String name, String lastName) {
		super();
		this.email = email;
		this.name = name;
		this.lastName = lastName;
	}

	public User() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Set<Query> getQueries() {
		return queries;
	}

	public void setQueries(Set<Query> queries) {
		this.queries = queries;
	}

	public Set<Result> getResults() {
		return results;
	}

	public void setResults(Set<Result> results) {
		this.results = results;
	}

	public Set<Query> getCanModify() {
		return canModify;
	}

	public void setCanModify(Set<Query> canModify) {
		this.canModify = canModify;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
