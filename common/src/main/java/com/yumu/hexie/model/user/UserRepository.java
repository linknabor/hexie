package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
	public User findByOpenid(String openid);
	public List<User> findByShareCode(String shareCode);
	@Query("from User a")
	public List<User> getBindHouseUser();
}
