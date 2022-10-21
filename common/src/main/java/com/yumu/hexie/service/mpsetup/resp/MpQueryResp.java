package com.yumu.hexie.service.mpsetup.resp;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MpQueryResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -69168231710920929L;

	private String appid;
	@JsonProperty("app_name")
	private String appName;
	@JsonProperty("default_sign")
	private String defaultSign;
	private String abbr;
	@JsonProperty("app_menu")
	private List<String> appMenu;
	@JsonProperty("app_logo")
	private String appLogo;
	private String remark;
	
	private List<MsgTemplateVO> templates;
	
	public static class MsgTemplateVO{
		
		@JsonProperty("template_name")
		private String templateName;
		@JsonProperty("template_id")
		private String templateId;
		
		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		@Override
		public String toString() {
			return "MsgTemplate [templateName=" + templateName + ", templateId=" + templateId + "]";
		}
		
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
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

	public List<String> getAppMenu() {
		return appMenu;
	}

	public void setAppMenu(List<String> appMenu) {
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

	public List<MsgTemplateVO> getTemplates() {
		return templates;
	}

	public void setTemplates(List<MsgTemplateVO> templates) {
		this.templates = templates;
	}

	@Override
	public String toString() {
		return "MpQueryResp [appid=" + appid + ", appName=" + appName + ", defaultSign=" + defaultSign + ", abbr="
				+ abbr + ", appMenu=" + appMenu + ", appLogo=" + appLogo + ", remark=" + remark + ", templates="
				+ templates + "]";
	}

	
}
