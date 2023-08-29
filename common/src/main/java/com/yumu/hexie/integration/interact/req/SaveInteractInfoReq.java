package com.yumu.hexie.integration.interact.req;

import com.yumu.hexie.integration.common.ServiceOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2023 8月 星期五
 * @Desc :
 */
public class SaveInteractInfoReq {

    private static final Logger logger = LoggerFactory.getLogger(SaveInteractInfoReq.class);

    private String ex_title; //标题
    private String ex_content; //内容
    private String ex_type; //互动类型
    private String user_id; //公众号用户ID
    private String user_name; //用户名称
    private String user_head; //用户头像
    private String user_mobile; //用户手机号
    private String user_address; //用户地址
    private String openid; //公众号openid
    private String appid; //公众号appid
    private String upload_pic_urls; //上传图片原图
    private String attachment_urls; //上传图片缩略图
    private String ex_source; //数据来源
    private String ex_group; //互动归类
    private String sect_id; //物业项目ID
    private String mng_cell_id; //房屋ID

    public String getEx_title() {
        return ex_title;
    }

    public void setEx_title(String ex_title) {
        this.ex_title = ex_title;
    }

    public String getEx_content() {
        return ex_content;
    }

    public void setEx_content(String ex_content) {
        if(!StringUtils.isEmpty(ex_content)) {
            try {
                ex_content = URLEncoder.encode(ex_content, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.ex_content = ex_content;
    }

    public String getEx_type() {
        return ex_type;
    }

    public void setEx_type(String ex_type) {
        this.ex_type = ex_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        if(!StringUtils.isEmpty(user_name)) {
            try {
                user_name = URLEncoder.encode(user_name, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.user_name = user_name;
    }

    public String getUser_head() {
        return user_head;
    }

    public void setUser_head(String user_head) {
        this.user_head = user_head;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        if(!StringUtils.isEmpty(user_address)) {
            try {
                user_address = URLEncoder.encode(user_address, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.user_address = user_address;
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

    public String getUpload_pic_urls() {
        return upload_pic_urls;
    }

    public void setUpload_pic_urls(String upload_pic_urls) {
        this.upload_pic_urls = upload_pic_urls;
    }

    public String getAttachment_urls() {
        return attachment_urls;
    }

    public void setAttachment_urls(String attachment_urls) {
        this.attachment_urls = attachment_urls;
    }

    public String getEx_source() {
        return ex_source;
    }

    public void setEx_source(String ex_source) {
        this.ex_source = ex_source;
    }

    public String getEx_group() {
        return ex_group;
    }

    public void setEx_group(String ex_group) {
        this.ex_group = ex_group;
    }

    public String getSect_id() {
        return sect_id;
    }

    public void setSect_id(String sect_id) {
        this.sect_id = sect_id;
    }

    public String getMng_cell_id() {
        return mng_cell_id;
    }

    public void setMng_cell_id(String mng_cell_id) {
        this.mng_cell_id = mng_cell_id;
    }

    @Override
    public String toString() {
        return "SaveInteractInfoReq{" +
                "ex_title='" + ex_title + '\'' +
                ", ex_content='" + ex_content + '\'' +
                ", ex_type='" + ex_type + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_head='" + user_head + '\'' +
                ", user_mobile='" + user_mobile + '\'' +
                ", user_address='" + user_address + '\'' +
                ", openid='" + openid + '\'' +
                ", appid='" + appid + '\'' +
                ", upload_pic_urls='" + upload_pic_urls + '\'' +
                ", attachment_urls='" + attachment_urls + '\'' +
                ", ex_source='" + ex_source + '\'' +
                ", ex_group='" + ex_group + '\'' +
                ", sect_id='" + sect_id + '\'' +
                ", mng_cell_id='" + mng_cell_id + '\'' +
                '}';
    }
}
