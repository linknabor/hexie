package com.yumu.hexie.service.user.dto;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 8月 星期五
 * @Desc :
 */
public class H5AuthorizeVo {
    private String appid;
    private String sourceType;
    private String code;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
