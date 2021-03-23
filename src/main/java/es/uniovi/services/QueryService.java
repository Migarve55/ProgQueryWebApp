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

import es.uniovi.controllers.ForbiddenException;
import es.uniovi.entities.Query;
import es.uniovi.entities.User;
import es.uniovi.reflection.processing.CypherAdapter;
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
	
	public Page<Query> getQueriesFromUser(Pageable pageable, User user, String searchText, boolean onlyOwner) {
		if (searchText == null)
			searchText = "%";
		else
			searchText = "%" + searchText + "%";
		if (onlyOwner)
			return queriesRepository.findAllByUserAndNameLike(pageable, user, searchText);
		else
			return queriesRepository.findAvailableQueriesForUser(pageable, user, searchText);
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
	
	public void saveQuery(User user, Query query) {
		checkUserWriteQueryAccess(user, query);
		query.setModified(new Date());
		queriesRepository.save(query);
		logger.info("Query '{}' was saved", query.getName());
	}
	

	public boolean addUser(User user, Query query, User toAdd) {
		if (toAdd == null)
			return false;
		if (query.getPublicTo().contains(toAdd))
			return false;
		checkUserWriteQueryAccess(user, query);
		query.getPublicTo().add(toAdd);
		queriesRepository.save(query);
		logger.info("Query '{}' is now visible to user '{}'", query.getName(), toAdd.getEmail());
		return true;
	}

	public boolean removeUser(User user, Query query, User toRemove) {
		if (toRemove == null)
			return false;
		if (!query.getPublicTo().contains(toRemove))
			return false;
		checkUserWriteQueryAccess(user, query);
		query.getPublicTo().remove(toRemove);
		queriesRepository.save(query);
		logger.info("Query '{}' is no longer visible to user '{}'", query.getName(), toRemove.getEmail());
		return true;
	}
	
	@Transactional
	public void deleteQuery(User user, Query query) {
		checkUserWriteQueryAccess(user, query);
		queriesRepository.delete(query);
		problemsRepository.setQueryAsDeleted(query);
		logger.info("Query '{}' was deleted", query.getName());
	}
	
	// Auxiliar
	
	public void checkUserReadQueryAccess(User user, Query query) {
		if (!canSeeQuery(user, query)) {
			logger.warn("User '{}' does not have read access to query '{}'", user.getEmail(), query.getName());
			throw new ForbiddenException();
		}
	}
	
	public void checkUserWriteQueryAccess(User user, Query query) {
		if (!canModifyQuery(user, query)) {
			logger.warn("User '{}' does not have write access to query '{}'", user.getEmail(), query.getName());
			throw new ForbiddenException();
		}
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
	
	public boolean isQueryOk(String query) {
		return CypherAdapter.limitQuery(query, "test") != null;
	}
	
	public void deleteAll() {
		queriesRepository.deleteAll();
	}
	
	public boolean validateQueryName(String name) {
		return name.matches(NAME_REGEX);
	}
	
}
