package com.yumu.hexie.integration.park.req;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-30 16:30
 */
public class PayUserCarInfo {
    private String scanChannel;
    private String appid;
    private String user_id;
    private String openid;
    private String car_no;
    private String park_id;
    private String channel_id;
    private String record_id;
    private String query_type; //查询方式，1.按账单查询 2.按月数查询
    private int pay_months = 0; //缴月份数
    private String pay_scenarios; //支付场景
    private String device_order_id; //道闸订单号

    public String getScanChannel() {
        return scanChannel;
    }

    public void setScanChannel(String scanChannel) {
        this.scanChannel = scanChannel;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        if (!StringUtils.isEmpty(car_no)) {
            try {
                car_no = URLEncoder.encode(car_no,"GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        this.car_no = car_no;
    }

    public String getPark_id() {
        return park_id;
    }

    public void setPark_id(String park_id) {
        this.park_id = park_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getQuery_type() {
        return query_type;
    }

    public void setQuery_type(String query_type) {
        this.query_type = query_type;
    }

    public int getPay_months() {
        return pay_months;
    }

    public void setPay_months(int pay_months) {
        this.pay_months = pay_months;
    }

    public String getPay_scenarios() {
        return pay_scenarios;
    }

    public void setPay_scenarios(String pay_scenarios) {
        this.pay_scenarios = pay_scenarios;
    }

    public String getDevice_order_id() {
        return device_order_id;
    }

    public void setDevice_order_id(String device_order_id) {
        this.device_order_id = device_order_id;
    }

    @Override
    public String toString() {
        return "PayUserCarInfo{" +
                "scanChannel='" + scanChannel + '\'' +
                ", appid='" + appid + '\'' +
                ", user_id='" + user_id + '\'' +
                ", openid='" + openid + '\'' +
                ", car_no='" + car_no + '\'' +
                ", park_id='" + park_id + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", record_id='" + record_id + '\'' +
                ", query_type='" + query_type + '\'' +
                ", pay_months=" + pay_months +
                ", pay_scenarios='" + pay_scenarios + '\'' +
                ", device_order_id='" + device_order_id + '\'' +
                '}';
    }
}
