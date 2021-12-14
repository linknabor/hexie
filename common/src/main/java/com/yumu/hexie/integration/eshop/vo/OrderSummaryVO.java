package com.yumu.hexie.integration.eshop.vo;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-12-14 14:32
 */
public class OrderSummaryVO {
    private String agentNo;
    private String userid;
    private List<String> sectIds;

    public String getAgentNo() {
        return agentNo;
    }

    public void setAgentNo(String agentNo) {
        this.agentNo = agentNo;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<String> getSectIds() {
        return sectIds;
    }

    public void setSectIds(List<String> sectIds) {
        this.sectIds = sectIds;
    }

    @Override
    public String toString() {
        return "OrderSummaryVO{" +
                "agentNo='" + agentNo + '\'' +
                ", userid='" + userid + '\'' +
                ", sectIds=" + sectIds +
                '}';
    }
}
