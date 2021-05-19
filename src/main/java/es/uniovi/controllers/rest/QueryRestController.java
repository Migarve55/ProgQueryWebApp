package es.uniovi.controllers.rest;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.uniovi.entities.Analysis;
import es.uniovi.entities.User;
import es.uniovi.services.AnalysisService;
import es.uniovi.services.UsersService;
import es.uniovi.validators.AddAnalysisValidator;
import es.uniovi.validators.EditAnalysisValidator;

@RestController
public class QueryRestController extends BaseRestController {

	@Autowired
	private AnalysisService queryService;
	
	@Autowired 
	private UsersService usersService;
	
	@Autowired
	private AddAnalysisValidator addQueryValidator;
	
	@Autowired
	private EditAnalysisValidator editQueryValidator;
	
	@GetMapping("/api/analyses")
	public List<Analysis> list(Principal principal, @RequestParam(required = false) String user, @RequestParam(required = false) Boolean owner) {
		// Get queries
		List<Analysis> queries;
		if (user != null) { // From user
			User from = usersService.getUserByEmail(user);
			if (from == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not Found");
			}
			if (owner != null && owner)
				queries = queryService.getAnalysisFromUser(from);
			else
				queries = queryService.getAvailableAnalysesForUser(from);
		} else { // All public queries
			queries = queryService.getPublicAnalyses();
		}
		return queries
				.stream()
				.collect(Collectors.toList());
	}
	
	@GetMapping("/api/analyses/{id}")
	public Analysis get(@PathVariable(value = "id") String name, Principal principal) {
		Analysis query = queryService.findAnalysisByName(name);
		User user = usersService.getUserByEmail(principal.getName());
 		if (query == null) {
 			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not Found");
		} else if (!queryService.canSeeAnalysis(user, query)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can not access this query");
		} else {
			return query;
		}
	}
	
	@PostMapping("/api/analyses")
	public Analysis post(@Validated @RequestBody Analysis query, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		//Prepare query
		query.setUser(user);
		addQueryValidator.validate(query, result);
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed query, errors: " + toErrorList(result));
		}
		//Add it
		queryService.saveAnalysis(user, query);
		return query;
	}

	@PutMapping("/api/analyses/{id}")
	public Analysis edit(@PathVariable(value = "id") String name, @Validated @RequestBody Analysis query, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Analysis original = queryService.findAnalysisByName(name);
		editQueryValidator.validate(query, result);
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed query, errors: " + toErrorList(result));
		}
		if (original == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not Found");
		}
		if (!queryService.canModifyAnalysis(user, original)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify query");
		}
		//Finally save
		original.setDescription(query.getDescription());
		original.setQueryText(query.getQueryText());
		original.setPublicForAll(query.isPublicForAll());
		original.setPublicTo(query.getPublicTo());
		queryService.saveAnalysis(user, original);
		return original;
	}
	
	@DeleteMapping("/api/analyses/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(value = "id") String name, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		Analysis query = queryService.findAnalysisByName(name);
		if (query == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not Found");
		} else if (!queryService.canModifyAnalysis(user, query)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can not delete this query");
		} else {
			queryService.deleteAnalysis(user, query);
		}
	}
	
}
