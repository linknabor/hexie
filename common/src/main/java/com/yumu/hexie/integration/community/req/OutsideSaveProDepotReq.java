package com.yumu.hexie.integration.community.req;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.model.distribution.region.Region;

public class OutsideSaveProDepotReq implements Serializable {
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3606242338688431255L;
	
	private String operType;	//add,edit;
	private String proTags;	//商品标签
	
	//商品信息
	private String id;	//公众号的商品depotID
	private String name;
	private String type;	//产品类型
	private String oriPrice;//市场价
	private String miniPrice;	//结算价
	private String singlePrice;	//销售价
	private String startDate;	//上架日期
	private String endDate;	//下架日期
	private String totalCount;	//库存
	private String pictures;	//商品图
	private String description;	//描述
	
	//物业或服务商信息
	private String agentName;
	private String agentNo;

	//上架区域信息
	private List<Region> saleAreas;

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOriPrice() {
		return oriPrice;
	}

	public void setOriPrice(String oriPrice) {
		this.oriPrice = oriPrice;
	}

	public String getMiniPrice() {
		return miniPrice;
	}

	public void setMiniPrice(String miniPrice) {
		this.miniPrice = miniPrice;
	}

	public String getSinglePrice() {
		return singlePrice;
	}

	public void setSinglePrice(String singlePrice) {
		this.singlePrice = singlePrice;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public List<Region> getSaleAreas() {
		return saleAreas;
	}

	public void setSaleAreas(List<Region> saleAreas) {
		this.saleAreas = saleAreas;
	}

	public String getPictures() {
		return pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public String getProTags() {
		return proTags;
	}

	public void setProTags(String proTags) {
		this.proTags = proTags;
	}

	@Override
	public String toString() {
		return "OutsideSaveProDepotReq [operType=" + operType + ", proTags=" + proTags + ", id=" + id + ", name=" + name
				+ ", type=" + type + ", oriPrice=" + oriPrice + ", miniPrice=" + miniPrice + ", singlePrice="
				+ singlePrice + ", startDate=" + startDate + ", endDate=" + endDate + ", totalCount=" + totalCount
				+ ", pictures=" + pictures + ", description=" + description + ", agentName=" + agentName + ", agentNo="
				+ agentNo + ", saleAreas=" + saleAreas + "]";
	}

    
}
