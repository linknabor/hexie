package com.yumu.hexie.model.market;

import java.util.Date;

import javax.persistence.Entity;

import org.springframework.data.annotation.Transient;

import com.yumu.hexie.model.BaseModel;
@Entity
public class Evoucher extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 142709793093193198L;
	
	private String code;	//which can convert to qrcode.
	private long orderId;	//serviceOrder id 购买的订单号
	private int status;	//0不可用，1可用，2过期
	
	private long userId;	//下单用户id
	private String tel;		//下单用户手机号
	private String openid;	//下单用户openid
	
	private long productId;	//优惠产品ID
	private String productName;	//优惠项目名称
	
	private Date beginDate;	//生效日期
	private Date endDate;	//过期日期
	private Date cosumeDate;	//使用日期

	private long operatorId;	//操作人id
	private String operatorName;	//操作人
	private String operatorTel;	//操作人手机号
	
	@Transient
	private boolean available() {
		Date nowdate = new Date();
		return beginDate.before(nowdate) && endDate.after(nowdate);
	}
	
	@Transient
	private String getQrCode() {
		
		return "";
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Date getCosumeDate() {
		return cosumeDate;
	}
	public void setCosumeDate(Date cosumeDate) {
		this.cosumeDate = cosumeDate;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getOperatorTel() {
		return operatorTel;
	}
	public void setOperatorTel(String operatorTel) {
		this.operatorTel = operatorTel;
	}
	
	
}
