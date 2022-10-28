package com.yumu.hexie.service.sales.req;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.model.user.User;

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
    private String sectName;	//小区名称
    private User sendUser;	//需要发送消息的用户
    
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
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	public User getSendUser() {
		return sendUser;
	}
	public void setSendUser(User sendUser) {
		this.sendUser = sendUser;
	}
	@Override
	public String toString() {
		return "NoticeRgroupSuccess [opers=" + opers + ", createDate=" + createDate + ", orderType=" + orderType
				+ ", productName=" + productName + ", price=" + price + ", groupNum=" + groupNum + ", sectId=" + sectId
				+ ", ruleId=" + ruleId + ", sectName=" + sectName + ", sendUser=" + sendUser + "]";
	}
	
    
    
}
