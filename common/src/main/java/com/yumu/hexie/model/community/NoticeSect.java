package com.yumu.hexie.model.community;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class NoticeSect extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8189598305039252905L;
	
	private long noticeId;	//公告ID
	private long sectId;	//小区ID
	
	public long getSectId() {
		return sectId;
	}
	public void setSectId(long sectId) {
		this.sectId = sectId;
	}
	public long getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(long noticeId) {
		this.noticeId = noticeId;
	}
	
	

}
