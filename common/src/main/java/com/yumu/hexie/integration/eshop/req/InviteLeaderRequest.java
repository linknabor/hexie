/**
 * 
 */
package com.yumu.hexie.integration.eshop.req;

import java.io.Serializable;

/**
 * @author david
 *
 */
public class InviteLeaderRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2408790637494751306L;

	private String code;
	private String leaderId;	//团长userId
	private String tel;			//团长手机号
	private String name;
	private String openid;
	private String miniopenid;
	
	public String getLeaderId() {
		return leaderId;
	}
	public void setLeaderId(String leaderId) {
		this.leaderId = leaderId;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getMiniopenid() {
		return miniopenid;
	}
	public void setMiniopenid(String miniopenid) {
		this.miniopenid = miniopenid;
	}
	@Override
	public String toString() {
		return "InviteLeaderDTO [leaderId=" + leaderId + ", tel=" + tel + ", code=" + code + ", name=" + name
				+ ", openid=" + openid + ", miniopenid=" + miniopenid + "]";
	}
	
}
