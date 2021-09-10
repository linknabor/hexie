package com.yumu.hexie.integration.park.resp;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-30 16:12
 */
public class PayingDetail {

    private String car_no; //车牌号
    private String in_time; //进场时间
    private String park_time; //停车时长
    private String park_name; //停车场名称
    private String fee_amt; //应付金额
    private String pay_prompt; //支付提示语
    private String cust_tel; //客服电话
    private String out_park_prompt; //出场提示语
    private String refresh_time; //刷新间隔

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public String getIn_time() {
        return in_time;
    }

    public void setIn_time(String in_time) {
        this.in_time = in_time;
    }

    public String getPark_time() {
        return park_time;
    }

    public void setPark_time(String park_time) {
        this.park_time = park_time;
    }

    public String getPark_name() {
        return park_name;
    }

    public void setPark_name(String park_name) {
        this.park_name = park_name;
    }

    public String getFee_amt() {
        return fee_amt;
    }

    public void setFee_amt(String fee_amt) {
        this.fee_amt = fee_amt;
    }

    public String getPay_prompt() {
        return pay_prompt;
    }

    public void setPay_prompt(String pay_prompt) {
        this.pay_prompt = pay_prompt;
    }

    public String getCust_tel() {
        return cust_tel;
    }

    public void setCust_tel(String cust_tel) {
        this.cust_tel = cust_tel;
    }

    public String getOut_park_prompt() {
        return out_park_prompt;
    }

    public void setOut_park_prompt(String out_park_prompt) {
        this.out_park_prompt = out_park_prompt;
    }

    public String getRefresh_time() {
        return refresh_time;
    }

    public void setRefresh_time(String refresh_time) {
        this.refresh_time = refresh_time;
    }
}
