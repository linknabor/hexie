package com.yumu.hexie.integration.community.resp;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-08 15:47
 */
public class AccountInfoVO {

    private String tipsDesc; //底部提示语
    private String amount; //账户余额
    private String totalAmt; //账户总金额
    private List<String> tipsContent; //底部提示语

    public String getTipsDesc() {
        return tipsDesc;
    }

    public void setTipsDesc(String tipsDesc) {
        this.tipsDesc = tipsDesc;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<String> getTipsContent() {
        return tipsContent;
    }

    public void setTipsContent(List<String> tipsContent) {
        this.tipsContent = tipsContent;
    }

    public String getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(String totalAmt) {
        this.totalAmt = totalAmt;
    }
}
