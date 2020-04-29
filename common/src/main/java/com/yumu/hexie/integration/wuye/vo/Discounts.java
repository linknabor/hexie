package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Discounts implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6923935581841948790L;
	
	private List<DiscountDetail> list;
	
	public static class DiscountDetail {
		
		@JsonProperty("rule_type")
		private String ruleType;
		@JsonProperty("reduction_msg")
		private String reductionMsg;
		@JsonProperty("reduction_amt")
		private String reductionAmt;
		
		public String getRuleType() {
			return ruleType;
		}
		public void setRuleType(String ruleType) {
			this.ruleType = ruleType;
		}
		public String getReductionMsg() {
			return reductionMsg;
		}
		public void setReductionMsg(String reductionMsg) {
			this.reductionMsg = reductionMsg;
		}
		public String getReductionAmt() {
			return reductionAmt;
		}
		public void setReductionAmt(String reductionAmt) {
			this.reductionAmt = reductionAmt;
		}
		
	}

	public List<DiscountDetail> getList() {
		return list;
	}

	public void setList(List<DiscountDetail> list) {
		this.list = list;
	}
	
	

}
