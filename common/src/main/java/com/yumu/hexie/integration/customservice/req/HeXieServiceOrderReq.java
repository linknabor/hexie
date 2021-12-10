package com.yumu.hexie.integration.customservice.req;

import com.yumu.hexie.model.ModelConstant;

import java.util.Date;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-11-30 13:22
 */
public class HeXieServiceOrderReq {
    private int status = ModelConstant.ORDER_STATUS_INIT;//0. 创建完成 1. 已支付 2. 已用户取消 3. 待退款 4. 已退款  5. 已使用/已发货 6.已签收 7. 已后台取消 8. 商户取消
    private String operatorName;
    private long operatorUserId;
    private String operatorTel;
    private String operatorOpenId;
    private String confirmer;		//确认完工人
    private Date acceptedDate;	//接单日期
    private Date confirmDate;	//确认完工日期

    private String orderNo; //订单号

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public long getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(long operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getOperatorTel() {
        return operatorTel;
    }

    public void setOperatorTel(String operatorTel) {
        this.operatorTel = operatorTel;
    }

    public String getOperatorOpenId() {
        return operatorOpenId;
    }

    public void setOperatorOpenId(String operatorOpenId) {
        this.operatorOpenId = operatorOpenId;
    }

    public String getConfirmer() {
        return confirmer;
    }

    public void setConfirmer(String confirmer) {
        this.confirmer = confirmer;
    }

    public Date getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(Date acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "HeXieServiceOrderReq{" +
                "status=" + status +
                ", operatorName='" + operatorName + '\'' +
                ", operatorUserId=" + operatorUserId +
                ", operatorTel='" + operatorTel + '\'' +
                ", operatorOpenId='" + operatorOpenId + '\'' +
                ", confirmer='" + confirmer + '\'' +
                ", acceptedDate=" + acceptedDate +
                ", confirmDate=" + confirmDate +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }
}
