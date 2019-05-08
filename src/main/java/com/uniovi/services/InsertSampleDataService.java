package com.uniovi.services;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.entities.Query;
import com.uniovi.entities.User;

@Service
public class InsertSampleDataService {
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private QueryService queryService;
	
	@PostConstruct
	public void init() {
		
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

		Query query1 = new Query("es.uniovi.query1", "Warning [CMU-MET50]", "...");
		query1.setUser(user1);
		query1.setPublicForAll(true);
		
		Query query2 = new Query("es.uniovi.query2", "Warning [CMU-MET51]", "...");
		query2.setUser(user1);
		query2.setPublicForAll(false);
		Set<User> publicTo = new HashSet<User>();
		publicTo.add(user2);
		publicTo.add(user3);
		publicTo.add(user4);
		query2.setPublicTo(publicTo);
		
		Query query3 = new Query("es.uniovi.query3", "Warning [CMU-MET52]", "...");
		query3.setUser(user2);
		
		Query query4 = new Query("es.uniovi.query4", "Warning [CMU-MET53]", "...");
		query4.setUser(user3);
		
		Query query5 = new Query("es.uniovi.query5", "Warning [CMU-MET54]", "...");
		query5.setUser(user4);
		query5.setPublicForAll(true);
		
		queryService.saveQuery(query1);
		queryService.saveQuery(query2);
		queryService.saveQuery(query3);
		queryService.saveQuery(query4);
		queryService.saveQuery(query5);
		
	}
	
}