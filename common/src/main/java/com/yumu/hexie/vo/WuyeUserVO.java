package com.yumu.hexie.vo;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.desensitize.annotation.Sensitive;
import com.yumu.hexie.common.util.desensitize.enums.SensitiveType;

public class WuyeUserVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8710654065349491288L;

	private long id;
	private long createDate;
	@JsonProperty("create_date")
	private String createDateStr;
	private String name;
	@JsonProperty("real_name")
	private String realName;
	@JsonProperty("show_name")
	@Sensitive(SensitiveType.CHINESE_NAME)
	private String showName;
	private String tel;
	@JsonProperty("show_tel")
	@Sensitive(SensitiveType.MOBILE)
	private String showTel;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("csp_id")
	private String cspId;
	private String openid;
	private String appid;
	private String wuyeId;
	@JsonProperty("sect_name")
	private String xiaoquName;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
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
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getShowTel() {
		return showTel;
	}
	public void setShowTel(String showTel) {
		this.showTel = showTel;
	}
	public String getXiaoquName() {
		return xiaoquName;
	}
	public void setXiaoquName(String xiaoquName) {
		this.xiaoquName = xiaoquName;
	}
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getCreateDateStr() {
		if (createDate > 0) {
			createDateStr = DateUtil.dtFormat(createDate, DateUtil.dttmSimple);
		}
		return createDateStr;
	}
	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}
	
}
