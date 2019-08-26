package com.eshequ.hexie.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.eshequ.hexie.common.Constants;
import com.eshequ.hexie.common.WechatConfig;
import com.eshequ.hexie.exception.AppSysException;
import com.eshequ.hexie.exception.BusinessException;
import com.eshequ.hexie.service.AuthService;
import com.eshequ.hexie.util.RestUtil;
import com.eshequ.hexie.vo.ComponentAcessToken;
import com.eshequ.hexie.vo.PreAuthCode;
import com.eshequ.hexie.vo.VerifyTicket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Service
public class AuthServiceImpl implements AuthService{
	
	private static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	@Value("${component.appid}")
	private String componentAppid;
	
	@Value("${component.secret}")
	private String componetSecret;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private RestUtil restUtil;


	@Override
	public void saveVerifyTicket(VerifyTicket verifyTicket) {

		redisTemplate.opsForValue().set(Constants.VERIFY_TICKET, verifyTicket.getVerifyTicket());
	}

	@Override
	public ComponentAcessToken getComponentAccessToken(String verifyTicket) {
		
		String reqUrl = WechatConfig.COMPONENT_ACCESS_TOKEN_URL;
		Map<String, String> postData = new HashMap<>();
		postData.put("component_appid", componentAppid);
		postData.put("component_appsecret", componetSecret);
		postData.put("component_verify_ticket", verifyTicket);
		
		ComponentAcessToken cat = restUtil.doPost(reqUrl, postData, ComponentAcessToken.class);
		return cat;
		
	}
	
	/**
	 * 获取预授权码
	 * 先从缓存中取，如果没有，访问微信获取。缓存设置10分钟过期。不做定时操作，因为这个码的使用频率很低，没必要每10分钟取一次。做缓存是为了单个用户反复刷新页面操作不用重复做http开销
	 */
	@Override
	public PreAuthCode getPreAuthCode() {
		
		PreAuthCode pac = (PreAuthCode) redisTemplate.opsForValue().get(Constants.PRE_AUTH_CODE);
		
		if (pac == null) {
			ComponentAcessToken cat = (ComponentAcessToken) redisTemplate.opsForValue().get(Constants.COMPONENT_ACCESS_TOKEN);
			String reqUrl = WechatConfig.PRE_AUTH_CODE_URL;
			reqUrl = String.format(reqUrl, cat.getComponentAcessToken());
			Map<String, String> postData = new HashMap<>();
			postData.put("component_appid", componentAppid);
			PreAuthCode preAuthCode = restUtil.doPost(reqUrl, postData, PreAuthCode.class);
			redisTemplate.opsForValue().set(Constants.PRE_AUTH_CODE, preAuthCode);
			redisTemplate.expire(Constants.PRE_AUTH_CODE, 10, TimeUnit.MINUTES);	//设置10分钟过期
			pac = preAuthCode;
		}
		return pac;
	}

	@Override
	public void authEventHandle(String xml) {

		try {
			XmlMapper xmlMapper = new XmlMapper();
			JsonNode jsonNode = xmlMapper.readTree(xml);
			JsonNode typeNode = jsonNode.path("InfoType");
			if (typeNode!=null) {
				String infotype = typeNode.asText();	//节点内容转文本
				switch (infotype) {
				case WechatConfig.EVENT_TYPE_VERIFY_TICKET:
					VerifyTicket verifyTicket = xmlMapper.readValue(xml, VerifyTicket.class);
					saveVerifyTicket(verifyTicket);
					break;
				default:
					logger.error("unknow info type : " + infotype);
					throw new BusinessException("invalid request !");
				}
				
			}else {
				logger.error("no xml node 'InfoType' !");
				throw new BusinessException("invalid request !");
			}
		} catch (Exception e) {
			throw new AppSysException(e);
		}
		
	}

	
}
