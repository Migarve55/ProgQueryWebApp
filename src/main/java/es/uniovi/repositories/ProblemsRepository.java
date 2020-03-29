package es.uniovi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Problem;
import es.uniovi.entities.Result;

public interface ProblemsRepository extends CrudRepository<Problem, Long> {

	@Modifying
	@Query("update Problem set query_id = null where query_id = ?1")
	void setQueryAsDeleted(es.uniovi.entities.Query query);
	
	Page<Problem> findAllByResult(Pageable pageable, Result result);
	
}
