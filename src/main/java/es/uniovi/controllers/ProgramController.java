package es.uniovi.controllers;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.uniovi.analyzer.tasks.AnalyzerTask;
import es.uniovi.entities.Program;
import es.uniovi.entities.Query;
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
		Program program = programService.findProgram(id);
		if (program == null)
			return "redirect:/program/list";
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (!program.getUser().equals(user))
			return "redirect:/program/list";
		if (!isTaskDone(user))
			return "redirect:/analyzer/loading";
		// Finally analyze
		model.addAttribute("program", program);
		model.addAttribute("queriesList", queryService.getAvailableQueriesForUser(user));
		return "program/analyze";
	}
	
	@RequestMapping(path = "/program/analyze/{id}", method = RequestMethod.POST)
	public String postAnalyze(Principal principal, @PathVariable Long id, @RequestParam("queries") String[] queries) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Program program = programService.findProgram(id);
		if (program == null)
			return "redirect:/program/list";
		if (!program.getUser().equals(user))
			return "redirect:/program/list";
		if (isTaskDone(user))
			analyzerService.analyzeProgram(user, program, queries);
		return "redirect:/analyzer/loading";
	}

	@RequestMapping(path = "/program/playground", method = RequestMethod.GET)
	public String getPlaygroundAnalyze(Principal principal, Model model, @RequestParam(required = false) String queryName, @RequestParam(required = false) String programName) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		// Cargar consulta
		model.addAttribute("queryText", "");
		if (queryName != null) {
			Optional<Query> query = queryService.getQueriesFromUserByName(user, queryName);
			if (query.isPresent()) {
				model.addAttribute("queryText", query.get().getQueryText());
			} else { // No ha sido encontrada
				model.addAttribute("error", "program.playground.queryNotFound");
			}
		}
		return "program/playground";
	}
	
	@RequestMapping(path = "/program/playground", method = RequestMethod.POST)
	public String postPlaygroundAnalyze(Model model, Principal principal, @RequestParam("qrText") String queryText, @RequestParam("pgSource") String programSource) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		analyzerService.analyzeSourceWithQueryText(user, queryText, programSource);
		return "redirect:/analyzer/loading";
	}
	
	@RequestMapping("/program/detail/{id}")
	public String detail(Model model, Pageable pageable, @PathVariable Long id) {
		Program program = programService.findProgram(id);
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
		Program program = programService.findProgram(id);
		if (program == null)
			return "redirect:/program/list";
		if (!program.getUser().equals(user))
			return "redirect:/program/list";
		programService.deleteProgram(id);
		return "redirect:/program/list";
	}
	
	// Auxiliary methods
	
	/**
	 * 
	 * @param user
	 * @return true if no user is being computed for the user
	 */
	private boolean isTaskDone(User user) {
		AnalyzerTask task = analyzerService.getCurrentTask(user);
		if (task != null) {
			if (!task.isDone() && !task.isCancelled())
				return false;
		}
		return true;
	}
	
}
