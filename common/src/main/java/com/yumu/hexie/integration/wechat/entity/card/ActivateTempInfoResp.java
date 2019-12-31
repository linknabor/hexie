package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;
import java.util.List;

public class ActivateTempInfoResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7378620849104409571L;
	
	private String errcode;
	private String errmsg;
	private Info info;
	
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public Info getInfo() {
		return info;
	}
	public void setInfo(Info info) {
		this.info = info;
	}

	public static class Info {
		
		private List<ActivateField> common_field_list;
		private List<ActivateField> custom_field_list;
		
		public List<ActivateField> getCommon_field_list() {
			return common_field_list;
		}
		public void setCommon_field_list(List<ActivateField> common_field_list) {
			this.common_field_list = common_field_list;
		}
		public List<ActivateField> getCustom_field_list() {
			return custom_field_list;
		}
		public void setCustom_field_list(List<ActivateField> custom_field_list) {
			this.custom_field_list = custom_field_list;
		}

		@Override
		public String toString() {
			return "Info [common_field_list=" + common_field_list + ", custom_field_list=" + custom_field_list + "]";
		}
		public static class ActivateField{
			
			private String name;
			private String value;
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			@Override
			public String toString() {
				return "ActivateField [name=" + name + ", value=" + value + "]";
			}
			
		}
	
	}

	@Override
	public String toString() {
		return "ActivateTempInfoResp [errcode=" + errcode + ", errmsg=" + errmsg + ", info=" + info + "]";
	}
	
}
