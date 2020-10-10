package com.yumu.hexie.model.market;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.model.ModelConstant;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
	
	@Query("from ServiceOrder p left outer join fetch p.items where p.id=?1")
	public ServiceOrder findOneWithItem(long orderId);
	@Query("from ServiceOrder p where p.status = "+ModelConstant.ORDER_STATUS_INIT + " and p.closeTime<?1")
	public List<ServiceOrder> findTimeoutServiceOrder(long timeLast);
	
	@Query(value = "select * from ServiceOrder p where p.userId = ?1 and p.status in ?2 order by id desc", nativeQuery = true)
	public List<ServiceOrder> findByUserAndStatus(long userId,List<Integer> statuses);
	
	@Query(value = "select * from ServiceOrder p where p.userId = ?1 and p.status in ?2 and p.orderType in ?3 order by id desc", nativeQuery = true)
	public List<ServiceOrder> findByUserAndStatusAndTypes(long userId,List<Integer> statuses, List<Integer> types);

	@Query(value = "select * from ServiceOrder p where p.userId = ?1 and p.status in ?2 and p.orderType = ?3 order by id desc", nativeQuery = true)
	public List<ServiceOrder> findByUserAndStatusAndType(long userId,List<Integer> statuses, int orderType);
	
	@Query(value = "select * from ServiceOrder p where p.userId = ?1 and p.orderType in ?2 order by id desc", nativeQuery = true)
	public List<ServiceOrder> findByUserIdAndOrderType(long userId, List<Integer> types);
	
	public List<ServiceOrder> findByTelAndStatusAndOrderType(String tel, int status, int orderType);
	
	public ServiceOrder findByOrderNo(String orderNo);

	public List<ServiceOrder> findAllByTel(String tel);

	public List<ServiceOrder> findAllByStatus(int status,Pageable page);
	public int countByStatus(int status);
	

	@Query("from ServiceOrder p where p.status != "+ModelConstant.ORDER_STATUS_CANCEL + " and p.groupRuleId=?1 and p.orderType=" + ModelConstant.ORDER_TYPE_RGROUP)
	public List<ServiceOrder> findByRGroup(long ruleId);

	public List<ServiceOrder> findAllByProductId(long productId);
	
	@Query(" from ServiceOrder p where p.operatorUserId =?1 and p.status in ?2 and p.orderType = ?3 and p.subType =?4 order by p.id desc ")
	public List<ServiceOrder> findByOperAndStatusAndOrderTypeAndSubType(long userId, List<Integer> statuses, int orderType, long subType);
	
	@Query(value = "select p.* from ServiceOrder p where p.status in ?1 and p.orderType = ?2 and p.subType =?3 order by p.id desc ", nativeQuery = true)
	public List<ServiceOrder> findByOrderStatusAndOrderTypeAndSubType(List<Integer> statuses, int orderType, long subType);
	
	@Query(" from ServiceOrder p where p.userId =?1 and p.status in ?2 and p.merchantId = ?3 and p.orderType = ?4 ")
	public List<ServiceOrder> findByUserAndStatusAndMerchatIdAndOrderType(long userId, List<Integer> statuses, long merchantId, int orderType);
	
	
	@Query(" from ServiceOrder p where p.userId =?1 and p.status in ?2 and p.productId = ?3 and p.orderType = ?4 ")
	public List<ServiceOrder> findByUserAndStatusAndProductIdAndOrderType(long userId, List<Integer> statuses, long productId, int orderType);
	
	@Query(" from ServiceOrder p where p.status in ?1 and p.productId = ?2 and p.orderType = ?3 ")
	public List<ServiceOrder> CheckCountByStatusAndProductIdAndOrderType(List<Integer> statuses, long productId, int orderType);
	
	@Query(" from ServiceOrder p where p.userId =?1 and p.status in ?2 and p.groupRuleId = ?3 and p.orderType = ?4 ")
	public List<ServiceOrder> CheckByUserAndStatusAndRuleIdAndOrderType(long userId, List<Integer> statuses, long ruleId, int orderType);
	
	@Query(value = "select s.id from serviceorder s join orderitem i on s.id = i.orderId where s.userId = ?1 and i.productId = ?2 and s.status =?3 group by s.id ", nativeQuery = true)
	public List<ServiceOrder> findAllByUserAndStatusAndProductIdAndOrderType(long userId, long productId, List<Integer> statuses);
	
	@Transactional
	@Modifying
	@Query(value = "update serviceorder set comment = ?1,  commentAttitude = ?2, commentQuality = ?3, "
			+ "commentService = ?4, commentImgUrls = ?5, pingJiaStatus = ?6, "
			+ "commentDate = ?7 where id = ?8 ", nativeQuery = true)
	public void updateComment(String comment, int commentAttitude, int commentQuality, int commentService, 
			String commentImrUrls, int commentStatus, Date commentDate, long id);

	
	@Transactional
	@Modifying
	@Query(value = "update serviceorder set imgUrls = ?1 where id = ?2 ", nativeQuery = true)
	public void updateImgUrls(String imgUrls, long orderId);
	
	@Transactional
	@Modifying
	@Query(value = "update serviceorder set commentImgUrls = ?1 where id = ?2 ", nativeQuery = true)
	public void updateCommentImgUrls(String imgUrls, long orderId);

	public List<ServiceOrder> findByGroupOrderId(long groupOrderId);
	
	public ServiceOrder findByGroupOrderIdAndAgentId(long groupOrderId, long agentId);
	
	String queryString = "o.id, o.address, o.count, o.logisticName, o.logisticNo, o.logisticType, o.orderNo, o.orderType, o.productName, "
			+ "o.refundDate, o.sendDate, o.status, o.tel, o.receiverName, o.totalAmount, o.agentNo, o.agentName";
	
	@Query(value = "select " + queryString + " from serviceorder o "
			+ "where o.orderType in ( ?1 ) "
			+ "and o.status in ( ?2 ) "
			+ "and if(?3!='', o.id = ?3, 1=1) "
			+ "and if(?4!='', o.productName like CONCAT('%',?4,'%'), 1=1) "
			+ "and if(?5!='', o.orderNo = ?5, 1=1) "
			+ "and if(?6!='', o.receiverName like CONCAT('%',?6,'%'), 1=1) "
			+ "and if(?7!='', o.tel like CONCAT('%',?7,'%'), 1=1) "
			+ "and if(?8!='', o.logisticNo = ?8, 1=1) "
			+ "and if(?9!='', o.sendDate >= ?9, 1=1) "
			+ "and if(?10!='', o.sendDate <= ?10, 1=1) "
			+ "and if(?11!='', o.agentNo = ?11, 1=1) "
			+ "and if(?12!='', o.agentName like CONCAT('%',?12,'%'), 1=1) "
			, countQuery = "select count(1) from serviceorder o "
					+ "where o.orderType in ( ?1 ) "
							+ "and o.status in ( ?2 ) "
							+ "and if(?3!='', o.id = ?3, 1=1) "
							+ "and if(?4!='', o.productName like CONCAT('%',?4,'%'), 1=1) "
							+ "and if(?5!='', o.orderNo = ?5, 1=1) "
							+ "and if(?6!='', o.receiverName like CONCAT('%',?6,'%'), 1=1) "
							+ "and if(?7!='', o.tel like CONCAT('%',?7,'%'), 1=1) "
							+ "and if(?8!='', o.logisticNo = ?8, 1=1) "
							+ "and if(?9!='', o.sendDate >= ?9, 1=1) "
							+ "and if(?10!='', o.sendDate <= ?10, 1=1) "
							+ "and if(?11!='', o.agentNo = ?11, 1=1) "
							+ "and if(?12!='', o.agentName like CONCAT('%',?12,'%'), 1=1) "
							, nativeQuery = true)
	public Page<Object[]> findByMultiCondition(List<Integer> types, List<Integer> status, String orderId, String productName, 
			String orderNo, String receiverName, String tel, String logisticNo, String sendDateBegin, String sendDateEnd, 
			String agentNo, String agentName, Pageable pageable);

}
