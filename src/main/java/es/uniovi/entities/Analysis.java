package es.uniovi.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.HashSet;
import java.util.Set; 

@Entity
public class Analysis {
	
	public final static int NAME_LENGTH = 60;
	public final static int DESCRIPTION_LENGTH = 1000;
	public final static int QUERY_LENGTH = 15012;
	
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
	@JsonIgnore
	private User user;
	
	@ManyToMany(cascade = {CascadeType.MERGE})
	@JoinTable(
			name="PUBLIC_TO",
	        joinColumns={@JoinColumn(name="ANALYSIS_ID")},
	        inverseJoinColumns={@JoinColumn(name="USER_ID")})
	@JsonIgnore
	private Set<User> publicTo = new HashSet<User>();

	public Analysis() {
	}

	public Analysis(String name, String description, String queryText) {
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
		Analysis other = (Analysis) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
