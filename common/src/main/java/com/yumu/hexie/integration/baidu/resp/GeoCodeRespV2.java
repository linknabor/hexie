package com.yumu.hexie.integration.baidu.resp;

import java.io.Serializable;
import java.util.List;

public class GeoCodeRespV2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3092222593952798263L;

	private String status;	//0正常
	private GeoCodeResult result;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public GeoCodeResult getResult() {
		return result;
	}

	public void setResult(GeoCodeResult result) {
		this.result = result;
	}

	public static class GeoCodeResult {
		
		private GeoCodeLocation location;	//经纬度坐标
		private String formatted_address;	//结构化地址（不包含POI信息）。如果需要标准的结构化地址，比如获取XX路XX号，推荐使用这个参数
		private String formatted_address_poi;	//结构化地址（包含POI信息）。需设置extensions_poi=1才能返回。如果需要详细的地址描述，推荐使用这个参数。
		private GeoCodeEdz edz;	//所属开发区
		private String business;	//坐标所在商圈信息，如 "人民大学,中关村,苏州街"。最多返回3个。
		private AddressComponent addressComponent;	//行政区划
		private String sematic_description;	//当前位置结合POI的语义化结果描述。需设置extensions_poi=1才能返回。
		private List<GeoCodePois> pois;
		
		public GeoCodeLocation getLocation() {
			return location;
		}
		public void setLocation(GeoCodeLocation location) {
			this.location = location;
		}
		public String getFormatted_address() {
			return formatted_address;
		}
		public void setFormatted_address(String formatted_address) {
			this.formatted_address = formatted_address;
		}
		public String getFormatted_address_poi() {
			return formatted_address_poi;
		}
		public void setFormatted_address_poi(String formatted_address_poi) {
			this.formatted_address_poi = formatted_address_poi;
		}
		public GeoCodeEdz getEdz() {
			return edz;
		}
		public void setEdz(GeoCodeEdz edz) {
			this.edz = edz;
		}
		public String getBusiness() {
			return business;
		}
		public void setBusiness(String business) {
			this.business = business;
		}
		public String getSematic_description() {
			return sematic_description;
		}
		public void setSematic_description(String sematic_description) {
			this.sematic_description = sematic_description;
		}
		public AddressComponent getAddressComponent() {
			return addressComponent;
		}
		public void setAddressComponent(AddressComponent addressComponent) {
			this.addressComponent = addressComponent;
		}
		public List<GeoCodePois> getPois() {
			return pois;
		}
		public void setPois(List<GeoCodePois> pois) {
			this.pois = pois;
		}
	}
	
	public static class GeoCodeLocation {
		private Float lng;
		private Float lat;
		
		public Float getLng() {
			return lng;
		}
		public void setLng(Float lng) {
			this.lng = lng;
		}
		public Float getLat() {
			return lat;
		}
		public void setLat(Float lat) {
			this.lat = lat;
		}
	}
	
	public static class GeoCodeEdz {
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class AddressComponent {
		private String country;
		private Integer country_code;
		private String country_code_iso;	//国家英文缩写（三位）
		private String country_code_iso2;	//国家英文缩写（两位）
		private String province;	//省
		private String city;	//市
		private Integer city_level;
		private String district;	//区县
		private String town;	//乡镇名
		private String town_code;	//乡镇id
		private String street;	//道路名
		private String street_number;	//道路门牌号
		private Integer adcode;	//在行政区划编码
		private String direction;	//相对当前坐标点的方向，当有门牌号的时候返回数据
		private String distance;	//相对当前坐标点的距离，当有门牌号的时候返回数据
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public Integer getCountry_code() {
			return country_code;
		}
		public void setCountry_code(Integer country_code) {
			this.country_code = country_code;
		}
		public String getCountry_code_iso() {
			return country_code_iso;
		}
		public void setCountry_code_iso(String country_code_iso) {
			this.country_code_iso = country_code_iso;
		}
		public String getCountry_code_iso2() {
			return country_code_iso2;
		}
		public void setCountry_code_iso2(String country_code_iso2) {
			this.country_code_iso2 = country_code_iso2;
		}
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
		public Integer getCity_level() {
			return city_level;
		}
		public void setCity_level(Integer city_level) {
			this.city_level = city_level;
		}
		public String getDistrict() {
			return district;
		}
		public void setDistrict(String district) {
			this.district = district;
		}
		public String getTown() {
			return town;
		}
		public void setTown(String town) {
			this.town = town;
		}
		public String getTown_code() {
			return town_code;
		}
		public void setTown_code(String town_code) {
			this.town_code = town_code;
		}
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getStreet_number() {
			return street_number;
		}
		public void setStreet_number(String street_number) {
			this.street_number = street_number;
		}
		public Integer getAdcode() {
			return adcode;
		}
		public void setAdcode(Integer adcode) {
			this.adcode = adcode;
		}
		public String getDirection() {
			return direction;
		}
		public void setDirection(String direction) {
			this.direction = direction;
		}
		public String getDistance() {
			return distance;
		}
		public void setDistance(String distance) {
			this.distance = distance;
		}
	}
	
	public static class GeoCodePois {
		private String addr;	//浦东张江新区学林路36弄11号楼
		private String direction;	//内
		private String distance;
		private String name;	//博彦科技(上海)大楼
		
		public String getAddr() {
			return addr;
		}
		public void setAddr(String addr) {
			this.addr = addr;
		}
		public String getDirection() {
			return direction;
		}
		public void setDirection(String direction) {
			this.direction = direction;
		}
		public String getDistance() {
			return distance;
		}
		public void setDistance(String distance) {
			this.distance = distance;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
}
