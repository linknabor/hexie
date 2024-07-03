package com.yumu.hexie.integration.beyondsoft;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:beyondsoft.properties")
@ConfigurationProperties(prefix = "west")
public class BeyondSoftConfig {

	private String baseUrl;
	private String loginUri;
	private String statisticUri;
	private String username;
	private String password;
	private String appkey;
	
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getLoginUri() {
		return loginUri;
	}
	public void setLoginUri(String loginUri) {
		this.loginUri = loginUri;
	}
	public String getStatisticUri() {
		return statisticUri;
	}
	public void setStatisticUri(String statisticUri) {
		this.statisticUri = statisticUri;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	
	
}
