package com.yumu.hexie.integration.wuye.resp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.assertj.core.util.Arrays;
import org.springframework.util.StringUtils;

public class CellVO {

	private String id;
	private String name;
	private String version;
	private String tel;
	@JsonProperty("cust_name")
	private String custName; //业主名称
	private String qrCodeOper;	//是否为二维码收费工作人员, 0否1是

	private List<Object> telList;
	private Map<String, String> params;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
		if (!StringUtils.isEmpty(tel) && tel.contains("/")) {
			String[]telArr = tel.split("/");
			this.telList = Arrays.asList(telArr);
		} else {
			this.telList = new ArrayList<>(1);
			this.telList.add(this.tel);
		}
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public List<Object> getTelList() {
		return telList;
	}
	public String getQrCodeOper() {
		return qrCodeOper;
	}
	public void setQrCodeOper(String qrCodeOper) {
		this.qrCodeOper = qrCodeOper;
	}
	
	
}
