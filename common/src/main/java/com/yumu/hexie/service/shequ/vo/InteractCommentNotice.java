package com.yumu.hexie.service.shequ.vo;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2023 8月 星期五
 * @Desc :
 */
public class InteractCommentNotice {
    private String interactId;
    private String content;
    private String commentContent;
    private String commentId;
    private String sectName;
    private String cellAddr;
    private String openid;
    private String appid;
    private String commentName;
    private String opinionDate;
    private String userName;

    public String getInteractId() {
        return interactId;
    }

    public void setInteractId(String interactId) {
        this.interactId = interactId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
    }

    public String getCellAddr() {
        return cellAddr;
    }

    public void setCellAddr(String cellAddr) {
        this.cellAddr = cellAddr;
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

    public String getCommentName() {
        return commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getOpinionDate() {
        return opinionDate;
    }

    public void setOpinionDate(String opinionDate) {
        this.opinionDate = opinionDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "InteractCommentNotice{" +
                "interactId='" + interactId + '\'' +
                ", content='" + content + '\'' +
                ", commentContent='" + commentContent + '\'' +
                ", commentId='" + commentId + '\'' +
                ", sectName='" + sectName + '\'' +
                ", cellAddr='" + cellAddr + '\'' +
                ", openid='" + openid + '\'' +
                ", appid='" + appid + '\'' +
                ", commentName='" + commentName + '\'' +
                ", opinionDate='" + opinionDate + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
