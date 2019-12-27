package com.yumu.hexie.model.user;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

/**
 * 微信卡券大类
 * @author david
 *
 */
@Entity
public class WechatCardCatagory extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7712112260263944570L;

	private int cardType;	//卡类型，0会员卡，其余值代表其他
	private String cardId;	//卡id
	private String appId;	//appId
	private Long quantity;	//库存

	public int getCardType() {
		return cardType;
	}
	public void setCardType(int cardType) {
		this.cardType = cardType;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "WechatCard [cardType=" + cardType + ", cardId=" + cardId + ", appId=" + appId + ", quantity=" + quantity
				+ "]";
	}
	
	
	
}
