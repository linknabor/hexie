package com.yumu.hexie.model.market;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.commonsupport.cache.ProductRuleCache;
import com.yumu.hexie.model.market.vo.RgroupCartVO;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 *团购购物车
 *2022-04-26
 * @author david
 *
 */
public class RgroupCart extends BaseModel {

	private static final long serialVersionUID = 8850980148126766715L;
	
	private Map<Long, Map<Long, OrderItem>> itemsMap;	//key:ruleId, value:Map<productId, orderItem>
	private Map<Long, BigDecimal> itemsAmount;	//key:ruleId, value: totalAmount 
	private Map<Long, Integer> itemsCount; //key:ruleId, value: totalCount
	
	/**
	 * 添加商品
	 * @param goods
	 */
	public RgroupCartVO add(OrderItem orderItem, ProductRuleCache productRule, int stock) {
		
		Integer currentCount = 0;
		BigDecimal currentAmount = BigDecimal.ZERO;
		BigDecimal unitPrice = new BigDecimal(String.valueOf(productRule.getSinglePrice()));	//前端传上来的价格不可信，全部采用后端计算
		BigDecimal count = new BigDecimal(orderItem.getCount());	//数量采用前端传的
		BigDecimal amount = unitPrice.multiply(count);
		
		fillRuleInfo(orderItem, productRule);
		
		if (itemsMap == null) {
			itemsMap = new HashMap<>();
		}
		if (!itemsMap.containsKey(orderItem.getRuleId())) {	//用map存放，避免每次迭代itemsList
			int perLimit = productRule.getUserLimitCount();
			if (orderItem.getCount() > perLimit) {
				throw new BizValidateException("每人限购" + perLimit + "件。");
			}
			Map<Long, OrderItem> productMap = new TreeMap<>();
			orderItem.setAmount(amount.floatValue());
			productMap.put(orderItem.getProductId(), orderItem);
			itemsMap.put(orderItem.getRuleId(), productMap);
			
		}else {
			Map<Long, OrderItem> exitItemMap = itemsMap.get(orderItem.getRuleId());
			if (exitItemMap == null) {
				exitItemMap = new TreeMap<>();
			}
			OrderItem existItem = exitItemMap.get(orderItem.getProductId());
			if (existItem == null) {
				int perLimit = productRule.getUserLimitCount();
				if (orderItem.getCount() > perLimit) {
					throw new BizValidateException("每人限购" + perLimit + "件。");
				}
				orderItem.setAmount(amount.floatValue());
				exitItemMap.put(orderItem.getProductId(), orderItem);
				itemsMap.put(orderItem.getRuleId(), exitItemMap);
				
			} else {
				
				int perLimit = productRule.getLimitNumOnce();
				if (existItem.getCount() + orderItem.getCount() > perLimit) {
					throw new BizValidateException("每人限购" + perLimit + "件。");
				}
				existItem.setCount(existItem.getCount() + orderItem.getCount());
				BigDecimal existAmount = new BigDecimal(String.valueOf(existItem.getAmount()));
				existItem.setAmount(existAmount.add(amount).floatValue());
				fillRuleInfo(existItem, productRule);
			}
			
		}
		if (itemsAmount == null) {
			itemsAmount = new HashMap<>();
		}
		if (!itemsAmount.containsKey(orderItem.getRuleId())) {
			itemsAmount.put(orderItem.getRuleId(), amount);
			currentAmount = amount;
		} else {
			BigDecimal existAmount = itemsAmount.get(orderItem.getRuleId());
			existAmount = existAmount.add(amount);
			itemsAmount.put(orderItem.getRuleId(), existAmount);
			currentAmount = existAmount;
		}
		if (itemsCount == null) {
			itemsCount = new HashMap<>();
		}
		if (!itemsCount.containsKey(orderItem.getRuleId())) {
			itemsCount.put(orderItem.getRuleId(), orderItem.getCount());
			currentCount = orderItem.getCount();
		} else {
			Integer existCount = itemsCount.get(orderItem.getRuleId());
			existCount += orderItem.getCount();
			itemsCount.put(orderItem.getRuleId(), existCount);
			currentCount = existCount;
		}
		resizeItemList();
		RgroupCartVO vo = new RgroupCartVO();
		vo.setTotalCount(currentCount);
		vo.setTotalAmount(currentAmount);
		return vo;
		
	}
	
	/**
	 * 填充规则和商品的属性，这些是不会跟随前端变化的
	 * @param orderItem
	 * @param productRule
	 */
	private void fillRuleInfo(OrderItem orderItem, ProductRuleCache productRule) {
		
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
	public RgroupCartVO del(OrderItem orderItem, ProductRuleCache productRule) {
		
		RgroupCartVO vo = new RgroupCartVO();
		if (this.itemsMap == null || this.itemsMap.isEmpty()) {
			return vo;
		}
		if (!this.itemsMap.containsKey(orderItem.getRuleId())) {
			return vo;
		}
		if (this.itemsAmount == null || this.itemsAmount.isEmpty() || this.itemsCount == null || this.itemsCount.isEmpty()) {
			return vo;
		}
		
		Map<Long, OrderItem> existItemMap = itemsMap.get(orderItem.getRuleId());
		OrderItem existItem = existItemMap.get(orderItem.getProductId());
		if (existItem == null) {
			return vo;
		}
		
		BigDecimal unitPrice = new BigDecimal(String.valueOf(productRule.getSinglePrice()));	//前端传上来的价格不可信，全部采用后端计算
		BigDecimal count = new BigDecimal(orderItem.getCount());	//数量采用前端传的
		BigDecimal amount = unitPrice.multiply(count);
		
		fillRuleInfo(existItem, productRule);
		
		int reduceCount = orderItem.getCount();
		if (existItem.getCount() - orderItem.getCount() < 1) {
			amount = new BigDecimal(String.valueOf(existItem.getAmount()));
			reduceCount = existItem.getCount();
		}
		
		if (reduceCount == existItem.getCount()) {	//如果本次清空当前类商品，即减为0了，则：
			existItemMap.remove(orderItem.getProductId());
		}else {	//本次减去商品之后，仍有商品在购物车里，则：
			existItem.setCount(existItem.getCount() - reduceCount);
			BigDecimal newAmount = new BigDecimal(String.valueOf(existItem.getAmount())).subtract(amount);
			existItem.setAmount(newAmount.floatValue());
		}
		if (existItemMap.isEmpty()) {
			itemsMap.remove(orderItem.getRuleId());
		}
		BigDecimal currAmount = itemsAmount.get(orderItem.getRuleId());
		currAmount = currAmount.subtract(amount);
		if (currAmount.compareTo(BigDecimal.ZERO) < 0) {
			currAmount = BigDecimal.ZERO;
		}
		itemsAmount.put(orderItem.getRuleId(), currAmount);
		
		Integer currCount = itemsCount.get(orderItem.getRuleId());
		currCount = currCount - orderItem.getCount();
		if (currCount < 0) {
			currCount = 0;
		}
		itemsCount.put(orderItem.getRuleId(), currCount);
		resizeItemList();

		vo.setTotalAmount(currAmount);
		vo.setTotalCount(currCount);
		return vo;
		
	}
	
	/**
	 * 清空购物车
	 */
	public void clear() {
		
		this.itemsMap = null;
		this.itemsAmount = null;
		this.itemsCount = null;
	}
	
	/**
	 * 重新计算运费
	 */
	@JsonIgnore
	private void resizeItemList() {

//		totalShipFee = BigDecimal.ZERO;
//		items = new ArrayList<>(itemsMap.entrySet().size());
//		Iterator<Map.Entry<Long, OrderItem>> it = itemsMap.entrySet().iterator();
//		while(it.hasNext()) {
//			Entry<Long, OrderItem> entry = it.next();
//			OrderItem orderItem = entry.getValue();
//			items.add(orderItem);
//			//重新计算运费，有可能改了配置，导致运费满减规则变更，所以这里重新算
//			if (orderItem.getCount() < orderItem.getFreeShippingNum()) {
//				BigDecimal shipFee = new BigDecimal(String.valueOf(orderItem.getPostageFee())).multiply(new BigDecimal(orderItem.getCount()));
//				orderItem.setShipFee(shipFee.floatValue());
//				totalShipFee = totalShipFee.add(shipFee);
//			}else {
//				orderItem.setShipFee(0f);
//			}
//		}
//		this.totalPrice = this.totalAmount.add(totalShipFee);

	}
	
	public Map<Long, Map<Long, OrderItem>> getItemsMap() {
		return itemsMap;
	}
	public void setItemsMap(Map<Long, Map<Long, OrderItem>> itemsMap) {
		this.itemsMap = itemsMap;
	}

	public Map<Long, BigDecimal> getItemsAmount() {
		return itemsAmount;
	}

	public void setItemsAmount(Map<Long, BigDecimal> itemsAmount) {
		this.itemsAmount = itemsAmount;
	}

	public Map<Long, Integer> getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Map<Long, Integer> itemsCount) {
		this.itemsCount = itemsCount;
	}
	
	
}
