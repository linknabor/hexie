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
	@JsonProperty("is_enable_integral")
	private String isEnableIntegral;
	@JsonProperty("integral")
	private String integral;

	/*2020-07 添加 begin*/
	@JsonProperty("reality_amt")
	private String realityAmt;	//is_qrcode =1时有，实际支付金额
	private String image;	//is_qrcode =1时有，图片的BASE64串
	@JsonProperty("sect_name")
	private String sectName;
	@JsonProperty("cycle_arrs")
	private List<String> cycleList;
	/*2020-07 添加 end*/
	
	
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

	public String getIsEnableIntegral() {
		return isEnableIntegral;
	}

	public void setIsEnableIntegral(String isEnableIntegral) {
		this.isEnableIntegral = isEnableIntegral;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public String getRealityAmt() {
		return realityAmt;
	}

	public void setRealityAmt(String realityAmt) {
		this.realityAmt = realityAmt;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	

}
