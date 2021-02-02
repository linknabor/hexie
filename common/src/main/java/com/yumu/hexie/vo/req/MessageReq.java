package com.yumu.hexie.vo.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageReq implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6002841857660674469L;
	
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("build_id")
	private String buildId;
	@JsonProperty("unit_id")
	private String unitId;
	@JsonProperty("mng_cell_id")
	private String cellId;
	
	private int type;	//消息推送方式，0公众号，1短信，2全部
	private int range = 0;	//发送范围,0指定方位，1全部
	private String content;	//发送内容
	private String imgUrls;	//上传图片链接
	
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getBuildId() {
		return buildId;
	}
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getImgUrls() {
		return imgUrls;
	}
	public void setImgUrls(String imgUrls) {
		this.imgUrls = imgUrls;
	}
	@Override
	public String toString() {
		return "MessageReq [sectId=" + sectId + ", buildId=" + buildId + ", unitId=" + unitId + ", cellId=" + cellId
				+ ", type=" + type + ", range=" + range + ", content=" + content + ", imgUrls=" + imgUrls + "]";
	}
	
}
