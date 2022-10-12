package com.yumu.hexie.model.market.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class RgroupCartVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3120808814176175511L;
	
	private Integer totalCount = 0;
	private BigDecimal totalAmount = BigDecimal.ZERO;
	
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	

}
