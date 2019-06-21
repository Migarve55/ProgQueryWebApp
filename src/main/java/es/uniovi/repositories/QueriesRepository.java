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
	
	Query findByNameAndUser(String name, User user);
	
	@org.springframework.data.jpa.repository.Query(value = "select q.* from Query q where REGEXP_MATCHES(q.name,?1)", nativeQuery = true)
	List<Query> findAllByFamily(String family, User user);
	
	@org.springframework.data.jpa.repository.Query("select q from Query q where user = ?1 and name like ?2 order by name")
	Page<Query> findAllByUserAndNameLike(Pageable pageable, User user, String name);
	
	@org.springframework.data.jpa.repository.Query(value = "select q.* from Query q where q.public_for_all = true or q.user_id = ?1 or q.id in (select c.query_id from can_edit c where c.user_id = ?1)", nativeQuery = true)
	List<Query> findAvailableQueriesForUser(User user);
	
}
