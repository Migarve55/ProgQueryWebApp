package es.uniovi.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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


	@PostConstruct
	public void init() {
		if (!isDBCreated())
			createAllData();
	}
	
	private void createAllData() {
		User user = new User("miguel@email.com", "Miguel", "Garnacho Vélez");
		user.setPassword("123456");
		usersService.addUser(user);
		loadQueriesFromFile(user);
	}
	
	/**
	 * This checks the database has the basic initial data
	 * @return
	 */
	private boolean isDBCreated() {
		return usersService.getUserByEmail("miguel@email.com") != null;
	}

	private void loadQueriesFromFile(User user) {
		try (InputStream resource = new ClassPathResource("queries.json").getInputStream();) {
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