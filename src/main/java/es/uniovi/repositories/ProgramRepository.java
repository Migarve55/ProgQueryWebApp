package es.uniovi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Program;
import es.uniovi.entities.User;

public interface ProgramRepository extends CrudRepository<Program, Long> {
	
	List<Program> findAllByUser(User user);
	
	Optional<Program> findByName(String name);
	
	@Query("select p from Program p where p.user = ?1 order by timestamp desc")
	Page<Program> findAllByUser(Pageable pageable, User user);

	@Query("select p from Program p where p.user = ?1 order by timestamp desc")
	List<Program> findLastByUser(User user);

	@Query(value = "select p.* from program p where p.user_id = ?1 and p.name like ?2 order by p.name limit ?3", nativeQuery = true)
	List<Program> findAvailableProgramsForUserByName(User user, String searchText, int limit);
	
}
