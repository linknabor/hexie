package com.yumu.hexie.integration.workorder.resp;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.integration.wuye.vo.HexieHouse;

public class WorkOrderServiceVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7496439345281009317L;
	
	private String accept_type;	//工单接单类型
	private String repair_hotline;	//报修热线
	private String start_time;
	private String end_time;
	private List<HexieHouse> hou_info;	//业主绑定的房屋
	
	public String getAccept_type() {
		return accept_type;
	}
	public void setAccept_type(String accept_type) {
		this.accept_type = accept_type;
	}
	public String getRepair_hotline() {
		return repair_hotline;
	}
	public void setRepair_hotline(String repair_hotline) {
		this.repair_hotline = repair_hotline;
	}
	public List<HexieHouse> getHou_info() {
		return hou_info;
	}
	public void setHou_info(List<HexieHouse> hou_info) {
		this.hou_info = hou_info;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
	
}
