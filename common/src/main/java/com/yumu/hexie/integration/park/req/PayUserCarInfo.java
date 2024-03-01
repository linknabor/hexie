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
    private String appid;
    private String user_id;
    private String openid;
    private String car_no;
    private String park_id;
    private String record_id;

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

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    @Override
    public String toString() {
        return "PayUserCarInfo{" +
                "appid='" + appid + '\'' +
                ", user_id='" + user_id + '\'' +
                ", openid='" + openid + '\'' +
                ", car_no='" + car_no + '\'' +
                ", park_id='" + park_id + '\'' +
                ", record_id='" + record_id + '\'' +
                '}';
    }
}
