package com.yumu.hexie.integration.wechat.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.wechat.util.WechatConfig;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * 文件上传下载
 */
public class FileService {
	
	private static final Logger log = LoggerFactory.getLogger(FileService.class);
	
	private static final String INVALID_PATH_STR1 = "../";
	private static final String INVALID_PATH_STR2 = "..\\";

	/**
	 * 下载文件URL
	 */
	private static String dwonloadFileURL = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";

	/**
	 * 下载文件
	 * @param mediaId
	 */
	public static InputStream downloadFile(String mediaId,String accessToken){
		
		String requestUrl = dwonloadFileURL.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", mediaId);
		try {
			HttpGet httpGet = new HttpGet(requestUrl);
			HttpClient httpclient = HttpClients.createDefault();
			
			log.debug("start to call httpclient ... ");
			HttpResponse response = httpclient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			log.info("start to get response ...");
			log.info(response.getStatusLine().toString());
			
			String responseStr = null;
			if (entity != null) {

				log.info("response content length: " + entity.getContentLength());
				Header header = entity.getContentType();
				
				log.info("header :" + header.getName()+":"+header.getValue());
				
				InputStream input = entity.getContent();
				if (header.getValue().contains("image")) {	//返回图片
					
					return input;
				
				}else {	//返回错误信息
					
					byte[]bytes = new byte[1024];	//错误信息
					input.read(bytes);
				    responseStr = new String(bytes, WechatConfig.INPUT_CHARSET);	//转UTF-8
				    responseStr = responseStr.trim();
				    log.error("response : \n" + responseStr);
				    throw new BizValidateException(responseStr);
				    
				}
				
			}
			
		} catch (Exception e) {
			 throw new BizValidateException(e.getMessage());
		}
		
		return null;
	}

	/**
	 * inputStream转文件
	 * @param is
	 * @param filePath
	 * @return
	 */
	public static void inputStream2File(InputStream is, String filePath){
		
		try {
			FileService.checkFilePath(filePath);	//校验文件路径
			OutputStream os = new FileOutputStream(new File(filePath));
			int bytesRead = 0;
			byte[] buffer = new byte[1024*1024];
			while ((bytesRead = is.read(buffer, 0, 1024*1024)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.flush();
			os.close();
			is.close();
		
		} catch (Exception e) {
			
			log.error("convert stream to file failed ...");
		}
		
	}
	
	/**
	 * 去除文件中的非法字符
	 * @param fileName
	 * @return
	 */
	public static void checkFilePath(String fileName) {
		
		boolean isValid = true;
		if (StringUtils.isEmpty(fileName)) {
			return;
		}
		if (fileName.indexOf(INVALID_PATH_STR1) > -1) {
			isValid = false;
		}else if (fileName.indexOf(INVALID_PATH_STR2) > -1) {
			isValid = false;
		}
		if (!isValid) {
			throw new BizValidateException("invalid file path !");
		}
		
	}
	
	
	
}
