package com.yumu.hexie.service.maintenance.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaintenanceVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5926719968186141598L;
	
	private String server;
	@JsonProperty("switch")
	private String queueSwitch;
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getQueueSwitch() {
		return queueSwitch;
	}
	public void setQueueSwitch(String queueSwitch) {
		this.queueSwitch = queueSwitch;
	}
	@Override
	public String toString() {
		return "MaintenanceVO [server=" + server + ", queueSwitch=" + queueSwitch + "]";
	}
	

}
