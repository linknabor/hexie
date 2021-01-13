package com.yumu.hexie.model.hexiemessage;

import javax.persistence.Entity;
import com.yumu.hexie.model.BaseModel;
@Entity
public class HexieMessage extends BaseModel{
	private static final long serialVersionUID = 8352306912013958919L;
	
	private String batchNo;	//批次号
	private long userId;
	private String wuyeId;
	private String type;
	private String mng_cell_id;
	private String sect_name;
	private String cell_addr;
	private String date_time;
	private String content;
	private boolean success;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getMng_cell_id() {
		return mng_cell_id;
	}
	public void setMng_cell_id(String mng_cell_id) {
		this.mng_cell_id = mng_cell_id;
	}
	public String getSect_name() {
		return sect_name;
	}
	public void setSect_name(String sect_name) {
		this.sect_name = sect_name;
	}
	public String getCell_addr() {
		return cell_addr;
	}
	public void setCell_addr(String cell_addr) {
		this.cell_addr = cell_addr;
	}
	public String getDate_time() {
		return date_time;
	}
	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	@Override
	public String toString() {
		return "HexieMessage [batchNo=" + batchNo + ", userId=" + userId + ", wuyeId=" + wuyeId + ", type=" + type
				+ ", mng_cell_id=" + mng_cell_id + ", sect_name=" + sect_name + ", cell_addr=" + cell_addr
				+ ", date_time=" + date_time + ", content=" + content + ", success=" + success + "]";
	}
	


}
