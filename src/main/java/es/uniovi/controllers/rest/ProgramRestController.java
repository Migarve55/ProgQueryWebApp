package es.uniovi.controllers.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ProgramRestController {

	@GetMapping("/api/program")
	public List<Map<String, Object>> list(Principal principal) {
		List<Map<String, Object>> responseBody = new ArrayList<Map<String, Object>>();

		return responseBody;
	}
	
	@GetMapping("/api/program/{id}")
	public Map<String, Object> get(@PathVariable(value = "id") Long id, Principal principal, HttpServletResponse response) {
		Map<String, Object> responseBody = new HashMap<String, Object>();

		return responseBody;
	}
	
	@DeleteMapping("/api/program/{id}")
	public void delete(@PathParam(value = "id") Long id, Principal principal, HttpServletResponse response) {
		
	}
	
}
