package com.yumu.hexie.vo;

import java.io.Serializable;

public class QQMapVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3158120850246924878L;
	
	private String address;
	private String city;
	private String district;
	private String latitude;
	private String longitude;
	private String name;
	private String province;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	@Override
	public String toString() {
		return "QQMapVO [address=" + address + ", city=" + city + ", district=" + district + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", name=" + name + ", province=" + province + "]";
	}
	

}
