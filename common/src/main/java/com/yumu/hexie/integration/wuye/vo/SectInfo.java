package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

public class SectInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1181239828010038443L;
	
	private String sect_id;
	private String sect_name;
	private String sect_addr;
	
	public String getSect_id() {
		return sect_id;
	}
	public void setSect_id(String sect_id) {
		this.sect_id = sect_id;
	}
	public String getSect_name() {
		return sect_name;
	}
	public void setSect_name(String sect_name) {
		this.sect_name = sect_name;
	}
	public String getSect_addr() {
		return sect_addr;
	}
	public void setSect_addr(String sect_addr) {
		this.sect_addr = sect_addr;
	}
	@Override
	public String toString() {
		return "SectInfo [sect_id=" + sect_id + ", sect_name=" + sect_name + ", sect_addr=" + sect_addr + "]";
	}
	
}
