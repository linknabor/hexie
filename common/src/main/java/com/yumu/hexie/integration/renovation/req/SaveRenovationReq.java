package com.yumu.hexie.integration.renovation.req;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 12月 星期五
 * @Desc :
 */
public class SaveRenovationReq {
    private String register_id;
    private String mng_cell_id;
    private String openid;
    private String appid;
    private String attachment_urls;

    public String getRegister_id() {
        return register_id;
    }

    public void setRegister_id(String register_id) {
        this.register_id = register_id;
    }

    public String getMng_cell_id() {
        return mng_cell_id;
    }

    public void setMng_cell_id(String mng_cell_id) {
        this.mng_cell_id = mng_cell_id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAttachment_urls() {
        return attachment_urls;
    }

    public void setAttachment_urls(String attachment_urls) {
        this.attachment_urls = attachment_urls;
    }

    @Override
    public String toString() {
        return "SaveRenovationReq{" +
                "register_id='" + register_id + '\'' +
                ", mng_cell_id='" + mng_cell_id + '\'' +
                ", openid='" + openid + '\'' +
                ", appid='" + appid + '\'' +
                ", attachment_urls='" + attachment_urls + '\'' +
                '}';
    }
}
