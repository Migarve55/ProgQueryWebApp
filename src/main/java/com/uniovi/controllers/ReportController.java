package com.uniovi.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniovi.analyzer.tasks.AnalyzerTask;
import com.uniovi.analyzer.tools.reporter.CodeError;

@Controller
public class ReportController {
	
	@Autowired
	private HttpSession session;
	
	@RequestMapping("/loading")
	public String getLoading(RedirectAttributes redirect) {
		AnalyzerTask task = (AnalyzerTask) session.getAttribute("task");
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
	@RequestMapping("/progress")
	public Map<String, String> getProgress(HttpServletResponse response) {
		AnalyzerTask task = (AnalyzerTask) session.getAttribute("task");
		if (task == null) {
			response.setStatus(403);
			return null;
		} 
		//Response
		Map<String, String> map = new HashMap<>();
		map.put("progress", String.format("%d", task.getProgress()));
		map.put("status", task.getStatus());
		return map;
	}
	
	@RequestMapping("/cancel")
	public String cancelTask() {
		AnalyzerTask task = (AnalyzerTask) session.getAttribute("task");
		task.cancel(false);
		return "redirect:/";
	}
	
	@RequestMapping("/report")
	public String getReport(Model model, RedirectAttributes redirect) {
		AnalyzerTask task = (AnalyzerTask) session.getAttribute("task");
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
