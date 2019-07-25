package es.uniovi.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import es.uniovi.entities.Query;
import es.uniovi.entities.User;

public interface QueriesRepository extends CrudRepository<Query, Long> {

	Page<Query> findAllByUserOrderByName(Pageable pageable, User user);
	
	Query findByName(String name);
	
	@org.springframework.data.jpa.repository.Query(value = "select q.* from query q where q.name = ?1 and (q.public_for_all = true or q.user_id = ?2 or q.id in (select c.query_id from public_to c where c.user_id = ?2))", nativeQuery = true)
	Query findAvailableByNameAndUser(String name, User user);
	
	@org.springframework.data.jpa.repository.Query(value = "select q.* from query q where q.name REGEXP ?1 and (q.public_for_all = true or q.user_id = ?2 or q.id in (select c.query_id from public_to c where c.user_id = ?2))", nativeQuery = true)
	List<Query> findAllByFamily(String family, User user);
	
	@org.springframework.data.jpa.repository.Query("select q from Query q where user = ?1 and name like ?2 order by name")
	Page<Query> findAllByUserAndNameLike(Pageable pageable, User user, String name);
	
	@org.springframework.data.jpa.repository.Query(value = "select q.* from query q where q.public_for_all = true or q.user_id = ?1 or q.id in (select c.query_id from public_to c where c.user_id = ?1)", nativeQuery = true)
	List<Query> findAvailableQueriesForUser(User user);
	
}
