package es.uniovi.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uniovi.entities.Program;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ProgramService;
import es.uniovi.services.QueryService;
import es.uniovi.services.UsersService;
import es.uniovi.tasks.AbstractTask;
import es.uniovi.tasks.PlaygroundTask;

@Controller
public class AnalyzerController {

	@Autowired
	private AnalyzerService analyzerService;
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private QueryService queryService;
	
	@Autowired
	private ProgramService programService;
	
	@GetMapping("/analyzer/playground")
	public String getPlaygroundAnalyze(Model model, @RequestParam Map<String,String> params) {
		String querySource = params.get("querySource");
		String programSource = params.get("programSource");
		String noResult = params.get("noResult");
		model.addAttribute("querySource", querySource != null ? querySource : "");
		model.addAttribute("programSource", programSource != null ? programSource : "");
		// Load result 
		String results = "";
		if (params.containsKey("resultMsg")) {
			results = params.get("resultMsg");
		}
		model.addAttribute("noResult", noResult != null);
		model.addAttribute("results", results);
		return "analyzer/playground";
	}
	
	@PostMapping("/analyzer/playground")
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

	@RequestMapping(path = "/analyzer/zip", method = RequestMethod.GET)
	public String getAnalizeZip(Model model, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (!isTaskDone(user))
			return "redirect:/analyzer/loading";
		model.addAttribute("queriesList", queryService.getAvailableQueriesForUser(user));
		return "analyzer/zip";
	}

	@RequestMapping(path = "/analyzer/zip", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public String postAnalizeZip(
			@RequestParam("zip") MultipartFile zip, 
			@RequestParam(value = "programName") String name,
			@RequestParam("compOpt") String compOpt,
			@RequestParam(value = "classpath", required = false) String classpath, 
			@RequestParam(value = "queries[]", required = false) String[] queries, 
            Principal principal, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (!programService.validateProgramName(name)) {
			redirect.addFlashAttribute("error", "error.program.regex");
			return "redirect:/analyzer/zip";
		}
		if (programService.isProgramNameDuplicated(name)) {
			redirect.addFlashAttribute("error", "error.program.name");
			return "redirect:/analyzer/zip";
		}
		try {
			if (isTaskDone(user))
				analyzerService.analyzeZip(user, name, zip, compOpt, classpath, queries);
		} catch (IOException e) {
			e.printStackTrace();
			redirect.addFlashAttribute("error", "error.fileError");
			return "redirect:/";
		}
		return "redirect:/analyzer/loading";
	}

	@RequestMapping(path = "/analyzer/git", method = RequestMethod.GET)
	public String getAnalizeGit(Model model, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (!isTaskDone(user))
			return "redirect:/analyzer/loading";
		model.addAttribute("queriesList", queryService.getAvailableQueriesForUser(user));
		return "analyzer/git";
	}

	@RequestMapping(path = "/analyzer/git", method = RequestMethod.POST)
	public String postAnalizeGit(@RequestParam("url") String url, 
			@RequestParam("classpath") String classpath, 
			@RequestParam(value = "programName") String name,
			@RequestParam("compOpt") String compOpt, 
			@RequestParam(value = "queries[]", required = false) String[] queries, 
			Principal principal, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (!programService.validateProgramName(name)) {
			redirect.addFlashAttribute("error", "error.program.regex");
			return "redirect:/analyzer/git";
		}
		if (programService.isProgramNameDuplicated(name)) {
			redirect.addFlashAttribute("error", "error.program.name");
			return "redirect:/analyzer/git";
		}
		if (isTaskDone(user))
			analyzerService.analyzeGitRepo(user, name, url, compOpt, classpath, queries);
		return "redirect:/analyzer/loading";
	}
	
	@RequestMapping("/analyzer/loading")
	public String getLoading(Principal principal, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		AbstractTask task = analyzerService.getCurrentTask(user);
		if (task == null) {
			redirect.addFlashAttribute("error", "error.noTask");
			return "redirect:/";
		} else if (task.isCancelled()) {
			redirect.addFlashAttribute("error", "error.taskCancelled");
			return "redirect:/";
		} else if (task.isDone()) {
			return "redirect:/result/last";
		}
		return "loading";
	}
	
	@ResponseBody
	@RequestMapping("/analyzer/progress")
	public Map<String, Object> getProgress(Principal principal, HttpServletResponse response) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		AbstractTask task = analyzerService.getCurrentTask(user);
		if (task == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} 
		//Response
		Map<String, Object> map = new HashMap<>();
		map.put("progress", task.getProgress());
		map.put("status", task.getStatus());
		map.put("error", task.isCancelled());
		return map;
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
