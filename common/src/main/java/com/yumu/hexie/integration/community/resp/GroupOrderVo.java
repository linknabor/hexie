package com.yumu.hexie.integration.community.resp;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述:
 * 团购订单对象
 * @author jackie
 * @create 2022-04-19 15:40
 */
public class GroupOrderVo {
    private String groupNum; //团购号
    private String orderId; //订单ID
    private String orderStatus; //订单状态
    private String userName; //用户名称
    private String userHead; //用户头像
    private String orderDate; //订单日期
    private int orderNum; //购买件数
    private BigDecimal totalAmt; //订单金额

    private String receiverName; //收货人
    private String receiverTel; //收货人电话
    private String receiverAddr; //收货地址
    private String groupDesc; //团购备注
    private String logistics; //物流
    private List<BuyGoodsVo> buyGoodsVoList; //商品列表

    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverTel() {
        return receiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        this.receiverTel = receiverTel;
    }

    public String getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(String receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getLogistics() {
        return logistics;
    }

    public void setLogistics(String logistics) {
        this.logistics = logistics;
    }

    public List<BuyGoodsVo> getBuyGoodsVoList() {
        return buyGoodsVoList;
    }

    public void setBuyGoodsVoList(List<BuyGoodsVo> buyGoodsVoList) {
        this.buyGoodsVoList = buyGoodsVoList;
    }

    @Override
    public String toString() {
        return "GroupOrderVo{" +
                "groupNum='" + groupNum + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", userName='" + userName + '\'' +
                ", userHead='" + userHead + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", orderNum=" + orderNum +
                ", totalAmt=" + totalAmt +
                ", receiverName='" + receiverName + '\'' +
                ", receiverTel='" + receiverTel + '\'' +
                ", receiverAddr='" + receiverAddr + '\'' +
                ", groupDesc='" + groupDesc + '\'' +
                ", logistics='" + logistics + '\'' +
                ", buyGoodsVoList=" + buyGoodsVoList +
                '}';
    }
}
