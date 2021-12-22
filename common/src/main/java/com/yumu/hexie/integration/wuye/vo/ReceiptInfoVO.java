package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yumu.hexie.integration.wuye.vo.ReceiptInfo.Receipt;
import com.yumu.hexie.integration.wuye.vo.ReceiptInfo.ReceiptDetail;

public class ReceiptInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1315552601018723541L;
	
	private Receipt receipt;
	private Map<String, List<ReceiptDetail>> details;
	
	public ReceiptInfoVO() {
		super();
	}

	public ReceiptInfoVO(ReceiptInfo receiptInfo) {
		this.receipt = receiptInfo.getReceipt();
		setDetails(receiptInfo.getReceiptDetail());
	}
	
	private void setDetails(ReceiptDetail[]receiptDetail) {
		
		if (receiptDetail == null) {
			return;
		}
		details = new LinkedHashMap<>();
		for (ReceiptDetail detail : receiptDetail) {
			String paySubject = detail.getPaySubject();
			List<ReceiptDetail> list = details.get(paySubject);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(detail);
			details.put(paySubject, list);
			
		}
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public Map<String, List<ReceiptDetail>> getDetails() {
		return details;
	}

	public void setDetails(Map<String, List<ReceiptDetail>> details) {
		this.details = details;
	}
	
	

}
