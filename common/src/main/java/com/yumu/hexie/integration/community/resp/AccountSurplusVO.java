package com.yumu.hexie.integration.community.resp;

import java.math.BigDecimal;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-11 10:22
 */
public class AccountSurplusVO {

    private BigDecimal surplusAmt; //可用余额
    private String bankName; //开户行
    private String bankNo; //卡号
    private String ownerName; //持卡人
    private BigDecimal minAmt; //允许提现的最小金额

    public BigDecimal getSurplusAmt() {
        return surplusAmt;
    }

    public void setSurplusAmt(BigDecimal surplusAmt) {
        this.surplusAmt = surplusAmt;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(BigDecimal minAmt) {
        this.minAmt = minAmt;
    }

    @Override
    public String toString() {
        return "AccountSurplusVO{" +
                "surplusAmt=" + surplusAmt +
                ", bankName='" + bankName + '\'' +
                ", bankNo='" + bankNo + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", minAmt='" + minAmt + '\'' +
                '}';
    }
}
