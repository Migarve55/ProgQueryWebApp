package es.uniovi.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import es.uniovi.entities.Problem;
import es.uniovi.entities.Program;
import es.uniovi.entities.Analysis;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.repositories.ProblemsRepository;
import es.uniovi.repositories.ProgramRepository;
import es.uniovi.repositories.ResultsRepository;

@Service
public class InsertSampleDataService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UsersService usersService;

	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private ResultsRepository resultRepository;
	
	@Autowired
	private ProgramRepository programRepository;
	
	@Autowired 
	private ProblemsRepository problemsRepository;
	
	@PostConstruct
    public void init() {
		User miguel = usersService.getUserByEmail("miguel@email.com");
		if (miguel == null) {
			insertProdData();
		}
	}
	
	private void insertProdData() {
		logger.info("Inserting data for production...");
		User miguel = createUser("miguel@email.com", "Miguel", "Garnacho Vélez", "123456");
		createUser("oscar@email.com", "Oscar", "Rodrigez Prieto", "123456");
		loadAnalysesFromFile(miguel);
	}

	public void resetDB() {
		logger.info("Resetting database...");
		problemsRepository.deleteAll();
		resultRepository.deleteAll();
		programRepository.deleteAll();
		analysisService.deleteAll();
		usersService.deleteAll();
	}
	
	public void createAllData() {
		logger.info("Creating test sample data");
		
		// Users
		User miguel = createUser("miguel@email.com", "Miguel", "Garnacho Vélez", "123456");
		User oscar =  createUser("oscar@email.com", "Oscar", "Rodrigez Prieto", "123456");
		
		// Queries
		Analysis a1 = createAnalysis("test1", "...", "...", true, miguel);
		createAnalysis("test2", "...", "...", false, miguel);
		createAnalysis("test3", "...", "...", false, oscar, miguel);
		
		// Programs
		Program p1 = createProgram("program1", miguel, new Date());
		Program p2 = createProgram("program2", oscar, new Date());
		
		// Results
		Result r1 = createResult(p1, new Date());
		Result r2 = createResult(p2, new Date());
		
		// Problems
		createProblem(r1, a1, "You got a problem");
		createProblem(r2, a1, "You got a problem");
		
	}
	
	private User createUser(String email, String name, String surname, String password) {
		User user = new User(email, name, surname);
		user.setPassword(password);
		usersService.addUser(user);
		return user;
	}
	
	private Analysis createAnalysis(String name, String description, String cipher, boolean isPublic, User creator, User... publicTo) {
		Analysis analysis = new Analysis();
		analysis.setName(name);
		analysis.setDescription(description);
		analysis.setQueryText(cipher);
		analysis.setUser(creator);
		analysis.setPublicForAll(isPublic);
		if (publicTo.length > 0) {
			Set<User> publicToUsers = new HashSet<User>();
			for (User user : publicTo) {
				publicToUsers.add(user);
			}
			analysis.setPublicTo(publicToUsers);
		}
		analysisService.saveAnalysis(creator, analysis);
		return analysis;
	}
	
	private Program createProgram(String name, User owner, Date timestamp) {
		Program program = new Program();
		program.setName(name);
		program.setUser(owner);
		program.setTimestamp(timestamp);
		programRepository.save(program);
		return program;
	}
	
	private Result createResult(Program program, Date timestamp) {
		Result result = new Result();
		result.setProgram(program);
		result.setTimestamp(timestamp);
		resultRepository.save(result);
		return result;
	}
	
	private Problem createProblem(Result result, Analysis analysis, String msg) {
		Problem problem = new Problem(msg);
		problem.setResult(result);
		problem.setAnalysis(analysis);
		problemsRepository.save(problem);
		return problem;
	}

	private void loadAnalysesFromFile(User user) {
		try (InputStream resource = new ClassPathResource("analyses.json").getInputStream();) {
			Object obj = new JSONParser().parse(new InputStreamReader(resource));
			JSONObject jo = (JSONObject) obj;
			JSONArray ja = (JSONArray) jo.get("analyses");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> analysisIterator = ja.iterator(); 
			while (analysisIterator.hasNext()) {
				JSONObject analysisJson = analysisIterator.next();
				Analysis analysis = new Analysis(
					(String) analysisJson.get("name"), 
					(String) analysisJson.get("description"), 
					(String) analysisJson.get("query"));
				analysis.setUser(user);
				analysis.setPublicForAll(true);
				analysisService.saveAnalysis(user, analysis);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}