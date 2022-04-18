package com.yumu.hexie.integration.community.resp;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-11 14:09
 */
public class QueryWaterListResp {

    private BigDecimal debitAmt; //借方金额
    private BigDecimal creditAmt; //贷方金额
    private List<WaterList> waterList;

    public static class WaterList {
        private String waterId; //流水号
        private String subjectName; //科目名称
        private String loanFlag; //借贷标志
        private String waterType; //流水类型
        private String orderId; //关联订单号
        private String acctDate; //入账日期
        private String acctTime; //入账时间
        private String tranAmt; //金额

        public String getWaterId() {
            return waterId;
        }

        public void setWaterId(String waterId) {
            this.waterId = waterId;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getLoanFlag() {
            return loanFlag;
        }

        public void setLoanFlag(String loanFlag) {
            this.loanFlag = loanFlag;
        }

        public String getWaterType() {
            return waterType;
        }

        public void setWaterType(String waterType) {
            this.waterType = waterType;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getAcctDate() {
            return acctDate;
        }

        public void setAcctDate(String acctDate) {
            this.acctDate = acctDate;
        }

        public String getAcctTime() {
            return acctTime;
        }

        public void setAcctTime(String acctTime) {
            this.acctTime = acctTime;
        }

        public String getTranAmt() {
            return tranAmt;
        }

        public void setTranAmt(String tranAmt) {
            this.tranAmt = tranAmt;
        }

        @Override
        public String toString() {
            return "QueryWaterListResp{" +
                    "waterId='" + waterId + '\'' +
                    ", subjectName='" + subjectName + '\'' +
                    ", loanFlag='" + loanFlag + '\'' +
                    ", waterType='" + waterType + '\'' +
                    ", orderId='" + orderId + '\'' +
                    ", acctDate='" + acctDate + '\'' +
                    ", acctTime='" + acctTime + '\'' +
                    ", tranAmt='" + tranAmt + '\'' +
                    '}';
        }
    }

    public BigDecimal getDebitAmt() {
        return debitAmt;
    }

    public void setDebitAmt(BigDecimal debitAmt) {
        this.debitAmt = debitAmt;
    }

    public BigDecimal getCreditAmt() {
        return creditAmt;
    }

    public void setCreditAmt(BigDecimal creditAmt) {
        this.creditAmt = creditAmt;
    }

    public List<WaterList> getWaterList() {
        return waterList;
    }

    public void setWaterList(List<WaterList> waterList) {
        this.waterList = waterList;
    }
}
