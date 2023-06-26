package com.yumu.hexie.service.wdwechat.req;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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
    private String uniqueCode; //会员平台唯一标识

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
        if(StringUtils.hasText(phone)) { //先解码
            try {
                phone = URLDecoder.decode(phone, "UTF-8");
            } catch (Exception ignored) {

            }
        }
        this.phone = phone;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        if(StringUtils.hasText(uniqueCode)) {
            try {
                uniqueCode = URLDecoder.decode(uniqueCode, "UTF-8");
            } catch (Exception ignored) {

            }
        }
        this.uniqueCode = uniqueCode;
    }

    @Override
    public String toString() {
        return "WdCenterReq{" +
                "time='" + time + '\'' +
                ", phone='" + phone + '\'' +
                ", sign='" + sign + '\'' +
                ", uniqueCode='" + uniqueCode + '\'' +
                '}';
    }
}
