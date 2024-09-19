package com.yumu.hexie.integration.park.resp;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-30 16:12
 */
public class PayingDetail {
    private String record_id; //进出记录ID
    private String car_no; //车牌号
    private String in_time; //进场时间
    private String park_time; //停车时长
    private String park_name; //停车场名称
    private String tot_amt; //总应收金额
    private String fee_amt; //应付金额
    private String already_amt; //已付金额
    private String pay_prompt; //支付提示语
    private String cust_tel; //客服电话
    private String out_park_prompt; //出场提示语
    private String refresh_time; //刷新间隔
    private String device_order_id; //道闸订单号

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

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

    public String getTot_amt() {
        return tot_amt;
    }

    public void setTot_amt(String tot_amt) {
        this.tot_amt = tot_amt;
    }

    public String getFee_amt() {
        return fee_amt;
    }

    public void setFee_amt(String fee_amt) {
        this.fee_amt = fee_amt;
    }

    public String getAlready_amt() {
        return already_amt;
    }

    public void setAlready_amt(String already_amt) {
        this.already_amt = already_amt;
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

    public String getDevice_order_id() {
        return device_order_id;
    }

    public void setDevice_order_id(String device_order_id) {
        this.device_order_id = device_order_id;
    }

    @Override
    public String toString() {
        return "PayingDetail{" +
                "record_id='" + record_id + '\'' +
                ", car_no='" + car_no + '\'' +
                ", in_time='" + in_time + '\'' +
                ", park_time='" + park_time + '\'' +
                ", park_name='" + park_name + '\'' +
                ", tot_amt='" + tot_amt + '\'' +
                ", fee_amt='" + fee_amt + '\'' +
                ", already_amt='" + already_amt + '\'' +
                ", pay_prompt='" + pay_prompt + '\'' +
                ", cust_tel='" + cust_tel + '\'' +
                ", out_park_prompt='" + out_park_prompt + '\'' +
                ", refresh_time='" + refresh_time + '\'' +
                ", device_order_id='" + device_order_id + '\'' +
                '}';
    }
}
