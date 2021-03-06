package com.yumu.hexie.model.distribution;

import javax.persistence.Entity;

import org.springframework.data.annotation.Transient;

//特卖上架管理
@Entity
public class OnSaleAreaItem extends RuleDistribution {
	
	private static final long serialVersionUID = 4808669460780339640L;
	
	private boolean featured = false;
	private int productType;
	private String ruleDesDescribe;//热卖商品或新品上市
	@Transient
	private int count = 0;	//页面显示用
	@Transient
	private int totalCount = 0;
	
	public boolean isFeatured() {
		return featured;
	}
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
	public int getProductType() {
		return productType;
	}
	public void setProductType(int productType) {
		this.productType = productType;
	}
	public String getRuleDesDescribe() {
		return ruleDesDescribe;
	}
	public void setRuleDesDescribe(String ruleDesDescribe) {
		this.ruleDesDescribe = ruleDesDescribe;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
		

}
