package com.uniovi.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.entities.Query;
import com.uniovi.entities.User;

public interface QueriesRepository extends CrudRepository<Query, Long> {

	Page<Query> findAllByUser(Pageable pageable, User user);
	
	Query findByName(String name);
	
	@org.springframework.data.jpa.repository.Query(value = "select q.* from Query q where REGEXP_MATCHES(q.name,?1)", nativeQuery = true)
	List<Query> findAllByFamily(String family);
	
	@org.springframework.data.jpa.repository.Query("select q from Query q where user = ?1 and name like ?2")
	Page<Query> findAllByUserAndName(Pageable pageable, User user, String name);
	
	@org.springframework.data.jpa.repository.Query("select q from Query q where user = ?1 or publicForAll = true")
	List<Query> findAvailableQueriesForUser(User user);
	
}
