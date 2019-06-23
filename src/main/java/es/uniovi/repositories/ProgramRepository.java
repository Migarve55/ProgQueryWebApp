package es.uniovi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Program;
import es.uniovi.entities.User;

public interface ProgramRepository extends CrudRepository<Program, Long> {

	Page<Program> findAllByUser(Pageable pageable, User user);
	
}
