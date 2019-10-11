package es.uniovi.controllers.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.uniovi.entities.Query;
import es.uniovi.entities.User;
import es.uniovi.services.QueryService;
import es.uniovi.services.UsersService;

@RestController
public class QueryRestController {

	@Autowired
	private QueryService queryService;
	
	@Autowired 
	private UsersService usersService;
	
	@GetMapping("/api/query/all")
	public List<Map<String, Object>> list(Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		List<Map<String, Object>> queries = new ArrayList<Map<String, Object>>();
		for (Query query : queryService.getQueriesFromUser(user)) {
			Map<String, Object> qMap = new HashMap<String, Object>();
			loadQueryIntoMap(query, qMap);
			queries.add(qMap);
		}
		return queries;
	}
	
	@GetMapping("/api/query")
	public Map<String, Object> get(@PathParam(value = "id") Long id, Principal principal, HttpServletResponse response) {
		Query query = queryService.findQuery(id);
		User user = usersService.getUserByEmail(principal.getName());
		Map<String, Object> responseBody = new HashMap<String, Object>();
 		if (query == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else if (!queryService.canSeeQuery(user, query)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			loadQueryIntoMap(query, responseBody);
			return responseBody;
		}
		return responseBody;
	}
	
	@PostMapping("/api/query")
	public void post(@RequestBody Map<String, Object> payload, Principal principal, HttpServletResponse response) {
		
	}
	
	@PutMapping("/api/query")
	public void put(@RequestBody Map<String, Object> payload, Principal principal, HttpServletResponse response) {
		
	}
	
	@DeleteMapping("/api/query")
	public void delete(@PathParam(value = "id") Long id, Principal principal, HttpServletResponse response) {
		User user = usersService.getUserByEmail(principal.getName());
		Query query = queryService.findQuery(id);
		if (query == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else if (!queryService.canModifyQuery(user, query)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			queryService.deleteQuery(query);
		}
	}
	
	private void loadQueryIntoMap(Query query, Map<String, Object> map) {
		map.put("name", query.getName());
		map.put("description", query.getDescription());
		map.put("query", query.getQueryText());
		map.put("user", query.getUser().getEmail());
		map.put("isPublic", query.isPublicForAll());
		map.put("modified", query.getModified());
	}
	
}
