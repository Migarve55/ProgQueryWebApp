package es.uniovi.controllers.rest;

import java.security.Principal;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.uniovi.entities.Program;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ProgramService;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;

@RestController
public class ResultRestController extends BaseRestController {

	@Autowired
	private UsersService usersService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private ProgramService programService;
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@GetMapping("/api/results")
	public List<Map<String, Object>> list(
			@RequestParam(required = false) Long programId,
			@RequestParam(required = false) Long analysisId,
			@RequestParam(required = false) String user) {		
		// Get results
		List<Result> results = new ArrayList<Result>();
		if (programId != null && analysisId != null) {
			results = resultService.getByProgramAndQuery(programId, analysisId);
		} else if (programId != null) {
			Program program = programService.findProgram(programId);
			if (program == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not Found");
			}
			results = program.getResults().stream().collect(Collectors.toList());
		} else if (user != null) {
			User u = usersService.getUserByEmail(user);
			results = resultService.getResultsByUser(u);
		} else if (analysisId != null) {
			results = resultService.getByQuery(analysisId);
		} 
		return resultsToMapList(results);
	}

	@GetMapping("/api/results/{id}")
	public Map<String, Object> get(@PathVariable(value = "id") Long id, Principal principal,
			HttpServletResponse response) {
		User user = usersService.getUserByEmail(principal.getName());
		Result result = resultService.getResult(id);
		if (result == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not Found");
		} else if (!result.getProgram().getUser().equals(user)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Result cannot be accessed");
		} else {
			return loadResultIntoMap(result);
		}
	}
	
	@PostMapping("/api/result")
	@ResponseStatus(HttpStatus.OK)
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
	@ResponseStatus(HttpStatus.OK)
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

}
