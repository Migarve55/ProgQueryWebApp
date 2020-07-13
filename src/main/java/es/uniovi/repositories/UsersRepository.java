package es.uniovi.repositories;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import es.uniovi.entities.User;

public interface UsersRepository extends CrudRepository<User, Long> {
	
	@Query("select user from User user where user.email = ?1")
	User findByEmail(String email);

}
