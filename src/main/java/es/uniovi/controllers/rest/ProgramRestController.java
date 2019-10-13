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

import es.uniovi.entities.Program;
import es.uniovi.entities.User;
import es.uniovi.services.ProgramService;
import es.uniovi.services.UsersService;

@RestController
public class ProgramRestController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private ProgramService programService;

	@GetMapping("/api/program")
	public List<Map<String, Object>> list(Principal principal) {
		List<Map<String, Object>> responseBody = new ArrayList<Map<String, Object>>();
		User user = usersService.getUserByEmail(principal.getName());
		for (Program program : programService.listByUser(user)) {
			Map<String, Object> pMap = new HashMap<String, Object>();
			loadProgramIntoMap(program, pMap);
			responseBody.add(pMap);
		}
		return responseBody;
	}

	@GetMapping("/api/program/{id}")
	public Map<String, Object> get(@PathVariable(value = "id") Long id, Principal principal,
			HttpServletResponse response) {
		Map<String, Object> responseBody = new HashMap<String, Object>();
		User user = usersService.getUserByEmail(principal.getName());
		Program program = programService.findProgram(id);
		if (program == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else if (!program.getUser().equals(user)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			loadProgramIntoMap(program, responseBody);
			return responseBody;
		}
		return responseBody;
	}

	@DeleteMapping("/api/program/{id}")
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

	private void loadProgramIntoMap(Program program, Map<String, Object> map) {
		map.put("id", program.getId());
		map.put("name", program.getName());
		map.put("results", program.getResults().size());
	}

}
