package com.yumu.hexie.model.localservice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: ServiceOperatorRepository.java, v 0.1 2016年1月1日 上午6:55:38  Exp $
 */
public interface ServiceOperatorRepository  extends JpaRepository<ServiceOperator, Long> {

    @Query("FROM ServiceOperator ro where ro.id in ?1")
	List<ServiceOperator> findOperators(List<Long> operatorIds);

    List<ServiceOperator> findByUserId(long userId);
    
    List<ServiceOperator> findByTypeAndUserId(int type, long userId);
    
    List<ServiceOperator> findByTypeAndUserIdAndAgentId(int type, long userId, long agentId);
    
    List<ServiceOperator> findByTypeAndUserIdAndAgentIdIsNull(int type, long userId);
    
    List<ServiceOperator> findByType(int type);

	List<ServiceOperator> findByTypeAndProductId(int type, long productId);

	ServiceOperator findByTypeAndUserIdAndProductIdAndAgentId(int type, long userId, long productId, long agentId);

    List<ServiceOperator> findByTypeAndAgentId(int type, long agentId);

	List<ServiceOperator> findByTypeAndProductIdAndAgentId(int type, long productId, long agentId);

    List<ServiceOperator> findByTypeAndAgentIdIsNull(int type);

	ServiceOperator findByTypeAndUserIdAndProductIdAndAgentIdIsNull(int type, long userId, long productId);

    @Query(nativeQuery = true,value="select * from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId "
    		+ "where b.sectId = ?1 "
    		+ "and a.type = ?2 ")
	List<ServiceOperator> findBySectId(String sectId, int type);
    
    
    @Query(nativeQuery = true,value="select a.* from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id = b.operatorId "
    		+ "where a.userId = ?1 "
    		+ "and b.sectId = ?2 "
    		+ "and a.type = ?3 ")
	List<ServiceOperator> findByUserIdAndSectIdAndType(long userId, String sectId, int type);
    
    @Query(value="select a.*,b.sectId from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel like CONCAT('%',?3,'%'), 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and (COALESCE(?5) IS NULL OR (b.sectId IN (?5) )) "
			+ "group by b.operatorId",
			countQuery="select count(1) from ( select b.operatorId from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel like CONCAT('%',?3,'%'), 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and (COALESCE(?5) IS NULL OR (b.sectId IN (?5) )) "
			+ "GROUP BY b.operatorId ) b" 
			,nativeQuery = true)
	Page<Object>  getServiceoperator(int type, String name, String tel, String sectId,
									 List<String> sectIds, Pageable pageable);
    
    @Query(value="select distinct a.* from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel like CONCAT('%',?3,'%'), 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and b.sectId in ?5 ",
			countQuery="select count(distinct a.id) from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel like CONCAT('%',?3,'%'), 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and b.sectId in ?5 " 
			,nativeQuery = true)
	Page<ServiceOperator>  getResvOper(int type, String name, String tel, String sectId,
									   List<String> sectIds, Pageable pageable);
    
    List<ServiceOperator> findByTypeAndUserIdAndCompanyName(int type, long userId, String companyName);
    
    @Query(nativeQuery = true, value="select a.* from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id = b.operatorId "
    		+ "where a.type = ?1 and b.sectId = ?2 ")
	List<ServiceOperator> findByTypeAndSectId(int type, String sectId);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from serviceoperator where type = ?1 ")
	void deleteByType(int type);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from serviceoperator where type = ?1 and agentId =?2 and productId = ?3 ")
	void deleteByTypeAndAgentId(int type, long agentId, long productId);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from serviceoperator where type = ?1 and agentId is null ")
	void deleteByTypeAndNullAgent(int type);
    
    String column1 = "s.id, s.openId, s.name, s.tel, '' as appid, s.userId ";
	String column2 = "s.id, s.openId, s.name, s.tel, u.appid, s.userId ";

    @Query(value = "select "
    		+ column1
    		+ "from serviceoperator s "
    		+ "join serviceOperatorItem oi on s.id = oi.operatorId "
    		+ "where s.type = ?1 and oi.serviceId = ?2 ", nativeQuery = true)
	List<Object[]> findByTypeAndServiceId(int type, long serviceId);
    
    @Query(value = "select "
    		+ column1
    		+ "from serviceoperator s "
    		+ "where s.type = ?1 and s.agentId is null ", nativeQuery = true)
	List<Object[]> findByTypeWithAppid(int type);
    
    @Query(value = "select "
    		+ column1
    		+ "from serviceoperator s "
    		+ "where s.type = ?1 and s.agentId = ?2 ", nativeQuery = true)
	List<Object[]> findByTypeAndAgentIdWithAppid(int type, Long agentId);
    
    @Query(value = "select s.* from serviceoperator s "
    		+ "left join serviceOperatorItem oi on s.id = oi.operatorId "
    		+ "where s.type = ?1 and oi.id is null ", nativeQuery = true)
	List<ServiceOperator> queryNoServiceOper(int type);

    @Query(value="select "
    		+ column2
    		+ "from serviceoperator s "
    		+ "join serviceoperatorSect b on s.id = b.operatorId "
    		+ "join user u on u.id = s.userId "
    		+ " where type = ?1 "
			+ " and IF (?2!='', s.name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', s.tel =?3, 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and (COALESCE(?5) IS NULL OR (b.sectId IN (?5) )) "
			+ " group by s.id",
			countQuery="select count(1) from ( select a.id from serviceoperator a "
    		+ " join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel =?3, 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and (COALESCE(?5) IS NULL OR (b.sectId IN (?5) )) "
			+ " GROUP BY a.id ) b" 
			,nativeQuery = true)
	Page<Object[]> getServOperByType(int type, String name, String tel, String sectId,
									 List<String> sectIds, Pageable pageable);
    
    
    @Query(value = "select r.name, r.sectId, r.xiaoquAddress from serviceoperatorSect ss "
    		+ "join region r on ss.sectId = r.sectId "
    		+ "where ss.operatorId = ?1 "
    		+ "and (COALESCE(?2) IS NULL OR (r.sectId IN (?2) )) "
    		+ "order by r.sectId ", 
    		nativeQuery = true)
	List<Object[]> getServeRegion(long operId, List<String> sectIds);
    
}
