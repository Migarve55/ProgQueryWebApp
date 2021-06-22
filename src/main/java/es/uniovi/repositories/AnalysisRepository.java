package es.uniovi.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Analysis;
import es.uniovi.entities.User;

public interface AnalysisRepository extends CrudRepository<Analysis, Long> {

	Page<Analysis> findAllByUserOrderByName(Pageable pageable, User user);
	
	List<Analysis> findAllByUserOrderByName(User user);
	
	Analysis findByName(String name);
	
	@Query("select q from Analysis q where q.publicForAll = true")
	List<Analysis> findAllPublic();
	
	@Query(value = "select q.* from analysis q where q.name = ?1 and (q.public_for_all = true or q.user_id = ?2 or q.id in (select c.analysis_id from public_to c where c.user_id = ?2))", nativeQuery = true)
	Analysis findAvailableByNameAndUser(String name, User user);
	
	@Query(value = "select q.* from analysis q where q.name REGEXP ?1 and (q.public_for_all = true or q.user_id = ?2 or q.id in (select c.analysis_id from public_to c where c.user_id = ?2))", nativeQuery = true)
	List<Analysis> findAllByFamily(String family, User user);
	
	@Query("select q from Analysis q where user = ?1 and name like ?2 order by name")
	Page<Analysis> findAllByUserAndNameLike(Pageable pageable, User user, String name);
	
	@Query(value = "select q.* from analysis q where q.public_for_all = true or q.user_id = ?1 or q.id in (select c.analysis_id from public_to c where c.user_id = ?1) order by q.name", nativeQuery = true)
	List<Analysis> findAvailableForUser(User user);
	
	@Query(value = "select q.* from analysis q where q.name like ?2 and (q.public_for_all = true or q.user_id = ?1 or q.id in (select c.analysis_id from public_to c where c.user_id = ?1)) order by q.name limit ?3", nativeQuery = true)
	List<Analysis> findAvailableForUserByName(User user, String name, int limit);
	
	@Query(value = "select q.* from analysis q where q.name like ?2 and ( q.public_for_all = true or q.user_id = ?1 or q.id in (select c.analysis_id from public_to c where c.user_id = ?1)) order by q.name", nativeQuery = true)
	Page<Analysis> findAvailableForUser(Pageable pageable, User user, String name);
	
}
