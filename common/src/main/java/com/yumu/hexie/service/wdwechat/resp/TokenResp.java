package com.yumu.hexie.service.wdwechat.resp;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 5月 星期二
 * @Desc :
 */
public class TokenResp {
    private String token; //token
    private String expire_time; //过期时间 2023-05-20 12:12:12

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(String expire_time) {
        this.expire_time = expire_time;
    }
}
