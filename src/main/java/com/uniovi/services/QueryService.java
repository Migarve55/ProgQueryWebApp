package com.uniovi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uniovi.entities.Query;
import com.uniovi.entities.User;
import com.uniovi.repositories.QueriesRepository;

@Service
public class QueryService {
	
	@Autowired
	private QueriesRepository queriesRepository;
	
	public Query findQuery(Long id) {
		return queriesRepository.findById(id).orElse(null);
	}
	
	public Query findQueryByName(String name) {
		return queriesRepository.findByName(name);
	}

	public Page<Query> getQueriesFromUser(Pageable pageable, User user) {
		return queriesRepository.findAllByUserOrderByName(pageable, user);
	}
	
	public Page<Query> getQueriesFromUser(Pageable pageable, User user, String searchText) {
		searchText = "%" + searchText + "%";
		return queriesRepository.findAllByUserAndName(pageable, user, searchText);
	}
	
	public List<Query> getAvailableQueriesForUser(User user) {
		return queriesRepository.findAvailableQueriesForUser(user);
	}
	
	public void saveQuery(Query query) {
		queriesRepository.save(query);
	}
	
	public void deleteQuery(Query query) {
		queriesRepository.delete(query);
	}
	
}
