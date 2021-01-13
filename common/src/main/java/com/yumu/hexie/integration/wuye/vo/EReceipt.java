package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EReceipt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8015843722070337233L;
	
	private String trade_water_id;
	private String mch_name;
	private String pay_method;
	private String tran_date;
	private String total_amt;
	@JsonProperty("trade_detail")
	private List<TradeDetail> tradeList;
	
	public static class TradeDetail {
		
		private String pay_mng_cell_id;
		private String pay_cell_addr;
		private String pay_cust_name;
		private String sect_name;
		private String tran_amt;
		@JsonProperty("fee_type_list")
		private List<FeeTypeList> feeTypeList;
		public String getPay_mng_cell_id() {
			return pay_mng_cell_id;
		}
		public void setPay_mng_cell_id(String pay_mng_cell_id) {
			this.pay_mng_cell_id = pay_mng_cell_id;
		}
		public String getPay_cell_addr() {
			return pay_cell_addr;
		}
		public void setPay_cell_addr(String pay_cell_addr) {
			this.pay_cell_addr = pay_cell_addr;
		}
		public String getPay_cust_name() {
			return pay_cust_name;
		}
		public void setPay_cust_name(String pay_cust_name) {
			this.pay_cust_name = pay_cust_name;
		}
		public String getSect_name() {
			return sect_name;
		}
		public void setSect_name(String sect_name) {
			this.sect_name = sect_name;
		}
		public String getTran_amt() {
			return tran_amt;
		}
		public void setTran_amt(String tran_amt) {
			this.tran_amt = tran_amt;
		}
		public List<FeeTypeList> getFeeTypeList() {
			return feeTypeList;
		}
		public void setFeeTypeList(List<FeeTypeList> feeTypeList) {
			this.feeTypeList = feeTypeList;
		}
		
	}
	
	public static class FeeTypeList{
		
		private String fee_name;
		private String bill_date;

		public String getFee_name() {
			return fee_name;
		}

		public void setFee_name(String fee_name) {
			this.fee_name = fee_name;
		}
		public String getBill_date() {
			return bill_date;
		}
		public void setBill_date(String bill_date) {
			this.bill_date = bill_date;
		}
		
	}
	
	public String getTrade_water_id() {
		return trade_water_id;
	}

	public void setTrade_water_id(String trade_water_id) {
		this.trade_water_id = trade_water_id;
	}

	public String getMch_name() {
		return mch_name;
	}

	public void setMch_name(String mch_name) {
		this.mch_name = mch_name;
	}

	public String getPay_method() {
		return pay_method;
	}

	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}

	public String getTran_date() {
		return tran_date;
	}

	public void setTran_date(String tran_date) {
		this.tran_date = tran_date;
	}

	public String getTotal_amt() {
		return total_amt;
	}

	public void setTotal_amt(String total_amt) {
		this.total_amt = total_amt;
	}

	public List<TradeDetail> getTradeList() {
		return tradeList;
	}

	public void setTradeList(List<TradeDetail> tradeList) {
		this.tradeList = tradeList;
	}

	
}
