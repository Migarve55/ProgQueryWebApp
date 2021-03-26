package es.uniovi.controllers.rest;

import java.util.List;
import java.util.Map;

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
	
	// Los servicios REST de GET all the XXXX of the User "pepe", deben tener la signatura GET /api/XXX?user={user}, no GET /api/analyses

	@GetMapping("/api/users/{user}")
	public Map<String, Object> getUser(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		return userToMap(user);
	}
	
	@GetMapping("/api/users/{user}/programs")
	public List<Map<String, Object>> getUserPrograms(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		return programToMapList(user.getPrograms());
	}

	@GetMapping("/api/users/{user}/analyses")
	public List<Map<String, Object>> getUserAnalyses(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		return queriesToMapList(user.getQueries());
	}

	@GetMapping("/api/users/{user}/results")
	public List<Map<String, Object>> getUserResults(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		List<Result> results = resultService.getResultsByUser(user);
		return resultsToMapList(results);
	}

	@PostMapping("/api/users")
	public Map<String, Object> createUser(@Validated @RequestBody User user, BindingResult result) {
		addUserValidator.validate(user, result);
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed user, errors: " + toErrorList(result));
		}
		usersService.addUser(user);
		return userToMap(user);
	}
	
	@PutMapping("/api/users/{user}")
	public Map<String, Object> updateUser(@PathVariable(value = "user") String email, @Validated @RequestBody User user, BindingResult result) {
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
		return userToMap(user);
	}
	
	@DeleteMapping("/api/users/{user}")
	public Map<String, Object> deleteUser(@PathVariable(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		usersService.deleteUser(user.getId());
		return userToMap(user);
	}
	
}
