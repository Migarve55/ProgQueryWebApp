package es.uniovi.controllers.rest;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

	// /api/programs?user={email}
	@GetMapping("/api/programs")
	public List<Program> list(Principal principal, @RequestParam(value = "user") String email) {
		User user = usersService.getUserByEmail(email);
		return user.getPrograms()
				.stream()
				.collect(Collectors.toList());
	}

	@GetMapping("/api/programs/{id}")
	public Program get(@PathVariable(value = "id") String name, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		Program program = programService.findProgramByName(name);
		if (program == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not Found");
		} 
		if (!program.getUser().equals(user)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Program can not be accessed");
		} 
		return program;
	}
	
	@PostMapping("/api/programs")
	@ResponseStatus(HttpStatus.OK)
	public void postProgram(@RequestParam("url") String url, 
			@RequestParam(value = "args", required = false) String args, 
			@RequestParam(value = "programId") String programId,
			@RequestParam(value = "compOpt", required = false) String compOpt, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		analyzerService.uploadGitRepo(user, programId, url, compOpt, args);
	}
	
	@PutMapping("/api/programs/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void putProgram(
			@PathVariable(value = "id") String name,
			@RequestParam("url") String url, 
			@RequestParam(value = "args", required = false) String args, 
			@RequestParam(value = "compOpt", required = false) String compOpt, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		Program program = programService.findProgramByName(name);
		if (program == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not Found");
		} 
		programService.deleteProgram(program.getId());
		analyzerService.uploadGitRepo(user, program.getName(), url, compOpt, args);
	}
	
	/**
	 * @param id
	 * @param principal
	 * @param response
	 */

	@DeleteMapping("/api/programs/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(value = "id") String name, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		Program program = programService.findProgramByName(name);
		if (program == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not Found");
		} 
		if (!program.getUser().equals(user)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this program");
		} 
		programService.deleteProgram(program.getId());
		
	}

}
