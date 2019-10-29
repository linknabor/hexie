package com.eshequ.eurekaclient.entity;

import java.io.Serializable;

public class EurekaApplicationInstance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1735258291833268087L;
	
	private volatile String instanceId;                      // 实例ID
    private volatile String hostName;                        // 实例主机名
    private volatile String app;                             // 实例名定义
    private volatile String ipAddr;                          // 实例所在IP
    private volatile String status;                          // 实例状态，UP或DOWN
    private volatile String overriddenstatus;                // 未知
    private volatile String port;                            // 实例端口
    private volatile String securePort;                      // 实例加密端口
    private volatile boolean securePortEnabled = false;      // 实例是否开启加密
    private volatile String countryId;
    private volatile String appGroupName;                    // 集群名称，一般没用
    private volatile String homePageUrl;                     // 实例访问url
    private volatile String statusPageUrl;                   // 实例状态访问url
    private volatile String healthCheckUrl;                  // 实例健康检查url
    private volatile String vipAddress;                      // 实例的实际名（未大小写转换，基本没用）
    
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOverriddenstatus() {
		return overriddenstatus;
	}
	public void setOverriddenstatus(String overriddenstatus) {
		this.overriddenstatus = overriddenstatus;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getSecurePort() {
		return securePort;
	}
	public void setSecurePort(String securePort) {
		this.securePort = securePort;
	}
	public boolean isSecurePortEnabled() {
		return securePortEnabled;
	}
	public void setSecurePortEnabled(boolean securePortEnabled) {
		this.securePortEnabled = securePortEnabled;
	}
	public String getCountryId() {
		return countryId;
	}
	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}
	public String getAppGroupName() {
		return appGroupName;
	}
	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}
	public String getHomePageUrl() {
		return homePageUrl;
	}
	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}
	public String getStatusPageUrl() {
		return statusPageUrl;
	}
	public void setStatusPageUrl(String statusPageUrl) {
		this.statusPageUrl = statusPageUrl;
	}
	public String getHealthCheckUrl() {
		return healthCheckUrl;
	}
	public void setHealthCheckUrl(String healthCheckUrl) {
		this.healthCheckUrl = healthCheckUrl;
	}
	public String getVipAddress() {
		return vipAddress;
	}
	public void setVipAddress(String vipAddress) {
		this.vipAddress = vipAddress;
	}

}
