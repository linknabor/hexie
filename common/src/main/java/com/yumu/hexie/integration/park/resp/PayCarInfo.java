package com.yumu.hexie.integration.park.resp;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-18 18:15
 */
public class PayCarInfo {

    private String trade_id;
    private String start_date;
    private String end_date;
    private String park_name;
    private String car_no;
    private String pay_amt;
    private String park_time;
    private String is_invoice;

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getPark_name() {
        return park_name;
    }

    public void setPark_name(String park_name) {
        this.park_name = park_name;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public String getPay_amt() {
        return pay_amt;
    }

    public void setPay_amt(String pay_amt) {
        this.pay_amt = pay_amt;
    }

    public String getPark_time() {
        return park_time;
    }

    public void setPark_time(String park_time) {
        this.park_time = park_time;
    }

    public String getIs_invoice() {
        return is_invoice;
    }

    public void setIs_invoice(String is_invoice) {
        this.is_invoice = is_invoice;
    }

    @Override
    public String toString() {
        return "PayCarInfo{" +
                "trade_id='" + trade_id + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", park_name='" + park_name + '\'' +
                ", car_no='" + car_no + '\'' +
                ", pay_amt='" + pay_amt + '\'' +
                ", park_time='" + park_time + '\'' +
                ", is_invoice='" + is_invoice + '\'' +
                '}';
    }
}
