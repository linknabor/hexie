/**
 * 
 */
package com.yumu.hexie.integration.eucp;

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

/**
 * @author HuYM
 *
 */
@Component
public class CreateBlueUtil {

	private static final Logger logger = LoggerFactory.getLogger(CreateBlueUtil.class);
	
	@Value("${sms.createblue.account}")
	private String account = "shnbxxkj";
	
	@Value("${sms.createblue.password}")
	private String password = "Sh666666";
	
	@Value("${sms.createblue.url}")
	private String urlStr = "http://222.73.117.158/msg/";
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 短信发送
	 * @param mobile
	 * @param message
	 * @return
	 */
	public boolean sendMessage(String mobile, String message) {
		
		try {

			Assert.hasLength(urlStr, "未配置 创蓝短信请求服务地址，请检查配置文件。key: sms.createblue.url");
			
			LinkedMultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<String, String>();
			paramsMap.add("account", account);
			paramsMap.add("pswd", password);
			paramsMap.add("mobile", mobile);
			paramsMap.add("needstatus", String.valueOf(true));
			paramsMap.add("msg", message);
			
			logger.info("CreateBlue request : " + urlStr + "param : " + paramsMap);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlStr);
			java.net.URI uri = builder.queryParams(paramsMap).path("HttpBatchSendSM").build().encode().toUri();
			ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
			logger.info("CreateBlue response : " + responseEntity);
			
			//返回串由逗号分割，逗号后面的值为短信状态，0表正常，其他值都为异常。
			String response = responseEntity.getBody();
			int begin = response.indexOf(",") + 1;
			int end = response.indexOf("\n")==-1?response.length():response.indexOf("\n");
			String status = response.substring(begin, end);
			logger.info("status : " + status);
			return "0".equals(status);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} 
		
	}
	

}
