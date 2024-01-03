package com.yumu.hexie.integration.interact.req;

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
public class SaveInteractCommentReq {
    private static final Logger logger = LoggerFactory.getLogger(SaveInteractCommentReq.class);

    private String interact_id; //主信息ID
    private String comment_content; //回复内容
    private String comment_user_id; //回复人userid
    private String comment_user_name; //回复人名称
    private String comment_user_head; //回复人头像
    private String attachment_urls; //回复图片图
    private String is_manage_comment = "0"; //是否是物业人员回复

    public String getInteract_id() {
        return interact_id;
    }

    public void setInteract_id(String interact_id) {
        this.interact_id = interact_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        if(!StringUtils.isEmpty(comment_content)) {
            try {
                comment_content = URLEncoder.encode(comment_content, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.comment_content = comment_content;
    }

    public String getComment_user_id() {
        return comment_user_id;
    }

    public void setComment_user_id(String comment_user_id) {
        this.comment_user_id = comment_user_id;
    }

    public String getComment_user_name() {
        return comment_user_name;
    }

    public void setComment_user_name(String comment_user_name) {
        if(!StringUtils.isEmpty(comment_user_name)) {
            try {
                comment_user_name = URLEncoder.encode(comment_user_name, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.comment_user_name = comment_user_name;
    }

    public String getComment_user_head() {
        return comment_user_head;
    }

    public void setComment_user_head(String comment_user_head) {
        this.comment_user_head = comment_user_head;
    }

    public String getAttachment_urls() {
        return attachment_urls;
    }

    public void setAttachment_urls(String attachment_urls) {
        this.attachment_urls = attachment_urls;
    }

    public String getIs_manage_comment() {
        return is_manage_comment;
    }

    public void setIs_manage_comment(String is_manage_comment) {
        this.is_manage_comment = is_manage_comment;
    }

    @Override
    public String toString() {
        return "SaveInteractCommentReq{" +
                "interact_id='" + interact_id + '\'' +
                ", comment_content='" + comment_content + '\'' +
                ", comment_user_id='" + comment_user_id + '\'' +
                ", comment_user_name='" + comment_user_name + '\'' +
                ", comment_user_head='" + comment_user_head + '\'' +
                ", attachment_urls='" + attachment_urls + '\'' +
                ", is_manage_comment='" + is_manage_comment + '\'' +
                '}';
    }
}
