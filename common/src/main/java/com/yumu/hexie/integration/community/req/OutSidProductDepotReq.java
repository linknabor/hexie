package com.yumu.hexie.integration.community.req;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-05-05 21:15
 */
public class OutSidProductDepotReq implements Serializable {
    private String productName;
    private int currentPage;
    private int pageSize;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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
}
