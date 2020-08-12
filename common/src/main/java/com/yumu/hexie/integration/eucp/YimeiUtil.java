package com.yumu.hexie.integration.eucp;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class YimeiUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(YimeiUtil.class);
	
	@Value("${sms.yimei.cdkey}")
	private String cdkey;
	
	@Value("${sms.yimei.password}")
	private String password;
	
	@Value("${sms.yimei.url}")
	private String urlStr;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public boolean sendMessage(String mobile, String message, long id) {
		
		try {
			Assert.hasLength(urlStr, "未配置 yimei短信请求服务地址，请检查配置文件。key: sms.yimei.url");
			Assert.hasText(message, "发送内容不能为空。");
			
			String sendContent = URLEncoder.encode(message, Charset.forName("utf8").toString());
			LinkedMultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<String, String>();
			paramsMap.add("cdkey", cdkey);
			paramsMap.add("password", password);
			paramsMap.add("phone", mobile);//5：bd09ll(百度经纬度坐标);
			paramsMap.add("message", sendContent);
			paramsMap.add("addserial", String.valueOf(id));

			logger.info("Yimei util, request url : " + urlStr + "param : " + paramsMap);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlStr);
			URI uri = builder.queryParams(paramsMap).build().encode().toUri();
			ResponseEntity<YimeiResult> response = restTemplate.exchange(uri, HttpMethod.GET, null, YimeiResult.class);
			logger.info("Yimei util, response : " + response);
			YimeiResult result = response.getBody();
			if(result == null){
				return false;
			}
			return result.isSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} 
		
	}
	
	
}
