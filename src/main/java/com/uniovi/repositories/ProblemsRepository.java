package com.uniovi.repositories;

import org.springframework.data.repository.CrudRepository;

import com.uniovi.entities.Problem;

public interface ProblemsRepository extends CrudRepository<Problem, Long> {

}
