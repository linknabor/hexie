package com.yumu.hexie.integration.repair.mapper;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryROrderMapper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 359659450472057901L;
	
	//商品信息
	private BigInteger id;	//订单id
	@JsonProperty("create_date")
	private BigInteger createDate;	//下单日期
	private Integer status;	//订单状态
	@JsonProperty("csp_id")
	private String cspId;	//发帖用户所在物业ID
	@JsonProperty("sect_id")
	private String sectId;	//小区ID
	@JsonProperty("sect_name")	
	private String xiaoquName;	//小区名称
	
	private String address;	//报修地址
	@JsonProperty("receiver_name")
	private String receiverName;	//下单用户名称
	private String tel;	//用户手机号
	@JsonProperty("operator_name")
	private String operatorName;	//维修工名称
	@JsonProperty("operator_tel")
	private String operatorTel;	//维修工手机号
	@JsonProperty("pay_type")
	private Integer payType;	//支付方式
	
	private Float price;
	@JsonProperty("finish_time")
	private Timestamp finishTime;
	@JsonProperty("operator_finish_time")
	private Timestamp operatorFinishTime;
	
	@JsonProperty("comment_quality")
	private Integer commentQuality;
	@JsonProperty("comment_attitude")
	private Integer commentAttitude;
	@JsonProperty("comment_service")
	private Integer commentService;
	private String comment;
	private String memo;
	
	public QueryROrderMapper(BigInteger id, BigInteger createDate, Integer status, String cspId, String sectId, String xiaoquName,
			String address, String receiverName, String tel, String operatorName, String operatorTel, Integer payType,
			Float price, Timestamp finishTime, Timestamp operatorFinishTime, Integer commentQuality, Integer commentAttitude,
			Integer commentService, String comment, String memo) {
		super();
		this.id = id;
		this.createDate = createDate;
		this.status = status;
		this.cspId = cspId;
		this.sectId = sectId;
		this.xiaoquName = xiaoquName;
		this.address = address;
		this.receiverName = receiverName;
		this.tel = tel;
		this.operatorName = operatorName;
		this.operatorTel = operatorTel;
		this.payType = payType;
		this.price = price;
		this.finishTime = finishTime;
		this.operatorFinishTime = operatorFinishTime;
		this.commentQuality = commentQuality;
		this.commentAttitude = commentAttitude;
		this.commentService = commentService;
		this.comment = comment;
		this.memo = memo;
	}
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public BigInteger getCreateDate() {
		return createDate;
	}
	public void setCreateDate(BigInteger createDate) {
		this.createDate = createDate;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getCspId() {
		return cspId;
	}
	public void setCspId(String cspId) {
		this.cspId = cspId;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getXiaoquName() {
		return xiaoquName;
	}
	public void setXiaoquName(String xiaoquName) {
		this.xiaoquName = xiaoquName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getOperatorTel() {
		return operatorTel;
	}
	public void setOperatorTel(String operatorTel) {
		this.operatorTel = operatorTel;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}
	public Date getOperatorFinishTime() {
		return operatorFinishTime;
	}
	public void setOperatorFinishTime(Timestamp operatorFinishTime) {
		this.operatorFinishTime = operatorFinishTime;
	}

	public Integer getCommentQuality() {
		return commentQuality;
	}

	public void setCommentQuality(Integer commentQuality) {
		this.commentQuality = commentQuality;
	}

	public Integer getCommentAttitude() {
		return commentAttitude;
	}

	public void setCommentAttitude(Integer commentAttitude) {
		this.commentAttitude = commentAttitude;
	}

	public Integer getCommentService() {
		return commentService;
	}

	public void setCommentService(Integer commentService) {
		this.commentService = commentService;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
	public String toString() {
		return "QueryROrderMapper [id=" + id + ", createDate=" + createDate + ", status=" + status + ", cspId=" + cspId
				+ ", sectId=" + sectId + ", xiaoquName=" + xiaoquName + ", address=" + address + ", receiverName="
				+ receiverName + ", tel=" + tel + ", operatorName=" + operatorName + ", operatorTel=" + operatorTel
				+ ", payType=" + payType + ", price=" + price + ", finishTime=" + finishTime + ", operatorFinishTime="
				+ operatorFinishTime + ", commentQuality=" + commentQuality + ", commentAttitude=" + commentAttitude
				+ ", commentService=" + commentService + ", comment=" + comment + ", memo=" + memo + "]";
	}
	
	
}
