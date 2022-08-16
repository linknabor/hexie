package com.yumu.hexie.integration.community.resp;

import org.springframework.util.StringUtils;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-05-10 22:24
 */
public class AccountBankResp {
    private String bank_no;
    private String bank_name;
    private String branch_bank;
    private String show_bank_no;
    public String getBank_no() {
        return bank_no;
    }

    public void setBank_no(String bank_no) {
        this.bank_no = bank_no;
        if(!StringUtils.isEmpty(bank_no)) {
            if(bank_no.length() > 4) {
                this.show_bank_no = "**** **** **** " + bank_no.substring(bank_no.length()-4);
            } else {
                this.show_bank_no = bank_no;
            }

        }
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBranch_bank() {
        return branch_bank;
    }

    public void setBranch_bank(String branch_bank) {
        this.branch_bank = branch_bank;
    }

    public String getShow_bank_no() {
        return show_bank_no;
    }

    public void setShow_bank_no(String show_bank_no) {
        this.show_bank_no = show_bank_no;
    }

    @Override
    public String toString() {
        return "AccountBankResp{" +
                "bank_no='" + bank_no + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", branch_bank='" + branch_bank + '\'' +
                ", show_bank_no='" + show_bank_no + '\'' +
                '}';
    }
}
