package com.yumu.hexie.model.community;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class Notice extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1144177388450035925L;
	
	private int noticeType;	//资讯类型  0.物业公告, 1.业委会公告, 2.居委会公告, 3.便民信息, 9.系统资讯-全局，10系统资讯-公众号级, 11圈子, 12通知消息推送
	private String title;	//资讯主标题
	private String summary;	//资讯副标题
	private String content;	//资讯内容
	private String publishDate;	//发布日期
	private int status;	//0正常 1失效
	private boolean top;	//是否置顶
	private String image;	//资讯图片
	private String smallImage;	//缩略图
	private String appid;	//平台ID，如果appid是0，表示所有公众号可见
	private String creator;	//发布人
	private String url;	//跳转链接
	private long outsideKey; //外部主键 可能是message表
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
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean isTop() {
		return top;
	}
	public void setTop(boolean top) {
		this.top = top;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getSmallImage() {
		return smallImage;
	}
	public void setSmallImage(String smallImage) {
		this.smallImage = smallImage;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getOutsideKey() {
		return outsideKey;
	}

	public void setOutsideKey(long outsideKey) {
		this.outsideKey = outsideKey;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Override
	public String toString() {
		return "Notice{" +
				"noticeType=" + noticeType +
				", title='" + title + '\'' +
				", summary='" + summary + '\'' +
				", content='" + content + '\'' +
				", publishDate='" + publishDate + '\'' +
				", status=" + status +
				", top=" + top +
				", image='" + image + '\'' +
				", smallImage='" + smallImage + '\'' +
				", appid='" + appid + '\'' +
				", creator='" + creator + '\'' +
				", url='" + url + '\'' +
				", outsideKey=" + outsideKey +
				'}';
	}
}
