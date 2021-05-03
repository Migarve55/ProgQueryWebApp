package es.uniovi.controllers.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.uniovi.entities.Program;
import es.uniovi.entities.Query;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;
import es.uniovi.validators.AddUserValidator;
import es.uniovi.validators.EditUserValidator;

@RestController
public class UserRestController extends BaseRestController {
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private AddUserValidator addUserValidator;
	
	@Autowired
	private EditUserValidator editUserValidator;

	@GetMapping("/api/users/{user}")
	public User getUser(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		return user;
	}
	
	@GetMapping("/api/users/{user}/programs")
	public List<Program> getUserPrograms(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		return user.getPrograms()
				.stream()
				.collect(Collectors.toList());
	}

	@GetMapping("/api/users/{user}/analyses")
	public List<Query> getUserAnalyses(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		return user.getQueries()
				.stream()
				.collect(Collectors.toList());
	}

	@GetMapping("/api/users/{user}/results")
	public List<Result> getUserResults(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		List<Result> results = resultService.getResultsByUser(user);
		return results;
	}

	@PostMapping("/api/users")
	public User createUser(@Validated @RequestBody User user, BindingResult result) {
		addUserValidator.validate(user, result);
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed user, errors: " + toErrorList(result));
		}
		usersService.addUser(user);
		return user;
	}
	
	@PutMapping("/api/users/{user}")
	public User updateUser(@PathVariable(value = "user") String email, @Validated @RequestBody User user, BindingResult result) {
		User origUser = usersService.getUserByEmail(email);
		if (origUser == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		editUserValidator.validate(user, result);
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed user, errors: " + toErrorList(result));
		}
		// Update user
		origUser.setName(user.getName());
		origUser.setLastName(user.getLastName());
		usersService.modifyUser(origUser);
		return user;
	}
	
	@DeleteMapping("/api/users/{user}")
	public User deleteUser(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		usersService.deleteUser(user.getId());
		return user;
	}
	
}
