package com.yumu.hexie.integration.repair.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;

public class QueryROrderVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1822561534988952604L;

	private String startDate;
	private String endDate;
	private List<String> sectIds;	//订单所在小区，逗号分隔
	private String sectId;
	private String address;	//订单地址
	private String tel;	//订单联系方式
	private String operatorName;	//维修工
	private String operatorTel;	//维修工电话
	private String status;	//订单状态
	private String payType;	//支付方式
	private String finishByUser;	//用户确认完成
	private String finishByOperator;	//维修工确认完成
	
	private int currentPage;
	private int pageSize;
	
	public String getStartDate() {
		if (!StringUtils.isEmpty(startDate)) {
			Date d = DateUtil.getDateTimeFromString(startDate);
			return String.valueOf(d.getTime());
		}
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		if (!StringUtils.isEmpty(endDate)) {
			Date d = DateUtil.getDateTimeFromString(endDate);
			return String.valueOf(d.getTime());
		}
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getOperatorTel() {
		return operatorTel;
	}
	public void setOperatorTel(String operatorTel) {
		this.operatorTel = operatorTel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getFinishByUser() {
		return finishByUser;
	}
	public void setFinishByUser(String finishByUser) {
		this.finishByUser = finishByUser;
	}
	public String getFinishByOperator() {
		return finishByOperator;
	}
	public void setFinishByOperator(String finishByOperator) {
		this.finishByOperator = finishByOperator;
	}
	public List<String> getSectIds() {
		return sectIds;
	}
	public void setSectIds(List<String> sectIds) {
		this.sectIds = sectIds;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	@Override
	public String toString() {
		return "QueryROrderVO [startDate=" + startDate + ", endDate=" + endDate + ", sectIds=" + sectIds + ", sectId="
				+ sectId + ", address=" + address + ", tel=" + tel + ", operatorName=" + operatorName
				+ ", operatorTel=" + operatorTel + ", status=" + status + ", payType=" + payType + ", finishByUser="
				+ finishByUser + ", finishByOperator=" + finishByOperator + ", currentPage=" + currentPage
				+ ", pageSize=" + pageSize + "]";
	}
	
	
}
