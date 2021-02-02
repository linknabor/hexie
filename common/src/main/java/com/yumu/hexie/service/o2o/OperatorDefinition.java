package com.yumu.hexie.service.o2o;

import java.io.Serializable;

public class OperatorDefinition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -168629557912114341L;
	
	private boolean isServiceOperator = false;
	private boolean isRepairOperator = false;
	private boolean isEvoucherOperator = false;
	private boolean isOnsaleTaker = false;
	private boolean isRgroupTaker = false;
	private boolean isMsgSender = false;
	
	public boolean isServiceOperator() {
		return isServiceOperator;
	}
	public void setServiceOperator(boolean isServiceOperator) {
		this.isServiceOperator = isServiceOperator;
	}
	public boolean isRepairOperator() {
		return isRepairOperator;
	}
	public void setRepairOperator(boolean isRepairOperator) {
		this.isRepairOperator = isRepairOperator;
	}
	public boolean isEvoucherOperator() {
		return isEvoucherOperator;
	}
	public void setEvoucherOperator(boolean isEvoucherOperator) {
		this.isEvoucherOperator = isEvoucherOperator;
	}
	public boolean isOnsaleTaker() {
		return isOnsaleTaker;
	}
	public void setOnsaleTaker(boolean isOnsaleTaker) {
		this.isOnsaleTaker = isOnsaleTaker;
	}
	public boolean isRgroupTaker() {
		return isRgroupTaker;
	}
	public void setRgroupTaker(boolean isRgroupTaker) {
		this.isRgroupTaker = isRgroupTaker;
	}
	public boolean isMsgSender() {
		return isMsgSender;
	}
	public void setMsgSender(boolean isMsgSender) {
		this.isMsgSender = isMsgSender;
	}
	@Override
	public String toString() {
		return "OperatorDefinition [isServiceOperator=" + isServiceOperator + ", isRepairOperator=" + isRepairOperator
				+ ", isEvoucherOperator=" + isEvoucherOperator + ", isOnsaleTaker=" + isOnsaleTaker + ", isRgroupTaker="
				+ isRgroupTaker + ", isMsgSender=" + isMsgSender + "]";
	}
	
	
	
}
