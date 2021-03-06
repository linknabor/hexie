package com.yumu.hexie.service.billpush.vo;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-02-22 14:25
 */
public class BillPushDetail implements Serializable {

    private String sectId;
    private String sectName;
    private String wuyeId;
    private String period;
    private String feePrice;
    private String cellAddr;
    private String showFirstMsg;
    private String appid;
    private String remark;

    public String getSectId() {
        return sectId;
    }

    public void setSectId(String sectId) {
        this.sectId = sectId;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
    }

    public String getWuyeId() {
        return wuyeId;
    }

    public void setWuyeId(String wuyeId) {
        this.wuyeId = wuyeId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getFeePrice() {
        return feePrice;
    }

    public void setFeePrice(String feePrice) {
        this.feePrice = feePrice;
    }

    public String getCellAddr() {
        return cellAddr;
    }

    public void setCellAddr(String cellAddr) {
        this.cellAddr = cellAddr;
    }

    public String getShowFirstMsg() {
        return showFirstMsg;
    }

    public void setShowFirstMsg(String showFirstMsg) {
        this.showFirstMsg = showFirstMsg;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BillPushDetail{" +
                "sectId='" + sectId + '\'' +
                ", sectName='" + sectName + '\'' +
                ", wuyeId='" + wuyeId + '\'' +
                ", period='" + period + '\'' +
                ", feePrice='" + feePrice + '\'' +
                ", cellAddr='" + cellAddr + '\'' +
                ", showFirstMsg='" + showFirstMsg + '\'' +
                ", appid='" + appid + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
