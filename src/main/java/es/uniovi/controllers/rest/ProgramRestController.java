package es.uniovi.controllers.rest;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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

import es.uniovi.entities.Program;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ProgramService;
import es.uniovi.services.UsersService;

@RestController
public class ProgramRestController extends BaseRestController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private ProgramService programService;
	
	@Autowired
	private AnalyzerService analyzerService;

	@GetMapping("/api/programs")
	public List<Map<String, Object>> list(Principal principal, @RequestParam(value = "email", required = true) String email) {
		User user = usersService.getUserByEmail(email);
		return programToMapList(user.getPrograms());
	}

	@GetMapping("/api/programs/{id}")
	public Map<String, Object> get(@PathVariable(value = "id") Long id, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		Program program = programService.findProgram(id);
		if (program == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not Found");
		} else if (!program.getUser().equals(user)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Program can not be accessed");
		} else {
			return loadProgramIntoMap(program);
		}
	}
	
	@PostMapping("/api/program")
	public void postAnalizeGit(@RequestParam("url") String url, 
			@RequestParam(value = "args", required = false) String args, 
			@RequestParam(value = "compOpt", required = false) String compOpt, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		analyzerService.uploadGitRepo(user, url, compOpt, args);
	}

	@DeleteMapping("/api/programs/{id}")
	public void delete(@PathVariable(value = "id") Long id, Principal principal, HttpServletResponse response) {
		User user = usersService.getUserByEmail(principal.getName());
		Program program = programService.findProgram(id);
		if (program == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else if (!program.getUser().equals(user)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			programService.deleteProgram(program.getId());
		}
	}

}
