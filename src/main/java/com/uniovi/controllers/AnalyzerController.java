package com.uniovi.controllers;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniovi.analyzer.tasks.AnalyzerTask;
import com.uniovi.services.AnalyzerService;

@Controller
public class AnalyzerController {

	@Autowired
	private AnalyzerService analyzerService;

	@RequestMapping(path = "/analyzer/file", method = RequestMethod.GET)
	public String getAnalizeFile() {
		if (!isTaskDone())
			return "redirect:/loading";
		return "/analyzer/file";
	}

	@RequestMapping(path = "/analyzer/file", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public String postAnalizeFile(@RequestParam("file") MultipartFile file, @RequestParam("args") String args, RedirectAttributes redirect) {
		try {
			analyzerService.analyzeFile(file, args);
		} catch (IOException e) {
			e.printStackTrace();
			redirect.addFlashAttribute("error", "error.fileError");
			return "redirect:/";
		}
		return "redirect:/loading";
	}

	@RequestMapping(path = "/analyzer/zip", method = RequestMethod.GET)
	public String getAnalizeZip() {
		if (!isTaskDone())
			return "redirect:/loading";
		return "/analyzer/zip";
	}

	@RequestMapping(path = "/analyzer/zip", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public String postAnalizeZip(@RequestParam("zip") MultipartFile zip, @RequestParam("args") String args, RedirectAttributes redirect) {
		try {
			analyzerService.analyzeZip(zip, args);
		} catch (IOException e) {
			e.printStackTrace();
			redirect.addFlashAttribute("error", "error.fileError");
			return "redirect:/";
		}
		return "redirect:/loading";
	}

	@RequestMapping(path = "/analyzer/git", method = RequestMethod.GET)
	public String getAnalizeGit() {
		if (!isTaskDone())
			return "redirect:/loading";
		return "/analyzer/git";
	}

	@RequestMapping(path = "/analyzer/git", method = RequestMethod.POST)
	public String postAnalizeGit(@RequestParam("url") String url, @RequestParam("args") String args) {
		analyzerService.analyzeGitRepo(url, args);
		return "redirect:/loading";
	}

	// Auxiliary methods

	private boolean isTaskDone() {
		AnalyzerTask task = analyzerService.getCurrentTask();
		if (task != null) {
			if (!task.isDone() && !task.isCancelled())
				return false;
		}
		return true;
	}

}
