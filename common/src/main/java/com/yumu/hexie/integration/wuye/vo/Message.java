package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8750566066110023141L;

	@JsonProperty("batch_no")
	private String batchNo;
	
	private String content;
	
	@JsonProperty("img_urls")
	private String imgUrls;

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(String imgUrls) {
		this.imgUrls = imgUrls;
	}
	
	
	
}
