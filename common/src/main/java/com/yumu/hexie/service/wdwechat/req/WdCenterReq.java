package com.yumu.hexie.service.wdwechat.req;

import java.io.Serializable;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 5月 星期二
 * @Desc :
 */
public class WdCenterReq implements Serializable {
    private String time;
    private String phone;
    private String sign;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "WdCenterReq{" +
                "time='" + time + '\'' +
                ", phone='" + phone + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
