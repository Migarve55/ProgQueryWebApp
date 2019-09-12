package es.uniovi.controllers;

import java.security.Principal;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uniovi.analyzer.tasks.AnalyzerTask;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;

@Controller
public class ResultController {
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@RequestMapping("/result/list")
	public String list(Model model, Principal principal, Pageable pageable) {
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
		try {
			task.get();
		} catch (CancellationException | InterruptedException e) {
			redirect.addFlashAttribute("error", "error.taskCancelled");
			return "redirect:/";
		} catch (ExecutionException e) {
			redirect.addFlashAttribute("error", getRootCause(e).getLocalizedMessage());
			return "redirect:/";
		} 
		// Get result
		Result result = resultService.getLastFromUser(user);
		if (result == null) {
			redirect.addFlashAttribute("error", "error.noReport");
			return "redirect:/";
		}
		return "redirect:/result/" + result.getId();
	}
	
	private Throwable getRootCause(Throwable t) {
		return t.getCause() == null ? t : getRootCause(t.getCause());
	}
 	
	@RequestMapping("/result/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal, RedirectAttributes redirect) {
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
		model.addAttribute("problems", result.getProblems());
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
