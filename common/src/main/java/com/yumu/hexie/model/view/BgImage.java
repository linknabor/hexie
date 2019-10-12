package com.yumu.hexie.model.view;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class BgImage extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 465940802174591676L;

	private int type;	//类型
	private String imgUrl;	//图片地址
	private String fromSys;	//系统
	private String forwardUrl;	//跳转地址
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getFromSys() {
		return fromSys;
	}
	public void setFromSys(String fromSys) {
		this.fromSys = fromSys;
	}
	public String getForwardUrl() {
		return forwardUrl;
	}
	public void setForwardUrl(String forwardUrl) {
		this.forwardUrl = forwardUrl;
	}
	@Override
	public String toString() {
		return "BgImage [type=" + type + ", imgUrl=" + imgUrl + ", fromSys=" + fromSys + ", forwardUrl=" + forwardUrl
				+ "]";
	}
	
	
	
}
