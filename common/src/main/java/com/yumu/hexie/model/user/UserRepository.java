package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
	public List<User> findByOpenid(String openid);
	public List<User> findByTel(String tel);
	
	public List<User> findByShareCode(String shareCode);
	
	@Query(nativeQuery=true ,value="select *  from user where shareCode is null")
	public List<User> getShareCodeIsNull();
	
	@Query(nativeQuery=true ,value="SELECT shareCode from user group by shareCode having count(1) > 1")
	public List<String> getRepeatShareCodeUser();
	
	@Query(nativeQuery=true ,value="SELECT * from user where shareCode = ?")
    public List<User> getUserByShareCode(String shareCode);
	
	public List<User> findByWuyeId(String wuyeId);
	
	public User findById(long id);
	
	/**
	 * 获取已注册用户的总数
	 * @return
	 */
	@Query(nativeQuery = true, value = "select count(1) from user where tel is not null ")
	public Integer getRegisteredUserCount();
	
	public List<User> findByAppId(String appId);


}
