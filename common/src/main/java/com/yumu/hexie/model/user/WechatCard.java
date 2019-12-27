package com.yumu.hexie.model.user;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class WechatCard extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String userName;
	private String userOpenId;
	private String userAppId;
	private String cardId;	//卡券ID
	private String cardCode;	//卡序列号
	private int cardType;	//卡券类型
	private int status;	//卡券状态
	private String oldCardCode;	//为保证安全，微信会在转赠发生后变更该卡券的code号，该字段表示转赠前的code。
	private String outerStr;	//领取场景值，用于领取渠道数据统计。可在生成二维码接口及添加Addcard接口中自定义该字段的字符串值。
	private String IsRestoreMemberCard;	//用户删除会员卡后可重新找回，当用户本次操作为找回时，该值为1，否则为0
	private String unionId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserOpenId() {
		return userOpenId;
	}
	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}
	public String getUserAppId() {
		return userAppId;
	}
	public void setUserAppId(String userAppId) {
		this.userAppId = userAppId;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getCardCode() {
		return cardCode;
	}
	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}
	public int getCardType() {
		return cardType;
	}
	public void setCardType(int cardType) {
		this.cardType = cardType;
	}
	public String getOldCardCode() {
		return oldCardCode;
	}
	public void setOldCardCode(String oldCardCode) {
		this.oldCardCode = oldCardCode;
	}
	public String getOuterStr() {
		return outerStr;
	}
	public void setOuterStr(String outerStr) {
		this.outerStr = outerStr;
	}
	public String getIsRestoreMemberCard() {
		return IsRestoreMemberCard;
	}
	public void setIsRestoreMemberCard(String isRestoreMemberCard) {
		IsRestoreMemberCard = isRestoreMemberCard;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
