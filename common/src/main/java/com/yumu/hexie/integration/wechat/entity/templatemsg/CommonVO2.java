package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.io.Serializable;

/**
 * 通用模板消息
 * @author david
 *
 */
public class CommonVO2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5070756033519051653L;
	/**
	 * 
	 */
	private TemplateItem first;
	private TemplateItem keyword1;
	private TemplateItem keyword2;
	private TemplateItem keyword3;
	private TemplateItem keyword4;
	private TemplateItem keyword5;
	private TemplateItem remark;
	
	public TemplateItem getFirst() {
		return first;
	}
	public void setFirst(TemplateItem first) {
		this.first = first;
	}
	public TemplateItem getKeyword1() {
		return keyword1;
	}
	public void setKeyword1(TemplateItem keyword1) {
		this.keyword1 = keyword1;
	}
	public TemplateItem getKeyword2() {
		return keyword2;
	}
	public void setKeyword2(TemplateItem keyword2) {
		this.keyword2 = keyword2;
	}
	public TemplateItem getKeyword3() {
		return keyword3;
	}
	public void setKeyword3(TemplateItem keyword3) {
		this.keyword3 = keyword3;
	}
	public TemplateItem getKeyword4() {
		return keyword4;
	}
	public void setKeyword4(TemplateItem keyword4) {
		this.keyword4 = keyword4;
	}
	public TemplateItem getRemark() {
		return remark;
	}
	public void setRemark(TemplateItem remark) {
		this.remark = remark;
	}
	public TemplateItem getKeyword5() {
		return keyword5;
	}
	public void setKeyword5(TemplateItem keyword5) {
		this.keyword5 = keyword5;
	}
	@Override
	public String toString() {
		return "CommonVO2 [first=" + first + ", keyword1=" + keyword1 + ", keyword2=" + keyword2 + ", keyword3="
				+ keyword3 + ", keyword4=" + keyword4 + ", keyword5=" + keyword5 + ", remark=" + remark + "]";
	}
	

}
