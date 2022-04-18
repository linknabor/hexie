package com.yumu.hexie.integration.community.req;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-11 14:07
 */
public class QueryWaterVO {
    private String queryDate;
    private String minAmt;
    private String maxAmt;
    private String loanFlag;
    private String orderNo;
    private String curr_page;
    private String total_count = "9999";
    private String orgOperId;
    private String orgId;
    private String roleId;

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    public String getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(String minAmt) {
        this.minAmt = minAmt;
    }

    public String getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(String maxAmt) {
        this.maxAmt = maxAmt;
    }

    public String getLoanFlag() {
        return loanFlag;
    }

    public void setLoanFlag(String loanFlag) {
        this.loanFlag = loanFlag;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCurr_page() {
        return curr_page;
    }

    public void setCurr_page(String curr_page) {
        this.curr_page = curr_page;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public String getOrgOperId() {
        return orgOperId;
    }

    public void setOrgOperId(String orgOperId) {
        this.orgOperId = orgOperId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "QueryWaterVO{" +
                "queryDate='" + queryDate + '\'' +
                ", minAmt='" + minAmt + '\'' +
                ", maxAmt='" + maxAmt + '\'' +
                ", loanFlag='" + loanFlag + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", curr_page='" + curr_page + '\'' +
                ", total_count='" + total_count + '\'' +
                ", orgOperId='" + orgOperId + '\'' +
                ", orgId='" + orgId + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
