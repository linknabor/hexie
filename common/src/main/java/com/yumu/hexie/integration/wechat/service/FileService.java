package com.yumu.hexie.integration.wechat.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * 微信文件上传下载
 */
@Component
public class FileService {
	
	private static final String INVALID_PATH_STR1 = "../";
	private static final String INVALID_PATH_STR2 = "..\\";

	@Autowired
	private RestUtil restUtil;
	
	/**
	 * 下载文件URL
	 */
	private static String DOWNLOAD_FILE_URL = "https://api.weixin.qq.com/cgi-bin/media/get";

	/**
	 * 下载文件
	 * @param mediaId
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public byte[] downloadFile(String mediaId,String accessToken) throws Exception {
		
		Map<String, String> map = new HashMap<>();
		map.put("access_token", accessToken);
		map.put("media_id", mediaId);
		TypeReference<CommonResponse<byte[]>> typeReference = new TypeReference<CommonResponse<byte[]>>(){};
		CommonResponse<byte[]> response = restUtil.exchange4ResourceOnUri(DOWNLOAD_FILE_URL, map, typeReference);
		return response.getData();
	}
	
	/**
	 * 下载文件
	 * @param mediaId
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public void downloadFile(String mediaId, String accessToken, File file) throws Exception {
		
		Map<String, String> map = new HashMap<>();
		map.put("access_token", accessToken);
		map.put("media_id", mediaId);
		TypeReference<CommonResponse<byte[]>> typeReference = new TypeReference<CommonResponse<byte[]>>(){};
		CommonResponse<byte[]> response = restUtil.exchange4ResourceOnUri(DOWNLOAD_FILE_URL, map, typeReference);
		byte2File(response.getData(), file);
	}
	
	/**
	 * byte转文件
	 * @param fileByte
	 * @param filePath
	 * @throws Exception
	 */
	public void byte2File(byte[]fileByte, File file) throws Exception {
		
		checkFilePath(file.getPath());
		OutputStream output = new FileOutputStream(file);
		BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
		bufferedOutput.write(fileByte);
		bufferedOutput.close();
	}

	/**
	 * 去除文件中的非法字符
	 * @param filePath
	 * @return
	 */
	private void checkFilePath(String filePath) {
		
		boolean isValid = true;
		if (StringUtils.isEmpty(filePath)) {
			return;
		}
		if (filePath.indexOf(INVALID_PATH_STR1) > -1) {
			isValid = false;
		}else if (filePath.indexOf(INVALID_PATH_STR2) > -1) {
			isValid = false;
		}
		if (!isValid) {
			throw new BizValidateException("invalid file path !");
		}
		
	}
	
	
	
}
