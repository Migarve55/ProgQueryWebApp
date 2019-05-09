package com.uniovi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uniovi.entities.Result;
import com.uniovi.entities.User;
import com.uniovi.repositories.ResultsRepository;

@Service
public class ResultService {

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
	}
	
}
