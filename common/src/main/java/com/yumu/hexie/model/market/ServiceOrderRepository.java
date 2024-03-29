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
    ServiceOrder findOneWithItem(long orderId);

    @Query("from ServiceOrder p where p.status = "+ModelConstant.ORDER_STATUS_INIT + " and p.closeTime<?1")
    List<ServiceOrder> findTimeoutServiceOrder(long timeLast);

    @Query(value = "select * from ServiceOrder p where p.userId = ?1 and p.status in ?2 and p.orderType in ?3 order by id desc", nativeQuery = true)
    List<ServiceOrder> findByUserAndStatusAndTypes(long userId, List<Integer> statuses, List<Integer> types);

    @Query(value = "select * from ServiceOrder p where p.userId = ?1 and p.status in ?2 and p.orderType = ?3 order by id desc", nativeQuery = true)
    List<ServiceOrder> findByUserAndStatusAndType(long userId, List<Integer> statuses, int orderType);
    
    @Query(value = "select distinct p.* from ServiceOrder p "
    		+ "join orderItem o on o.orderId = p.id "
    		+ "where p.userId = ?1 and p.status in ?2 and p.orderType = ?3 "
    		+ "and if(?4!='', o.productName like CONCAT('%',?4,'%'), 1=1) "
    		+ "and if(?5!='', p.groupRuleId = ?5, 1=1) "
    		+ "and (COALESCE(?6) IS NULL OR (o.isRefund IN (?6) )) "
    		+ "and p.groupOrderId is not null order by p.id desc "
    		, countQuery = "select count(distinct p.*) from ServiceOrder p "
    				+ "join orderItem o on o.orderId = p.id "
    	    		+ "where p.userId = ?1 and p.status in ?2 and p.orderType = ?3 "
    	    		+ "and if(?4!='', o.productName like CONCAT('%',?4,'%'), 1=1) "
    	    		+ "and if(?5!='', p.groupRuleId = ?5, 1=1) "
    	    		+ "and (COALESCE(?6) IS NULL OR (o.isRefund IN (?6) )) "
    	    		+ "and p.groupOrderId is not null order by p.id desc "
    		, nativeQuery = true)
    List<ServiceOrder> findByUserAndStatusAndTypeV3(long userId, List<Integer> status, int orderType, String productName, String ruleId, List<Integer> itemStatus, Pageable page);

    @Query(value = "select * from ServiceOrder p where p.userId = ?1 and p.orderType in ?2 order by id desc", nativeQuery = true)
    List<ServiceOrder> findByUserIdAndOrderType(long userId, List<Integer> types);

    List<ServiceOrder> findByTelAndStatusAndOrderType(String tel, int status, int orderType);

    ServiceOrder findByOrderNo(String orderNo);

    ServiceOrder findById(long orderId);

    @Query("from ServiceOrder p where p.status != " + ModelConstant.ORDER_STATUS_CANCEL + " and p.groupRuleId=?1 and p.orderType=" + ModelConstant.ORDER_TYPE_RGROUP)
    List<ServiceOrder> findByRGroup(long ruleId);

    @Query(" from ServiceOrder p where p.operatorUserId =?1 and p.status in ?2 and p.orderType = ?3 and p.subType =?4 order by p.id desc ")
    List<ServiceOrder> findByOperAndStatusAndOrderTypeAndSubType(long userId, List<Integer> statuses, int orderType, long subType);

    @Query(value = "select p.* from ServiceOrder p where p.status in ?1 and p.orderType = ?2 and p.subType =?3 order by p.id desc ", nativeQuery = true)
    List<ServiceOrder> findByOrderStatusAndOrderTypeAndSubType(List<Integer> statuses, int orderType, long subType);

    @Query(" from ServiceOrder p where p.userId =?1 and p.status in ?2 and p.productId = ?3 and p.orderType = ?4 ")
    List<ServiceOrder> findByUserAndStatusAndProductIdAndOrderType(long userId, List<Integer> statuses, long productId, int orderType);

    @Query(" from ServiceOrder p where p.status in ?1 and p.productId = ?2 and p.orderType = ?3 ")
    List<ServiceOrder> CheckCountByStatusAndProductIdAndOrderType(List<Integer> statuses, long productId, int orderType);

    @Transactional
    @Modifying
    @Query(value = "update serviceorder set comment = ?1,  commentAttitude = ?2, commentQuality = ?3, "
            + "commentService = ?4, commentImgUrls = ?5, pingJiaStatus = ?6, "
            + "commentDate = ?7 where id = ?8 ", nativeQuery = true)
    void updateComment(String comment, int commentAttitude, int commentQuality, int commentService,
                       String commentImrUrls, int commentStatus, Date commentDate, long id);

    @Transactional
    @Modifying
    @Query(value = "update serviceorder set imgUrls = ?1 where id = ?2 ", nativeQuery = true)
    void updateImgUrls(String imgUrls, long orderId);

    @Transactional
    @Modifying
    @Query(value = "update serviceorder set commentImgUrls = ?1 where id = ?2 ", nativeQuery = true)
    void updateCommentImgUrls(String imgUrls, long orderId);

    List<ServiceOrder> findByGroupOrderId(long groupOrderId);
    
    @Query(value = "select p.* from ServiceOrder p where p.status != " + ModelConstant.ORDER_STATUS_CANCEL + " "
    		+ "and p.groupRuleId=?1 and p.orderType=" + ModelConstant.ORDER_TYPE_RGROUP + " "
    		+ "and if(?2!='', p.groupLeaderId = ?2, 1=1) "
    		+ "and (COALESCE(?3) IS NULL OR (p.groupStatus IN (?3) )) "
    		, nativeQuery = true)
    List<ServiceOrder> findByRGroupAndGroupStatusAndLeaderId(String ruleId, String leaderId, List<Integer> groupStatus);


    String queryString = "o.id, o.address, o.count, o.logisticName, o.logisticNo, o.logisticType, o.orderNo, o.orderType, o.productName, "
            + "o.refundDate, o.sendDate, o.status, o.groupStatus, o.tel, o.receiverName, o.price, o.totalAmount, o.agentNo, o.agentName, o.xiaoquName as sectName, o.createDate ";

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
            + "and if(?13!='', o.xiaoquName like CONCAT('%',?13,'%'), 1=1) "
            + "and if(?14!='', o.groupStatus = ?14, 1=1) "
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
            + "and if(?13!='', o.xiaoquName like CONCAT('%',?13,'%'), 1=1) "
            + "and if(?14!='', o.groupStatus = ?14, 1=1) "
            , nativeQuery = true)
    Page<Object[]> findByMultiCondition(List<Integer> types, List<Integer> status, String orderId, String productName,
                                        String orderNo, String receiverName, String tel, String logisticNo, String sendDateBegin, String sendDateEnd,
                                        String agentNo, String agentName, String sectName, String groupStatus, Pageable pageable);

    @Query(value = "select distinct " + queryString + " from serviceorder o "
            + "left join serviceOperatorItem s on o.productId = s.serviceId "
            + "where o.orderType in ( ?1 ) "
            + "and o.status in ( ?2 ) "
            + "and if(?3!='', o.id = ?3, 1=1) "
            + "and if(?4!='', o.productName like CONCAT('%',?4,'%'), 1=1) "
            + "and if(?5!='', o.orderNo = ?5, 1=1) "
            + "and if(?6!='', o.receiverName like CONCAT('%',?6,'%'), 1=1) "
            + "and if(?7!='', o.tel like CONCAT('%',?7,'%'), 1=1) "
            + "and if(?8!='', o.logisticNo = ?8, 1=1) "
            + "and if(?9!='', o.createDate >= ?9, 1=1) "
            + "and if(?10!='', o.createDate <= ?10, 1=1) "
            + "and if(?11!='', o.agentNo = ?11, 1=1) "
            + "and if(?12!='', o.agentName like CONCAT('%',?12,'%'), 1=1) "
            + "and if(?13!='', o.xiaoquName like CONCAT('%',?13,'%'), 1=1) "
            + "and if(?14!='', o.groupStatus = ?14, 1=1) "
            + "and if(?15!='', s.operatorId = ?15, 1=1) "
            + "and if(?16 is not null, o.xiaoquId in (?16), 1=1) "
            , countQuery = "select count(distinct o.id) from serviceorder o "
            + "left join serviceOperatorItem s on o.productId = s.serviceId "
            + "where o.orderType in ( ?1 ) "
            + "and o.status in ( ?2 ) "
            + "and if(?3!='', o.id = ?3, 1=1) "
            + "and if(?4!='', o.productName like CONCAT('%',?4,'%'), 1=1) "
            + "and if(?5!='', o.orderNo = ?5, 1=1) "
            + "and if(?6!='', o.receiverName like CONCAT('%',?6,'%'), 1=1) "
            + "and if(?7!='', o.tel like CONCAT('%',?7,'%'), 1=1) "
            + "and if(?8!='', o.logisticNo = ?8, 1=1) "
            + "and if(?9!='', o.createDate >= ?9, 1=1) "
            + "and if(?10!='', o.createDate <= ?10, 1=1) "
            + "and if(?11!='', o.agentNo = ?11, 1=1) "
            + "and if(?12!='', o.agentName like CONCAT('%',?12,'%'), 1=1) "
            + "and if(?13!='', o.xiaoquName like CONCAT('%',?13,'%'), 1=1) "
            + "and if(?14!='', o.groupStatus = ?14, 1=1) "
            + "and if(?15!='', s.operatorId = ?15, 1=1) "
            + "and if(?16 is not null, o.xiaoquId in (?16), 1=1) "
            , nativeQuery = true)
    Page<Object[]> findByOrder(List<Integer> types, List<Integer> status, String orderId, String productName,
                                        String orderNo, String receiverName, String tel, String logisticNo, String sendDateBegin, String sendDateEnd,
                                        String agentNo, String agentName, String sectName, String groupStatus, String userId, List<String> listSect, Pageable pageable);

    @Query(value = "select distinct " + queryString + " from serviceorder o "
            + "left join serviceOperatorItem s on o.productId = s.serviceId "
            + "where o.orderType in ( ?1 ) "
            + "and o.status in ( ?2 ) "
            + "and if(?3!='', o.createDate >= ?3, 1=1) "
            + "and if(?4!='', o.createDate <= ?4, 1=1) "
            + "and if(?5!='', o.agentNo = ?5, 1=1) "
            + "and if(?6!='', s.operatorId = ?6, 1=1) "
            + "and if(?7 is not null, o.xiaoquId in (?7), 1=1) "
            , nativeQuery = true)
    List<ServiceOrder> findOrderSummary(List<Integer> types, List<Integer> status, long sDate, long eDate, String agentNo, String userid, List<String> listSect);

    
    @Query(value = "select " + queryString + " from serviceorder o "
            + "where o.orderType in ( ?1 ) "
            + "and o.status in ( ?2 ) "
            + "and if(?3!='', o.id = ?3, 1=1) "
            + "and if(?4!='', o.productName like CONCAT('%',?4,'%'), 1=1) "
            + "and if(?5!='', o.orderNo = ?5, 1=1) "
            + "and if(?6!='', o.receiverName like CONCAT('%',?6,'%'), 1=1) "
            + "and if(?7!='', o.tel like CONCAT('%',?7,'%'), 1=1) "
            + "and if(?8!='', o.address like CONCAT('%',?8,'%'), 1=1) "
            + "and if(?9!='', o.logisticNo = ?9, 1=1) "
            + "and if(?10!='', o.sendDate >= ?10, 1=1) "
            + "and if(?11!='', o.sendDate <= ?11, 1=1) "
            + "and if(?12!='', o.agentNo = ?12, 1=1) "
            + "and if(?13!='', o.agentName like CONCAT('%',?13,'%'), 1=1) "
            + "and if(?14!='', o.xiaoquName like CONCAT('%',?14,'%'), 1=1) "
            + "and if(?15!='', o.groupRuleId = ?15, 1=1) "
            + "and if(?16!='', o.groupStatus = ?16, 1=1) "
            + "and if(?17!='', o.groupLeaderId = ?17, 1=1) "
            + "and if(?18!='', o.createDate >= ?18, 1=1) "
            + "and if(?19!='', o.createDate <= ?19, 1=1) "
            + "and if(?20 is not null, o.xiaoquId in (?20), 1=1) "
            , countQuery = "select count(1) from serviceorder o "
            + "where o.orderType in ( ?1 ) "
            + "and o.status in ( ?2 ) "
            + "and if(?3!='', o.id = ?3, 1=1) "
            + "and if(?4!='', o.productName like CONCAT('%',?4,'%'), 1=1) "
            + "and if(?5!='', o.orderNo = ?5, 1=1) "
            + "and if(?6!='', o.receiverName like CONCAT('%',?6,'%'), 1=1) "
            + "and if(?7!='', o.tel like CONCAT('%',?7,'%'), 1=1) "
            + "and if(?8!='', o.address like CONCAT('%',?8,'%'), 1=1) "
            + "and if(?9!='', o.logisticNo = ?9, 1=1) "
            + "and if(?10!='', o.sendDate >= ?10, 1=1) "
            + "and if(?11!='', o.sendDate <= ?11, 1=1) "
            + "and if(?12!='', o.agentNo = ?12, 1=1) "
            + "and if(?13!='', o.agentName like CONCAT('%',?13,'%'), 1=1) "
            + "and if(?14!='', o.xiaoquName like CONCAT('%',?14,'%'), 1=1) "
            + "and if(?15!='', o.groupRuleId = ?15, 1=1) "
            + "and if(?16!='', o.groupStatus = ?16, 1=1) "
            + "and if(?17!='', o.groupLeaderId = ?17, 1=1) "
            + "and if(?18!='', o.createDate >= ?18, 1=1) "
            + "and if(?19!='', o.createDate <= ?19, 1=1) "
            + "and if(?20 is not null, o.xiaoquId in (?20), 1=1) "
            , nativeQuery = true)
    Page<Object[]> findByMultiConditionAndLeaderId(List<Integer> types, List<Integer> status, String orderId, String productName,
                                        String orderNo, String receiverName, String tel, String address, String logisticNo, String sendDateBegin, String sendDateEnd,
                                        String agentNo, String agentName, String sectName, String groupRuleId, String groupStatus, String groupLeaderId, 
                                        long createDateBegin, long createDateEnd, List<String> sectList, Pageable pageable);


    //查询团购的订单列表
    List<ServiceOrder> findByGroupRuleId(long ruleId);
    
    //查询团购的订单列表
    List<ServiceOrder> findByGroupLeaderId(long groupLeaderId);

    //汇总团购订单里包含的商品和购买数量
    @Query(value = "select p.id as productId, p.name as productName, sum(i.count) as count, sum(case when i.verifyStatus='0' then 1 else 0 end) as verifyNum "
            + "from serviceorder o "
            + "join orderItem i on o.id = i.orderId "
            + "join product p on i.productId = p.id "
            + "where o.groupLeaderId = ?1 "
            + "and if(?2!=0, o.groupRuleId = ?2, 1=1) "
            + "and o.status in ( ?3 ) "
            + "group by p.id "
            , nativeQuery = true)
    List<Object[]> findProductSum(long groupLeaderId, long groupRuleId, List<Integer> status);

    //分页查询团购订单
    String sqlCol = "o.groupNum, o.id as orderId, o.orderNo, o.status, o.payDate, o.createDate, o.count, o.price, o.receiverName, " +
            "o.tel, o.address, o.logisticType, o.memo, o.userId, o.refundType ";
    @Query(value = "select distinct " + sqlCol + " from serviceorder o "
            + "join orderItem i on o.id = i.orderId "
            + "where o.groupLeaderId = ?1 "
            + "and IF(?2 !=0, o.groupRuleId = ?2, 1 = 1) "
            + "and IF(?3 !=0, o.xiaoquId = ?3, 1 = 1) "
            + "and o.status in ( ?4 ) "
            + "and IF(?5 !='', i.verifyStatus = ?5, 1 = 1) "
            + "and (COALESCE(?6) IS NULL OR (i.isRefund IN (?6) )) "
            + "and IF(?7 != '', o.tel like CONCAT('%',?7,'%'), 1=1 )"
            , countQuery = "select count(1) from serviceorder o "
            + "join orderItem i on o.id = i.orderId "
            + "where o.groupLeaderId = ?1 "
            + "and IF(?2 !=0, o.groupRuleId = ?2, 1 = 1) "
            + "and IF(?3 !=0, o.xiaoquId = ?3, 1 = 1) "
            + "and o.status in ( ?4 ) "
            + "and IF(?5 !='', i.verifyStatus = ?5, 1 = 1) "
            + "and (COALESCE(?6) IS NULL OR (i.isRefund IN (?6) )) "
            + "and IF(?7 != '', o.tel like CONCAT('%',?7,'%'), 1=1 )"
            , nativeQuery = true)
    Page<Object[]> findByGroupRuleIdPage(long groupLeaderId, long ruleId, long regionId, List<Integer> status, String verifyStatus, 
    		List<Integer> itemStatus, String tel, Pageable pageable);

    //根据订单和团长ID查询订单
    ServiceOrder findByIdAndGroupLeaderId(long orderId, long groupLeaderId);
}
