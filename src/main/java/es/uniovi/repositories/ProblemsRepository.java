package es.uniovi.repositories;

import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Problem;

public interface ProblemsRepository extends CrudRepository<Problem, Long> {

}
