package com.yumu.hexie.model.market;

import java.util.Date;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

/**
 * 退款记录表
 * @author david
 *
 */
@Entity
public class RefundRecord extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -604606892630217214L;

	private long orderId;	//订单id
	private int totalCount;	//订单总商品数
	private Float totalAmt;	//订单总金额
	private Float refundAmt;	//本次退款金额
	private int refundCount;	//本次退款商品件数
	private int	applyType;	//页面上选择的申请类型,0我要退款（无需退货）,1我要退货退款
	private String applyReason;	//申请原因
	private Date applyDate;	//申请退款日期,如果是C端用户发起，记录申请日期。如果是团长发起，不做记录
	private Date auditDate;	//团长审核日期
	private String memo;	//页面填写的补充描述
	private String itemIds;	//退货商品id，以逗号分割
	private long userId;	//用户id
	private long ownerId;	//团长id，对应servieOrder表中的groupLeader
	private int refundType;	//退款类型，参考ModelConstant.REFUND_REASON_GROUP_USER_REFUND,3用户发起，4团长发起
	private int refundStatus;	//本条记录的退款状态,同serverOrder的 status字段
	private Date finishDate;	//退款完成日期
	
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public Float getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(Float totalAmt) {
		this.totalAmt = totalAmt;
	}
	public Float getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(Float refundAmt) {
		this.refundAmt = refundAmt;
	}
	public int getRefundCount() {
		return refundCount;
	}
	public void setRefundCount(int refundCount) {
		this.refundCount = refundCount;
	}
	public int getApplyType() {
		return applyType;
	}
	public void setApplyType(int applyType) {
		this.applyType = applyType;
	}
	public String getApplyReason() {
		return applyReason;
	}
	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getItemIds() {
		return itemIds;
	}
	public void setItemIds(String itemIds) {
		this.itemIds = itemIds;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	public int getRefundType() {
		return refundType;
	}
	public void setRefundType(int refundType) {
		this.refundType = refundType;
	}
	public int getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(int refundStatus) {
		this.refundStatus = refundStatus;
	}
	public Date getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	
	
	
}
