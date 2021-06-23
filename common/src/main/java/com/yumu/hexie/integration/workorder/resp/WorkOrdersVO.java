package com.yumu.hexie.integration.workorder.resp;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkOrdersVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -165948551146299607L;
	
	@JsonProperty("work_orders")
	private List<WorkOrder> orderList;
	public List<WorkOrder> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<WorkOrder> orderList) {
		this.orderList = orderList;
	}
	@Override
	public String toString() {
		return "WorkOrdersVO [orderList=" + orderList + "]";
	}
	
	
	
	

}
