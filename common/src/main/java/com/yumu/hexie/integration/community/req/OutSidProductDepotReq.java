package com.yumu.hexie.integration.community.req;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-05-05 21:15
 */
public class OutSidProductDepotReq implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3814700291640200869L;
	
	private long depotId;
	private long ownerId;
	private String productName;
    private String ownerName;
    private String agentNo;
    private int currentPage;
    private int pageSize;
    
    public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public long getDepotId() {
		return depotId;
	}

	public void setDepotId(long depotId) {
		this.depotId = depotId;
	}

	@Override
	public String toString() {
		return "OutSidProductDepotReq [depotId=" + depotId + ", ownerId=" + ownerId + ", productName=" + productName
				+ ", ownerName=" + ownerName + ", agentNo=" + agentNo + ", currentPage=" + currentPage + ", pageSize="
				+ pageSize + "]";
	}
    
}
