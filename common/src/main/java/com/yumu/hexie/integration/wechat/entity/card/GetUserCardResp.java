package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetUserCardResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4554718730623004977L;

	private String errcode;
	private String errmsg;
	private String openid;
	private String nickname;
	@JsonProperty("membership_number")
	private String membershipNumber;
	private String bonus;
	private String sex;
	@JsonProperty("user_info")
	private UserInfo userInfo;
	@JsonProperty("user_card_status")
	private String userCardStatus;
	
	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getUserCardStatus() {
		return userCardStatus;
	}

	public void setUserCardStatus(String userCardStatus) {
		this.userCardStatus = userCardStatus;
	}

	public static class UserInfo{
		
		private List<CardField> common_field_list;
		private List<CardField> custom_field_list;
		
		public List<CardField> getCommon_field_list() {
			return common_field_list;
		}

		public void setCommon_field_list(List<CardField> common_field_list) {
			this.common_field_list = common_field_list;
		}

		public List<CardField> getCustom_field_list() {
			return custom_field_list;
		}

		public void setCustom_field_list(List<CardField> custom_field_list) {
			this.custom_field_list = custom_field_list;
		}

		public static class CardField{
			
			private String name;
			private String value;
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			@Override
			public String toString() {
				return "CardField [name=" + name + ", value=" + value + "]";
			}
			
		}
		
	}

	@Override
	public String toString() {
		return "GetUserCardResp [errcode=" + errcode + ", errmsg=" + errmsg + ", openid=" + openid + ", nickname="
				+ nickname + ", membershipNumber=" + membershipNumber + ", bonus=" + bonus + ", sex=" + sex
				+ ", userInfo=" + userInfo + ", userCardStatus=" + userCardStatus + "]";
	}
	
	
	
	
}
