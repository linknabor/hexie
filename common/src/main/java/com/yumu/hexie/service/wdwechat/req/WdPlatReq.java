package com.yumu.hexie.service.wdwechat.req;

import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.RSAUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWd;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 6月 星期五
 * @Desc :
 */
public class WdPlatReq {
    private String type = "";
    private String platform = "";
    private String socialite_code = "";
    private String socialite_name = "";
    private String socialite_type = "";
    private String openid = "";
    private String unionid = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSocialite_code() {
        return socialite_code;
    }

    public void setSocialite_code(String socialite_code) {
        this.socialite_code = socialite_code;
    }

    public String getSocialite_name() {
        return socialite_name;
    }

    public void setSocialite_name(String socialite_name) {
        this.socialite_name = socialite_name;
    }

    public String getSocialite_type() {
        return socialite_type;
    }

    public void setSocialite_type(String socialite_type) {
        this.socialite_type = socialite_type;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        openid = "";
        try {
            openid = RSAUtil.encrypt(openid, ConstantWd.PUBLIC_KEY);
        } catch (Exception e) {

        }
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        unionid = "";
        try {
            unionid = RSAUtil.encrypt(unionid, ConstantWd.PUBLIC_KEY);
        } catch (Exception e) {

        }
        this.unionid = unionid;
    }
}
