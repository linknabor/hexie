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
	@Override
	public String toString() {
		return "QuerySectRequet [sectName=" + sectName + ", openid=" + openid + ", appid=" + appid + "]";
	}
	
}
