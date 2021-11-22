package com.yumu.hexie.service.sales.req;

import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-11-11 16:42
 */
public class NoticeServiceOperator implements Serializable {

    private static final long serialVersionUID = -8690857153975384483L;

    private List<Long> opers;

    private Long createDate;
    private String receiverName;
    private Long id;
    private String address;
    private String productName;

    private int orderType;

    public NoticeServiceOperator() {

    }

    public List<Long> getOpers() {
        return opers;
    }

    public void setOpers(List<Long> opers) {
        this.opers = opers;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "NoticeServiceOperator{" +
                "opers=" + opers +
                ", createDate=" + createDate +
                ", receiverName='" + receiverName + '\'' +
                ", id=" + id +
                ", address='" + address + '\'' +
                ", productName='" + productName + '\'' +
                ", orderType='" + orderType + '\'' +
                '}';
    }
}
