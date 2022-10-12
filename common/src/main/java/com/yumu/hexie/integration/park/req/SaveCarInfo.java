package com.yumu.hexie.integration.park.req;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-09-03 15:40
 */
public class SaveCarInfo {
    private String carNo;
    private String checked;
    private String user_id;
    private String appid;

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        if (!StringUtils.isEmpty(carNo)) {
            try {
                carNo = URLEncoder.encode(carNo,"GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        this.carNo = carNo;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
}
