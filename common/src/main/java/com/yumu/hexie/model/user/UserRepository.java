package com.yumu.hexie.model.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	public List<User> findByOpenid(String openid);
	public List<User> findByTel(String tel);
	public List<User> findByTelAndOpenid(String tel, String openid);
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
	
	public List<User> findByAppId(String appId, Pageable pageable);
	
	public List<User> findBySectId(String sectId);

	public List<User> findByWuyeIdAndAppId(String wuyeId, String appId);

	/**
	 * 根据增量更新。更新语句的where 条件必须带上原积分值，这样可以解决多次调用带来的幂等性问题。
	 * @param userId
	 * @param point
	 * @return
	 */
	@Modifying
	@Transactional
	@Query(value = "update user set point = point + ?1 where id = ?2 and point = ?3 ", nativeQuery = true )
	public int updatePointByIncrement(int addPoint, long userId, int oriPoint);
	
	/**
	 * 根据增量更新。更新语句的where 条件必须带上原积分值，这样可以解决多次调用带来的幂等性问题。
	 * @param userId
	 * @param point
	 * @return
	 */
	@Modifying
	@Transactional
	@Query(value = "update user set point = ?1 where id = ?2 ", nativeQuery = true )
	public int updatePointByTotal(int totalPoint, long userId);
	
	@Modifying
	@Transactional
	@Query(value = "update user set wuyeId = ?1 where id = ?2 ", nativeQuery = true)
	public int updateUserWuyeId(String wuyeId, long id);

	@Modifying
	@Transactional
	@Query(value = "update user set couponCount = ?1 where id = ?2 and couponCount = ?3 ", nativeQuery = true)
	public int updateUserCoupon(int currCount, long id, int oriCount);
	
	@Modifying
	@Transactional
	@Query(value = "update user set zhima = zhima + ?1 where id = ?2 ", nativeQuery = true)
	public int updateUserZhima(int increase, long id);
	
	public List<User> findByUnionid(String unionid);
	
	@Query(value = "select * from user u where u.cspId is not null "
			+ "and IF (?1!='', u.cspId = ?1, 1=1) "
			+ "and IF (?2!='', u.tel = ?2, 1=1) " 
			+ "and IF (?3!='', u.name like CONCAT('%',?3,'%'), 1=1) "
			+ "and (COALESCE(?4) IS NULL OR (u.sectId IN (?4) )) "
			, countQuery = "select count(1) from user u where u.cspId is not null "
					+ "and IF (?1!='', u.cspId = ?1, 1=1) "
					+ "and IF (?2!='', u.tel = ?2, 1=1) " 
					+ "and IF (?3!='', u.name like CONCAT('%',?3,'%'), 1=1) "
					+ "and (COALESCE(?4) IS NULL OR (u.sectId IN (?4) )) "
			, nativeQuery = true )
	public Page<User> findByMultiCondition(String cspId, String tel, String name, List<String> sectId, Pageable pageable);
	
	
	public List<User> findByCspIdAndWuyeIdIn(String cspId, List<String> wuyeIds);
	
	public User findByMiniopenid(String miniopenid);

	public List<User> findByTelAndAppId(String tel, String appid);

	public User findByAppIdAndUniqueCode(String appid, String uniqueCode);
	
	public List<User> findByAliuserid(String aliuserid);
	
	public List<User> findByOriSysAndOriUserId(String oriSys, Long oriUserId);
	
	@Query(value = "select count(1) as counts from user where miniappid = ?1 and tel is not null group by miniappid ", nativeQuery = true)
	public List<Map<String, Object>> getTotalRegisterByMiniAppid(String miniappid);
	
	@Query(value = "select count(1) as counts from user where miniappid = ?1 and ( sectId is not null or sectId <> '' ) ", nativeQuery = true)
	public List<Map<String, Object>> getTotalBindByMiniAppid(String miniappid);
}
