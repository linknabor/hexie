package com.yumu.hexie.integration.wuye.req;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-06-29 15:28
 */
public class CommunityRequest extends WuyeRequest{

    private int noticeType;
    private String title;
    private String summary;
    private String content;
    private String image;
    private String appid;
    private String publishDate;
    private long outsideKey;
    private String sectIds;
    private String openid;

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public long getOutsideKey() {
        return outsideKey;
    }

    public void setOutsideKey(long outsideKey) {
        this.outsideKey = outsideKey;
    }

    public String getSectIds() {
        return sectIds;
    }

    public void setSectIds(String sectIds) {
        this.sectIds = sectIds;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "CommunityRequest{" +
                "noticeType='" + noticeType + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", appid='" + appid + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", outsideKey='" + outsideKey + '\'' +
                ", sectIds='" + sectIds + '\'' +
                '}';
    }
}
