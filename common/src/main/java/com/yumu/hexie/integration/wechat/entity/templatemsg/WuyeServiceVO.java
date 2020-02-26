/**
 * 
 */
package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author HuYM
 *
 */
public class WuyeServiceVO implements Serializable {
	
	private static final long serialVersionUID = 4594080226499648498L;

	@JsonProperty("first")
	private TemplateItem title;
	
	@JsonProperty("keyword1")
	private TemplateItem orderNum;	//快递单号
	
	@JsonProperty("keyword2")
	private TemplateItem recvDate;	//时间
	
	private TemplateItem remark;	//备注

	public TemplateItem getTitle() {
		return title;
	}

	public void setTitle(TemplateItem title) {
		this.title = title;
	}

	public TemplateItem getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(TemplateItem orderNum) {
		this.orderNum = orderNum;
	}

	public TemplateItem getRecvDate() {
		return recvDate;
	}

	public void setRecvDate(TemplateItem recvDate) {
		this.recvDate = recvDate;
	}

	public TemplateItem getRemark() {
		return remark;
	}

	public void setRemark(TemplateItem remark) {
		this.remark = remark;
	}
	
	

}
