package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
	public User findByOpenid(String openid);
	public User findByTel(String tel);
	
	public List<User> findByShareCode(String shareCode);
	@Query(nativeQuery=true ,value="select *  from User a limit ?1,?2")
	public List<User> getBindHouseUser1(int pageNum,int pageSise);
	
	@Query(nativeQuery=true ,value="select *  from tempuser a limit ?1,?2")
	public List<User>  getBindHouseUser(int pageNum,int pageSise);
}
