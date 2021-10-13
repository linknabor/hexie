package com.yumu.hexie.integration.workorder.req;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.service.workorder.req.WorkOrderReq;

public class SaveWorkOrderRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4821662021373055017L;
	
	private String acceptType;	//接派单类型
	private String distType;	//维修区域类型，0公共，1入室
	private String address;	//维修地址,公共部位填写
	private String content;	//维修内容
	private List<String> images;
	
	private String creator;	//创建人名称
	private String creatorUserId;	//创建人用户id
	private String creatorContact;	//创建人联系方式
	private String creatorOpenid;	//创建人openid
	private String creatorAppid;	//创建人appid
	private String wuyeId;	//创建人wuyeId;
	private String sectId;
	private String cspId;
	
	public SaveWorkOrderRequest() {
		super();
	}
	public SaveWorkOrderRequest(WorkOrderReq workOrderReq){
		super();
		this.acceptType = workOrderReq.getAcceptType();
		this.distType = workOrderReq.getDistType();
		this.address = workOrderReq.getAddress();
		this.content = workOrderReq.getContent();
		this.images = workOrderReq.getImages();
	}
	public String getDistType() {
		return distType;
	}
	public void setDistType(String distType) {
		this.distType = distType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreatorUserId() {
		return creatorUserId;
	}
	public void setCreatorUserId(String creatorUserId) {
		this.creatorUserId = creatorUserId;
	}
	public String getCreatorContact() {
		return creatorContact;
	}
	public void setCreatorContact(String creatorContact) {
		this.creatorContact = creatorContact;
	}
	public String getCreatorOpenid() {
		return creatorOpenid;
	}
	public void setCreatorOpenid(String creatorOpenid) {
		this.creatorOpenid = creatorOpenid;
	}
	public String getCreatorAppid() {
		return creatorAppid;
	}
	public void setCreatorAppid(String creatorAppid) {
		this.creatorAppid = creatorAppid;
	}
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public List<String> getImages() {
		return images;
	}
	public void setImages(List<String> images) {
		this.images = images;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getCspId() {
		return cspId;
	}
	public void setCspId(String cspId) {
		this.cspId = cspId;
	}
	public String getAcceptType() {
		return acceptType;
	}
	public void setAcceptType(String acceptType) {
		this.acceptType = acceptType;
	}
	@Override
	public String toString() {
		return "SaveWorkOrderRequest [acceptType=" + acceptType + ", distType=" + distType + ", address=" + address
				+ ", content=" + content + ", images=" + images + ", creator=" + creator + ", creatorUserId="
				+ creatorUserId + ", creatorContact=" + creatorContact + ", creatorOpenid=" + creatorOpenid
				+ ", creatorAppid=" + creatorAppid + ", wuyeId=" + wuyeId + ", sectId=" + sectId + ", cspId=" + cspId
				+ "]";
	}
	
	
}
