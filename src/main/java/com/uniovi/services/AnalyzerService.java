package com.uniovi.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import com.uniovi.analyzer.tasks.AnalyzerTask;
import com.uniovi.analyzer.tasks.file.FileAnalyzerCallable;
import com.uniovi.analyzer.tasks.github.GithubCodeAnalyzerCallable;
import com.uniovi.analyzer.tasks.zip.ZipAnalizerCallable;
import com.uniovi.entities.User;

@Service
public class AnalyzerService {
	
	private Map<User,AnalyzerTask> usersTasks = new ConcurrentHashMap<User,AnalyzerTask>();
	
	private ExecutorService executor = Executors.newFixedThreadPool(4); 
	
	public AnalyzerTask getCurrentTask(User user) {
		return usersTasks.get(user);
	}
	
	private void setCurrentTask(User user, AnalyzerTask task) {
		usersTasks.put(user, task);
	}
	
	public void cancelCurrentTask(User user) {
		getCurrentTask(user).cancel(false);
	}
	
	public void analyzeFile(User user, MultipartFile file, String args) throws IOException {
		launchAnalyzerTask(user, new FileAnalyzerCallable(args, file.getOriginalFilename(), file.getInputStream()));
	}
	
	public void analyzeZip(User user, MultipartFile zip, String args) throws IOException {
		launchAnalyzerTask(user, new ZipAnalizerCallable(args, zip.getInputStream()));
	}

	public void analyzeGitRepo(User user, String repoUrl, String args) {
		launchAnalyzerTask(user, new GithubCodeAnalyzerCallable(repoUrl, args));
	}
	
	private void launchAnalyzerTask(User user, AbstractAnalyzerCallable callable) {
		AnalyzerTask oldTask = getCurrentTask(user);
		if (oldTask != null) {
			if (!oldTask.isDone())
				oldTask.cancel(false);
		}
		AnalyzerTask task = new AnalyzerTask(callable);
		executor.execute(task);
		setCurrentTask(user, task);
	}

	@PreDestroy
	public void shutdownThreadExecutor() {
		executor.shutdown();
	}
	
}
