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
import es.uniovi.analyzer.callables.github.GithubCodeAnalyzerCallable;
import es.uniovi.analyzer.callables.program.ProgramAnalyzerCallable;
import es.uniovi.analyzer.callables.source.SourceAnalyzerCallable;
import es.uniovi.analyzer.callables.zip.ZipAnalizerCallable;
import es.uniovi.analyzer.tools.ToolFactory;
import es.uniovi.analyzer.tools.compilators.CompilerTool;
import es.uniovi.analyzer.tools.reporter.Neo4jFacade;
import es.uniovi.analyzer.tools.reporter.dto.ProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.AnalysisDto;
import es.uniovi.analyzer.tools.reporter.dto.AnalysisExecutionProblemDto;
import es.uniovi.analyzer.tools.reporter.dto.ResultDto;
import es.uniovi.entities.Problem;
import es.uniovi.entities.Program;
import es.uniovi.entities.Analysis;
import es.uniovi.entities.AnalysisExecutionProblem;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.repositories.ProblemsRepository;
import es.uniovi.repositories.ProgramRepository;
import es.uniovi.repositories.AnalysisRepository;
import es.uniovi.repositories.AnalysisExecutionProblemRepository;
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
	private AnalysisExecutionProblemRepository analysisExecutionProblemRepository;
	
	@Autowired
	private ProblemsRepository problemsRepository;
	
	@Autowired
	private ProgramRepository programRepository;
	
	@Autowired
	private AnalysisRepository analysisService;
	
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
	
	public void analyzeProgramWithAnalysisText(User user, String analysisText, Program program) {
		AbstractAnalyzerCallable callable = new ProgramAnalyzerCallable(program.getName(), user.getEmail());
		callable.setQueries(getSimpleAnalysisList(analysisText));
		AbstractTask newTask = new PlaygroundTask(callable, analysisText);
		replaceTasks(user, newTask, callable, (resultDto, t) -> {
			PlaygroundTask task = (PlaygroundTask) t;
			if (shouldCreateResult(resultDto)) {
				task.setResultMsg(resultDto.getTextSummary());
			}
			finalizeUserTask(user, task);
		});
		logger.info("User {} started playground analysis of program {} with analysis: {}...", user.getEmail(), program.getName(), analysisText);
	}

	public void analyzeSourceWithAnalysisText(User user, String analysisText, String programSource) {
		String programId = getPlaygroundProgramId(user);
		AbstractAnalyzerCallable callable = new SourceAnalyzerCallable(programSource, programId, user.getEmail());
		callable.setQueries(getSimpleAnalysisList(analysisText));
		callable.setCompiler(ToolFactory.getJavaCompilerTool());
		AbstractTask newTask = new PlaygroundTask(callable, analysisText, programSource);
		replaceTasks(user, newTask, callable, (resultDto, t) -> {
			PlaygroundTask task = (PlaygroundTask) t;
			if (shouldCreateResult(resultDto)) {
				task.setResultMsg(resultDto.getTextSummary());
			}
			cleanUpProgram(programId);
			finalizeUserTask(user, task);
		});
		logger.info("User {} started playground analysis of program source with analysis: {}", user.getEmail(), analysisText);
	}

	private String getPlaygroundProgramId(User user) {
		int hash = 17;
		hash = hash * 31 + user.hashCode();
		hash = hash * 31 + new Date().hashCode();
		return "playground.program.id_" + Integer.toHexString(hash);
	}
	
	private List<AnalysisDto> getSimpleAnalysisList(String queryText) {
		List<AnalysisDto> queries = new ArrayList<AnalysisDto>();
		AnalysisDto analysis = new AnalysisDto();
		analysis.setName("");
		analysis.setQueryText(queryText);
		queries.add(analysis);
		return queries;
	}
	
	public void analyzeProgram(User user, Program program, String[] queries) {
		AbstractAnalyzerCallable callable = new ProgramAnalyzerCallable(program.getName(), user.getEmail());
		List<Analysis> queriesList = getQueries(queries, user);
		callable.setQueries(toAnalysisDto(queriesList));
		AbstractTask newTask = new AnalyzerTask(callable);
		replaceTasks(user, newTask, callable, (resultDto, t) -> {
			AnalyzerTask task = (AnalyzerTask) t;
			task.setProgramId(program.getId());
			if (shouldCreateResult(resultDto)) {
				Result result = createResult(user, program, resultDto);
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
			List<Analysis> queriesList = getQueries(queries, user);
			callable.setQueries(toAnalysisDto(queriesList));
		}
		callable.setCompiler(getCompilationTool(compOp));
		AbstractTask newTask = new AnalyzerTask(callable);
		replaceTasks(user, newTask, callable, (resultDto, t) -> {
			AnalyzerTask task = (AnalyzerTask) t;
			Program program = createProgram(user, programId);
			task.setProgramId(program.getId());
			if (shouldCreateResult(resultDto)) {
				Result result = createResult(user, program, resultDto);
				task.setResultId(result.getId());
			}
			finalizeUserTask(user, task);
		});
		logger.info("User {} started analysis of new program '{}' using {}", user.getEmail(), programId, compOp);
	}
	
	private void replaceTasks(User user, AbstractTask newTask, AbstractAnalyzerCallable callable, BiConsumer<ResultDto, AbstractTask> callback) {
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
	private Result createResult(User user, Program program, ResultDto resultDto) {
		Result result = new Result();
		result.setProgram(program);
		result.setTimestamp(new Date());
		result = resultsRepository.save(result);
		// Problems
		for (ProblemDto problemDto : resultDto.getProblems()) {
			Problem problem = new Problem();
			problem.setResult(result);
			problem.setAnalysis(analysisService.findByName(problemDto.getAnalysisName()));
			problem.setMsg(problemDto.getMsg());
			problemsRepository.save(problem);
		}
		// Analysis execution problems problems
		for (AnalysisExecutionProblemDto analysisExecutionProblemDto : resultDto.getAnalysisExecutionProblems()) {
			AnalysisExecutionProblem analysisExecutionProblem = new AnalysisExecutionProblem();
			analysisExecutionProblem.setMsg(analysisExecutionProblemDto.getMsg());
			analysisExecutionProblem.setAnalysisName(analysisExecutionProblemDto.getName());
			analysisExecutionProblem.setResult(result);
			analysisExecutionProblemRepository.save(analysisExecutionProblem);
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
	
	private void cleanUpProgram(String programId) {
		try (Neo4jFacade neo4jFacade = new Neo4jFacade(System.getProperty("neo4j.url"))) {
			neo4jFacade.removeProgram(programId);
			logger.info("Cleaned up program '{}'", programId);
		}
	}
	
	private boolean shouldCreateResult(ResultDto resultDto) {
		return !resultDto.getProblems().isEmpty() || 
			   !resultDto.getAnalysisExecutionProblems().isEmpty();
	}
	
	/**
	 * Extracts all the necesary queries.
	 * If a analysis ends in '*' it gets all the 'family' of queries.
	 * @param queriesIds
	 * @param user the user that requested the queries
	 * @return
	 */
	private List<Analysis> getQueries(String[] queriesIds, User user) {
		List<Analysis> result = new ArrayList<Analysis>();
		for (String analysisId : queriesIds) {
			if (analysisId.endsWith("*")) {
			    String regex = analysisId.replace(".", "\\.");
			    regex = analysisId.replace("*", "");
			    regex = String.format("^%s[^\\.]*", regex);
				result.addAll(analysisService.findAllByFamily(regex, user));
			} else {
				Analysis analysis = analysisService.findAvailableByNameAndUser(analysisId, user);
				if (analysis != null)
					result.add(analysis);
			}
		}
		//Return only  the distinct queries
		return result.stream()
				.distinct()
				.collect(Collectors.toList());
	}
	
	private List<AnalysisDto> toAnalysisDto(List<Analysis> analyses) {
		return analyses.stream()
				.map((q) -> toAnalysisDto(q))
				.collect(Collectors.toList());
	}
	
	private AnalysisDto toAnalysisDto(Analysis analysis) {
		AnalysisDto analysisDto = new AnalysisDto();
		analysisDto.setName(analysis.getName());
		analysisDto.setQueryText(analysis.getQueryText());
		return analysisDto;
	}

	@PreDestroy
	public void shutdownThreadExecutor() {
		executor.shutdown();
	}
	
}
