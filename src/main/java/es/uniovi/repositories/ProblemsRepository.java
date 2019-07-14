package es.uniovi.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Problem;

public interface ProblemsRepository extends CrudRepository<Problem, Long> {

	@Modifying
	@Query("update Problem set query_id = null where query_id = ?1")
	void setQueryAsDeleted(es.uniovi.entities.Query query);
	
}
