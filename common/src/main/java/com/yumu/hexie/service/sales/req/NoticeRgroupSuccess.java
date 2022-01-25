package com.yumu.hexie.service.sales.req;

import java.io.Serializable;
import java.util.List;

/**
 * 成团提醒
 * @author david
 *
 */
public class NoticeRgroupSuccess implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6066738197459423973L;
	
	private List<Long> opers;
    private long createDate;
    private int orderType;
    private String productName;
    private String price;	//拼团价格
    private int groupNum;	//当前小区拼团人数
    private String sectId;
    private long ruleId;	//团购id
    
	public List<Long> getOpers() {
		return opers;
	}
	public void setOpers(List<Long> opers) {
		this.opers = opers;
	}
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public int getGroupNum() {
		return groupNum;
	}
	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	@Override
	public String toString() {
		return "NoticeRgroupSuccess [opers=" + opers + ", createDate=" + createDate + ", orderType=" + orderType
				+ ", productName=" + productName + ", price=" + price + ", groupNum=" + groupNum + ", sectId=" + sectId
				+ ", ruleId=" + ruleId + "]";
	}
	
    
    
}
