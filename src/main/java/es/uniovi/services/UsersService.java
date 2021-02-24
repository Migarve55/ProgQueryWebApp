package es.uniovi.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import es.uniovi.analyzer.tools.reporter.Neo4jFacade;
import es.uniovi.entities.User;
import es.uniovi.repositories.UsersRepository;

@Service
public class UsersService {
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public List<User> getUsers() {
		List<User> users = new ArrayList<User>();
		usersRepository.findAll().forEach(users::add);
		return users;
	}

	public User getUser(Long id) {
		return usersRepository.findById(id).orElse(null);
	}

	@Transactional
	public void addUser(User user) {
		try (Neo4jFacade neo4jFacade = new Neo4jFacade(System.getProperty("neo4j.url"))) {
			if (user.getPassword() != null)
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			User newUser = usersRepository.save(user);
			logger.info("User {} has been added", newUser.getEmail());
		}
	}
	
	public boolean checkUserPassword(User user, String password) {
		return bCryptPasswordEncoder.matches(password, user.getPassword());
	}
	
	public void changeUserPassword(User user, String newPassword) {
		if (validateUserPassword(newPassword)) {
			user.setPassword(newPassword);
			usersRepository.save(user);
			logger.info("User {} has changed the password", user.getEmail());
		}
	}
	
	public boolean validateUserPassword(String password) {
		if (password.length() < 8 || password.length() > User.PASSWORD_LENGTH)
			return false;
		if (!password.matches(".*[0-9].*"))
			return false;
		return true;
	}

	public User getUserByEmail(String email) {
		return usersRepository.findByEmail(email);
	}

	@Transactional
	public void deleteUser(Long id) {
		try (Neo4jFacade neo4jFacade = new Neo4jFacade(System.getProperty("neo4j.url"))) {
			usersRepository.deleteById(id);
			logger.info("User with id {} has been deleted", id);
		}
	}
	
	public void deleteAll() {
		usersRepository.deleteAll();
	}

}
