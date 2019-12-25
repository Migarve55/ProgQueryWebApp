package es.uniovi.controllers.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.uniovi.entities.Problem;
import es.uniovi.entities.Program;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ProgramService;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;

@RestController
public class ResultRestController {

	@Autowired
	private UsersService usersService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private ProgramService programService;
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@GetMapping("/api/result")
	public List<Map<String, Object>> list(Principal principal, @RequestParam(required = false) String programId) {
		List<Map<String, Object>> responseBody = new ArrayList<Map<String, Object>>();
		User user = usersService.getUserByEmail(principal.getName());
		// Get results
		List<Result> results;
		if (programId != null) {
			try {
				Long id = Long.parseLong(programId);
				Program program = programService.findProgram(id);
				if (program == null) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not Found");
				}
				results = program.getResults().stream().collect(Collectors.toList());
			} catch (NumberFormatException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "programId should be a number");
			}
		} else {
			results = resultService.getResultsByUser(user);
		}
		// Create response body
		for (Result result : results) {
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
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		} else if (!result.getProgram().getUser().equals(user)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Result cannot be accessed");
		} else {
			loadResultIntoMap(result, responseBody);
			return responseBody;
		}
	}
	
	@PostMapping("/api/result")
	public void analyzeProgram(Principal principal, @RequestParam String programId, @RequestParam String[] queries) {
		User user = usersService.getUserByEmail(principal.getName());
		try {
			Long id = Long.parseLong(programId);
			Program program = programService.findProgram(id);
			if (program == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not Found");
			}
			analyzerService.analyzeProgram(user, program, queries);
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "programId should be a number");
		}
	}

	@DeleteMapping("/api/result/{id}")
	public void delete(@PathVariable(value = "id") Long id, Principal principal, HttpServletResponse response) {
		User user = usersService.getUserByEmail(principal.getName());
		Result result = resultService.getResult(id);
		if (result == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		} else if (!result.getProgram().getUser().equals(user)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Result cannot be accessed");
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
