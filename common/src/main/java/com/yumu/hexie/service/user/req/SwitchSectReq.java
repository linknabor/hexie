package com.yumu.hexie.service.user.req;

import java.io.Serializable;

public class SwitchSectReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6762146124947597285L;
	
	private String province;
	private String city;
	private String county;
	private String sectId;
	private String sectName;
	private String cspId;
	private String officeTel;
	private String appid;
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	public String getCspId() {
		return cspId;
	}
	public void setCspId(String cspId) {
		this.cspId = cspId;
	}
	public String getOfficeTel() {
		return officeTel;
	}
	public void setOfficeTel(String officeTel) {
		this.officeTel = officeTel;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@Override
	public String toString() {
		return "SwitchSectReq [province=" + province + ", city=" + city + ", county=" + county + ", sectId=" + sectId
				+ ", sectName=" + sectName + ", cspId=" + cspId + ", officeTel=" + officeTel + ", appid=" + appid + "]";
	}
	


}
