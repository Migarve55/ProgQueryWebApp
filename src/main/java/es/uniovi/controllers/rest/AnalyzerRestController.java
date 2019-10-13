package es.uniovi.controllers.rest;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.UsersService;

@RestController
public class AnalyzerRestController {
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private AnalyzerService analyzerService;

	@PostMapping("/api/analyze")
	public void postAnalizeGit(@RequestParam("url") String url, @RequestParam("args") String args, 
			@RequestParam("compOpt") String compOpt, @RequestParam("queries") String[] queries, Principal principal) {
		User user = usersService.getUserByEmail(principal.getName());
		analyzerService.analyzeGitRepo(user, url, compOpt, args, queries);
	}
	
}
