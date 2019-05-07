package com.uniovi.controllers;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniovi.analyzer.tasks.AnalyzerTask;
import com.uniovi.analyzer.tools.reporter.CodeError;
import com.uniovi.entities.Result;
import com.uniovi.entities.User;
import com.uniovi.services.AnalyzerService;
import com.uniovi.services.ResultService;
import com.uniovi.services.UsersService;

@Controller
public class ResultController {
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private ResultService resultService;
	
	@RequestMapping("/result/list")
	public String list(Model model, Principal principal, Pageable pageable) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Page<Result> results = resultService.getResultsByUser(pageable, user);
		model.addAttribute("resultsList", results.getContent());
		model.addAttribute("page", results);
		return "result/list";
	}
	
	@RequestMapping("/result/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal, RedirectAttributes redirect) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		AnalyzerTask task = analyzerService.getCurrentTask(user);
		if (task == null) {
			redirect.addFlashAttribute("error", "error.noReport");
			return "redirect:/";
		} else if (task.isCancelled()) {
			redirect.addFlashAttribute("error", "error.taskCancelled");
			return "redirect:/";
		} else if (!task.isDone())
			return "redirect:/loading/";
		//Is done
		List<CodeError> errorList = null;
		try {
			errorList = task.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			redirect.addFlashAttribute("error", e.getCause().getLocalizedMessage());
			return "redirect:/";
		} 
		model.addAttribute("errorList", errorList);
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
		if (!result.getUser().equals(user)) {
			return "redirect:/result/list";
		}
		resultService.deleteResult(result);
		return "redirect:/result/list";
	}

}
