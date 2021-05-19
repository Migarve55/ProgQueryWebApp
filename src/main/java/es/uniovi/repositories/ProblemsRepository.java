package es.uniovi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Analysis;
import es.uniovi.entities.Problem;
import es.uniovi.entities.Result;

public interface ProblemsRepository extends CrudRepository<Problem, Long> {

	@Modifying
	@Query("update Problem set analysis_id = null where analysis_id = ?1")
	void setAnalysisAsDeleted(Analysis analysis);
	
	Page<Problem> findAllByResult(Pageable pageable, Result result);
	
}
