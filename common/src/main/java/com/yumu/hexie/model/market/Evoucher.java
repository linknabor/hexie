package com.yumu.hexie.model.market;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.Transient;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.BaseModel;

@Entity
@Table(name = "evoucher", uniqueConstraints = {@UniqueConstraint(columnNames="code")})	
public class Evoucher extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 142709793093193198L;
	
	private String code;	//which can convert to qrcode.
	private long orderId;	//serviceOrder id 购买的订单号
	private int status;	//0不可用，1可用，2过期
	private float actualPrice;	//实际售价
	private float oriPrice;	//原价
	
	private long userId;	//下单用户id
	private String tel;		//下单用户手机号
	private String openid;	//下单用户openid
	
	private long productId;	//优惠产品ID
	private String productName;	//优惠项目名称
	private String smallPicture;	//商品小图
	
	private Date beginDate;	//生效日期
	private Date endDate;	//过期日期
	private Date consumeDate;	//使用日期

	private long operatorUserId;	//操作人id
	private String operatorName;	//操作人
	private String operatorTel;	//操作人手机号
	
	private long agentId;		//代理商ID
	private String agentName;	//代理商名称
	private String agentNo;		//代理商编号
		
	private long merchantId;	//商户ID
	private String merchantName;	//商户名称
	
	@Transient
	public boolean available() {
		Date nowdate = new Date();
		if (!StringUtil.isEmpty(beginDate) && !StringUtil.isEmpty(endDate)) {
			return beginDate.before(nowdate) && endDate.after(nowdate);
		}
		return false;
		
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
	public Date getConsumeDate() {
		return consumeDate;
	}
	public void setConsumeDate(Date consumeDate) {
		this.consumeDate = consumeDate;
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

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	
	public String getAgentNo() {
		return agentNo;
	}
	
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	public float getActualPrice() {
		return actualPrice;
	}
	public void setActualPrice(float actualPrice) {
		this.actualPrice = actualPrice;
	}
	public float getOriPrice() {
		return oriPrice;
	}
	public void setOriPrice(float oriPrice) {
		this.oriPrice = oriPrice;
	}
	public String getSmallPicture() {
		return smallPicture;
	}
	public void setSmallPicture(String smallPicture) {
		this.smallPicture = smallPicture;
	}
	public long getOperatorUserId() {
		return operatorUserId;
	}
	public void setOperatorUserId(long operatorUserId) {
		this.operatorUserId = operatorUserId;
	}
	
	
}
