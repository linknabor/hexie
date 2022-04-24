package com.yumu.hexie.integration.community.resp;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-19 15:37
 */
public class GroupInfoVo implements Serializable {
    private BigInteger id; //团购ID
    private BigInteger createDate; //创建时间
    private String description; //团购名称
    private Timestamp startDate;
    private Timestamp endDate;
    private Float price; //团购金额
    private Integer status; //上下架状态
    private Integer groupStatus; //团购状态

    private String groupStatusCn; //团购状态
    private String groupDate; //团购创建日期转换
    private Float realityAmt; //已支付金额
    private Float refundAmt; //退款金额
    private int followNum; //跟团人数
    private int cancelNum; //取消人数
    private int queryNum; //查看人数

    public GroupInfoVo(BigInteger id, BigInteger createDate, String description, Timestamp startDate, Timestamp endDate, Float price, Integer status, Integer groupStatus) {
        this.id = id;
        this.createDate = createDate;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.status = status;
        this.groupStatus = groupStatus;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getCreateDate() {
        return createDate;
    }

    public void setCreateDate(BigInteger createDate) {
        this.createDate = createDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
    }

    public String getGroupStatusCn() {
        return groupStatusCn;
    }

    public void setGroupStatusCn(String groupStatusCn) {
        this.groupStatusCn = groupStatusCn;
    }

    public String getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(String groupDate) {
        this.groupDate = groupDate;
    }

    public Float getRealityAmt() {
        return realityAmt;
    }

    public void setRealityAmt(Float realityAmt) {
        this.realityAmt = realityAmt;
    }

    public Float getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(Float refundAmt) {
        this.refundAmt = refundAmt;
    }

    public int getFollowNum() {
        return followNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public int getCancelNum() {
        return cancelNum;
    }

    public void setCancelNum(int cancelNum) {
        this.cancelNum = cancelNum;
    }

    public int getQueryNum() {
        return queryNum;
    }

    public void setQueryNum(int queryNum) {
        this.queryNum = queryNum;
    }

    @Override
    public String toString() {
        return "GroupInfoVo{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", price=" + price +
                ", status=" + status +
                ", groupStatus=" + groupStatus +
                ", groupStatusCn='" + groupStatusCn + '\'' +
                ", groupDate='" + groupDate + '\'' +
                ", realityAmt=" + realityAmt +
                ", refundAmt=" + refundAmt +
                ", followNum=" + followNum +
                ", cancelNum=" + cancelNum +
                ", queryNum=" + queryNum +
                '}';
    }
}
