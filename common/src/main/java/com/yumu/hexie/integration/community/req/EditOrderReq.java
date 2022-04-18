package com.yumu.hexie.integration.community.req;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-18 21:23
 */
public class EditOrderReq {
    private String orderId;
    private String recvName;
    private String recvTel;
    private String recvAddr;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRecvName() {
        return recvName;
    }

    public void setRecvName(String recvName) {
        this.recvName = recvName;
    }

    public String getRecvTel() {
        return recvTel;
    }

    public void setRecvTel(String recvTel) {
        this.recvTel = recvTel;
    }

    public String getRecvAddr() {
        return recvAddr;
    }

    public void setRecvAddr(String recvAddr) {
        this.recvAddr = recvAddr;
    }

    @Override
    public String toString() {
        return "EditOrderReq{" +
                "orderId='" + orderId + '\'' +
                ", recvName='" + recvName + '\'' +
                ", recvTel='" + recvTel + '\'' +
                ", recvAddr='" + recvAddr + '\'' +
                '}';
    }
}
