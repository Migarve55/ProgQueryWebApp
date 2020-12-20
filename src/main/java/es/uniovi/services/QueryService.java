package es.uniovi.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uniovi.entities.Query;
import es.uniovi.entities.User;
import es.uniovi.repositories.ProblemsRepository;
import es.uniovi.repositories.QueriesRepository;

@Service
public class QueryService {
	
	private final static String NAME_REGEX = "((([a-zA-Z0-9_]+)\\.)+(\\*|[a-zA-Z0-9_]+))|[a-zA-Z0-9_]+";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private QueriesRepository queriesRepository;
	
	@Autowired
	private ProblemsRepository problemsRepository;
	
	public Query findQuery(Long id) {
		return queriesRepository.findById(id).orElse(null);
	}
	
	public Query findQueryByName(String name) {
		return queriesRepository.findByName(name);
	}
	
	public List<Query> getQueriesFromUser(User user) {
		return queriesRepository.findAllByUserOrderByName(user);
	}

	public Page<Query> getQueriesFromUser(Pageable pageable, User user) {
		return queriesRepository.findAllByUserOrderByName(pageable, user);
	}
	
	public Page<Query> getQueriesFromUser(Pageable pageable, User user, String searchText) {
		searchText = "%" + searchText + "%";
		return queriesRepository.findAllByUserAndNameLike(pageable, user, searchText);
	}
	
	public List<Query> getPublicQueries() {
		return queriesRepository.findAllPublic();
	}
	
	public List<Query> getAvailableQueriesForUser(User user) {
		return queriesRepository.findAvailableQueriesForUser(user);
	}
	
	public Optional<Query> getQueriesFromUserByName(User user, String queryName) {
		return getAvailableQueriesForUser(user).stream()
				.filter(q -> q.getName().equals(queryName))
				.findFirst();
	}
	
	public void saveQuery(Query query) {
		query.setModified(new Date());
		queriesRepository.save(query);
		logger.info("Query {} was saved", query.getName());
	}
	
	/**
	 * Makes a query visible to an user
	 * @param query
	 * @param user
	 * @return
	 */
	public boolean addUser(Query query, User user) {
		if (user == null)
			return false;
		if (query.getPublicTo().contains(user))
			return false;
		query.getPublicTo().add(user);
		queriesRepository.save(query);
		logger.info("Query {} is now visible to user {}", query.getName(), user.getEmail());
		return true;
	}
	
	/**
	 * Makes a query no longer visible to an user
	 * @param query
	 * @param user
	 * @return
	 */
	public boolean removeUser(Query query, User user) {
		if (user == null)
			return false;
		if (!query.getPublicTo().contains(user))
			return false;
		query.getPublicTo().remove(user);
		queriesRepository.save(query);
		logger.info("Query {} is no longer visible to user {}", query.getName(), user.getEmail());
		return true;
	}
	
	@Transactional
	public void deleteQuery(Query query) {
		queriesRepository.delete(query);
		problemsRepository.setQueryAsDeleted(query);
		logger.info("Query {} was deleted", query.getName());
	}
	
	public boolean canSeeQuery(User user, Query query) {
		if (!query.isPublicForAll()) {
			if (!(query.getPublicTo().contains(user) || query.getUser().equals(user))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean canModifyQuery(User user, Query query) {
		return query.getUser().equals(user);
	}
	
	public void deleteAll() {
		queriesRepository.deleteAll();
	}
	
	public boolean validateQueryName(String name) {
		return name.matches(NAME_REGEX);
	}
	
}
