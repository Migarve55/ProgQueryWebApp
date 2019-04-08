package com.uniovi.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.uniovi.tasks.AnalyzerTask;
import com.uniovi.tasks.callables.AbstractAnalyzerCallable;
import com.uniovi.tasks.callables.GithubCodeAnalyzerCallable;

@Service
public class AnalyzerService {
	
	@Autowired
	private HttpSession session;
	
	private ExecutorService executor = Executors.newFixedThreadPool(4); 
	
	public void analyzeFile(Resource file, String args) {
		launchAnalyzerTask(null);
	}
	
	public void analyzeZip(Resource zip, String args) {
		launchAnalyzerTask(null);
	}

	public void analyzeGitRepo(String repoUrl, String args) {
		launchAnalyzerTask(new GithubCodeAnalyzerCallable(repoUrl, args));
	}
	
	private void launchAnalyzerTask(AbstractAnalyzerCallable callable) {
		AnalyzerTask task = new AnalyzerTask(callable, () -> {
			//session.setAttribute("task", null);
		});
		executor.execute(task);
		session.setAttribute("task", task);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		executor.shutdown();
	}
	
}
