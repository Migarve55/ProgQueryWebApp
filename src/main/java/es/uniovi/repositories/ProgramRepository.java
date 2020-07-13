package es.uniovi.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Program;
import es.uniovi.entities.User;

public interface ProgramRepository extends CrudRepository<Program, Long> {

	List<Program> findAllByUser(User user);
	
	Page<Program> findAllByUser(Pageable pageable, User user);

	@Query("select p from Program p where p.user = ?1 order by timestamp desc")
	List<Program> findLastByUser(User user);
	
}
