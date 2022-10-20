package com.yumu.hexie.service.mpsetup.req;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MpSetupReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 173430945063740566L;
	
	@JsonProperty("csp_name")
	private String cspName;
	private String appid;
	@JsonProperty("app_name")
	private String appName;
	@JsonProperty("default_sign")
	private String defaultSign;
	private String abbr;
	@JsonProperty("app_menu")
	private String []appMenu;
	@JsonProperty("app_logo")
	private String appLogo;
	private String remark;
	@JsonProperty("template_name")
	private String []templateName;
	@JsonProperty("template_id")
	private String []templateId;
	private String edit;
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getCspName() {
		return cspName;
	}
	public void setCspName(String cspName) {
		this.cspName = cspName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getDefaultSign() {
		return defaultSign;
	}
	public void setDefaultSign(String defaultSign) {
		this.defaultSign = defaultSign;
	}
	public String getAbbr() {
		return abbr;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	public String[] getAppMenu() {
		return appMenu;
	}
	public void setAppMenu(String[] appMenu) {
		this.appMenu = appMenu;
	}
	public String getAppLogo() {
		return appLogo;
	}
	public void setAppLogo(String appLogo) {
		this.appLogo = appLogo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String[] getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String[] templateName) {
		this.templateName = templateName;
	}
	public String[] getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String[] templateId) {
		this.templateId = templateId;
	}
	public String getEdit() {
		return edit;
	}
	public void setEdit(String edit) {
		this.edit = edit;
	}
	@Override
	public String toString() {
		return "MpSetupReq [cspName=" + cspName + ", appid=" + appid + ", appName=" + appName + ", defaultSign="
				+ defaultSign + ", abbr=" + abbr + ", appMenu=" + Arrays.toString(appMenu) + ", appLogo=" + appLogo
				+ ", remark=" + remark + ", templateName=" + Arrays.toString(templateName) + ", templateId="
				+ Arrays.toString(templateId) + ", edit=" + edit + "]";
	}
	

}
