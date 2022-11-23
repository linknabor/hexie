package com.yumu.hexie.integration.community.resp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryProDepotResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8355256664152269923L;

	private long id;
	private String name;
	@JsonProperty("single_price")
	private float singlePrice;
	@JsonProperty("mini_price")
	private float miniPrice;
	@JsonProperty("ori_price")
	private float oriPrice;
	@JsonProperty("area_limit")
	private int areaLimit;
	private String pictures;
	private String tags;
	@JsonProperty("service_desc")
	private String serviceDesc;
	private String specs;
	@JsonProperty("total_count")
	private int totalCount;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getSinglePrice() {
		return singlePrice;
	}
	public void setSinglePrice(float singlePrice) {
		this.singlePrice = singlePrice;
	}
	public float getMiniPrice() {
		return miniPrice;
	}
	public void setMiniPrice(float miniPrice) {
		this.miniPrice = miniPrice;
	}
	public float getOriPrice() {
		return oriPrice;
	}
	public void setOriPrice(float oriPrice) {
		this.oriPrice = oriPrice;
	}
	public int getAreaLimit() {
		return areaLimit;
	}
	public void setAreaLimit(int areaLimit) {
		this.areaLimit = areaLimit;
	}
	public String getServiceDesc() {
		return serviceDesc;
	}
	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}
	public String getSpecs() {
		return specs;
	}
	public void setSpecs(String specs) {
		this.specs = specs;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public String getPictures() {
		return pictures;
	}
	public void setPictures(String pictures) {
		this.pictures = pictures;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	
	
}
