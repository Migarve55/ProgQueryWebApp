package com.uniovi.services;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import com.uniovi.analyzer.tasks.AnalyzerTask;
import com.uniovi.analyzer.tasks.FileAnalyzerCallable;
import com.uniovi.analyzer.tasks.GithubCodeAnalyzerCallable;
import com.uniovi.analyzer.tasks.ZipAnalizerCallable;

@Service
public class AnalyzerService {
	
	@Autowired
	private HttpSession session;
	
	private ExecutorService executor = Executors.newFixedThreadPool(4); 
	
	public void analyzeFile(MultipartFile file, String args) throws IOException {
		launchAnalyzerTask(new FileAnalyzerCallable(args, file.getOriginalFilename(), file.getInputStream()));
	}
	
	public void analyzeZip(MultipartFile zip, String args) throws IOException {
		launchAnalyzerTask(new ZipAnalizerCallable(args, zip.getInputStream()));
	}

	public void analyzeGitRepo(String repoUrl, String args) {
		launchAnalyzerTask(new GithubCodeAnalyzerCallable(repoUrl, args));
	}
	
	private void launchAnalyzerTask(AbstractAnalyzerCallable callable) {
		AnalyzerTask oldTask = (AnalyzerTask) session.getAttribute("task");
		if (oldTask != null) {
			if (!oldTask.isDone())
				oldTask.cancel(false);
		}
		AnalyzerTask task = new AnalyzerTask(callable);
		executor.execute(task);
		session.setAttribute("task", task);
	}

	@PreDestroy
	public void clearMovieCache() {
		executor.shutdown();
	}
	
}
