package es.uniovi.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@GetMapping("/program/list")
	public String list(Model model, Principal principal, Pageable pageable) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Page<Program> programs = programService.listByUser(pageable, user);
		model.addAttribute("programsList", programs.getContent());
		model.addAttribute("page", programs);
		return "program/list";
	}
	
	@GetMapping("/program/analyze/{id}")
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
	
	@PostMapping("/program/analyze/{id}")
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

	@GetMapping("/program/playground")
	public String getPlaygroundAnalyze(
			Principal principal, Model model,
			@RequestParam Map<String,String> params) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		String queryName = params.get("queryName");
		String programSource = params.get("programSource");
		// Load source
		model.addAttribute("programSource", programSource != null ? programSource : "");
		// Load programs
		List<Program> programs = programService.listByUser(user);
		model.addAttribute("programs", programs);
		// Load queries
		model.addAttribute("queryText", "");
		if (queryName != null) {
			if (!queryName.trim().isEmpty()) {
				Optional<Query> query = queryService.getQueriesFromUserByName(user, queryName);
				if (query.isPresent()) {
					model.addAttribute("queryText", query.get().getQueryText());
				} else { // No ha sido encontrada
					model.addAttribute("error", "program.playground.queryNotFound");
				}
			}
		}
		// Load result 
		String results = "";
		if (params.containsKey("resultId")) {
			Long resultId = Long.parseLong(params.get("resultId"));
			Result result = resultService.getResult(resultId);
			results = result.getTextSummary();
		}
		model.addAttribute("results", results);
		return "program/playground";
	}
	
	@PostMapping("/program/playground")
	public String postPlaygroundAnalyze(Principal principal, @RequestParam Map<String,String> params, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		String queryText = params.get("queryText");
		String programId = params.get("programId");
		String programSource = params.get("programSource");
		// Analyze
		if (programSource != null) {
			if (programSource.trim().isEmpty()) {
				redirect.addFlashAttribute("error", "program.playground.noProgram");
				return "redirect:/program/playground";
			}
			analyzerService.analyzeSourceWithQueryText(user, queryText, programSource);
		} else if (programId != null) {
			Program program = programService.findProgram(Long.parseLong(programId));
			analyzerService.analyzeProgramWithQueryText(user, queryText, program);
		} 
		return "redirect:/analyzer/loading";
	}
	
	@GetMapping("/program/detail/{id}")
	public String detail(Model model, Pageable pageable, @PathVariable Long id) {
		Program program = programService.findProgram(id);
		if (program == null)
			return "redirect:/program/list";
		Page<Result> results = resultService.listByProgram(pageable, program);
		int maxSize = 5;
		model.addAttribute("maxSize", maxSize);
		model.addAttribute("program", program);
		model.addAttribute("resultsList", results.getContent());
		model.addAttribute("page", results);
		return "program/detail";
	}
	
	@GetMapping("/program/delete/{id}")
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
