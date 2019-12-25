package es.uniovi.controllers.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.uniovi.entities.Query;
import es.uniovi.entities.User;
import es.uniovi.services.QueryService;
import es.uniovi.services.UsersService;
import es.uniovi.validators.AddQueryValidator;
import es.uniovi.validators.EditQueryValidator;

@RestController
public class QueryRestController {

	@Autowired
	private QueryService queryService;
	
	@Autowired 
	private UsersService usersService;
	
	@Autowired
	private AddQueryValidator addQueryValidator;
	
	@Autowired
	private EditQueryValidator editQueryValidator;
	
	@GetMapping("/api/query")
	public List<Map<String, Object>> list(Principal principal, @RequestParam(required = false) String userId) {
		// Get queries
		List<Query> queries;
		if (userId != null) { // From user
			try {
				Long id = Long.parseLong(userId);
				User from = usersService.getUser(id);
				if (from == null) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not Found");
				}
				queries = queryService.getAvailableQueriesForUser(from);
			} catch (NumberFormatException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId should be a number");
			}
		} else { // All public queries
			queries = queryService.getPublicQueries();
		}
		// Create response body
		List<Map<String, Object>> responseBody = new ArrayList<Map<String, Object>>();
		for (Query query : queries) {
			Map<String, Object> qMap = new HashMap<String, Object>();
			loadQueryIntoMap(query, qMap);
			responseBody.add(qMap);
		}
		return responseBody;
	}
	
	@GetMapping("/api/query/{id}")
	public Map<String, Object> get(@PathVariable(value = "id") Long id, Principal principal) {
		Query query = queryService.findQuery(id);
		User user = usersService.getUserByEmail(principal.getName());
		Map<String, Object> responseBody = new HashMap<String, Object>();
 		if (query == null) {
 			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not Found");
		} else if (!queryService.canSeeQuery(user, query)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can not access this query");
		} else {
			loadQueryIntoMap(query, responseBody);
			return responseBody;
		}
	}
	
	@PostMapping("/api/query")
	public Map<String, Object> post(@Validated @RequestBody Query query, BindingResult result, Principal principal) {
		Map<String, Object> responseBody = new HashMap<String, Object>();
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		//Prepare query
		query.setUser(user);
		addQueryValidator.validate(query, result);
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed query, errors: " + toErrorList(result));
		}
		//Add it
		queryService.saveQuery(query);
		loadQueryIntoMap(query, responseBody);
		return responseBody;
	}

	@PutMapping("/api/query")
	public Map<String, Object> edit(@Validated @RequestBody Query query, BindingResult result, Principal principal) {
		Map<String, Object> responseBody = new HashMap<String, Object>();
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		editQueryValidator.validate(query, result);
		Query original = queryService.findQuery(query.getId());
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed query, errors: " + toErrorList(result));
		}
		if (original == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not Found");
		}
		if (!queryService.canModifyQuery(user, original)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify query");
		}
		//Finally save
		original.setDescription(query.getDescription());
		original.setQueryText(query.getQueryText());
		original.setPublicForAll(query.isPublicForAll());
		original.setPublicTo(query.getPublicTo());
		queryService.saveQuery(original);
		loadQueryIntoMap(original, responseBody);
		return responseBody;
	}
	
	@DeleteMapping("/api/query/{id}")
	public void delete(@PathVariable(value = "id") Long id, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		Query query = queryService.findQuery(id);
		if (query == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not Found");
		} else if (!queryService.canModifyQuery(user, query)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can not delete this query");
		} else {
			queryService.deleteQuery(query);
		}
	}
	
	// Utility
	
	private String toErrorList(BindingResult result) {
		StringBuilder sb = new StringBuilder();
		List<ObjectError> errors = result.getAllErrors();
		if (!errors.isEmpty())
			sb.append(errors.get(0).getCode());
		for (int i = 1;i < errors.size();i++) {
			sb.append(", ");
			sb.append(errors.get(i).getCode());
		}
		return sb.toString();
	}
	
	private void loadQueryIntoMap(Query query, Map<String, Object> map) {
		map.put("id", query.getId());
		map.put("name", query.getName());
		map.put("description", query.getDescription());
		map.put("query", query.getQueryText());
		map.put("user", query.getUser().getEmail());
		map.put("isPublic", query.isPublicForAll());
		map.put("publicTo", query
				.getPublicTo().stream()
				.map(user -> user.getEmail())
				.collect(Collectors.toList())
			);
		map.put("modified", query.getModified());
	}
	
}
