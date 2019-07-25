package es.uniovi.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;
import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import es.uniovi.entities.Problem;
import es.uniovi.entities.Program;
import es.uniovi.entities.Query;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.repositories.ProblemsRepository;
import es.uniovi.repositories.ProgramRepository;
import es.uniovi.repositories.ResultsRepository;

@Service
public class InsertSampleDataService {

	@Autowired
	private UsersService usersService;

	@Autowired
	private QueryService queryService;
	
	@Autowired
	private ResultsRepository resultRepository;
	
	@Autowired
	private ProgramRepository programRepository;
	
	@Autowired 
	private ProblemsRepository problemsRepository;

	@PostConstruct
	public void init() {
		if (!isDBCreated())
			createAllData();
	}
	
	private void createAllData() {
		User user = createUser("miguel@email.com", "Miguel", "Garnacho Vélez", "123456");
		createUser("oscar@email.com", "Oscar", "Rodrigez Prieto", "123456");
		loadQueriesFromFile(user);
		//Add sample error
		Program program = new Program();
		program.setName("Test");
		program.setUser(user);
		program = programRepository.save(program);
		Result result = new Result();
		result.setProgram(program);
		result.setTimestamp(new Date());
		resultRepository.save(result);
		createProblem(result, "Global error");
		createProblem(result, "file.java", -1, -1);
		createProblem(result, "file.java", 5, 10);
	}
	
	private User createUser(String email, String name, String surname, String password) {
		User user = new User(email, name, surname);
		user.setPassword(password);
		usersService.addUser(user);
		return user;
	}
	
	private Problem createProblem(Result result, String msg) {
		Problem problem = new Problem(msg);
		problem.setResult(result);
		problemsRepository.save(problem);
		return problem;
	}
	
	private Problem createProblem(Result result, String file, int line, int col) {
		Problem problem = new Problem(file, line, col);
		problem.setResult(result);
		problemsRepository.save(problem);
		return problem;
	}
	
	/**
	 * This checks the database has the basic initial data
	 * @return
	 */
	private boolean isDBCreated() {
		return usersService.getUserByEmail("miguel@email.com") != null;
	}

	private void loadQueriesFromFile(User user) {
		try (InputStream resource = new ClassPathResource("queries.json").getInputStream();) {
			Object obj = new JSONParser().parse(new InputStreamReader(resource));
			JSONObject jo = (JSONObject) obj;
			JSONArray ja = (JSONArray) jo.get("queries");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> queryIterator = ja.iterator(); 
			while (queryIterator.hasNext()) {
				JSONObject queryJson = queryIterator.next();
				Query query = new Query(
						(String) queryJson.get("name"), 
						(String) queryJson.get("description"), 
						(String) queryJson.get("query"));
				query.setUser(user);
				query.setPublicForAll(true);
				queryService.saveQuery(query);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}