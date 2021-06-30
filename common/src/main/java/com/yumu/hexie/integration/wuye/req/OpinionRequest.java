package com.yumu.hexie.integration.wuye.req;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-06-07 16:07
 */
public class OpinionRequest implements Serializable {

    private Logger logger = LoggerFactory.getLogger(OpinionRequest.class);

    private String opinionDate;
    private String threadId;
    private String replyId; //回复ID
    private String sectName;
    private String cellAddr;
    private String content;
    private String commMan;
    private String threadContent;
    private String appId;
    private String openId;
    private String sectId;
    private String userOpenid;

    public String getOpinionDate() {
        return opinionDate;
    }

    public void setOpinionDate(String opinionDate) {
        this.opinionDate = opinionDate;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        if (!StringUtils.isEmpty(sectName)) {
            try {
                sectName = URLEncoder.encode(sectName, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.sectName = sectName;
    }

    public String getCellAddr() {
        return cellAddr;
    }

    public void setCellAddr(String cellAddr) {
        if (!StringUtils.isEmpty(cellAddr)) {
            try {
                cellAddr = URLEncoder.encode(cellAddr, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.cellAddr = cellAddr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (!StringUtils.isEmpty(content)) {
            try {
                content = URLEncoder.encode(content, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.content = content;
    }

    public String getCommMan() {
        return commMan;
    }

    public void setCommMan(String commMan) {
        this.commMan = commMan;
    }

    public String getThreadContent() {
        return threadContent;
    }

    public void setThreadContent(String threadContent) {
        if (!StringUtils.isEmpty(threadContent)) {
            try {
                threadContent = URLEncoder.encode(threadContent, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        this.threadContent = threadContent;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSectId() {
        return sectId;
    }

    public void setSectId(String sectId) {
        this.sectId = sectId;
    }

    public String getUserOpenid() {
        return userOpenid;
    }

    public void setUserOpenid(String userOpenid) {
        this.userOpenid = userOpenid;
    }
}
