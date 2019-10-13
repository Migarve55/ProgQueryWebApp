package es.uniovi.controllers.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.uniovi.entities.Problem;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;

@RestController
public class ResultRestController {

	@Autowired
	private UsersService usersService;
	
	@Autowired
	private ResultService resultService;
	
	@GetMapping("/api/result")
	public List<Map<String, Object>> list(Principal principal) {
		List<Map<String, Object>> responseBody = new ArrayList<Map<String, Object>>();
		User user = usersService.getUserByEmail(principal.getName());
		for (Result result : resultService.getResultsByUser(user)) {
			Map<String, Object> rMap = new HashMap<String, Object>();
			loadResultIntoMap(result, rMap);
			responseBody.add(rMap);
		}
		return responseBody;
	}

	@GetMapping("/api/result/{id}")
	public Map<String, Object> get(@PathVariable(value = "id") Long id, Principal principal,
			HttpServletResponse response) {
		Map<String, Object> responseBody = new HashMap<String, Object>();
		User user = usersService.getUserByEmail(principal.getName());
		Result result = resultService.getResult(id);
		if (result == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else if (!result.getProgram().getUser().equals(user)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			loadResultIntoMap(result, responseBody);
			return responseBody;
		}
		return responseBody;
	}

	@DeleteMapping("/api/result/{id}")
	public void delete(@PathVariable(value = "id") Long id, Principal principal, HttpServletResponse response) {
		User user = usersService.getUserByEmail(principal.getName());
		Result result = resultService.getResult(id);
		if (result == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else if (!result.getProgram().getUser().equals(user)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			resultService.deleteResult(result);
		}
	}
	
	private void loadResultIntoMap(Result result, Map<String, Object> map) {
		map.put("id", result.getId());
		map.put("program", result.getProgram().getName());
		map.put("timeStamp", result.getTimestamp().toString());
		map.put("problems", result.getProblems().stream().map(p -> problemToMap(p)));
	}
	
	private Map<String, Object> problemToMap(Problem problem) {
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("compilationUnit", problem.getCompilationUnit());
		pMap.put("msg", problem.getMsg());
		pMap.put("query", problem.getQuery().getId());
		pMap.put("line", problem.getLine());
		pMap.put("col", problem.getCol());
		return pMap;
	}

}
