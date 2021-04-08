package es.uniovi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.uniovi.analyzer.callables.AbstractAnalyzerCallable;
import es.uniovi.analyzer.callables.file.FileAnalyzerCallable;
import es.uniovi.analyzer.callables.github.GithubCodeAnalyzerCallable;
import es.uniovi.analyzer.callables.program.ProgramAnalyzerCallable;
import es.uniovi.analyzer.callables.source.SourceAnalyzerCallable;
import es.uniovi.analyzer.callables.zip.ZipAnalizerCallable;
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
import es.uniovi.tasks.AbstractTask;
import es.uniovi.tasks.AnalyzerTask;
import es.uniovi.tasks.PlaygroundTask;

@Service
public class AnalyzerService {
	
	private Map<User,AbstractTask> usersTasks = new ConcurrentHashMap<User,AbstractTask>();
	private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); 
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private ResultsRepository resultsRepository;
	
	@Autowired
	private ProblemsRepository problemsRepository;
	
	@Autowired
	private ProgramRepository programRepository;
	
	@Autowired
	private QueriesRepository queriesRepository;
	
	public AbstractTask getCurrentTask(User user) {
		return usersTasks.get(user);
	}
	
	private void setCurrentTask(User user, AbstractTask task) {
		logger.info("User {} was assigned a new task {}", user.getEmail(), task);
		usersTasks.put(user, task);
	}
	
	/**
	 * This method just logs the event now.
	 * User "clearUserTask" to remove the task assigned to the user
	 * @param user
	 * @param task
	 */
	private void finalizeUserTask(User user, AbstractTask task) {
		logger.info("Task {} of user {} has ended", task, user.getEmail());
	}
	
	public void clearUserTask(User user) {
		usersTasks.remove(user);
	}
	
	public void cancelCurrentTask(User user) {
		AbstractTask task = getCurrentTask(user);
		if (task.cancel(false))
			logger.info("User {} cancelled their task {} succesfully", user.getEmail(), task);
		else
			logger.info("User {} could not cancel their task {}", user.getEmail(), task);
		finalizeUserTask(user, task);
	}
	
	public void analyzeFile(User user, String programId, MultipartFile file, String[] queries) throws IOException {
		launchAnalyzerTask(user, programId, "java", new FileAnalyzerCallable(programId, user.getEmail(), file.getOriginalFilename(), file.getInputStream()), queries);
	}
	
	public void analyzeZip(User user, String programId, MultipartFile zip, String compOp, String classpath, String[] queries) throws IOException {
		launchAnalyzerTask(user, programId, compOp, new ZipAnalizerCallable(classpath, programId, user.getEmail(), zip.getInputStream()), queries);
	}

	public void analyzeGitRepo(User user, String programId, String repoUrl, String compOp, String classpath, String[] queries) {
		launchAnalyzerTask(user, programId, compOp, new GithubCodeAnalyzerCallable(classpath, programId, user.getEmail(), repoUrl), queries);
	}

	public void uploadGitRepo(User user, String programId, String repoUrl, String compOp, String classpath) {
		String[] queries = new String[0]; //Empty array with no queries
		launchAnalyzerTask(user, programId, compOp, new GithubCodeAnalyzerCallable(classpath, programId, user.getEmail(), repoUrl), queries);
	}
	
	public void analyzeProgramWithQueryText(User user, String queryText, Program program) {
		AbstractAnalyzerCallable callable = new ProgramAnalyzerCallable(program.getName(), user.getEmail());
		callable.setQueries(getSimpleQueryList(queryText));
		AbstractTask newTask = new PlaygroundTask(callable, queryText);
		replaceTasks(user, newTask, callable, (errors, task) -> {
			task.setProgramId(program.getId());
			if (errors.size() > 0) {
				Result result = createResult(user, program, errors);
				task.setResultId(result.getId());
			}
			finalizeUserTask(user, task);
		});
		logger.info("User {} started playground analysis of program {} with query: {}...", user.getEmail(), program.getName(), queryText);
	}

	public void analyzeSourceWithQueryText(User user, String queryText, String programSource) {
		String programId = getPlaygroundProgramId(user);
		AbstractAnalyzerCallable callable = new SourceAnalyzerCallable(programSource, programId, user.getEmail());
		callable.setQueries(getSimpleQueryList(queryText));
		callable.setCompiler(ToolFactory.getJavaCompilerTool());
		AbstractTask newTask = new PlaygroundTask(callable, queryText, programSource);
		replaceTasks(user, newTask, callable, (errors, task) -> {
			Program program = createProgram(user, programId);
			task.setProgramId(program.getId());
			if (errors.size() > 0) {
				Result result = createResult(user, program, errors);
				task.setResultId(result.getId());
			}
			finalizeUserTask(user, task);
		});
		logger.info("User {} started playground analysis of program source with query: {}", user.getEmail(), queryText);
	}
	
	private String getPlaygroundProgramId(User user) {
		int hash = 17;
		hash = hash * 31 + user.hashCode();
		hash = hash * 31 + new Date().hashCode();
		return "playground.program.id_" + Integer.toHexString(hash);
	}
	
	private List<QueryDto> getSimpleQueryList(String queryText) {
		List<QueryDto> queries = new ArrayList<QueryDto>();
		QueryDto query = new QueryDto();
		query.setName("");
		query.setQueryText(queryText);
		queries.add(query);
		return queries;
	}
	
	public void analyzeProgram(User user, Program program, String[] queries) {
		AbstractAnalyzerCallable callable = new ProgramAnalyzerCallable(program.getName(), user.getEmail());
		List<Query> queriesList = getQueries(queries, user);
		callable.setQueries(toQueryDto(queriesList));
		AbstractTask newTask = new AnalyzerTask(callable);
		replaceTasks(user, newTask, callable, (errors, task) -> {
			if (errors.size() > 0) {
				Result result = createResult(user, program, errors);
				task.setResultId(result.getId());
			}
			finalizeUserTask(user, task);
		});
		logger.info("User {} started program {} analysis", user.getEmail(), program.getName());
	}
	
	/**
	 * Auxiliar method for the git,file and zip analyzers
	 * @param user User that requested the analysis
	 * @param programId Name of the program
	 * @param compOp What compiler to use
	 * @param callable what callable to use
	 * @param queries what queries to use, can be null. If null, it will not create a report.
	 */
	private void launchAnalyzerTask(User user, String programId, String compOp, AbstractAnalyzerCallable callable, String[] queries) {
		if (queries != null) {
			List<Query> queriesList = getQueries(queries, user);
			callable.setQueries(toQueryDto(queriesList));
		}
		callable.setCompiler(getCompilationTool(compOp));
		AbstractTask newTask = new AnalyzerTask(callable);
		replaceTasks(user, newTask, callable, (errors, task) -> {
			Program program = createProgram(user, programId);
			task.setProgramId(program.getId());
			if (errors.size() > 0) {
				Result result = createResult(user, program, errors);
				task.setResultId(result.getId());
			}
			finalizeUserTask(user, task);
		});
		logger.info("User {} started analysis of new program '{}' using {}", user.getEmail(), programId, compOp);
	}
	
	private void replaceTasks(User user, AbstractTask newTask, AbstractAnalyzerCallable callable, BiConsumer<List<ProblemDto>, AbstractTask> callback) {
		AbstractTask oldTask = getCurrentTask(user);
		if (oldTask != null) {
			if (!oldTask.isDone())
				oldTask.cancel(false);
		}
		newTask.setCallback((list) -> {
			callback.accept(list, newTask);
		});
		executor.execute(newTask);
		setCurrentTask(user, newTask);
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
	private Result createResult(User user, Program program, List<ProblemDto> problems) {
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
		return result;
	}
	
	private Program createProgram(User user, String name) {
		Program program = new Program();
		program.setTimestamp(new Date());
		program.setUser(user);
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
