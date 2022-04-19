package com.yumu.hexie.integration.community.resp;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-19 15:37
 */
public class GroupInfoVo {
    private String groupId; //团购ID
    private String groupName; //团购名称
    private String groupPrice; //团购金额
    private String groupDate; //发布时间
    private String groupStatus; //团购状态
    private String groupStatusCn; //团购状态
    private String realityAmt; //已支付金额
    private String refundAmt; //退款金额
    private String followNum; //跟团人数
    private String cancelNum; //取消人数
    private String queryNum; //查看人数

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(String groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(String groupDate) {
        this.groupDate = groupDate;
    }

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    public String getGroupStatusCn() {
        return groupStatusCn;
    }

    public void setGroupStatusCn(String groupStatusCn) {
        this.groupStatusCn = groupStatusCn;
    }

    public String getRealityAmt() {
        return realityAmt;
    }

    public void setRealityAmt(String realityAmt) {
        this.realityAmt = realityAmt;
    }

    public String getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(String refundAmt) {
        this.refundAmt = refundAmt;
    }

    public String getFollowNum() {
        return followNum;
    }

    public void setFollowNum(String followNum) {
        this.followNum = followNum;
    }

    public String getCancelNum() {
        return cancelNum;
    }

    public void setCancelNum(String cancelNum) {
        this.cancelNum = cancelNum;
    }

    public String getQueryNum() {
        return queryNum;
    }

    public void setQueryNum(String queryNum) {
        this.queryNum = queryNum;
    }

    @Override
    public String toString() {
        return "GroupInfoListResp{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupPrice='" + groupPrice + '\'' +
                ", groupDate='" + groupDate + '\'' +
                ", groupStatus='" + groupStatus + '\'' +
                ", groupStatusCn='" + groupStatusCn + '\'' +
                ", realityAmt='" + realityAmt + '\'' +
                ", refundAmt='" + refundAmt + '\'' +
                ", followNum='" + followNum + '\'' +
                ", cancelNum='" + cancelNum + '\'' +
                ", queryNum='" + queryNum + '\'' +
                '}';
    }
}
