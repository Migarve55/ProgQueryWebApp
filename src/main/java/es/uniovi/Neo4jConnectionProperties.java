package es.uniovi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("neo4j")
public class Neo4jConnectionProperties {

	private String url;
	private String user;
	private String password;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
