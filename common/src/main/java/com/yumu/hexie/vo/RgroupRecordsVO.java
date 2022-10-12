package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.List;

public class RgroupRecordsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -24225548318236754L;
	
	private long totalSize;
	private List<RgroupOrderRecordVO> records;
	
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public List<RgroupOrderRecordVO> getRecords() {
		return records;
	}
	public void setRecords(List<RgroupOrderRecordVO> records) {
		this.records = records;
	}
	
	
}
