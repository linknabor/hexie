package com.yumu.hexie.service.shequ.req;

import java.io.Serializable;

public class UserAccessRecordReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4559511666919692406L;
	
	private String spotId;
	private String scene;
	private String mobile;
	private String reason;
	private String cellId;
	private String uid;
	private String appid;
	private String name;	//访客昵称
	private String role;	//访客角色
	private String accessDevice;
	private String accessIp;
	private String adminCode;	//城市国标码
	
	public String getSpotId() {
		return spotId;
	}
	public void setSpotId(String spotId) {
		this.spotId = spotId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public String getAccessDevice() {
		return accessDevice;
	}
	public void setAccessDevice(String accessDevice) {
		this.accessDevice = accessDevice;
	}
	public String getAccessIp() {
		return accessIp;
	}
	public void setAccessIp(String accessIp) {
		this.accessIp = accessIp;
	}
	public String getScene() {
		return scene;
	}
	public void setScene(String scene) {
		this.scene = scene;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAdminCode() {
		return adminCode;
	}
	public void setAdminCode(String adminCode) {
		this.adminCode = adminCode;
	}
	
}
