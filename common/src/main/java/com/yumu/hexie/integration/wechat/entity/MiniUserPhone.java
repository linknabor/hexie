package com.yumu.hexie.integration.wechat.entity;

import java.io.Serializable;

public class MiniUserPhone implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2556216516532901218L;
	
	private String errorcode;
	private String errmsg;
	private PhoneInfo phone_info;
	
	public static class PhoneInfo {
		
		private String phoneNumber;	//用户绑定的手机号（国外手机号会有区号）
		private String purePhoneNumber;	//没有区号的手机号
		private String countryCode;	//区号;
		private Watermark watermark;	//数据水印
		public String getPhoneNumber() {
			return phoneNumber;
		}
		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}
		public String getPurePhoneNumber() {
			return purePhoneNumber;
		}
		public void setPurePhoneNumber(String purePhoneNumber) {
			this.purePhoneNumber = purePhoneNumber;
		}
		public String getCountryCode() {
			return countryCode;
		}
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
		public Watermark getWatermark() {
			return watermark;
		}
		public void setWatermark(Watermark watermark) {
			this.watermark = watermark;
		}
		@Override
		public String toString() {
			return "PhoneInfo [phoneNumber=" + phoneNumber + ", purePhoneNumber=" + purePhoneNumber + ", countryCode="
					+ countryCode + ", watermark=" + watermark + "]";
		}
		
	}
	
	public static class Watermark {
		
		private String appid;		//小程序appid
		private String timestamp;	//用户获取手机号操作的时间戳
		
		public String getAppid() {
			return appid;
		}
		public void setAppid(String appid) {
			this.appid = appid;
		}
		public String getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}
		@Override
		public String toString() {
			return "Watermark [appid=" + appid + ", timestamp=" + timestamp + "]";
		}

	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public PhoneInfo getPhone_info() {
		return phone_info;
	}

	public void setPhone_info(PhoneInfo phone_info) {
		this.phone_info = phone_info;
	}

	@Override
	public String toString() {
		return "MiniUserPhone [errorcode=" + errorcode + ", errmsg=" + errmsg + ", phone_info=" + phone_info + "]";
	}

	
}
