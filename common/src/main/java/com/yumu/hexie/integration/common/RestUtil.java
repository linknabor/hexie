package com.yumu.hexie.integration.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * rest请求工具类
 * @author david
 *
 */
@Component
public class RestUtil {
	
	
	private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 物业模块的rest请求公共函数
	 * @param <V>
	 * @param requestUrl	请求链接
	 * @param jsonObject	请继承wuyeRequest
	 * @param typeReference	HexieResponse类型的子类
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public <T, V> V exchangeOnBody(String requestUrl, T jsonObject, TypeReference<V> typeReference)
			throws IOException, JsonParseException, JsonMappingException {
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Object> httpEntity = new HttpEntity<>(jsonObject, headers);
        logger.info("requestUrl : " + requestUrl + ", param : " + jsonObject);
        ResponseEntity<String> respEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, String.class);
        
        logger.info("response : " + respEntity);
        
		if (!HttpStatus.OK.equals(respEntity.getStatusCode())) {
			throw new BizValidateException("请求失败！ code : " + respEntity.getStatusCodeValue());
		}
		
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		V hexieResponse = objectMapper.readValue(respEntity.getBody(), typeReference);
		return hexieResponse;
	}
	
	/**
	 * 物业模块的rest请求公共函数
	 * @param <V>
	 * @param requestUrl	请求链接
	 * @param jsonObject	请继承wuyeRequest
	 * @param typeReference	HexieResponse类型的子类
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public <T extends CommonRequest, V> CommonResponse<V> exchangeOnUri(String requestUrl, T jsonObject, TypeReference<CommonResponse<V>> typeReference)
			throws IOException, JsonParseException, JsonMappingException {
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        LinkedMultiValueMap<String, String>paramsMap = new LinkedMultiValueMap<>();
        convertObject2Map(jsonObject, paramsMap);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
		URI uri = builder.queryParams(paramsMap).build().encode().toUri();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);
        
        logger.info("requestUrl : " + requestUrl + ", param : " + paramsMap);
        ResponseEntity<String> respEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        
        logger.info("response : " + respEntity);
        
		if (!HttpStatus.OK.equals(respEntity.getStatusCode())) {
			throw new BizValidateException("请求失败！ code : " + respEntity.getStatusCodeValue());
		}
		
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		CommonResponse<V> hexieResponse = objectMapper.readValue(respEntity.getBody(), typeReference);
		if (!"00".equals(hexieResponse.getResult())) {
			String errMsg = hexieResponse.getErrMsg();
			throw new BizValidateException(errMsg);
		}
		return hexieResponse;
	}
	
	/**
	 * 物业模块的rest请求公共函数
	 * @param <V>
	 * @param requestUrl	请求链接
	 * @param jsonObject	请继承wuyeRequest
	 * @param typeReference	HexieResponse类型的子类
	 * @return CommonResponse
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public <T extends CommonRequest> CommonResponse<byte[]> exchange4ResourceOnUri(String requestUrl, T jsonObject, TypeReference<CommonResponse<byte[]>> typeReference)
			throws IOException, JsonParseException, JsonMappingException {
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        LinkedMultiValueMap<String, String>paramsMap = new LinkedMultiValueMap<>();
        convertObject2Map(jsonObject, paramsMap);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
		URI uri = builder.queryParams(paramsMap).build().encode().toUri();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);
        
        logger.info("requestUrl : " + requestUrl + ", param : " + paramsMap);
        ResponseEntity<Resource> respEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Resource.class);
        logger.info("response : " + respEntity);
        
		if (!HttpStatus.OK.equals(respEntity.getStatusCode())) {
			throw new BizValidateException("请求失败！ code : " + respEntity.getStatusCodeValue());
		}
		InputStream inputStream = respEntity.getBody().getInputStream();
		CommonResponse<byte[]> hexieResponse = new CommonResponse<>();
		byte[] bytes = new byte[inputStream.available()];
		inputStream.read(bytes, 0, inputStream.available());
		hexieResponse.setData(bytes);
		hexieResponse.setResult("00");
		return hexieResponse;
	}
	
	/**
	 * 对象转LinkedMultiValueMap，如果对象有jsonProperty注解，则取注解的value值
	 * @param fromObject
	 * @param destMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void convertObject2Map(Object fromObject, LinkedMultiValueMap<String, String> destMap) {
		
		if (destMap == null) {
			return;
		}
		if (fromObject == null) {
			return;
		}
		if (fromObject instanceof Map) {
			
			Map map = (Map)fromObject;
			Iterator<Entry> it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry entry = it.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				if (StringUtils.isEmpty(key)) {
					continue;
				}
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				destMap.add(key, value);
			}
		}else {
			
			Field[] declaredFields = fromObject.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				field.setAccessible(true);
				if ("serialVersionUID".equals(field.getName())) {
					continue;
				}
				JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
				String fieldName = field.getName();
				if (jsonProperty != null && !StringUtils.isEmpty(jsonProperty.value())) {
					fieldName = jsonProperty.value();
				}
				try {
					Object value = field.get(fromObject);
					if (value instanceof List || value instanceof Object[]) {
						if (value instanceof List) {
							List<Object> list = (List<Object>)value;
							for (Object innerObject : list) {
								convertObject2Map(innerObject, destMap);
							}
						}
						if (value instanceof Object[]) {
							//TODO
						}
						
					}else {
						destMap.add(fieldName, value==null?"":String.valueOf(value));
					}

				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
		}
		
		
	}
	
	/**
	 * 非物业模块使用
	 * @param requestUrl
	 * @param jsonObject
	 * @param typeReference
	 * @return V
	 * @throws Exception
	 */
	public <T, V> V exchangeOnUri(String requestUrl, T jsonObject, TypeReference<V> typeReference) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        LinkedMultiValueMap<String, String>paramsMap = new LinkedMultiValueMap<>();
        convertObject2Map(jsonObject, paramsMap);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
		URI uri = builder.queryParams(paramsMap).build().encode().toUri();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);
        
        logger.info("requestUrl : " + requestUrl + ", param : " + paramsMap);
        ResponseEntity<String> respEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        
        logger.info("response : " + respEntity);
        
		if (!HttpStatus.OK.equals(respEntity.getStatusCode())) {
			throw new BizValidateException("请求失败！ code : " + respEntity.getStatusCodeValue());
		}
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		return objectMapper.readValue(respEntity.getBody(), typeReference);
	}
	
	/**
	 * 非物业模块的rest请求公共函数
	 * @param <V>
	 * @param requestUrl	请求链接
	 * @param jsonObject	请继承wuyeRequest
	 * @param typeReference	HexieResponse类型的子类
	 * @return CommonResponse<byte[]>
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public <T> CommonResponse<byte[]> exchange4ResourceOnUri(String requestUrl, T jsonObject, TypeReference<CommonResponse<byte[]>> typeReference)
			throws IOException, JsonParseException, JsonMappingException {
		
		HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
       LinkedMultiValueMap<String, String>paramsMap = new LinkedMultiValueMap<>();
       convertObject2Map(jsonObject, paramsMap);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
		URI uri = builder.queryParams(paramsMap).build().encode().toUri();
       HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);
       
       logger.info("requestUrl : " + requestUrl + ", param : " + paramsMap);
       ResponseEntity<Resource> respEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Resource.class);
       logger.info("response : " + respEntity);
       
		if (!HttpStatus.OK.equals(respEntity.getStatusCode())) {
			throw new BizValidateException("请求失败！ code : " + respEntity.getStatusCodeValue());
		}
		InputStream inputStream = respEntity.getBody().getInputStream();
		CommonResponse<byte[]> hexieResponse = new CommonResponse<>();
		byte[] bytes = new byte[inputStream.available()];
		inputStream.read(bytes, 0, inputStream.available());
		hexieResponse.setData(bytes);
		hexieResponse.setResult("00");
		return hexieResponse;
	}

	public <T> String exchange4Base64StrOnUri(String requestUrl, T jsonObject) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<Object> httpEntity = new HttpEntity<>(jsonObject, headers);

		logger.info("requestUrl : " + requestUrl + ", param : " + jsonObject);
		ResponseEntity<byte[]> respEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, byte[].class);
		logger.info("response : " + respEntity);

		if (!HttpStatus.OK.equals(respEntity.getStatusCode())) {
			throw new BizValidateException("请求失败！ code : " + respEntity.getStatusCodeValue());
		}
		byte[] result = respEntity.getBody();
		return Base64.getEncoder().encodeToString(result);
	}
	
	
	/**
	 * postByJson，可自定义header
	 * @param <V>
	 * @param requestUrl	请求链接
	 * @param jsonObject	请继承wuyeRequest
	 * @param typeReference	HexieResponse类型的子类
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public <T, V> V postOnBodyWithHeader(String requestUrl, T jsonObject, TypeReference<V> typeReference, Map<String, String> reqHeader)
			throws IOException, JsonParseException, JsonMappingException {
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        if (reqHeader!=null && !reqHeader.isEmpty()) {
			Iterator<Map.Entry<String, String>> it = reqHeader.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				headers.add(entry.getKey(), entry.getValue());
			}
		}
        HttpEntity<Object> httpEntity = new HttpEntity<>(jsonObject, headers);
        logger.info("requestUrl : " + requestUrl + ", param : " + jsonObject);
        ResponseEntity<String> respEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, String.class);
        
        logger.info("response : " + respEntity);
        
		if (!HttpStatus.OK.equals(respEntity.getStatusCode())) {
			throw new BizValidateException("请求失败！ code : " + respEntity.getStatusCodeValue());
		}
		
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		V hexieResponse = objectMapper.readValue(respEntity.getBody(), typeReference);
		return hexieResponse;
	}

}
