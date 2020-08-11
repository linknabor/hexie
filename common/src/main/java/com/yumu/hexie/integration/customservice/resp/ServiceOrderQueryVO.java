package com.yumu.hexie.integration.customservice.resp;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceOrderQueryVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 341835241449294643L;
	
	@JsonProperty("total_count")
	private String totalCount;
	
	private List<Orders> orders; 

	public static class Orders {
		
		@JsonProperty("sect_name")
		private String sectName;
		@JsonProperty("acct_date")
		private String acctDate;
		@JsonProperty("acct_time")
		private String acctTime;
		@JsonProperty("tran_amt")
		private String tranAmt;
		@JsonProperty("fee_name")
		private String feeName;
		private String remark;
		
		public String getSectName() {
			return sectName;
		}
		public void setSectName(String sectName) {
			this.sectName = sectName;
		}
		public String getAcctDate() {
			return acctDate;
		}
		public void setAcctDate(String acctDate) {
			this.acctDate = acctDate;
		}
		public String getAcctTime() {
			return acctTime;
		}
		public void setAcctTime(String acctTime) {
			this.acctTime = acctTime;
		}
		public String getTranAmt() {
			return tranAmt;
		}
		public void setTranAmt(String tranAmt) {
			this.tranAmt = tranAmt;
		}
		public String getFeeName() {
			return feeName;
		}
		public void setFeeName(String feeName) {
			this.feeName = feeName;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		@Override
		public String toString() {
			return "Orders [sectName=" + sectName + ", acctDate=" + acctDate + ", acctTime=" + acctTime + ", tranAmt="
					+ tranAmt + ", feeName=" + feeName + ", remark=" + remark + "]";
		}
		
		
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public List<Orders> getOrders() {
		return orders;
	}

	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "ServiceOrderQueryVO [totalCount=" + totalCount + ", orders=" + orders + "]";
	}
	
	
}
