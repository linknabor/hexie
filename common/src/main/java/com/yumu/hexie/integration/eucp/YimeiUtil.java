package com.yumu.hexie.integration.eucp;

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
import org.springframework.web.client.RestTemplate;

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
			StringBuffer bufUrl = new StringBuffer();
			bufUrl.append(urlStr).append("?cdkey=").append(cdkey).append("&password=").append(password);
			String sendContent = URLEncoder.encode(message, Charset.forName("utf8").toString());
			bufUrl.append("&phone=").append(mobile).append("&message=").append(sendContent).append("&addserial=").append(id);
			String requestStr = bufUrl.toString();
			logger.info("yimei request : " + requestStr);
			ResponseEntity<YimeiResult> responseEntity = restTemplate.exchange(requestStr, HttpMethod.GET, null, YimeiResult.class);
			logger.info("yimei response : " + responseEntity);
			YimeiResult result = responseEntity.getBody();
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
