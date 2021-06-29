package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.Notice;

public class NoticeVO implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6587218967469160907L;
	
	private long id;
	private long createDate;
	private int noticeType;	//资讯类型  0.物业公告, 1.业委会公告, 2.居委会公告, 3.便民信息, 9.系统资讯-全局，10系统资讯-公众号级, 11圈子, 12通知消息推送
	private String title;	//资讯抬头
	private String summary;	//资讯摘要	
	private String content;	//资讯内容
	private String publishDate;	//发布日期
	private int status;	//0正常 1失效
	private boolean top;	//是否置顶
	private String image;	//多个图，逗号分割
	private String appid;	//平台ID，如果appid是0，表示所有公众号可见
	private String creator;	//发布人
	
	private List<String> imgList;			//实际图
	private List<String> previewImgList;	//预览图
	private List<String> thumbnailImgList;	//缩略图
	
	private String noticeDate;	//转化过的时间
	private String url;	//跳转链接
	
	@JsonIgnore
	private QiniuUtil qiniuUtil;
	
	public NoticeVO() {
		super();
	}

	public NoticeVO(Notice notice, QiniuUtil qiniuUtil) {
		this.qiniuUtil = qiniuUtil;
		BeanUtils.copyProperties(notice, this);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		if (createDate > 0) {
			noticeDate = DateUtil.getSendTime(createDate);
		}
		this.createDate = createDate;
	}

	public int getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(int noticeType) {

		switch (noticeType) {
		case ModelConstant.NOTICE_TYPE2_ALL:
			this.creator = "系统消息";
			break;
		case ModelConstant.NOTICE_TYPE2_APP:
			this.creator = "系统消息";
			break;
		case ModelConstant.NOTICE_TYPE2_WUYE:
			this.creator = "物业公告";
			break;
		case ModelConstant.NOTICE_TYPE2_YEWEI:
			this.creator = "业委会公告";
			break;
		case ModelConstant.NOTICE_TYPE2_JUWEI:
			this.creator = "居委会公告";
			break;
		case ModelConstant.NOTICE_TYPE2_BIANMIN:
			this.creator = "便民信息";
			break;
		case ModelConstant.NOTICE_TYPE2_MOMENTS:
			this.creator = "社区圈";
			break;
		case ModelConstant.NOTICE_TYPE2_NOTIFICATIONS:
			this.creator = "群发通知";
			break;
		case ModelConstant.NOTICE_TYPE2_BIll:
			this.creator = "账单推送";
			break;
		case ModelConstant.NOTICE_TYPE2_ARREARS_BILL:
			this.creator = "欠费提醒";
			break;
		case ModelConstant.NOTICE_TYPE2_THREAD:
			this.creator = "意见回复提醒";
			break;
		case ModelConstant.NOTICE_TYPE2_ORDER:
			this.creator = "工单进度提醒";
			break;
		default:
			this.creator = "系统消息";
			break;
		}
		
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
		if (!StringUtils.isEmpty(image)) {
			String[]imgArr = image.split(",");
			imgList = Arrays.asList(imgArr);
			List<String> thumbnailList = new ArrayList<>(imgList.size());
			List<String> previewList = new ArrayList<>(imgList.size());
			imgList.forEach(img->{
				thumbnailList.add(qiniuUtil.getThumbnailLink(img, "3", "0"));
				previewList.add(qiniuUtil.getPreviewLink(img, "1", "0"));
			});
			setThumbnailImgList(thumbnailList);
			setPreviewImgList(previewList);
		}
		this.image = image;
	}
	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public List<String> getImgList() {
		return imgList;
	}

	public void setImgList(List<String> imgList) {
		this.imgList = imgList;
	}

	public List<String> getPreviewImgList() {
		return previewImgList;
	}

	public void setPreviewImgList(List<String> previewImgList) {
		if (previewImgList==null || previewImgList.isEmpty()) {
			return;
		}
		this.previewImgList = previewImgList;
	}

	public List<String> getThumbnailImgList() {
		return thumbnailImgList;
	}

	public void setThumbnailImgList(List<String> thumbnailImgList) {
		if (thumbnailImgList==null || thumbnailImgList.isEmpty()) {
			return;
		}
		this.thumbnailImgList = thumbnailImgList;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		if (StringUtils.isEmpty(creator)) {
			return;
		}
		this.creator = creator;
	}

	public String getNoticeDate() {
		return noticeDate;
	}

	public void setNoticeDate(String noticeDate) {
		if (StringUtils.isEmpty(noticeDate)) {
			return;
		}
		this.noticeDate = noticeDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
