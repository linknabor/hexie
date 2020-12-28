package com.yumu.hexie.model.user;

import java.util.Date;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;
@Entity
public class Partner extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -815117228963410281L;

	private Long userId;
	private String tel;
	private String name;
	private Date expiredDate;
	private Date initDate;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getInitDate() {
		return initDate;
	}
	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}
	@Override
	public String toString() {
		return "Partner [userId=" + userId + ", tel=" + tel + ", name=" + name + ", expiredDate=" + expiredDate
				+ ", initDate=" + initDate + "]";
	}
	
	
}
