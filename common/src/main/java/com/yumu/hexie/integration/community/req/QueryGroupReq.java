package com.yumu.hexie.integration.community.req;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-24 13:02
 */
public class QueryGroupReq {
    //团购筛选条件
    private String  queryName;
    private String groupStatus;

    //订单筛选条件
    private String groupId; //团购ID
    private String orderStatus; //订单状态

    private String regionId;	//小区的regionId
    
    private int currentPage;

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	@Override
	public String toString() {
		return "QueryGroupReq [queryName=" + queryName + ", groupStatus=" + groupStatus + ", groupId=" + groupId
				+ ", orderStatus=" + orderStatus + ", regionId=" + regionId + ", currentPage=" + currentPage + "]";
	}

}
