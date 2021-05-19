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

	@Query("select r from Result r where r.program.user = ?1 order by r.timestamp desc")
	List<Result> findAllByUser(User user);
	
	@Query("select r from Result r where r.program.user = ?1 order by r.timestamp desc")
	Page<Result> findAllByUser(Pageable pageable, User user);

	Page<Result> findAllByProgram(Pageable pageable, Program program);
	
	@Query(value = "select r.* from Result r, Problem p where p.result_id = r.id and p.query_id = ?2 and r.program_id = ?1", nativeQuery = true)
	List<Result> findAllByProgramAndAnalysis(Long programId, Long analysisId);

	@Query(value = "select r.* from Result r, Problem p where p.result_id = r.id and p.query_id = ?1", nativeQuery = true)
	List<Result> findAllByAnalysis(Long analysisId);
	
}
