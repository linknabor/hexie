package com.yumu.hexie.integration.park.resp;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-09-06 13:21
 */
public class PayDetail {
    private String order_id;
    private String car_no;
    private String park_name;
    private String in_park_time;
    private String out_park_time;
    private String park_time;
    private String tran_date;
    private String acct_date;
    private String tran_amt;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public String getIn_park_time() {
        return in_park_time;
    }

    public void setIn_park_time(String in_park_time) {
        this.in_park_time = in_park_time;
    }

    public String getOut_park_time() {
        return out_park_time;
    }

    public void setOut_park_time(String out_park_time) {
        this.out_park_time = out_park_time;
    }

    public String getPark_time() {
        return park_time;
    }

    public void setPark_time(String park_time) {
        this.park_time = park_time;
    }

    public String getTran_date() {
        return tran_date;
    }

    public void setTran_date(String tran_date) {
        this.tran_date = tran_date;
    }

    public String getAcct_date() {
        return acct_date;
    }

    public void setAcct_date(String acct_date) {
        this.acct_date = acct_date;
    }

    public String getPark_name() {
        return park_name;
    }

    public void setPark_name(String park_name) {
        this.park_name = park_name;
    }

    public String getTran_amt() {
        return tran_amt;
    }

    public void setTran_amt(String tran_amt) {
        this.tran_amt = tran_amt;
    }

    @Override
    public String toString() {
        return "PayDetail{" +
                "order_id='" + order_id + '\'' +
                ", car_no='" + car_no + '\'' +
                ", park_name='" + park_name + '\'' +
                ", in_park_time='" + in_park_time + '\'' +
                ", out_park_time='" + out_park_time + '\'' +
                ", park_time='" + park_time + '\'' +
                ", tran_date='" + tran_date + '\'' +
                ", acct_date='" + acct_date + '\'' +
                ", tran_amt='" + tran_amt + '\'' +
                '}';
    }
}
