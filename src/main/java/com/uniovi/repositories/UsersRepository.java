package com.uniovi.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.uniovi.entities.User;

public interface UsersRepository extends CrudRepository<User, Long> {
	
	@Query("select user from User user where user.email = ?1")
	User findByEmail(String email);
	
	@Query("select user from User user where user.role <> 'ROLE_ADMIN'")
	Page<User> findAll(Pageable pageable);
	
	@Query("select user from User user where user.role <> 'ROLE_ADMIN' and lower(user.name || user.lastName) like lower(?1)")
	Page<User> findByNameAndSurname(Pageable pageable, String searchText);
	
	@Modifying
	@Query("update User set balance = ?2 where id = ?1")
	void setUserBalance(Long id, double balance);
	
	@Modifying
	@Transactional
	//@Query("delete from User where id=?1")
	void deleteById(Long id);

}
