package es.uniovi.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import es.uniovi.entities.Query;
import es.uniovi.entities.User;

@Service
public class InsertSampleDataService {

	@Autowired
	private UsersService usersService;

	@Autowired
	private QueryService queryService;

	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String dbInitMode;

	@PostConstruct
	public void init() {
		if (!dbInitMode.equals("create"))
			return;
		User user1 = new User("miguel@email.com", "Miguel", "Garnacho Vélez");
		user1.setPassword("123456");
		user1.setRole(User.USER_ROLE);
		User user2 = new User("oscar@email.com", "Oscar", "Prieto");
		user2.setPassword("123456");
		user2.setRole(User.USER_ROLE);
		User user3 = new User("paco@email.com", "Paco", "Salvador Vega");
		user3.setPassword("123456");
		user3.setRole(User.USER_ROLE);
		User user4 = new User("maria@hotmail.es", "Maria Luisa", "Jorganes Hernandez");
		user4.setPassword("123456");
		user4.setRole(User.USER_ROLE);
		User user5 = new User("alvaro@email.com", "Álvaro", "Sanchez Dragó");
		user5.setPassword("123456");
		user5.setRole(User.USER_ROLE);
		// Admin
		User admin = new User("admin@email.com", "admin", "");
		admin.setPassword("admin");
		admin.setRole(User.ADMIN_ROLE);

		usersService.addUser(user1);
		usersService.addUser(user2);
		usersService.addUser(user3);
		usersService.addUser(user4);
		usersService.addUser(user5);
		usersService.addUser(admin);

		loadQueriesFromFile(user1);

		Query query2 = new Query("es.uniovi.query1", "Warning [CMU-MET51]", "...");
		query2.setUser(user1);
		query2.setPublicForAll(false);
		Set<User> publicTo = new HashSet<User>();
		publicTo.add(user2);
		publicTo.add(user3);
		publicTo.add(user4);
		query2.setPublicTo(publicTo);

		Query query3 = new Query("es.uniovi.query2", "Test 2", "...");
		query3.setUser(user2);

		Query query4 = new Query("es.uniovi.query3", "Test 3", "...");
		query4.setUser(user3);

		Query query5 = new Query("es.uniovi.query4", "Test 4", "...");
		query5.setUser(user4);
		query5.setPublicForAll(true);

		queryService.saveQuery(query2);
		queryService.saveQuery(query3);
		queryService.saveQuery(query4);
		queryService.saveQuery(query5);
	}

	private void loadQueriesFromFile(User user) {
		try (InputStream resource = new ClassPathResource("static/queries.json").getInputStream();) {
			Object obj = new JSONParser().parse(new InputStreamReader(resource));
			JSONObject jo = (JSONObject) obj;
			JSONArray ja = (JSONArray) jo.get("queries");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> queryIterator = ja.iterator(); 
			while (queryIterator.hasNext()) {
				JSONObject queryJson = queryIterator.next();
				Query query = new Query(
						(String) queryJson.get("name"), 
						(String) queryJson.get("description"), 
						(String) queryJson.get("query"));
				query.setUser(user);
				query.setPublicForAll(true);
				queryService.saveQuery(query);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}