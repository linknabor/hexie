package com.yumu.hexie.integration.wuye.req;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageRequest implements Serializable {
	
	private static Logger logger = LoggerFactory.getLogger(MessageRequest.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8388565095697323206L;
	
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
	
	@JsonProperty("oper_id")
	private String operId;
	@JsonProperty("oper_name")
	private String operName;
	
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
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
		String retStr = "";
		if (!StringUtils.isEmpty(content)) {
			try {
				retStr = URLEncoder.encode(content,"GBK");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}
		}
		this.content = retStr;
	}
	public String getOperId() {
		return operId;
	}
	public void setOperId(String operId) {
		this.operId = operId;
	}
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		String retStr = "";
		if (!StringUtils.isEmpty(operName)) {
			try {
				retStr = URLEncoder.encode(retStr,"GBK");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}
		}
		this.operName = retStr;
	}
	@Override
	public String toString() {
		return "MessageRequest [sectId=" + sectId + ", buildId=" + buildId + ", unitId=" + unitId + ", cellId=" + cellId
				+ ", type=" + type + ", range=" + range + ", content=" + content + ", operId=" + operId + ", operName="
				+ operName + "]";
	}
	
	
}
