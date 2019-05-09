package com.uniovi.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.entities.Result;
import com.uniovi.entities.User;

public interface ResultsRepository extends CrudRepository<Result, Long> {

	Page<Result> findAllByUser(Pageable pageable, User user);

	@Query("select r from Result r where user = ?1 order by timestamp desc")
	List<Result> findLastByUser(User user);
	
}
