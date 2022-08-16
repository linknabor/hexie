package com.yumu.hexie.integration.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceOrderRequest extends CommonRequest {

	private static Logger logger = LoggerFactory.getLogger(ServiceOrderRequest.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7769480227414232932L;
	
	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	@JsonProperty("fee_id")
	private String feeId;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("curr_page")
	private String currentPage;
	@JsonProperty("total_count")
	private String totalCount;
	
	@JsonProperty("refund_amt")
	private String refundAmt;	//退款金额
	private String memo;	//备注
	private String pictures;	//逗号拼接
	private String items;	//要退款的商品的productId，以逗号分割

	public String getTradeWaterId() {
		return tradeWaterId;
	}

	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}

	public String getSectId() {
		return sectId;
	}

	public void setSectId(String sectId) {
		this.sectId = sectId;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(String refundAmt) {
		this.refundAmt = refundAmt;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		if (!StringUtils.isEmpty(memo)) {
			try {
				memo = URLEncoder.encode(memo, "GBK");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}
		}
		this.memo = memo;
	}

	public String getPictures() {
		return pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "ServiceOrderRequest [tradeWaterId=" + tradeWaterId + ", feeId=" + feeId + ", sectId=" + sectId
				+ ", currentPage=" + currentPage + ", totalCount=" + totalCount + ", refundAmt=" + refundAmt + ", memo="
				+ memo + ", pictures=" + pictures + ", items=" + items + "]";
	}

	
	
	
	
}
