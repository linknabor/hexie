package com.yumu.hexie.integration.notify;

import java.io.Serializable;
import java.util.List;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 12月 星期三
 * @Desc :
 */
public class RenovationNotification implements Serializable {
    private String registerId;
    private String cellAddr;
    private String content;
    private String status;
    private String appid;
    private String openid;
    private String miniAppid;
    private String miniOpenid;

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public String getCellAddr() {
        return cellAddr;
    }

    public void setCellAddr(String cellAddr) {
        this.cellAddr = cellAddr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getMiniAppid() {
        return miniAppid;
    }

    public void setMiniAppid(String miniAppid) {
        this.miniAppid = miniAppid;
    }

    public String getMiniOpenid() {
        return miniOpenid;
    }

    public void setMiniOpenid(String miniOpenid) {
        this.miniOpenid = miniOpenid;
    }

    @Override
    public String toString() {
        return "RenovationNotification{" +
                "registerId='" + registerId + '\'' +
                ", cellAddr='" + cellAddr + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", appid='" + appid + '\'' +
                ", openid='" + openid + '\'' +
                ", miniAppid='" + miniAppid + '\'' +
                ", miniOpenid='" + miniOpenid + '\'' +
                '}';
    }
}
