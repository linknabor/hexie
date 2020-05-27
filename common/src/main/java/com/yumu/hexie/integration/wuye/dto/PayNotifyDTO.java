package com.yumu.hexie.integration.wuye.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.yumu.hexie.model.user.User;

public class PayNotifyDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2053345074969873725L;
	
	private User user;
	
	private String orderId;	//订单ID
	private String payMethod;	//支付方式
	private String tranDateTime;	//yyyy-MM-dd hh:mm:ss	交易时间
	private String wuyeId;	//付款用户的物业id
	private String couponId;	//优惠券id
	private String tranAmt;	//交易金额
	private String points;	//积分
	private String bindSwitch;	//是否自动绑定房屋标识
	private String cardNo;	//卡号，绑卡支付用
	private String quickToken;	//绑卡支付使用，二次支付时的token
	private String feeName;	//费项名称
	private String remark;	//备注
	private List<Map<String, String>> notifyOpenids;	//本次支付需要通知的用户id列表
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String getTranDateTime() {
		return tranDateTime;
	}
	public void setTranDateTime(String tranDateTime) {
		this.tranDateTime = tranDateTime;
	}
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public String getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	public String getPoints() {
		return points;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getBindSwitch() {
		return bindSwitch;
	}
	public void setBindSwitch(String bindSwitch) {
		this.bindSwitch = bindSwitch;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getQuickToken() {
		return quickToken;
	}
	public void setQuickToken(String quickToken) {
		this.quickToken = quickToken;
	}
	public List<Map<String, String>> getNotifyOpenids() {
		return notifyOpenids;
	}
	public void setNotifyOpenids(List<Map<String, String>> notifyOpenids) {
		this.notifyOpenids = notifyOpenids;
	}
	public String getFeeName() {
		return feeName;
	}
	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "PayNotifyDTO [user=" + user + ", orderId=" + orderId + ", payMethod=" + payMethod + ", tranDateTime="
				+ tranDateTime + ", wuyeId=" + wuyeId + ", couponId=" + couponId + ", tranAmt=" + tranAmt + ", points="
				+ points + ", bindSwitch=" + bindSwitch + ", cardNo=" + cardNo + ", quickToken=" + quickToken
				+ ", feeName=" + feeName + ", remark=" + remark + ", notifyOpenids=" + notifyOpenids + "]";
	}
	
}
