package com.yumu.hexie.integration.community.resp;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-11 10:22
 */
public class AccountSurplusVO {

    private BigDecimal surplusAmt; //可用余额
    private BigDecimal minAmt; //允许提现的最小金额

    private List<BankInfo> bankInfoList;

    public BigDecimal getSurplusAmt() {
        return surplusAmt;
    }

    public void setSurplusAmt(BigDecimal surplusAmt) {
        this.surplusAmt = surplusAmt;
    }

    public BigDecimal getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(BigDecimal minAmt) {
        this.minAmt = minAmt;
    }

    public List<BankInfo> getBankInfoList() {
        return bankInfoList;
    }

    public void setBankInfoList(List<BankInfo> bankInfoList) {
        this.bankInfoList = bankInfoList;
    }

    public static class BankInfo {
        private String bankNo;
        private String bankName; //开户行
        private String ownerName; //持卡人
        private String branchBank;

        public String getBankNo() {
            return bankNo;
        }

        public void setBankNo(String bankNo) {
            this.bankNo = bankNo;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public String getBranchBank() {
            return branchBank;
        }

        public void setBranchBank(String branchBank) {
            this.branchBank = branchBank;
        }

        @Override
        public String toString() {
            return "BankInfo{" +
                    "bankNo='" + bankNo + '\'' +
                    ", bankName='" + bankName + '\'' +
                    ", ownerName='" + ownerName + '\'' +
                    ", branchBank='" + branchBank + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AccountSurplusVO{" +
                "surplusAmt=" + surplusAmt +
                ", minAmt=" + minAmt +
                ", bankInfoList=" + bankInfoList +
                '}';
    }
}
