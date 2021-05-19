package es.uniovi.controllers;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uniovi.analyzer.exceptions.AnalyzerException;
import es.uniovi.entities.Problem;
import es.uniovi.entities.Analysis;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.services.AnalyzerService;
import es.uniovi.services.ResultService;
import es.uniovi.services.UsersService;
import es.uniovi.tasks.AbstractTask;

@Controller
public class ResultController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@RequestMapping("/result/list")
	public String list(Model model, Principal principal, @PageableDefault(value = 10) Pageable pageable) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Page<Result> results = resultService.getResultsByUser(pageable, user);
		// Change results list to adjust problems max size
		int maxSize = 5;
		model.addAttribute("resultsList", results.getContent());
		model.addAttribute("page", results);
		model.addAttribute("maxSize", maxSize);
		return "result/list";
	}
	
	@RequestMapping("/result/last") 
	public String last(Principal principal, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		AbstractTask task = analyzerService.getCurrentTask(user);
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
			Throwable exception = getRootCause(e);
			if (exception instanceof AnalyzerException) {
				AnalyzerException ae = (AnalyzerException) exception;
				redirect.addFlashAttribute("error", ae.getErrorCode());
				redirect.addFlashAttribute("errOutput", task.getRecordedOutput());
				return "redirect:" + task.getKoUrl();
			} else {
				logger.error("Unexpected exception for report of user '{}'", user.getEmail());
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} finally {
			// Clear user task
			analyzerService.clearUserTask(user);
		}
		return "redirect:" + task.getOkUrl();
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
		
		model.addAttribute("showLegend", shouldShowLegend(problems.getContent(), result));
		model.addAttribute("page", problems);
		model.addAttribute("problems", problems.getContent());
		model.addAttribute("timestamp", result.getTimestamp());
		model.addAttribute("queryProblems", result.getQueryExecutionProblems());
		return "result/detail";
	}
	
	@RequestMapping("/result/{id}/delete") 
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
	
	private boolean shouldShowLegend(List<Problem> problems, Result result) {
		return problems.stream().anyMatch(p -> {
			Analysis q = p.getAnalysis();
			if (q == null)
				return false;
			return q.getModified().after(result.getTimestamp());
		});
	}

}
