/**
 * 
 */
package com.yumu.hexie.integration.qiniu.util;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;

/**
 * @author HuYM
 *
 */
@Component
public class QiniuUtil {

	private static final Logger logger = LoggerFactory.getLogger(QiniuUtil.class);

	private Mac mac = null;
	private String upToken = null;
	private RSClient client = null;
	
	public static final String BUCKET_NAME = "e-shequ";
	private static final String DEFAULT_WIDTH = "290";	//缩略图的默认长度，iphone4以及5s 一屏为320长度，此处取280
	private static final String DEFAULT_HEIGHT = "";	//缩略图的默认高度，此处不给值。会根据宽度作等比例调整
	private static final String PREVIEW_WIDTH = "94";	//预览图尺寸
	private static final String PREVIEW_HEIGHT = "94";	//预览图尺寸
	
	@Value(value = "${qiniu.access.key}")
	private String accessKey = null;
	@Value(value= "${qiniu.secret.key}")
	private String secretKey = null;
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 初始化token
	 * @return
	 */
	@PostConstruct
	private void initToken(){
		
		if (ConstantWeChat.isMainServer()) {	//BK程序不跑下面的队列轮询
			return;
		}
		
        PutPolicy putPolicy = new PutPolicy(BUCKET_NAME);
        try {
        	mac = new Mac(accessKey, secretKey);
        	client = new RSClient(mac);
        	upToken = putPolicy.token(mac);
        	
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
	/**
	 * 获取qiniu的uptoken
	 * @return
	 */
	public String getUpToken(){
		
		return upToken;
	}

	/**
	 * 获取RSCLIENT
	 * @return
	 */
	public RSClient getRsClient(){
		
			
		return client;
		
	}
	
	/**
	 * 获取带有interlace效果的图片
	 * @param origLink
	 * @param interlace
	 * @return
	 */
	public String getInterlaceImgLink(String origLink, String interlace){
		
		String retLink = origLink;
		if ("1".equals(interlace)) {
			retLink+="/interlace/1";
		}
		return retLink;
	}
	
	/**
	 * 获取缩略图的链接
	 * @param origLink
	 * @param mode
	 * @param interlace
	 * @return
	 */
	public String getThumbnailLink(String origLink, String mode, String interlace){
		
		
		if (StringUtil.isEmpty(origLink)) {
			return "";
		}
		
		if (StringUtil.isEmpty(mode)) {
			return origLink;
		}
		
		String retLink = origLink;
		
		/*
		 * 此处提供0-3，共4种模式。另有两种模式，请自行登录七牛网查询。	http://developer.qiniu.com/docs/v6/api/reference/fop/image/imageview2.html
		 */
		if ("0".equals(mode)||"1".equals(mode)||"2".equals(mode)||"3".equals(mode)) {
			retLink+="?imageView2/"+mode+"/w/"+DEFAULT_WIDTH+"/h";
			if (!StringUtil.isEmpty(DEFAULT_HEIGHT)) {
				retLink+="/"+DEFAULT_HEIGHT;
			}
		}
		
		if ("1".equals(interlace)) {
			retLink+="/interlace/1";
		}
		
		return retLink;
		
	}
	
	/**
	 * 获取预览图的链接
	 * @param origLink		图片链接
	 * @param mode	//缩略模式
	 * @param interlace	//是否模糊渐进
	 * @return
	 */
	public String getPreviewLink(String origLink, String mode, String interlace){
		
		
		if (StringUtil.isEmpty(origLink)) {
			return "";
		}
		
		if (StringUtil.isEmpty(mode)) {
			return origLink;
		}
		
		String retLink = origLink;
		
		/*
		 * 此处提供0-3，共4种模式。另有两种模式，请自行登录七牛网查询。	http://developer.qiniu.com/docs/v6/api/reference/fop/image/imageview2.html
		 */
		if ("0".equals(mode)||"1".equals(mode)||"2".equals(mode)||"3".equals(mode)) {
			retLink+="?imageView2/"+mode+"/w/"+PREVIEW_WIDTH+"/h";
			if (!StringUtil.isEmpty(PREVIEW_HEIGHT)) {
				retLink+="/"+PREVIEW_HEIGHT;
			}
		}
		
		
		
		if ("1".equals(interlace)) {
			retLink+="/interlace/1";
		}
		
		return retLink;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getImgs(String imgLink){
		
		String requestUrl = imgLink+"?imageInfo";
		Map<String, String> map = restTemplate.getForObject(requestUrl, Map.class);
		return map;
	}
		
	

}
