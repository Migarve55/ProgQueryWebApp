package com.uniovi.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniovi.analyzer.tasks.AnalyzerTask;
import com.uniovi.analyzer.tools.reporter.CodeError;
import com.uniovi.services.AnalyzerService;

@Controller
public class ResultController {
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@RequestMapping("/analysis/{id}/loading")
	public String getLoading(@PathVariable Long id, RedirectAttributes redirect) {
		AnalyzerTask task = analyzerService.getCurrentTask();
		if (task == null) {
			return "";
		} else if (task.isCancelled()) {
			redirect.addFlashAttribute("error", "error.taskCancelled");
			return "redirect:/";
		} else if (task.isDone()) {
			return "redirect:/report";
		}
		return "loading";
	}
	
	@ResponseBody
	@RequestMapping("/analysis/{id}/progress")
	public Map<String, Object> getProgress(@PathVariable Long id, HttpServletResponse response) {
		AnalyzerTask task = analyzerService.getCurrentTask();
		if (task == null) {
			response.setStatus(400);
			return null;
		} 
		//Response
		Map<String, Object> map = new HashMap<>();
		map.put("progress", task.getProgress());
		map.put("status", task.getStatus());
		map.put("error", task.isCancelled());
		return map;
	}
	
	@RequestMapping("/analysis/{id}/cancel")
	public String cancelTask(@PathVariable Long id) {
		analyzerService.cancelCurrentTask();
		return "redirect:/";
	}
	
	@RequestMapping("/analysis/{id}")
	public String getReport(Model model, @PathVariable Long id, RedirectAttributes redirect) {
		AnalyzerTask task = analyzerService.getCurrentTask();
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
		return "report";
	}

}
