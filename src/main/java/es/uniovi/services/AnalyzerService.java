package es.uniovi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import es.uniovi.analyzer.tasks.AnalyzerTask;
import es.uniovi.analyzer.tasks.file.FileAnalyzerCallable;
import es.uniovi.analyzer.tasks.github.GithubCodeAnalyzerCallable;
import es.uniovi.analyzer.tasks.zip.ZipAnalizerCallable;
import es.uniovi.analyzer.tools.ToolFactory;
import es.uniovi.analyzer.tools.compilators.CompilerTool;
import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.QueryDto;
import es.uniovi.entities.Problem;
import es.uniovi.entities.Query;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.repositories.ProblemsRepository;
import es.uniovi.repositories.QueriesRepository;
import es.uniovi.repositories.ResultsRepository;

@Service
public class AnalyzerService {
	
	private Map<User,AnalyzerTask> usersTasks = new ConcurrentHashMap<User,AnalyzerTask>();
	private ExecutorService executor = Executors.newFixedThreadPool(4); 
	
	@Autowired 
	private ResultsRepository resultsRepository;
	
	@Autowired
	private ProblemsRepository problemsRepository;
	
	@Autowired
	private QueriesRepository queriesRepository;
	
	public AnalyzerTask getCurrentTask(User user) {
		return usersTasks.get(user);
	}
	
	private void setCurrentTask(User user, AnalyzerTask task) {
		usersTasks.put(user, task);
	}
	
	public void cancelCurrentTask(User user) {
		getCurrentTask(user).cancel(false);
	}
	
	public void analyzeFile(User user, MultipartFile file, String compOp, String args, String[] queries) throws IOException {
		launchAnalyzerTask(user, file.getOriginalFilename(), compOp, new FileAnalyzerCallable(args, file.getOriginalFilename(), file.getInputStream()), queries);
	}
	
	public void analyzeZip(User user, MultipartFile zip, String compOp, String args, String[] queries) throws IOException {
		launchAnalyzerTask(user, zip.getOriginalFilename(), compOp, new ZipAnalizerCallable(args, zip.getInputStream()), queries);
	}

	public void analyzeGitRepo(User user, String repoUrl, String compOp, String args, String[] queries) {
		launchAnalyzerTask(user, repoUrl, compOp, new GithubCodeAnalyzerCallable(repoUrl, args), queries);
	}
	
	private void launchAnalyzerTask(User user, String name, String compOp, AbstractAnalyzerCallable callable, String[] queries) {
		List<Query> queriesList = getQueries(queries, user);
		callable.setQueries(toQueryDto(queriesList));
		callable.setCompiler(getCompilationTool(compOp));
		AnalyzerTask oldTask = getCurrentTask(user);
		if (oldTask != null) {
			if (!oldTask.isDone())
				oldTask.cancel(false);
		}
		AnalyzerTask task = new AnalyzerTask(callable);
		task.setCallback((errors) -> {
			createReport(user, name, errors);
		});
		executor.execute(task);
		setCurrentTask(user, task);
	}
	
	private CompilerTool getCompilationTool(String compilator) {
		switch (compilator) {
			case "java":
				return ToolFactory.getJavaCompilerTool();
			case "maven":
				return ToolFactory.getMavenCompilerTool();
			default:
				return ToolFactory.getJavaCompilerTool();
		}
	}
	
	@Transactional
	private void createReport(User user, String name, List<ProblemDto> problems) {
		Result result = new Result();
		result.setUser(user);
		result.setTimestamp(new Date());
		result.setName(name);
		result = resultsRepository.save(result);
		for (ProblemDto problemDto : problems) {
			Problem problem = new Problem();
			problem.setResult(result);
			problem.setQuery(queriesRepository.findByName(problemDto.getQueryName()));
			problem.setLine((int) problemDto.getLine());
			problem.setColumn((int) problemDto.getColumn());
			problem.setCompilationUnit(problemDto.getFile());
			problemsRepository.save(problem);
		}
	}
	
	private List<Query> getQueries(String[] queriesIds, User user) {
		List<Query> result = new ArrayList<Query>();
		for (String queryId : queriesIds) {
			if (queryId.endsWith("*")) {
			    String regex = queryId.replace(".", "\\.");
			    regex = queryId.replace("*", "");
			    regex = String.format("^%s[^\\.]*", regex);
				result.addAll(queriesRepository.findAllByFamily(regex, user));
			} else {
				Query query = queriesRepository.findByNameAndUser(queryId, user);
				if (query != null)
					result.add(query);
			}
		}
		return result;
	}
	
	private List<QueryDto> toQueryDto(List<Query> queries) {
		return queries.stream()
				.map((q) -> toQueryDto(q))
				.collect(Collectors.toList());
	}
	
	private QueryDto toQueryDto(Query query) {
		QueryDto queryDto = new QueryDto();
		queryDto.setName(query.getName());
		queryDto.setQueryText(query.getQueryText());
		return queryDto;
	}

	@PreDestroy
	public void shutdownThreadExecutor() {
		executor.shutdown();
	}
	
}
