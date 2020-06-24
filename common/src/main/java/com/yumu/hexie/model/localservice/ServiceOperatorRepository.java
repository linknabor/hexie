/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.model.localservice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


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
    public List<ServiceOperator> findOperators(List<Long> operatorIds);

    public List<ServiceOperator> findByUserId(long userId);
    public List<ServiceOperator> findByTypeAndUserId(int type,long userId);
    
    public List<ServiceOperator> findByType(int type);
    
    @Query("From ServiceOperator r order by POWER(MOD(ABS(r.longitude - ?1),360),2) + POWER(ABS(r.latitude - ?2),2)")
    public List<ServiceOperator> findByLongitudeAndLatitude(Double longitude, Double latitude, Pageable pageable);
    
    @Query(nativeQuery = true,value="select * from serviceoperator a join serviceoperatorSect b on a.id=b.operatorId where b.sectId = ?1 ")
    public List<ServiceOperator> findBySectId(String sectId);
    
    @Query(value="select a.*,b.sectId from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel like CONCAT('%',?3,'%'), 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and b.sectId in ?5 GROUP BY b.operatorId \n#pageable\n",
			countQuery="select count(1) from ( select b.operatorId from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel like CONCAT('%',?3,'%'), 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and b.sectId in ?5 GROUP BY b.operatorId ) b" 
			,nativeQuery = true)
    public Page<Object>  getServiceoperator(int type, String name, String tel, String sectId, 
    		List<String> sectIds,Pageable pageable);
    
    @Query(value="select distinct a.* from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel like CONCAT('%',?3,'%'), 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and b.sectId in ?5 order by id desc \n#pageable\n",
			countQuery="select count(distinct a.id) from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id=b.operatorId where type = ?1 "
			+ " and IF (?2!='', name like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', tel like CONCAT('%',?3,'%'), 1=1)"
			+ " and IF (?4!='', b.sectId =?4, 1=1)"
			+ " and b.sectId in ?5 " 
			,nativeQuery = true)
    public Page<ServiceOperator>  getResvOper(int type, String name, String tel, String sectId, 
    		List<String> sectIds,Pageable pageable);

    public List<ServiceOperator> findByTypeAndUserIdAndCompanyName(int type, long userId, String companyName);
    
    @Query(nativeQuery = true, value="select a.* from serviceoperator a "
    		+ "join serviceoperatorSect b on a.id = b.operatorId "
    		+ "where a.type = ?1 and b.sectId = ?2 ")
    public List<ServiceOperator> findByTypeAndSectId(int type, String sectId);
    
    public ServiceOperator findByTypeAndTelAndOpenId(int type, String tel, String openId);
    
    @Query(nativeQuery = true, value = "delete from serviceoperator where type = ?1 ")
    public void deleteByType(int type);

}
