package com.yumu.hexie.integration.interact.resp;

import java.util.List;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2023 8月 星期五
 * @Desc :
 */
public class InteractCommentResp {

    private String comment_id; //回复ID
    private String interact_id; //信息ID
    private String comment_content; //回复内容
    private String comment_date; //回复日期
    private String comment_time; //回复时间
    private String comment_user_id; //回复人userid
    private String comment_user_name; //回复人名称
    private String comment_user_head; //回复人头像
    private String to_user_id; //回复对象userid
    private String to_user_name; //回复对象名称
    private String to_user_head; //回复对象头像
    private String attachment_urls; //回复图片
    private String is_manage_comment; //是否是物业人员回复

    private String isCommentOwner = "false"; //是否自己回复
    private String fmtCommentDateTime;
    private List<String> thumbnailLink;	//上传图片的缩略图
    private List<String> imgUrlLink;	//上传图片的原图链接

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

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
        this.comment_content = comment_content;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
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
        this.comment_user_name = comment_user_name;
    }

    public String getComment_user_head() {
        return comment_user_head;
    }

    public void setComment_user_head(String comment_user_head) {
        this.comment_user_head = comment_user_head;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTo_user_name() {
        return to_user_name;
    }

    public void setTo_user_name(String to_user_name) {
        this.to_user_name = to_user_name;
    }

    public String getTo_user_head() {
        return to_user_head;
    }

    public void setTo_user_head(String to_user_head) {
        this.to_user_head = to_user_head;
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

    public String getIsCommentOwner() {
        return isCommentOwner;
    }

    public void setIsCommentOwner(String isCommentOwner) {
        this.isCommentOwner = isCommentOwner;
    }

    public String getFmtCommentDateTime() {
        return fmtCommentDateTime;
    }

    public void setFmtCommentDateTime(String fmtCommentDateTime) {
        this.fmtCommentDateTime = fmtCommentDateTime;
    }

    public List<String> getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(List<String> thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public List<String> getImgUrlLink() {
        return imgUrlLink;
    }

    public void setImgUrlLink(List<String> imgUrlLink) {
        this.imgUrlLink = imgUrlLink;
    }

    @Override
    public String toString() {
        return "InteractCommentResp{" +
                "comment_id='" + comment_id + '\'' +
                ", interact_id='" + interact_id + '\'' +
                ", comment_content='" + comment_content + '\'' +
                ", comment_date='" + comment_date + '\'' +
                ", comment_time='" + comment_time + '\'' +
                ", comment_user_id='" + comment_user_id + '\'' +
                ", comment_user_name='" + comment_user_name + '\'' +
                ", comment_user_head='" + comment_user_head + '\'' +
                ", to_user_id='" + to_user_id + '\'' +
                ", to_user_name='" + to_user_name + '\'' +
                ", to_user_head='" + to_user_head + '\'' +
                ", attachment_urls='" + attachment_urls + '\'' +
                ", is_manage_comment='" + is_manage_comment + '\'' +
                ", isCommentOwner='" + isCommentOwner + '\'' +
                ", fmtCommentDateTime='" + fmtCommentDateTime + '\'' +
                ", thumbnailLink=" + thumbnailLink +
                ", imgUrlLink=" + imgUrlLink +
                '}';
    }
}
