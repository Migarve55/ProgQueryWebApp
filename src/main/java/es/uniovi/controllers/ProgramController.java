package es.uniovi.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.uniovi.entities.Program;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ProgramService;
import es.uniovi.services.QueryService;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;

@Controller
public class ProgramController {
	
	@Autowired
	private ProgramService programService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private QueryService queryService;
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@Autowired
	private UsersService usersService;

	@RequestMapping("/program/list")
	public String list(Model model, Principal principal, Pageable pageable) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Page<Program> programs = programService.listByUser(pageable, user);
		model.addAttribute("programsList", programs.getContent());
		model.addAttribute("page", programs);
		return "program/list";
	}
	
	@RequestMapping(path = "/program/analyze/{id}", method = RequestMethod.GET)
	public String getAnalyze(Model model, Principal principal, @PathVariable Long id) {
		Program program = programService.getProgram(id);
		if (program == null)
			return "redirect:/program/list";
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (!program.getUser().equals(user))
			return "redirect:/program/list";
		model.addAttribute("program", program);
		model.addAttribute("queriesList", queryService.getAvailableQueriesForUser(user));
		return "program/analyze";
	}
	
	@RequestMapping(path = "/program/analyze/{id}", method = RequestMethod.POST)
	public String postAnalyze(Principal principal, @PathVariable Long id, @RequestParam("queries") String[] queries) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Program program = programService.getProgram(id);
		if (program == null)
			return "redirect:/program/list";
		if (!program.getUser().equals(user))
			return "redirect:/program/list";
		analyzerService.analyzeProgram(user, program, queries);
		return "redirect:/analyzer/loading";
	}
	
	@RequestMapping("/program/detail/{id}")
	public String detail(Model model, Pageable pageable, @PathVariable Long id) {
		Program program = programService.getProgram(id);
		if (program == null)
			return "redirect:/program/list";
		Page<Result> results = resultService.listByProgram(pageable, program);
		model.addAttribute("program", program);
		model.addAttribute("resultsList", results.getContent());
		model.addAttribute("page", results);
		return "program/detail";
	}
	
	@RequestMapping("/program/delete/{id}")
	public String delete(@PathVariable Long id, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Program program = programService.getProgram(id);
		if (program == null)
			return "redirect:/program/list";
		if (!program.getUser().equals(user))
			return "redirect:/program/list";
		programService.deleteProgram(id);
		return "redirect:/program/list";
	}
	
}
