package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.model.distribution.region.Region;

public class SaveProductVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7337684573964362963L;
	
	private String operType;	//add,edit;
	
	//商品信息
	private String id;	//公众号的商品ID
	private String name;
	private String type;	//产品类型
	private String oriPrice;//市场价
	private String miniPrice;	//结算价
	private String singlePrice;	//销售价
	private String limitNumOnce;	//限购数
	private String sortNo;	//商品显示位置
	private String status;	//1上架  0未上架, RULE_STATUS_ON
	private String startDate;	//上架日期
	private String endDate;	//下架日期
	private String mainPicture;	//封面图链接
	private String smallPicture;	//小图
	private String pictures;	//轮播图，多个逗号分割
	private String totalCount;	//库存
	private String context;	//描述
	private String updateUser;	//更新商品的用户
	private String appid;	//公众号id
	
	//物业或代理商信息
	private String agentName;
	private String agentNo;
	
	//0.团购单 1.单个订单 3.特卖  4.团购 5.到家服务（预约）,6维修单，11自定义服务订单,12核销券
	private String salePlanType;
	
	private String counts;	//关联小区数
	
	//上架区域信息
	private List<Region> saleAreas;
	
	public SaveProductVO() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getLimitNumOnce() {
		return limitNumOnce;
	}

	public void setLimitNumOnce(String limitNumOnce) {
		this.limitNumOnce = limitNumOnce;
	}

	public String getSortNo() {
		return sortNo;
	}

	public void setSortNo(String sortNo) {
		this.sortNo = sortNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getMainPicture() {
		return mainPicture;
	}

	public void setMainPicture(String mainPicture) {
		this.mainPicture = mainPicture;
	}

	public String getSmallPicture() {
		return smallPicture;
	}

	public void setSmallPicture(String smallPicture) {
		this.smallPicture = smallPicture;
	}

	public String getPictures() {
		return pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}
	
	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getSalePlanType() {
		return salePlanType;
	}

	public void setSalePlanType(String salePlanType) {
		this.salePlanType = salePlanType;
	}

	public String getCounts() {
		return counts;
	}

	public void setCounts(String counts) {
		this.counts = counts;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public String toString() {
		return "SaveProductVO [operType=" + operType + ", id=" + id + ", name=" + name + ", type=" + type
				+ ", oriPrice=" + oriPrice + ", miniPrice=" + miniPrice + ", singlePrice=" + singlePrice
				+ ", limitNumOnce=" + limitNumOnce + ", sortNo=" + sortNo + ", status=" + status + ", startDate="
				+ startDate + ", endDate=" + endDate + ", mainPicture=" + mainPicture + ", smallPicture=" + smallPicture
				+ ", pictures=" + pictures + ", totalCount=" + totalCount + ", context=" + context + ", updateUser="
				+ updateUser + ", appid=" + appid + ", agentName=" + agentName + ", agentNo=" + agentNo
				+ ", salePlanType=" + salePlanType + ", counts=" + counts + ", saleAreas=" + saleAreas + "]";
	}


}
