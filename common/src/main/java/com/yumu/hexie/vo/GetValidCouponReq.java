package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.model.market.OrderItem;

public class GetValidCouponReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2774983865620345571L;
	
	private int salePlanType;	//订单类型
	private List<OrderItem> itemList;
	
	public int getSalePlanType() {
		return salePlanType;
	}
	public void setSalePlanType(int salePlanType) {
		this.salePlanType = salePlanType;
	}
	public List<OrderItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<OrderItem> itemList) {
		this.itemList = itemList;
	}
	@Override
	public String toString() {
		return "GetValidCouponReq [salePlanType=" + salePlanType + ", itemList=" + itemList + "]";
	}
	

}
