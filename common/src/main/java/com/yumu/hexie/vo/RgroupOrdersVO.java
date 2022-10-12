package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.model.market.RefundRecord;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.rgroup.RgroupUser;
import com.yumu.hexie.model.market.saleplan.RgroupRule;

public class RgroupOrdersVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2583221087671745092L;
	
	private RgroupRule rule;
	private ServiceOrder order;
	private RgroupUser rgroupUser;
	private RefundRecord latestRefund;
	private List<RefundRecord> refundRecords;
	
	public RgroupRule getRule() {
		return rule;
	}
	public void setRule(RgroupRule rule) {
		this.rule = rule;
	}
	public ServiceOrder getOrder() {
		return order;
	}
	public void setOrder(ServiceOrder order) {
		this.order = order;
	}
	public RgroupUser getRgroupUser() {
		return rgroupUser;
	}
	public void setRgroupUser(RgroupUser rgroupUser) {
		this.rgroupUser = rgroupUser;
	}
	public RefundRecord getLatestRefund() {
		return latestRefund;
	}
	public void setLatestRefund(RefundRecord latestRefund) {
		this.latestRefund = latestRefund;
	}
	public List<RefundRecord> getRefundRecords() {
		return refundRecords;
	}
	public void setRefundRecords(List<RefundRecord> refundRecords) {
		this.refundRecords = refundRecords;
	}
	
	
}
