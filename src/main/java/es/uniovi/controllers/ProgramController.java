package es.uniovi.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uniovi.controllers.exceptions.ForbiddenException;
import es.uniovi.controllers.exceptions.NotFoundException;
import es.uniovi.entities.Program;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ProgramService;
import es.uniovi.services.QueryService;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;
import es.uniovi.tasks.AbstractTask;
import es.uniovi.tasks.PlaygroundTask;

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
			throw new NotFoundException();
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (!program.getUser().equals(user))
			throw new ForbiddenException();
		if (!isTaskDone(user))
			return "redirect:/analyzer/loading";
		// Finally analyze
		model.addAttribute("program", program);
		model.addAttribute("queriesList", queryService.getAvailableQueriesForUser(user));
		return "program/analyze";
	}
	
	@PostMapping("/program/analyze/{id}")
	public String postAnalyze(Principal principal, @PathVariable Long id, @RequestParam("queries[]") String[] queries) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Program program = programService.findProgram(id);
		if (program == null)
			throw new NotFoundException();
		if (!program.getUser().equals(user))
			throw new ForbiddenException();
		if (isTaskDone(user))
			analyzerService.analyzeProgram(user, program, queries);
		return "redirect:/analyzer/loading";
	}

	@GetMapping("/program/playground")
	public String getPlaygroundAnalyze(Principal principal, Model model, @RequestParam Map<String,String> params) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		String querySource = params.get("querySource");
		String programSource = params.get("programSource");
		String noResult = params.get("noResult");
		model.addAttribute("querySource", querySource != null ? querySource : "");
		model.addAttribute("programSource", programSource != null ? programSource : "");
		// Load result 
		String results = "";
		if (params.containsKey("resultId")) {
			Long resultId = Long.parseLong(params.get("resultId"));
			Result result = resultService.getResult(resultId);
			if (!result.getProgram().getUser().equals(user)) {
				throw new ForbiddenException();
			}
			results = result.getTextSummary();
		}
		model.addAttribute("noResult", noResult != null);
		model.addAttribute("results", results);
		return "program/playground";
	}
	
	@PostMapping("/program/playground")
	public String postPlaygroundAnalyze(Principal principal, @RequestParam Map<String,String> params, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		String querySource = params.getOrDefault("querySource", "");
		String programId = params.get("programId_text");
		String programSource = params.getOrDefault("programSource", "");
		String useSource = params.get("useSource");
		String querySyntaxError = queryService.checkQuerySyntax(querySource);
		if (querySyntaxError != null) {
			redirect.addFlashAttribute("error", "error.query.text");
			redirect.addFlashAttribute("errOutput", querySyntaxError);
			return "redirect:" + PlaygroundTask.getBaseUrl(programSource, querySource);
		}
		// Analyze
		if (useSource != null) {
			if (programSource.trim().isEmpty()) {
				redirect.addFlashAttribute("error", "program.playground.noSource");
				return "redirect:" + PlaygroundTask.getBaseUrl(programSource, querySource);
			}
			analyzerService.analyzeSourceWithQueryText(user, querySource, programSource);
		} else if (programId != null) {
			Program program = programService.findProgramByName(programId);
			if (program == null) {
				redirect.addFlashAttribute("error", "program.playground.noProgram");
				return "redirect:" + PlaygroundTask.getBaseUrl(programSource, querySource);
			}
			analyzerService.analyzeProgramWithQueryText(user, querySource, program);
		} 
		return "redirect:/analyzer/loading";
	}
	
	@GetMapping("/program/detail/{id}")
	public String detail(Model model, Pageable pageable, @PathVariable Long id) {
		Program program = programService.findProgram(id);
		if (program == null)
			throw new NotFoundException();
		Page<Result> results = resultService.listByProgram(pageable, program);
		int maxSize = 5;
		model.addAttribute("maxSize", maxSize);
		model.addAttribute("program", program);
		model.addAttribute("resultsList", results.getContent());
		model.addAttribute("page", results);
		return "program/detail";
	}
	
	@GetMapping("/program/{id}/delete")
	public String delete(@PathVariable Long id, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Program program = programService.findProgram(id);
		if (program == null)
			throw new NotFoundException();
		if (!program.getUser().equals(user))
			throw new ForbiddenException();
		programService.deleteProgram(id);
		return "redirect:/program/list";
	}
	
	@GetMapping("/program/{programId}/result/{resultId}/delete")
	public String deleteProgramResult(@PathVariable Long programId, @PathVariable Long resultId, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Program program = programService.findProgram(programId);
		Result result = resultService.getResult(resultId);
		if (program == null || result == null)
			throw new NotFoundException();
		if (!program.getUser().equals(user))
			throw new ForbiddenException();
		if (!result.getProgram().getUser().equals(user))
			throw new ForbiddenException();
		resultService.deleteResult(result);
		return "redirect:/program/detail/" + programId;
	}
	
	
	@ResponseBody
	@RequestMapping("/program/search")
	public List<Program> getAutoCompleteQueryList(Principal principal, @RequestParam(value = "q") String searchText) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		return programService.findAvailableProgramsForUserByName(user, searchText, 10);
	}
	
	// Auxiliary methods
	
	/**
	 * 
	 * @param user
	 * @return true if no user is being computed for the user
	 */
	private boolean isTaskDone(User user) {
		AbstractTask task = analyzerService.getCurrentTask(user);
		if (task != null) {
			if (!task.isDone() && !task.isCancelled())
				return false;
		}
		return true;
	}
	
}
