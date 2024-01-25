package com.yumu.hexie.integration.wuye.req;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuerySectRequet extends WuyeRequest {
	
	private Logger logger = LoggerFactory.getLogger(QuerySectRequet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 5557721132112239844L;

	@JsonProperty("sect_name")
	private String sectName;
	private String openid;
	private String appid;
	@JsonProperty("query_appid")
	private String queryAppid;
	@JsonProperty("client_type")
	private String clientType;
	
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		if (!StringUtils.isEmpty(sectName)) {
			try {
				sectName = URLEncoder.encode(sectName, "GBK");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}
		}
		this.sectName = sectName;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getQueryAppid() {
		return queryAppid;
	}
	public void setQueryAppid(String queryAppid) {
		this.queryAppid = queryAppid;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	@Override
	public String toString() {
		return "QuerySectRequet [logger=" + logger + ", sectName=" + sectName + ", openid=" + openid + ", appid="
				+ appid + ", queryAppid=" + queryAppid + ", clientType=" + clientType + "]";
	}
	
}
