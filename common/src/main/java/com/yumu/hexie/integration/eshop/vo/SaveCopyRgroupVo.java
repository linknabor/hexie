package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class SaveCopyRgroupVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6944690552088960633L;
	
	private String srcProductId;	//复制源商品id
	private String destName;	//新商品名称
	private String startDate;	//新上架日期
	private String endDate;		//新下架日期
	private String totalCount;	//库存
	
	public String getSrcProductId() {
		return srcProductId;
	}
	public void setSrcProductId(String srcProductId) {
		this.srcProductId = srcProductId;
	}
	public String getDestName() {
		return destName;
	}
	public void setDestName(String destName) {
		this.destName = destName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	@Override
	public String toString() {
		return "SaveCopyRgroupVo [srcProductId=" + srcProductId + ", destName=" + destName + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", totalCount=" + totalCount + "]";
	}
	

}
