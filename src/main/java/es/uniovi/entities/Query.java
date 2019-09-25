package es.uniovi.entities;

import javax.persistence.*;

import java.util.Date;
import java.util.Set; 

@Entity
public class Query {
	
	public final static int NAME_LENGTH = 60;
	public final static int DESCRIPTION_LENGTH = 300;
	public final static int QUERY_LENGTH = 16012;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(unique=true, length=NAME_LENGTH)
	private String name;
	@Column(length=DESCRIPTION_LENGTH)
	private String description;
	@Column(length=QUERY_LENGTH)
	private String queryText;
	private boolean publicForAll;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToMany(cascade = {CascadeType.MERGE})
	@JoinTable(
			name="PUBLIC_TO",
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

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Query other = (Query) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
