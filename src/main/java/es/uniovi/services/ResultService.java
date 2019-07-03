package es.uniovi.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.uniovi.entities.Program;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;
import es.uniovi.repositories.ResultsRepository;

@Service
public class ResultService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ResultsRepository resultsRepository;
	
	public Page<Result> getResultsByUser(Pageable pageable, User user) {
		return resultsRepository.findAllByUser(pageable, user);
	}
	
	public Result getLastFromUser(User user) {
		List<Result> results = resultsRepository.findLastByUser(user);
		return results.isEmpty() ? null : results.get(0);
	}
	
	public Result getResult(Long id) {
		return resultsRepository.findById(id).orElse(null);
	}
	
	public void deleteResult(Result result) {
		resultsRepository.delete(result);
		logger.info("Result for program {} was deleted", result.getProgram());
	}

	public Page<Result> listByProgram(Pageable pageable, Program program) {
		return resultsRepository.findAllByProgram(pageable, program);
	}
	
}
