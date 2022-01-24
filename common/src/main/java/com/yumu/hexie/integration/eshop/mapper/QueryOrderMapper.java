package com.yumu.hexie.integration.eshop.mapper;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.common.util.desensitize.annotation.Sensitive;
import com.yumu.hexie.common.util.desensitize.enums.SensitiveType;

public class QueryOrderMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9087688605429705540L;
	
	//订单信息
	private BigInteger id;	//订单ID
	
	private String address;	//地址
	
	private Integer count;	//购买数量
	
	@JsonProperty("logistic_name")
	private String logisticName;	//物流公司名称
	
	@JsonProperty("logistic_no")
	private String logisticNo;	//物流单号
	
	@JsonProperty("logistic_type")
	private Integer logisticType;	//配送类型,0商户派送 1用户自提 2第三方配送
	
	@JsonProperty("order_no")
	private String orderNo;	//支付订单号
	
	@JsonProperty("order_type")
	private Integer orderType;	//订单类型
	
	@JsonProperty("product_name")
	private String productName;
	
	@JsonProperty("refund_date")
	private Timestamp refundDate;	//退款时间
	
	@JsonProperty("send_date")
	private Timestamp sendDate;	//发货时间
	
	private Integer status;	//订单状态，0. 创建完成 1. 已支付 2. 已用户取消 3. 待退款 4. 退款中  5. 已发货 6.已签收 7. 已后台取消 8. 商户取消 9. 已确认 10.已退货（退货中走线下流程） 11.已退款 12.配货中（商户确认中）

	private Integer groupStatus; //成团状态

	@Sensitive(SensitiveType.MOBILE)
	private String tel;
	
	@JsonProperty("receiver_name")
	private String receiverName;
	
	private Float price;

	private Float totalAmount;

	@JsonProperty("agent_no")
	private String agentNo;
	
	@JsonProperty("agent_name")
	private String agentName;

	@JsonProperty("sect_name")
	private String sectName;

	@JsonProperty("create_date")
	private BigInteger createDate;

	public QueryOrderMapper(BigInteger id, String address, Integer count, String logisticName, String logisticNo, Integer logisticType, String orderNo, Integer orderType, String productName, Timestamp refundDate, Timestamp sendDate, Integer status, Integer groupStatus, String tel, String receiverName, Float price, Float totalAmount, String agentNo, String agentName, String sectName, BigInteger createDate) {
		this.id = id;
		this.address = address;
		this.count = count;
		this.logisticName = logisticName;
		this.logisticNo = logisticNo;
		this.logisticType = logisticType;
		this.orderNo = orderNo;
		this.orderType = orderType;
		this.productName = productName;
		this.refundDate = refundDate;
		this.sendDate = sendDate;
		this.status = status;
		this.groupStatus = groupStatus;
		this.tel = tel;
		this.receiverName = receiverName;
		this.price = price;
		this.totalAmount = totalAmount;
		this.agentNo = agentNo;
		this.agentName = agentName;
		this.sectName = sectName;
		this.createDate = createDate;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getLogisticName() {
		return logisticName;
	}

	public void setLogisticName(String logisticName) {
		this.logisticName = logisticName;
	}

	public String getLogisticNo() {
		return logisticNo;
	}

	public void setLogisticNo(String logisticNo) {
		this.logisticNo = logisticNo;
	}

	public Integer getLogisticType() {
		return logisticType;
	}

	public void setLogisticType(Integer logisticType) {
		this.logisticType = logisticType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Timestamp getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Timestamp refundDate) {
		this.refundDate = refundDate;
	}

	public Timestamp getSendDate() {
		return sendDate;
	}

	public void setSendDate(Timestamp sendDate) {
		this.sendDate = sendDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getSectName() {
		return sectName;
	}

	public void setSectName(String sectName) {
		this.sectName = sectName;
	}

	public BigInteger getCreateDate() {
		return createDate;
	}

	public void setCreateDate(BigInteger createDate) {
		this.createDate = createDate;
	}

	public Integer getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(Integer groupStatus) {
		this.groupStatus = groupStatus;
	}

	@Override
	public String toString() {
		return "QueryOrderMapper{" +
				"id=" + id +
				", address='" + address + '\'' +
				", count=" + count +
				", logisticName='" + logisticName + '\'' +
				", logisticNo='" + logisticNo + '\'' +
				", logisticType=" + logisticType +
				", orderNo='" + orderNo + '\'' +
				", orderType=" + orderType +
				", productName='" + productName + '\'' +
				", refundDate=" + refundDate +
				", sendDate=" + sendDate +
				", status=" + status +
				", groupStatus=" + groupStatus +
				", tel='" + tel + '\'' +
				", receiverName='" + receiverName + '\'' +
				", price=" + price +
				", totalAmount=" + totalAmount +
				", agentNo='" + agentNo + '\'' +
				", agentName='" + agentName + '\'' +
				", sectName='" + sectName + '\'' +
				", createDate=" + createDate +
				'}';
	}
}
