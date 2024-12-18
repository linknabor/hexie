package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.io.Serializable;

public class AliTemplateMsg<T> implements Serializable {

	private static final long serialVersionUID = -3389303374046044405L;
	
	private String aliUserId;
	private String appid;
	private String templateId;//":"ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY",
	private String appCode;
	private String url;//":"http://weixin.qq.com/download",
	private String cityCode;	//城市编码
	private String indudstryType;
	private T data;
	
	public String getAliUserId() {
		return aliUserId;
	}
	public void setAliUserId(String aliUserId) {
		this.aliUserId = aliUserId;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getIndudstryType() {
		return indudstryType;
	}
	public void setIndudstryType(String indudstryType) {
		this.indudstryType = indudstryType;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "AliTemplateMsg [aliUserId=" + aliUserId + ", appid=" + appid + ", templateId=" + templateId
				+ ", appCode=" + appCode + ", url=" + url + ", cityCode=" + cityCode + ", indudstryType="
				+ indudstryType + ", data=" + data + "]";
	}
	
	
}
