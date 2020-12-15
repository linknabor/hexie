/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.model.localservice.repair;

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
 * @version $Id: RepairOrderRepository.java, v 0.1 2016年1月1日 上午6:56:47  Exp $
 */
public interface RepairOrderRepository  extends JpaRepository<RepairOrder, Long> {

    public List<RepairOrder> findByOrderId(long orderId);

    @Query("FROM RepairOrder ro where ro.operatorUserId = ?1 and ro.operatorDeleted = false and ro.status in ?2 order by ro.id desc")
    public List<RepairOrder> queryByOperatorUser(long operatorUserId,List<Integer> statuses,Pageable page);
    
    @Query("FROM RepairOrder ro where ro.userId = ?1 and ro.userDeleted = false order by ro.id desc")
    public List<RepairOrder> queryByUser(long userId,Pageable page);
    
	@Query(value="SELECT o.* FROM repairorder o where "
			+ " IF (?1!='', o.payType = ?1, 1=1)" 
			+ " and IF (?2!='', o.status = ?2, 1=1)"
			+ " and IF (?3!='', o.finishByUser = ?3, 1=1)"
			+ " and IF (?4!='', o.finishByOperator = ?4, 1=1)"
			+ " and IF (?5!='', o.address like CONCAT('%',?5,'%'), 1=1)"
			+ " and IF (?6!='', o.tel like CONCAT('%',?6,'%'), 1=1)"
			+ " and IF (?7!='', o.operatorName like CONCAT('%',?7,'%'), 1=1)"
			+ " and IF (?8!='', o.operatorTel like CONCAT('%',?8,'%'), 1=1)"
			+ " and IF (?9!='', o.sectId = ?9, 1=1)"
			+ " and o.sectId  in ?10 ",
			countQuery="SELECT count(*) FROM repairorder o where "
			+ " IF (?1!='', o.payType = ?1, 1=1)" 
			+ " and IF (?2!='', o.status = ?2, 1=1)"
			+ " and IF (?3!='', o.finishByUser = ?3, 1=1)"
			+ " and IF (?4!='', o.finishByOperator = ?4, 1=1)"
			+ " and IF (?5!='', o.address like CONCAT('%',?5,'%'), 1=1)"
			+ " and IF (?6!='', o.tel like CONCAT('%',?6,'%'), 1=1)"
			+ " and IF (?7!='', o.operatorName like CONCAT('%',?7,'%'), 1=1)"
			+ " and IF (?8!='', o.operatorTel like CONCAT('%',?8,'%'), 1=1)"
			+ " and IF (?9!='', o.sectId = ?9, 1=1)"
			+ " and o.sectId  in ?10 "
			,nativeQuery = true)
	public Page<RepairOrder> getRepairOderList(String payType, String status, String finishByUser, String finishByOpeator,
			String address, String tel, String operatorName, String operatorTel,String sectId,List<String> sectIds,Pageable pageable);
    
	final String column1 = "o.id, o.createDate, o.status, o.cspId, o.sectId, o.xiaoquName, o.address, o.receiverName, o.tel,"  
			+ "o.operatorName, o.operatorTel, o.payType, s.price, o.finishTime, o.operatorFinishTime, "
			+ "o.commentQuality, o.commentAttitude, o.commentService, o.comment, o.memo ";
	
	@Query(value = "select " + column1 + " from repairOrder o left join serviceOrder s on o.orderId = s.id where "
			+ "IF (?1!='', o.createDate >= ?1, 1=1) "
			+ "and IF (?2!='', o.createDate <= ?2, 1=1) "
			+ "and IF (?3!='', o.address like CONCAT('%',?3,'%'), 1=1) "
			+ "and IF (?4!='', o.tel = ?4, 1=1) "
			+ "and IF (?5!='', o.operatorName like CONCAT('%',?5,'%'), 1=1) "
			+ "and IF (?6!='', o.operatorTel = ?6, 1=1) "
			+ "and IF (?7!='', o.status = ?7, 1=1) "
			+ "and IF (?8!='', o.payType = ?8, 1=1) "
			+ "and IF (?9!='', o.finishByUser = ?9, 1=1) "
			+ "and IF (?10!='', o.finishByOperator = ?10, 1=1) "
			+ "and (COALESCE(?11) IS NULL OR (o.sectId IN (?11) )) "
			, countQuery = "select count(1) from repairOrder o left join serviceOrder s on o.orderId = s.id where "
					+ "IF (?1!='', o.createDate >= ?1, 1=1) "
					+ "and IF (?2!='', o.createDate <= ?2, 1=1) "
					+ "and IF (?3!='', o.address like CONCAT('%',?3,'%'), 1=1) "
					+ "and IF (?4!='', o.tel = ?4, 1=1) "
					+ "and IF (?5!='', o.operatorName like CONCAT('%',?5,'%'), 1=1) "
					+ "and IF (?6!='', o.operatorTel = ?6, 1=1) "
					+ "and IF (?7!='', o.status = ?7, 1=1) "
					+ "and IF (?8!='', o.payType = ?8, 1=1) "
					+ "and IF (?9!='', o.finishByUser = ?9, 1=1) "
					+ "and IF (?10!='', o.finishByOperator = ?10, 1=1) "
					+ "and (COALESCE(?11) IS NULL OR (o.sectId IN (?11) )) "
			, nativeQuery = true)
	public Page<Object[]> getROrderList(String startDate, String endDate, String address, String tel, 
			String operatorName, String operatorTel, String status, String payType, String finishByUser, String finishByOperator, 
			List<String> sectIds, Pageable pageable);
	
}
