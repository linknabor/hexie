package com.yumu.hexie.integration.kuaidi100.resp;

import java.io.Serializable;

public class LogisticCompanyQueryResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6519510630410867069L;
	
	private String lengthPre;
	private String comCode;
	private String noPre;
	private String noCount;
	
	public String getLengthPre() {
		return lengthPre;
	}
	public void setLengthPre(String lengthPre) {
		this.lengthPre = lengthPre;
	}
	public String getComCode() {
		return comCode;
	}
	public void setComCode(String comCode) {
		this.comCode = comCode;
	}
	public String getNoPre() {
		return noPre;
	}
	public void setNoPre(String noPre) {
		this.noPre = noPre;
	}
	public String getNoCount() {
		return noCount;
	}
	public void setNoCount(String noCount) {
		this.noCount = noCount;
	}
	@Override
	public String toString() {
		return "Company [lengthPre=" + lengthPre + ", comCode=" + comCode + ", noPre=" + noPre + ", noCount="
				+ noCount + "]";
	}
	

}
