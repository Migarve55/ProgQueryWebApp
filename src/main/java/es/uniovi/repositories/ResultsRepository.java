package es.uniovi.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Program;
import es.uniovi.entities.Result;
import es.uniovi.entities.User;

public interface ResultsRepository extends CrudRepository<Result, Long> {

	@Query("select r from Result r where r.program.user = ?1")
	Page<Result> findAllByUser(Pageable pageable, User user);

	Page<Result> findAllByProgram(Pageable pageable, Program program);
	
	@Query("select r from Result r where user = ?1 order by timestamp desc")
	List<Result> findLastByUser(User user);
	
}
