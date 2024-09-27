package com.yumu.hexie.integration.notify;

import java.io.Serializable;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 4月 星期二
 * @Desc :
 */
public class CcBindHouseNotification implements Serializable {

    private String appid;
    private String app_type;
    private String openid;
    private String unionid;
    private String phone;
    private String csp_id;
    private String sect_id;
    private String house_id;
    private String area;
    private String data_type;
    private String sect_name;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCsp_id() {
        return csp_id;
    }

    public void setCsp_id(String csp_id) {
        this.csp_id = csp_id;
    }

    public String getSect_id() {
        return sect_id;
    }

    public void setSect_id(String sect_id) {
        this.sect_id = sect_id;
    }

    public String getHouse_id() {
        return house_id;
    }

    public void setHouse_id(String house_id) {
        this.house_id = house_id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getSect_name() {
        return sect_name;
    }

    public void setSect_name(String sect_name) {
        this.sect_name = sect_name;
    }

    @Override
    public String toString() {
        return "CcBindHouseNotification{" +
                "appid='" + appid + '\'' +
                ", app_type='" + app_type + '\'' +
                ", openid='" + openid + '\'' +
                ", unionid='" + unionid + '\'' +
                ", phone='" + phone + '\'' +
                ", csp_id='" + csp_id + '\'' +
                ", sect_id='" + sect_id + '\'' +
                ", house_id='" + house_id + '\'' +
                ", area='" + area + '\'' +
                ", data_type='" + data_type + '\'' +
                ", sect_name='" + sect_name + '\'' +
                '}';
    }
}
