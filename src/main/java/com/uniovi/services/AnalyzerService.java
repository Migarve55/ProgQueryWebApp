package com.uniovi.services;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.tasks.AnalyzerTask;
import com.uniovi.tasks.callables.AbstractAnalyzerCallable;
import com.uniovi.tasks.callables.FileAnalyzerCallable;
import com.uniovi.tasks.callables.GithubCodeAnalyzerCallable;
import com.uniovi.tasks.callables.ZipAnalizerCallable;

@Service
public class AnalyzerService {
	
	@Autowired
	private HttpSession session;
	
	private ExecutorService executor = Executors.newFixedThreadPool(4); 
	
	public void analyzeFile(File file, String args) throws IOException {
		launchAnalyzerTask(new FileAnalyzerCallable(args, file));
	}
	
	public void analyzeZip(File zip, String args) throws IOException {
		launchAnalyzerTask(new ZipAnalizerCallable(args, zip));
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
		AnalyzerTask task = new AnalyzerTask(callable, () -> {
			//session.setAttribute("task", null);
		});
		executor.execute(task);
		session.setAttribute("task", task);
	}

	@PreDestroy
	public void clearMovieCache() {
		executor.shutdown();
	}
	
}
