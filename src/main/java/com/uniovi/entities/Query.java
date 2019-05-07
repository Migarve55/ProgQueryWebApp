package com.uniovi.entities;

import javax.persistence.*;
import java.util.Set; 

@Entity
public class Query {
	
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	private String description;
	private boolean isPublic;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<User> publicTo;

	public Query() {
		
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

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<User> getPublicTo() {
		return publicTo;
	}

	public void setPublicTo(Set<User> publicTo) {
		this.publicTo = publicTo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
