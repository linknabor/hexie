package com.yumu.hexie.integration.workorder.resp;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDetailVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2934790398357119234L;
	
	@JsonProperty("order_detail")
	private OrderDetail orderDetail;
	@JsonProperty("order_flow")
	private List<OrderFlow> orderFlows;
	
	public OrderDetail getOrderDetail() {
		return orderDetail;
	}
	public void setOrderDetail(OrderDetail orderDetail) {
		this.orderDetail = orderDetail;
	}
	public List<OrderFlow> getOrderFlows() {
		return orderFlows;
	}
	public void setOrderFlows(List<OrderFlow> orderFlows) {
		this.orderFlows = orderFlows;
	}
	@Override
	public String toString() {
		return "OrderDetailVO [orderDetail=" + orderDetail + ", orderFlows=" + orderFlows + "]";
	}
	
	
}
