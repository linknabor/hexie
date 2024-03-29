package com.yumu.hexie.model.market;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yumu.hexie.model.ModelConstant;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>  {

	public List<OrderItem> findByServiceOrder(ServiceOrder order);
	

	public int countByCollocationId(long collocationId);
	@Query("select count(o.id) from OrderItem o  left join o.serviceOrder "
			+ " where o.userId =?1"
			+ " and o.ruleId in ?2"
			+ " and o.orderType in ?3 "
			+ " and o.serviceOrder.status in ("
			+ ModelConstant.ORDER_STATUS_PAYED +","
			+ ModelConstant.ORDER_STATUS_SENDED +","
			+ ModelConstant.ORDER_STATUS_RECEIVED +","
			+ ModelConstant.ORDER_STATUS_CONFIRM +","
			+ ModelConstant.ORDER_STATUS_INIT
			+ ")")
	public int countBuyedOrderItem(long userId, long ruleId, int orderType);
	
	@Query(value = "select * from orderItem where ruleId = ?1 and userId = ?2 and orderId = ?3 ", nativeQuery = true)
	public List<OrderItem> findByRuleIdAndUserIdAndOrderId(long ruleId, long userId, long orderId);

	public List<OrderItem> findByServiceOrderAndIdIn(ServiceOrder order, List<Long> ids);

	public List<OrderItem> findByServiceOrderAndProductIdIn(ServiceOrder order, List<Long> productId);

	List<OrderItem> findByServiceOrderAndIsRefund(ServiceOrder order, int isRefund);
}
