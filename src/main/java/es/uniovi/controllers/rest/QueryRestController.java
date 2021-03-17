package es.uniovi.controllers.rest;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.uniovi.entities.Query;
import es.uniovi.entities.User;
import es.uniovi.services.QueryService;
import es.uniovi.services.UsersService;
import es.uniovi.validators.AddQueryValidator;
import es.uniovi.validators.EditQueryValidator;

@RestController
public class QueryRestController extends BaseRestController {

	@Autowired
	private QueryService queryService;
	
	@Autowired 
	private UsersService usersService;
	
	@Autowired
	private AddQueryValidator addQueryValidator;
	
	@Autowired
	private EditQueryValidator editQueryValidator;
	
	@GetMapping("/api/analyses")
	public List<Map<String, Object>> list(Principal principal, @RequestParam(required = false) String user, @RequestParam(required = false) Boolean owner) {
		// Get queries
		List<Query> queries;
		if (user != null) { // From user
			User from = usersService.getUserByEmail(user);
			if (from == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not Found");
			}
			if (owner != null && owner)
				queries = queryService.getQueriesFromUser(from);
			else
				queries = queryService.getAvailableQueriesForUser(from);
		} else { // All public queries
			queries = queryService.getPublicQueries();
		}
		return queriesToMapList(queries);
	}
	
	@GetMapping("/api/analyses/{id}")
	public Map<String, Object> get(@PathVariable(value = "id") Long id, Principal principal) {
		Query query = queryService.findQuery(id);
		User user = usersService.getUserByEmail(principal.getName());
 		if (query == null) {
 			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not Found");
		} else if (!queryService.canSeeQuery(user, query)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can not access this query");
		} else {
			return loadQueryIntoMap(query);
		}
	}
	
	@PostMapping("/api/analyses")
	public Map<String, Object> post(@Validated @RequestBody Query query, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		//Prepare query
		query.setUser(user);
		addQueryValidator.validate(query, result);
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed user, errors: " + toErrorList(result));
		}
		//Add it
		queryService.saveQuery(user, query);
		return loadQueryIntoMap(query);
	}

	@PutMapping("/api/analyses")
	public Map<String, Object> edit(@Validated @RequestBody Query query, BindingResult result, Principal principal) {
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
		queryService.saveQuery(user, original);
		return loadQueryIntoMap(original);
	}
	
	@DeleteMapping("/api/analyses/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(value = "id") Long id, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		Query query = queryService.findQuery(id);
		if (query == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not Found");
		} else if (!queryService.canModifyQuery(user, query)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can not delete this query");
		} else {
			queryService.deleteQuery(user, query);
		}
	}
	
}
