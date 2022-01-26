package com.yumu.hexie.integration.eshop.resp;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.integration.eshop.mapper.QueryRgroupMapper;

public class QueryRgoupsResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5300740825611054660L;
	
	private List<QueryRgroupMapper> groupList;
	private RgroupSummaryResp groupSummary;

	public static class RgroupSummaryResp {
		
		private int grouping;	//进行中
		private int grouped;		//已成团
		private int delivering;	//发货中
		private int delivered;	//已完成
		
		public int getGrouping() {
			return grouping;
		}
		public void setGrouping(int grouping) {
			this.grouping = grouping;
		}
		public int getGrouped() {
			return grouped;
		}
		public void setGrouped(int grouped) {
			this.grouped = grouped;
		}
		public int getDelivering() {
			return delivering;
		}
		public void setDelivering(int delivering) {
			this.delivering = delivering;
		}
		public int getDelivered() {
			return delivered;
		}
		public void setDelivered(int delivered) {
			this.delivered = delivered;
		}
		@Override
		public String toString() {
			return "RgroupSummaryResp [grouping=" + grouping + ", grouped=" + grouped + ", delivering=" + delivering
					+ ", delivered=" + delivered + "]";
		}
		
	}

	public List<QueryRgroupMapper> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<QueryRgroupMapper> groupList) {
		this.groupList = groupList;
	}

	public RgroupSummaryResp getGroupSummary() {
		return groupSummary;
	}

	public void setGroupSummary(RgroupSummaryResp groupSummary) {
		this.groupSummary = groupSummary;
	}

	@Override
	public String toString() {
		return "QueryRgoupsResp [groupList=" + groupList + ", groupSummary=" + groupSummary + "]";
	}

		

}
