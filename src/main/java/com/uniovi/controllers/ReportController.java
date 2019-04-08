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

import com.uniovi.entities.CodeError;
import com.uniovi.tasks.AnalyzerTask;

@Controller
public class ReportController {
	
	@Autowired
	private HttpSession session;
	
	@RequestMapping("/loading")
	public String getLoading() {
		AnalyzerTask task = (AnalyzerTask) session.getAttribute("task");
		if (task == null) {
			return "";
		} else if (task.isCancelled()) {
			return "redirect:/";
		} else if (task.isDone()) {
			return "redirect:/report";
		}
		return "loading";
	}
	
	@RequestMapping("/progress")
	@ResponseBody
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
	
	@RequestMapping("/report")
	public String getReport(Model model) {
		AnalyzerTask task = (AnalyzerTask) session.getAttribute("task");
		if (task == null)
			return "redirect:/";
		if (task.isCancelled())
			return "redirect:/";
		if (!task.isDone())
			return "redirect:/progress/";
		//Is done
		List<CodeError> errorList = null;
		try {
			errorList = task.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return "redirect:/";
		} 
		model.addAttribute("errorList", errorList);
		return "report";
	}

}
