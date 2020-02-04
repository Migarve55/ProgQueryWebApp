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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.uniovi.analyzer.tasks.AbstractAnalyzerCallable;
import es.uniovi.analyzer.tasks.AnalyzerTask;
import es.uniovi.analyzer.tasks.file.FileAnalyzerCallable;
import es.uniovi.analyzer.tasks.github.GithubCodeAnalyzerCallable;
import es.uniovi.analyzer.tasks.program.ProgramAnalyzerCallable;
import es.uniovi.analyzer.tasks.zip.ZipAnalizerCallable;
import es.uniovi.analyzer.tools.ToolFactory;
import es.uniovi.analyzer.tools.compilators.CompilerTool;
import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.QueryDto;
import es.uniovi.entities.Problem;
import es.uniovi.entities.Program;
import es.uniovi.entities.Query;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.repositories.ProblemsRepository;
import es.uniovi.repositories.ProgramRepository;
import es.uniovi.repositories.QueriesRepository;
import es.uniovi.repositories.ResultsRepository;

@Service
public class AnalyzerService {
	
	private Map<User,AnalyzerTask> usersTasks = new ConcurrentHashMap<User,AnalyzerTask>();
	private ExecutorService executor = Executors.newFixedThreadPool(4); 
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private ResultsRepository resultsRepository;
	
	@Autowired
	private ProblemsRepository problemsRepository;
	
	@Autowired
	private ProgramRepository programRepository;
	
	@Autowired
	private QueriesRepository queriesRepository;
	
	public AnalyzerTask getCurrentTask(User user) {
		return usersTasks.get(user);
	}
	
	private void setCurrentTask(User user, AnalyzerTask task) {
		logger.info("User {} was assigned a new task {}", user.getEmail(), task);
		usersTasks.put(user, task);
	}
	
	/**
	 * This method just logs the event now.
	 * User "clearUserTask" to remove the task assigned to the user
	 * @param user
	 * @param task
	 */
	private void finalizeUserTask(User user, AnalyzerTask task) {
		logger.info("Task {} of user {} has ended", task, user.getEmail());
	}
	
	public void clearUserTask(User user) {
		usersTasks.remove(user);
	}
	
	public void cancelCurrentTask(User user) {
		AnalyzerTask task = getCurrentTask(user);
		if (task.cancel(false))
			logger.info("User {} cancelled their task {} succesfully", user.getEmail(), task);
		else
			logger.info("User {} could not cancel their task {}", user.getEmail(), task);
		finalizeUserTask(user, task);
	}
	
	/**
	 * 
	 * @param user
	 * @param file
	 * @param args
	 * @param queries
	 * @throws IOException if the java file could not be saved
	 */
	public void analyzeFile(User user, MultipartFile file, String args, String[] queries) throws IOException {
		launchAnalyzerTask(user, file.getOriginalFilename(), "java", new FileAnalyzerCallable(args, file.getOriginalFilename(), file.getInputStream()), queries);
	}
	
	/**
	 * 
	 * @param user
	 * @param zip
	 * @param compOp
	 * @param args
	 * @param queries
	 * @throws IOException if the zip file could not be saved
	 */
	public void analyzeZip(User user, MultipartFile zip, String compOp, String args, String[] queries) throws IOException {
		launchAnalyzerTask(user, zip.getOriginalFilename(), compOp, new ZipAnalizerCallable(args, zip.getInputStream()), queries);
	}

	/**
	 * 
	 * @param user
	 * @param repoUrl
	 * @param compOp
	 * @param args
	 * @param queries
	 */
	public void analyzeGitRepo(User user, String repoUrl, String compOp, String args, String[] queries) {
		launchAnalyzerTask(user, repoUrl, compOp, new GithubCodeAnalyzerCallable(repoUrl, args), queries);
	}
	
	/**
	 * 
	 * @param user
	 * @param repoUrl
	 * @param compOp
	 * @param args
	 */
	public void uploadGitRepo(User user, String repoUrl, String compOp, String args) {
		String[] queries = new String[0]; //Empty array with no queries
		launchAnalyzerTask(user, repoUrl, compOp, new GithubCodeAnalyzerCallable(repoUrl, args), queries);
	}
	
	/**
	 * Analyzes a program
	 * @param user
	 * @param program
	 * @param queries 
	 */
	public void analyzeProgram(User user, Program program, String[] queries) {
		AbstractAnalyzerCallable callable = new ProgramAnalyzerCallable(program.getProgramIdentifier());
		List<Query> queriesList = getQueries(queries, user);
		callable.setQueries(toQueryDto(queriesList));
		AnalyzerTask oldTask = getCurrentTask(user);
		if (oldTask != null) {
			if (!oldTask.isDone())
				oldTask.cancel(false);
		}
		AnalyzerTask task = new AnalyzerTask(callable);
		task.setCallback((errors) -> {
			createReport(user, program, errors);
			finalizeUserTask(user, task);
		});
		executor.execute(task);
		setCurrentTask(user, task);
		logger.info("User {} started program {} analysis", user.getEmail(), program.getProgramIdentifier());
	}
	
	/**
	 * Auxiliar method for the git,file and zip analyzers
	 * @param user User that requested the analysis
	 * @param name Name of the program
	 * @param compOp What compiler to use
	 * @param callable what callable to use
	 * @param queries what queries to use, can be null. If null, it will not create a report.
	 */
	private void launchAnalyzerTask(User user, String name, String compOp, AbstractAnalyzerCallable callable, String[] queries) {
		if (queries != null) {
			List<Query> queriesList = getQueries(queries, user);
			callable.setQueries(toQueryDto(queriesList));
		}
		callable.setCompiler(getCompilationTool(compOp));
		
		AnalyzerTask oldTask = getCurrentTask(user);
		if (oldTask != null) {
			if (!oldTask.isDone())
				oldTask.cancel(false);
		}
		AnalyzerTask task = new AnalyzerTask(callable);
		task.setCallback((errors) -> {
			Program program = createProgram(user, name, callable.getProgramID());
			createReport(user, program, errors);
			finalizeUserTask(user, task);
		});
		executor.execute(task);
		setCurrentTask(user, task);
		logger.info("User {} started analysis of new program from {} using {}", user.getEmail(), name, compOp);
	}
	
	private CompilerTool getCompilationTool(String compilator) {
		switch (compilator) {
			case "java":
				return ToolFactory.getJavaCompilerTool();
			case "maven":
				return ToolFactory.getMavenCompilerTool();
			default:
				return ToolFactory.getMavenCompilerTool();
		}
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED)
	private void createReport(User user, Program program, List<ProblemDto> problems) {
		Result result = new Result();
		result.setProgram(program);
		result.setTimestamp(new Date());
		result = resultsRepository.save(result);
		for (ProblemDto problemDto : problems) {
			Problem problem = new Problem();
			problem.setResult(result);
			problem.setQuery(queriesRepository.findByName(problemDto.getQueryName()));
			problem.setMsg(problemDto.getMsg());
			problemsRepository.save(problem);
		}
	}
	
	private Program createProgram(User user, String name, String programID) {
		Program program = new Program();
		program.setTimestamp(new Date());
		program.setUser(user);
		program.setProgramIdentifier(programID);
		program.setName(name);
		return programRepository.save(program);
	}
	
	/**
	 * Extracts all the necesary queries.
	 * If a query ends in '*' it gets all the 'family' of queries.
	 * @param queriesIds
	 * @param user the user that requested the queries
	 * @return
	 */
	private List<Query> getQueries(String[] queriesIds, User user) {
		List<Query> result = new ArrayList<Query>();
		for (String queryId : queriesIds) {
			if (queryId.endsWith("*")) {
			    String regex = queryId.replace(".", "\\.");
			    regex = queryId.replace("*", "");
			    regex = String.format("^%s[^\\.]*", regex);
				result.addAll(queriesRepository.findAllByFamily(regex, user));
			} else {
				Query query = queriesRepository.findAvailableByNameAndUser(queryId, user);
				if (query != null)
					result.add(query);
			}
		}
		//Return only  the distinct queries
		return result.stream()
				.distinct()
				.collect(Collectors.toList());
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
