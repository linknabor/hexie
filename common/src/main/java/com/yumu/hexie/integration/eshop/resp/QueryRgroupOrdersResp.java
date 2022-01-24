package com.yumu.hexie.integration.eshop.resp;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.integration.eshop.mapper.QueryOrderMapper;

public class QueryRgroupOrdersResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8436363019339607458L;
	
	private List<QueryOrderMapper> orderList;
	private QueryRgroupSummaryResp orderSummary;
	
	public List<QueryOrderMapper> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<QueryOrderMapper> orderList) {
		this.orderList = orderList;
	}
	public QueryRgroupSummaryResp getOrderSummary() {
		return orderSummary;
	}
	public void setOrderSummary(QueryRgroupSummaryResp orderSummary) {
		this.orderSummary = orderSummary;
	}
	
	

}
