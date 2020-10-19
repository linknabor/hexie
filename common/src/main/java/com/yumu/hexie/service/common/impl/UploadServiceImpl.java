/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common.impl;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.integration.wechat.service.FileService;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.localservice.repair.RepairOrderRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.UploadService;
import com.yumu.hexie.service.user.UserService;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: UploadServiceImpl.java, v 0.1 2016年1月7日 上午2:04:59  Exp $
 */
@Service("uploadService")
public class UploadServiceImpl implements UploadService {

    private static final Logger log = LoggerFactory.getLogger(UploadServiceImpl.class);
    @Value(value = "${tmpfile.dir}")
    private String              tmpFileRoot;

    @Value(value = "${qiniu.domain}")
    private String              domain;
    
    @Inject
    private RepairOrderRepository repairOrderRepository;
    
	@Inject
	private SystemConfigService systemConfigService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private QiniuUtil qiniuUtil;
	
	@Autowired
	private FileService fileService;
	
    /** 
     * @param order
     * @see com.yumu.hexie.service.common.UploadService#updateRepairImg(com.yumu.hexie.model.localservice.repair.RepairOrder)
     */
    @Override
    @Async
    public void updateRepairImg(RepairOrder order) {
        if (order.isImageUploaded()
                ||( StringUtil.isEmpty(order.getImgUrls()) 
                && StringUtil.isEmpty(order.getCommentImgUrls()))) {
            return;
        }
        User user = userService.getById(order.getUserId());
        String imgUrls = moveImges(user.getAppId(), order.getImgUrls());
        String commentImgUrls = moveImges(user.getAppId(), order.getCommentImgUrls());
        
        RepairOrder nOrder = repairOrderRepository.findById(order.getId()).get();
        nOrder.setImgUrls(imgUrls);
        nOrder.setCommentImgUrls(commentImgUrls);
        repairOrderRepository.save(nOrder);
    }
    
    /**
     * 上传图片
     */
    @Override
    public Map<String, String> uploadImages(String appId, List<String> imgUrlList) {
    	
    	String accessToken = systemConfigService.queryWXAToken(appId);
    	Map<String, String> returnMap = Collections.synchronizedMap(new HashMap<>());
    	if (imgUrlList == null || imgUrlList.isEmpty()) {
    		return returnMap;
		}
    	//多线程并行，同时上传多个图片
    	imgUrlList.parallelStream().forEach(imgUrl->{
    		if (StringUtils.isEmpty(imgUrl)) {
				return;
			}
    		if (imgUrl.indexOf("http") == -1) {
    			String qiniuUrl = uploadFileToQiniu(downloadFromWechat(accessToken, imgUrl));
    			if(!StringUtils.isEmpty(qiniuUrl)) {
    				returnMap.put(imgUrl, qiniuUrl);
                } else {
                	returnMap.put(imgUrl, imgUrl);
                }
			}else {
				returnMap.put(imgUrl, imgUrl);
			}
    	});
    	return returnMap;
    	
    }
    
    /**
     * 迁移图片
     * @param appId
     * @param imgUrls
     * @return
     */
    private String moveImges(String appId, String imgUrls) {
    	
    	String accessToken = systemConfigService.queryWXAToken(appId);
    	
        if(StringUtil.isEmpty(imgUrls)) {
            return "";
        }
        String newImgUrls = "";
        String[] urls = imgUrls.split(RepairOrder.IMG_SPLIT);
        for (String url : urls) {
            if(StringUtils.isEmpty(url)) {
                continue;
            }
            if (url.indexOf("http")<0) {
                String qiniuUrl = uploadFileToQiniu(downloadFromWechat(accessToken, url));
                if(!StringUtils.isEmpty(qiniuUrl)) {
                    newImgUrls += qiniuUrl;
                } else {
                    newImgUrls += url;
                }
            } else {
                newImgUrls += url;
            }
            newImgUrls +=",";
        }
        return newImgUrls;
    }

    private String uploadFileToQiniu(File file) {

        if(file == null || !file.exists()){
            return "";
        }
        PutExtra extra = new PutExtra();
        String uptoken = qiniuUtil.getUpToken();

        if (file.exists() && file.getTotalSpace() > 0) {
            PutRet putRet = IoApi.putFile(uptoken, DateUtil.dtFormat(new Date(),"yyyyMMddHHmmssSSS")+(int)(Math.random()*1000), file, extra);
            log.error("上传图片：StatusCode" + putRet.getStatusCode()+"[key] "+putRet.getKey()
                +"[hash] "+putRet.getHash());
            if(putRet.getStatusCode() == 200 && !StringUtils.isEmpty(putRet.getKey())) {
                return domain + putRet.getKey();
            }
        }
        return "";
    }

    /**
     * 从微信服务器上下载
     * @param appId
     * @param mediaId
     * @return
     */
    @Override
    public File downloadFromWechat(String accessToken, String mediaId) {
    	
        String currDate = DateUtil.dtFormat(new Date(), "yyyyMMdd");
        String tmpPathRoot = tmpFileRoot + File.separator + currDate + File.separator;

        File file = new File(tmpPathRoot);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        
        String tmpPath = tmpPathRoot + System.currentTimeMillis();
        File mediaFile = new File(tmpPath);
        
        int failCount = 0;
        while (failCount < 3){
        	try {
            	fileService.downloadFile(mediaId, accessToken, mediaFile); //重试3次
            	break;
            } catch(Exception e){
            	log.error(e.getMessage(), e);
            	log.error("从腾讯下载图片失败: " + e.getMessage());
            	failCount++;
            }
        }
        return mediaFile;

    }
}
