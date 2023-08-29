package com.yumu.hexie.integration.interact.resp;

import java.util.List;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2023 8月 星期五
 * @Desc :
 */
public class InteractInfoResp {
    private String interact_id; //ID
    private String ex_title; //标题
    private String ex_content; //内容
    private String ex_type; //互动类型
    private String create_date; //创建日期
    private String create_time; //创建时间
    private String user_id; //公众号用户ID
    private String user_name; //用户名称
    private String user_head; //用户头像
    private String sect_name; //小区名称
    private String user_address; //用户地址
    private String comments_count; //评论数量
    private String remark; //备注
    private String attachment_urls; //上传图片缩略图
    private String ex_group; //互动归类
    private String sect_id; //物业项目ID
    private String grade; //是否满意
    private String feedback; //不满意原因

    private String formattedDateTime;	//时间。格式为：xx秒前，xx分钟前
    private List<String> imgUrlLink;	//上传图片的原图链接
    private List<String> thumbnailLink;	//上传图片的缩略图
    private String isThreadOwner = "false"; //所有人

    public String getInteract_id() {
        return interact_id;
    }

    public void setInteract_id(String interact_id) {
        this.interact_id = interact_id;
    }

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
        this.ex_content = ex_content;
    }

    public String getEx_type() {
        return ex_type;
    }

    public void setEx_type(String ex_type) {
        this.ex_type = ex_type;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
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
        this.user_name = user_name;
    }

    public String getUser_head() {
        return user_head;
    }

    public void setUser_head(String user_head) {
        this.user_head = user_head;
    }

    public String getSect_name() {
        return sect_name;
    }

    public void setSect_name(String sect_name) {
        this.sect_name = sect_name;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAttachment_urls() {
        return attachment_urls;
    }

    public void setAttachment_urls(String attachment_urls) {
        this.attachment_urls = attachment_urls;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFormattedDateTime() {
        return formattedDateTime;
    }

    public void setFormattedDateTime(String formattedDateTime) {
        this.formattedDateTime = formattedDateTime;
    }

    public List<String> getImgUrlLink() {
        return imgUrlLink;
    }

    public void setImgUrlLink(List<String> imgUrlLink) {
        this.imgUrlLink = imgUrlLink;
    }

    public List<String> getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(List<String> thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public String getIsThreadOwner() {
        return isThreadOwner;
    }

    public void setIsThreadOwner(String isThreadOwner) {
        this.isThreadOwner = isThreadOwner;
    }

    @Override
    public String toString() {
        return "InteractInfoResp{" +
                "interact_id='" + interact_id + '\'' +
                ", ex_title='" + ex_title + '\'' +
                ", ex_content='" + ex_content + '\'' +
                ", ex_type='" + ex_type + '\'' +
                ", create_date='" + create_date + '\'' +
                ", create_time='" + create_time + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_head='" + user_head + '\'' +
                ", sect_name='" + sect_name + '\'' +
                ", user_address='" + user_address + '\'' +
                ", comments_count='" + comments_count + '\'' +
                ", remark='" + remark + '\'' +
                ", attachment_urls='" + attachment_urls + '\'' +
                ", ex_group='" + ex_group + '\'' +
                ", sect_id='" + sect_id + '\'' +
                ", formattedDateTime='" + formattedDateTime + '\'' +
                ", imgUrlLink=" + imgUrlLink +
                ", thumbnailLink=" + thumbnailLink +
                ", isThreadOwner='" + isThreadOwner + '\'' +
                '}';
    }
}
