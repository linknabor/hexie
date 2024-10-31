package com.yumu.hexie.service.shequ.req;

import java.io.Serializable;

public class RadiusSectReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8572545907261546846L;

	private String appid;
	private String coordinate;	//经纬度，逗号分割
	private String bdCoordinate;	//百度经纬度
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	public String getBdCoordinate() {
		return bdCoordinate;
	}
	public void setBdCoordinate(String bdCoordinate) {
		this.bdCoordinate = bdCoordinate;
	}
	@Override
	public String toString() {
		return "RadiusSectReq [appid=" + appid + ", coordinate=" + coordinate + ", bdCoordinate=" + bdCoordinate + "]";
	}
	
}
