package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Discounts implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6923935581841948790L;
	
	@JsonProperty("total_fee_price")
	private String totalFeePrice;
	@JsonProperty("reduction_type")
	private String reductionType;
	private List<DiscountDetail> reduction;
	
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

	public String getTotalFeePrice() {
		return totalFeePrice;
	}

	public void setTotalFeePrice(String totalFeePrice) {
		this.totalFeePrice = totalFeePrice;
	}

	public String getReductionType() {
		return reductionType;
	}

	public void setReductionType(String reductionType) {
		this.reductionType = reductionType;
	}

	public List<DiscountDetail> getReduction() {
		return reduction;
	}

	public void setReduction(List<DiscountDetail> reduction) {
		this.reduction = reduction;
	}


}
