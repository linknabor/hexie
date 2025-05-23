package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class HexieUser implements Serializable {

	private static final long serialVersionUID = -8863855555160105591L;
	
	private String user_id;
	
	private String user_name;
	
	private String user_head;
	
	private String user_sex;
	
	private String user_email;
	
	private String email_activie;
	private String user_tel;
	private String is_house;//房屋数
	private String office_tel;//小区电话
	private String cell_id;	//房屋id
	private String cell_addr;//房屋地址
	private String sect_name;//小区名字
	private long province_id;//省id
	private String province_name;//省名
	private long city_id;//城市ID
	private String city_name;//城市名称
	private long region_id;//区域ID
	private String region_name;//区域名
	private String sect_addr;
	
	private String sect_id;//小区id
	private String csp_id;//公司
	private String center_id;//中心管理id
	private Integer total_bind = 0;
	
	private String ver_no;	//户号
	private String owner_name;	//绑定房屋的业主名称
	private String community_id;	//支付宝小区编号
	private String out_bill_url;	//支付宝小程序账单缴费地址
	
	public String getSect_id() {
		return sect_id;
	}
	public void setSect_id(String sect_id) {
		this.sect_id = sect_id;
	}
	public String getCsp_id() {
		return csp_id;
	}
	public void setCsp_id(String csp_id) {
		this.csp_id = csp_id;
	}
	public String getCenter_id() {
		return center_id;
	}
	public void setCenter_id(String center_id) {
		this.center_id = center_id;
	}
	public String getSect_addr() {
		return sect_addr;
	}
	public void setSect_addr(String sect_addr) {
		this.sect_addr = sect_addr;
	}
	public long getProvince_id() {
		return province_id;
	}
	public void setProvince_id(long province_id) {
		this.province_id = province_id;
	}
	public String getProvince_name() {
		return province_name;
	}
	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}
	public long getCity_id() {
		return city_id;
	}
	public void setCity_id(long city_id) {
		this.city_id = city_id;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public long getRegion_id() {
		return region_id;
	}
	public void setRegion_id(long region_id) {
		this.region_id = region_id;
	}
	public String getRegion_name() {
		return region_name;
	}
	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}
	public String getSect_name() {
		return sect_name;
	}
	public void setSect_name(String sect_name) {
		this.sect_name = sect_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_head() {
		return user_head;
	}
	public void setUser_head(String user_head) {
		this.user_head = user_head;
	}
	public String getUser_sex() {
		return user_sex;
	}
	public void setUser_sex(String user_sex) {
		this.user_sex = user_sex;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public String getEmail_activie() {
		return email_activie;
	}
	public void setEmail_activie(String email_activie) {
		this.email_activie = email_activie;
	}
	public String getUser_tel() {
		return user_tel;
	}
	public void setUser_tel(String user_tel) {
		this.user_tel = user_tel;
	}
	public String getIs_house() {
		return is_house;
	}
	public void setIs_house(String is_house) {
		this.is_house = is_house;
	}
	public String getOffice_tel() {
		return office_tel;
	}
	public void setOffice_tel(String office_tel) {
		this.office_tel = office_tel;
	}
	public String getCell_addr() {
		return cell_addr;
	}
	public void setCell_addr(String cell_addr) {
		this.cell_addr = cell_addr;
	}
	public Integer getTotal_bind() {
		return total_bind;
	}
	public void setTotal_bind(Integer total_bind) {
		this.total_bind = total_bind;
	}
	public String getCell_id() {
		return cell_id;
	}
	public void setCell_id(String cell_id) {
		this.cell_id = cell_id;
	}
	public String getVer_no() {
		return ver_no;
	}
	public void setVer_no(String ver_no) {
		this.ver_no = ver_no;
	}
	public String getOwner_name() {
		return owner_name;
	}
	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}
	public String getCommunity_id() {
		return community_id;
	}
	public void setCommunity_id(String community_id) {
		this.community_id = community_id;
	}
	public String getOut_bill_url() {
		return out_bill_url;
	}
	public void setOut_bill_url(String out_bill_url) {
		this.out_bill_url = out_bill_url;
	}
	
	@Override
	public String toString() {
		return "HexieUser [user_id=" + user_id + ", user_name=" + user_name + ", user_head=" + user_head + ", user_sex="
				+ user_sex + ", user_email=" + user_email + ", email_activie=" + email_activie + ", user_tel="
				+ user_tel + ", is_house=" + is_house + ", office_tel=" + office_tel + ", cell_id=" + cell_id
				+ ", cell_addr=" + cell_addr + ", sect_name=" + sect_name + ", province_id=" + province_id
				+ ", province_name=" + province_name + ", city_id=" + city_id + ", city_name=" + city_name
				+ ", region_id=" + region_id + ", region_name=" + region_name + ", sect_addr=" + sect_addr
				+ ", sect_id=" + sect_id + ", csp_id=" + csp_id + ", center_id=" + center_id + ", total_bind="
				+ total_bind + ", ver_no=" + ver_no + ", owner_name=" + owner_name + ", community_id=" + community_id
				+ ", out_bill_url=" + out_bill_url + "]";
	}
	
}
