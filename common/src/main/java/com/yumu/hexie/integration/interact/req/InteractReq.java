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
public class InteractReq {
    private static final Logger logger = LoggerFactory.getLogger(InteractReq.class);
    private String userId; //用户ID
    private String exGroup; //互动归类 多个以，号分割
    private int curr_page; //分页，当前页数
    private int total_count = 99999; //总条数
    private String appid; //用户所属公众号
    private String interactId; //互动主信息ID
    private String commentId; //回复ID

    private String grade; //评分  0：不满意 1：满意
    private String feedback; //不满意时的描述

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExGroup() {
        return exGroup;
    }

    public void setExGroup(String exGroup) {
        this.exGroup = exGroup;
    }

    public int getCurr_page() {
        return curr_page;
    }

    public void setCurr_page(int curr_page) {
        this.curr_page = curr_page;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getInteractId() {
        return interactId;
    }

    public void setInteractId(String interactId) {
        this.interactId = interactId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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
        if(!StringUtils.isEmpty(feedback)) {
            try {
                feedback = URLEncoder.encode(feedback, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "InteractReq{" +
                "userId='" + userId + '\'' +
                ", exGroup='" + exGroup + '\'' +
                ", curr_page=" + curr_page +
                ", total_count=" + total_count +
                ", appid='" + appid + '\'' +
                ", interactId='" + interactId + '\'' +
                ", commentId='" + commentId + '\'' +
                ", grade='" + grade + '\'' +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}
