package com.yumu.hexie.model.commonsupport.info;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class ProductRule extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5644044378216460486L;
	
	private long productId;
	private long ruleId;
	
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
}
