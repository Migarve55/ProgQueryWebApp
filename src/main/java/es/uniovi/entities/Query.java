package es.uniovi.entities;

import javax.persistence.*;
import java.util.Set; 

@Entity
public class Query {
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(unique = true, length = 60)
	private String name;
	private String description;
	@Column(length=4096)
	private String queryText;
	private boolean publicForAll;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToMany(cascade = {CascadeType.MERGE})
	@JoinTable(
			name="CAN_EDIT",
	        joinColumns={@JoinColumn(name="QUERY_ID")},
	        inverseJoinColumns={@JoinColumn(name="USER_ID")})
	private Set<User> publicTo;

	public Query() {
		
	}

	public Query(String name, String description, String queryText) {
		this.name = name;
		this.description = description;
		this.queryText = queryText;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public boolean isPublicForAll() {
		return publicForAll;
	}

	public void setPublicForAll(boolean publicForAll) {
		this.publicForAll = publicForAll;
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
	
}
