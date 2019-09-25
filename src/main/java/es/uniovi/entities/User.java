package es.uniovi.entities;

import javax.persistence.*;
import java.util.Set; //A collection that contains no duplicate elements

@Entity
public class User {
	
	public final static int EMAIL_LENGTH = 50;
	public final static int NAME_LENGTH = 24;
	public final static int LASTNAME_LENGTH = 50;
	public final static int PASSWORD_LENGTH = 20;
	
	@Id
	@GeneratedValue
	private long id;
	@Column(unique = true, length = EMAIL_LENGTH)
	private String email;
	@Column(length = NAME_LENGTH)
	private String name;
	@Column(length = LASTNAME_LENGTH)
	private String lastName;
	//Stores the hash, not the actual password
	@Column(length = 60)
	private String password;
	@Transient 
	private String passwordConfirm;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Query> queries;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Program> programs;
	
	@ManyToMany(cascade = {CascadeType.MERGE})
	@JoinTable(
			name="PUBLIC_TO",
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

	public Set<Query> getQueries() {
		return queries;
	}

	public void setQueries(Set<Query> queries) {
		this.queries = queries;
	}

	public Set<Program> getPrograms() {
		return programs;
	}

	public void setPrograms(Set<Program> programs) {
		this.programs = programs;
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
