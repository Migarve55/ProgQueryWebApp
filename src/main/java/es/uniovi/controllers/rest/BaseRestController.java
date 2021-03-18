package es.uniovi.controllers.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import es.uniovi.entities.Problem;
import es.uniovi.entities.Program;
import es.uniovi.entities.Query;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;

public abstract class BaseRestController {
	
	protected String toErrorList(BindingResult result) {
		StringBuilder sb = new StringBuilder();
		List<ObjectError> errors = result.getAllErrors();
		if (!errors.isEmpty())
			sb.append(errors.get(0).getCode());
		for (int i = 1;i < errors.size();i++) {
			sb.append(", ");
			sb.append(errors.get(i).getObjectName());
		}
		return sb.toString();
	}
	
	protected Map<String, Object> userToMap(User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", user.getId());
		map.put("email", user.getEmail());
		map.put("name", user.getName());
		map.put("lastName", user.getLastName());
		return map;
	}
	
	protected List<Map<String, Object>> programToMapList(Iterable<Program> programs) {
		return toMapList(programs, this::loadProgramIntoMap);
	}
	
	protected List<Map<String, Object>> queriesToMapList(Iterable<Query> queries) {
		return toMapList(queries, this::loadQueryIntoMap);
	}
	
	protected List<Map<String, Object>> resultsToMapList(Iterable<Result> results) {
		return toMapList(results, this::loadResultIntoMap);
	}
	
	protected <T> List<Map<String, Object>> toMapList(Iterable<T> list, Function<T, Map<String, Object>> func) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		list.forEach(item -> result.add(func.apply(item)));
		return result;
	}
	
	protected Map<String, Object> loadProgramIntoMap(Program program) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", program.getId());
		map.put("name", program.getName());
		map.put("results", program.getResults().size());
		return map;
	}
	
	protected Map<String, Object> loadQueryIntoMap(Query query) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", query.getId());
		map.put("name", query.getName());
		map.put("description", query.getDescription());
		map.put("query", query.getQueryText());
		map.put("user", query.getUser().getEmail());
		map.put("isPublic", query.isPublicForAll());
		map.put("publicTo", query
				.getPublicTo().stream()
				.map(user -> user.getEmail())
				.collect(Collectors.toList())
			);
		map.put("modified", query.getModified());
		return map;
	}
	
	protected Map<String, Object> loadResultIntoMap(Result result) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", result.getId());
		map.put("program", result.getProgram().getName());
		map.put("timeStamp", result.getTimestamp().toString());
		map.put("problems", result.getProblems().stream().map(p -> problemToMap(p)));
		return map;
	}
	
	protected Map<String, Object> problemToMap(Problem problem) {
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("msg", problem.getMsg());
		pMap.put("query", problem.getQuery().getId());
		return pMap;
	}


}
