package com.yumu.hexie.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Date;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;

public class EvoucherPageMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3712001947512906413L;

	private BigInteger orderId;
	private String tel;
	private Timestamp consumeDate;
	private String productName;
	private Double acturalPrice;
	private BigInteger counts;
	
	public EvoucherPageMapper(BigInteger orderId, String tel, Timestamp consumeDate, String productName, Double acturalPrice,
			BigInteger counts) {
		super();
		this.orderId = orderId;
		this.tel = tel;
		this.consumeDate = consumeDate;
		this.productName = productName;
		this.acturalPrice = acturalPrice;
		this.counts = counts;
	}
	
	public BigInteger getOrderId() {
		return orderId;
	}
	public void setOrderId(BigInteger orderId) {
		this.orderId = orderId;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getConsumeDate() {
		String showDate = "";
		if (!StringUtil.isEmpty(consumeDate)) {
			Date date = DateUtil.parse(consumeDate.toString(), DateUtil.dttmSimple);
			showDate = DateUtil.dtFormat(date, DateUtil.dttmSimple);
		}
		return showDate;
	}
	public void setConsumeDate(Timestamp consumeDate) {
		this.consumeDate = consumeDate;
	}
	public String getActuralPrice() {
		
		String aPrice = "0.00";
		if (!StringUtil.isEmpty(acturalPrice)) {
			BigDecimal price = new BigDecimal(String.valueOf(acturalPrice));
			price = price.setScale(2, RoundingMode.HALF_UP);
			aPrice = price.toString();
		}
		return aPrice;
	}
	public void setActuralPrice(Double acturalPrice) {
		this.acturalPrice = acturalPrice;
	}
	public BigInteger getCounts() {
		return counts;
	}
	public void setCounts(BigInteger counts) {
		this.counts = counts;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
