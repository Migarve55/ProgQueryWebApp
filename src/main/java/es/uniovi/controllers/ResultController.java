package es.uniovi.controllers;

import java.security.Principal;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uniovi.analyzer.tasks.AnalyzerTask;
import es.uniovi.entities.Problem;
import es.uniovi.entities.Program;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ProgramService;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;

@Controller
public class ResultController {
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private ProgramService programService;
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@RequestMapping("/result/list")
	public String list(Model model, Principal principal, @PageableDefault(value = 10) Pageable pageable) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Page<Result> results = resultService.getResultsByUser(pageable, user);
		model.addAttribute("resultsList", results.getContent());
		model.addAttribute("page", results);
		return "result/list";
	}
	
	@RequestMapping("/result/last") 
	public String last(Principal principal, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		AnalyzerTask task = analyzerService.getCurrentTask(user);
		// No task, return
		if (task == null) {
			redirect.addFlashAttribute("error", "error.noTask");
			return "redirect:/";
		}
		// Task is not done
		if (!task.isDone()) 
			return "redirect:/analyzer/loading";
		// Task is done, get
		try {
			task.get();
		} catch (CancellationException | InterruptedException e) {
			redirect.addFlashAttribute("error", "error.taskCancelled");
			return "redirect:/";
		} catch (ExecutionException e) {
			redirect.addFlashAttribute("error", getRootCause(e).getLocalizedMessage());
			return "redirect:/";
		} finally {
			// Clear user task
			analyzerService.clearUserTask(user);
		}
		if (task.hasCreatedReport()) {
			// Get result
			Result result = resultService.getLastFromUser(user);
			if (result == null) {
				redirect.addFlashAttribute("error", "error.noReport");
				return "redirect:/";
			}
			return "redirect:/result/" + result.getId();
		} else {
			// Get program
			Program program = programService.getLastFromUser(user);
			if (program == null) {
				redirect.addFlashAttribute("error", "error.noProgram");
				return "redirect:/";
			}
			return "redirect:/program/detail/" + program.getId();
		}
	}
	
	private Throwable getRootCause(Throwable t) {
		return t.getCause() == null ? t : getRootCause(t.getCause());
	}
 	
	@RequestMapping("/result/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal, RedirectAttributes redirect, @PageableDefault(value = 20) Pageable pageable) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Result result = resultService.getResult(id);
		if (result == null) {
			redirect.addFlashAttribute("error", "error.noReport");
			return "redirect:/";
		}
		if (!result.getProgram().getUser().equals(user)) {
			return "redirect:/";
		}
		Page<Problem> problems = resultService.getProblemsForResult(pageable, result);
		model.addAttribute("page", problems);
		model.addAttribute("problems", problems.getContent());
		model.addAttribute("timestamp", result.getTimestamp());
		return "result/detail";
	}
	
	@RequestMapping("/result/delete/{id}") 
	public String delete(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Result result = resultService.getResult(id);
		if (result == null) {
			return "redirect:/result/list";
		}
		if (!result.getProgram().getUser().equals(user)) {
			return "redirect:/result/list";
		}
		resultService.deleteResult(result);
		return "redirect:/result/list";
	}

}
