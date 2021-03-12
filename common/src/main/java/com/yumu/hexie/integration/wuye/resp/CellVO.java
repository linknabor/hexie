package com.yumu.hexie.integration.wuye.resp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.springframework.util.StringUtils;

public class CellVO {

	private String id;
	private String name;
	private String version;
	private String tel;
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
	public List<Object> getTelList() {
		return telList;
	}
	
	
}
