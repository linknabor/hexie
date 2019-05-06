package com.yumu.hexie.model.community;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Staffing implements Serializable{

	private static final long serialVersionUID = 6573056479052601172L;

	@Id
	private String staffing_id;
	
	private String staffing_name;
	
	private String staffing_phone;
	
	private String staffing_status;
	
	private String regiontype;
	
	private String region_id;
	
	private String staffing_userid;

	public String getStaffing_id() {
		return staffing_id;
	}

	public void setStaffing_id(String staffing_id) {
		this.staffing_id = staffing_id;
	}

	public String getStaffing_name() {
		return staffing_name;
	}

	public void setStaffing_name(String staffing_name) {
		this.staffing_name = staffing_name;
	}

	public String getStaffing_phone() {
		return staffing_phone;
	}

	public void setStaffing_phone(String staffing_phone) {
		this.staffing_phone = staffing_phone;
	}

	public String getStaffing_status() {
		return staffing_status;
	}

	public void setStaffing_status(String staffing_status) {
		this.staffing_status = staffing_status;
	}

	public String getRegiontype() {
		return regiontype;
	}

	public void setRegiontype(String regiontype) {
		this.regiontype = regiontype;
	}

	public String getRegion_id() {
		return region_id;
	}

	public void setRegion_id(String region_id) {
		this.region_id = region_id;
	}

	public String getStaffing_userid() {
		return staffing_userid;
	}

	public void setStaffing_userid(String staffing_userid) {
		this.staffing_userid = staffing_userid;
	}
	
	
}
