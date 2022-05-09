package com.yumu.hexie.integration.community.req;

import sun.nio.cs.ext.Big5;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-22 16:26
 */
public class RefundInfoReq {
    private String groupId;
    private String orderId;
    private BigDecimal refundAmt;
    private List<String> goodsIds;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    public List<String> getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(List<String> goodsIds) {
        this.goodsIds = goodsIds;
    }

    @Override
    public String toString() {
        return "RefundInfoReq{" +
                "groupId='" + groupId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", refundAmt=" + refundAmt +
                ", goodsIds=" + goodsIds +
                '}';
    }
}
