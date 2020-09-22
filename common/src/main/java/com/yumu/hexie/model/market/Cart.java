package com.yumu.hexie.model.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.commonsupport.info.ProductRule;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 *类似购物车，用来暂存商品项，以后可扩展成购物车 
 *2020-08-12 添加add del clear方法，改造成为购物车
 * @author david
 *
 */
public class Cart extends BaseModel {

	private static final long serialVersionUID = 8850980148126766715L;
	
	private List<OrderItem> items;
	private Map<Long, OrderItem> itemsMap;	//key:ruleId, value:orderItem
	private BigDecimal totalAmount = BigDecimal.ZERO;	//不含运费
	private BigDecimal totalShipFee = BigDecimal.ZERO;	//运费，需要计算几件包邮
	private BigDecimal totalPrice = BigDecimal.ZERO;	//商品费用 + 运费
	private int totalCount = 0;
	
	/**
	 * 添加商品
	 * @param goods
	 */
	public void add(OrderItem orderItem, ProductRule productRule) {
		
		BigDecimal unitPrice = new BigDecimal(String.valueOf(productRule.getPrice()));	//前端传上来的价格不可信，全部采用后端计算
		BigDecimal count = new BigDecimal(orderItem.getCount());	//数量采用前端传的
		BigDecimal amount = unitPrice.multiply(count);
		
		fillRuleInfo(orderItem, productRule);
		
		if (itemsMap == null) {
			itemsMap = new HashMap<>();
		}
		if (!itemsMap.containsKey(orderItem.getRuleId())) {	//用map存放，避免每次迭代itemsList
			
			int perLimit = productRule.getLimitNumOnce();
			if (orderItem.getCount() > perLimit) {
				throw new BizValidateException("每人限购" + perLimit + "件。");
			}
			int totalCount = productRule.getTotalCount();
			if (orderItem.getCount() > totalCount) {
				throw new BizValidateException("数量超出范围，库存不足。");
			}
			
			orderItem.setAmount(amount.floatValue());
			itemsMap.put(orderItem.getRuleId(), orderItem);
		}else {
			OrderItem existItem = itemsMap.get(orderItem.getRuleId());
			existItem.setCount(existItem.getCount() + orderItem.getCount());
			BigDecimal existAmount = new BigDecimal(String.valueOf(existItem.getAmount()));
			existItem.setAmount(existAmount.add(amount).floatValue());
			
			fillRuleInfo(existItem, productRule);
			
		}
		this.totalAmount = this.totalAmount.add(amount);
		this.totalCount += orderItem.getCount();
		
		resizeItemList();
		
	}

	/**
	 * 填充规则和商品的属性，这些是不会跟随前端变化的
	 * @param orderItem
	 * @param productRule
	 */
	private void fillRuleInfo(OrderItem orderItem, ProductRule productRule) {
		
		orderItem.setRuleName(productRule.getName());
		orderItem.setPrice(productRule.getPrice());	//前端传上来的价格不可信，全部采用后端计算
		orderItem.setOriPrice(productRule.getOriPrice());
		orderItem.setPostageFee(productRule.getPostageFee());
		orderItem.setFreeShippingNum(productRule.getFreeShippingNum());
		orderItem.setOrderType(productRule.getSalePlanType());
		
		//展示用
		orderItem.setProductName(productRule.getName());
		orderItem.setProductId(productRule.getProductId());
		orderItem.setProductPic(productRule.getMainPicture());
		orderItem.setProductThumbPic(productRule.getSmallPicture());
		orderItem.setProductCategoryId(productRule.getProductCategoryId());
	}

	/**
	 * 删除商品
	 * @param delGoods
	 */
	public void del(OrderItem orderItem, ProductRule productRule) {
		
		if (this.items == null || this.items.isEmpty()) {
			return;
		}
		BigDecimal unitPrice = new BigDecimal(String.valueOf(productRule.getPrice()));	//前端传上来的价格不可信，全部采用后端计算
		BigDecimal count = new BigDecimal(orderItem.getCount());	//数量采用前端传的
		BigDecimal amount = unitPrice.multiply(count);
		
		if (!itemsMap.containsKey(orderItem.getRuleId())) {
			return;
		}
		OrderItem existItem = itemsMap.get(orderItem.getRuleId());
		
		if (existItem != null) {
			
			fillRuleInfo(existItem, productRule);
			
			int reduceCount = orderItem.getCount();
			if (existItem.getCount() - orderItem.getCount() < 1) {
				amount = new BigDecimal(String.valueOf(existItem.getAmount()));
				reduceCount = existItem.getCount();
			}
			
			if (reduceCount == existItem.getCount()) {	//如果本次清空当前类商品，即减为0了，则：
				itemsMap.remove(orderItem.getRuleId());
			}else {	//本次减去商品之后，仍有商品在购物车里，则：
				existItem.setCount(existItem.getCount() - reduceCount);
				BigDecimal newAmount = new BigDecimal(String.valueOf(existItem.getAmount())).subtract(amount);
				existItem.setAmount(newAmount.floatValue());
				
			}
			this.totalAmount = this.totalAmount.subtract(amount);
			this.totalCount -= reduceCount;
			
			resizeItemList();
		}
		
		
	}
	
	/**
	 * 清空购物车
	 */
	public void clear() {
		
		this.itemsMap = null;
		this.items = null;
		this.totalAmount = BigDecimal.ZERO;
		this.totalShipFee = BigDecimal.ZERO;
		this.totalPrice = BigDecimal.ZERO;
		this.totalCount = 0;
		
	}
	
	/**
	 * 重新计算运费
	 */
	@JsonIgnore
	private void resizeItemList() {

		totalShipFee = BigDecimal.ZERO;
		items = new ArrayList<>(itemsMap.entrySet().size());
		Iterator<Map.Entry<Long, OrderItem>> it = itemsMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long, OrderItem> entry = it.next();
			OrderItem orderItem = entry.getValue();
			items.add(orderItem);
			//重新计算运费，有可能改了配置，导致运费满减规则变更，所以这里重新算
			if (orderItem.getCount() < orderItem.getFreeShippingNum()) {
				BigDecimal shipFee = new BigDecimal(String.valueOf(orderItem.getPostageFee())).multiply(new BigDecimal(orderItem.getCount()));
				orderItem.setShipFee(shipFee.floatValue());
				totalShipFee = totalShipFee.add(shipFee);
			}else {
				orderItem.setShipFee(0f);
			}
		}
		this.totalPrice = this.totalAmount.add(totalShipFee);

	}
	
	
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<OrderItem> getItems() {
		return items;
	}
	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	public BigDecimal getTotalShipFee() {
		return totalShipFee;
	}
	public void setTotalShipFee(BigDecimal totalShipFee) {
		this.totalShipFee = totalShipFee;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	@JsonIgnore
	public List<Long> getProductIds(){
		List<Long> ids = new ArrayList<Long>();
		if(items == null) {
			return ids;
		}
		for(OrderItem item : items) {
			ids.add(item.getProductId());
		}
		return ids;
	}

	@JsonIgnore
	public List<Long> getMerchantIds(){
		List<Long> ids = new ArrayList<Long>();
		if(items == null) {
			return ids;
		}
		for(OrderItem item : items) {
			ids.add(item.getMerchantId());
		}
		return ids;
	}

	/**
	 * 由于存在满减优惠，实际金额通常小于该金额
	 * @return
	 */
	@JsonIgnore
	public Float getTotalAmount() {
		Float totalAmount = 0f;
		for(OrderItem item : items) {
			totalAmount+=item.getAmount();
		}
		return totalAmount;
	}

	//如果是0，则不存在订单项或者为混合购物车，混合购物车暂时不做处理
	@JsonIgnore
	public int getOrderType(){
		int orderType = -1;
		for(OrderItem item : items) {
			if(orderType == -1){
				orderType = item.getOrderType();
			}else if(orderType != item.getOrderType()){
				return -1;
			}
		}
		return orderType;
	}
	
	//只考虑一个组合的问题
	@JsonIgnore
	public long getCollocationId(){
		long result = 0l;
		for(OrderItem item : items) {
			if(result == 0){
				result = item.getCollocationId();
			}else if(result != item.getCollocationId()){
				return 0l;
			}
		}
		return result;
	}
}
