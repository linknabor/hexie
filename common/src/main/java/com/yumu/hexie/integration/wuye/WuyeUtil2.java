package com.yumu.hexie.integration.wuye;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.yumu.hexie.config.WechatPropConfig;
import com.yumu.hexie.integration.wuye.dto.DiscountViewRequestDTO;
import com.yumu.hexie.integration.wuye.dto.OtherPayDTO;
import com.yumu.hexie.integration.wuye.dto.PrepayRequestDTO;
import com.yumu.hexie.integration.wuye.req.BillDetailRequest;
import com.yumu.hexie.integration.wuye.req.BillStdRequest;
import com.yumu.hexie.integration.wuye.req.DiscountViewRequest;
import com.yumu.hexie.integration.wuye.req.OtherPayRequest;
import com.yumu.hexie.integration.wuye.req.PaySmsCodeRequest;
import com.yumu.hexie.integration.wuye.req.PrepayRequest;
import com.yumu.hexie.integration.wuye.req.QrCodePayServiceRequest;
import com.yumu.hexie.integration.wuye.req.QueryOrderRequest;
import com.yumu.hexie.integration.wuye.req.QuickPayRequest;
import com.yumu.hexie.integration.wuye.req.WuyeRequest;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.vo.Discounts;
import com.yumu.hexie.integration.wuye.vo.HexieResponse;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.QrCodePayService;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.user.BankCard;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.LocationService;
import com.yumu.hexie.service.shequ.impl.LocationServiceImpl;

/**
 * 新的WuyeUtil
 * http的请求都改用restTemplate，旧的WuyeUtil中的方法，会慢慢代替掉,最后全部去掉旧版本的httpclient
 * @author david
 *
 */
@Component
public class WuyeUtil2 {
	
	private Logger logger = LoggerFactory.getLogger(WuyeUtil2.class);
	
	@Autowired
	private WechatPropConfig wechatPropConfig;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private static final String QUICK_PAY_URL = "quickPaySDO.do"; // 快捷支付
	private static final String WX_PAY_URL = "wechatPayRequestSDO.do"; // 微信支付请求
	private static final String DISCOUNT_URL = "getBillPayDetailSDO.do";	//获取优惠明细
	private static final String CARD_PAY_SMS_URL = "getCardPaySmsCodeSDO.do";	//获取优惠明细
	private static final String QUERY_ORDER_URL = "queryOrderSDO.do";	//订单查询
	private static final String BILL_DETAIL_URL = "getBillInfoMSDO.do"; // 获取账单详情
	private static final String BILL_LIST_STD_URL = "getPayListStdSDO.do"; // 获取账单列表
	private static final String OTHER_PAY_URL = "otherPaySDO.do";	//其他收入支付
	private static final String QRCODE_PAY_SERVICE_URL = "getServiceSDO.do";	//二维码支付服务信息
	
	
	/**
	 * 标准版查询账单
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param house_id
	 * @param sect_id
	 * @param regionurl
	 * @return
	 * @throws Exception 
	 */
	public BaseResult<BillListVO> queryBillList(User user, String startDate, String endDate, String houseId, String regionName) throws Exception {
		
		String requestUrl = getRequestUrl(user, regionName);
		requestUrl += BILL_LIST_STD_URL;
		
		BillStdRequest billStdRequest = new BillStdRequest();
		billStdRequest.setWuyeId(user.getWuyeId());
		billStdRequest.setHouseId(houseId);
		billStdRequest.setStartDate(startDate);
		billStdRequest.setEndDate(endDate);
		
		TypeReference<HexieResponse<BillListVO>> typeReference = new TypeReference<HexieResponse<BillListVO>>(){};
		HexieResponse<BillListVO> hexieResponse = wuyeRest(requestUrl, billStdRequest, typeReference);
		BaseResult<BillListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	
	
	/**
	 * 账单详情 anotherbillIds(逗号分隔) 汇总了去支付,来自BillInfo的bill_id
	 * @param user
	 * @param stmtId
	 * @param anotherbillIds
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public BaseResult<PaymentInfo> getBillDetail(User user, String stmtId,String anotherbillIds, String regionName) throws Exception {
		
		String requestUrl = getRequestUrl(user, regionName);
		requestUrl += BILL_DETAIL_URL;
		BillDetailRequest billDetailRequest = new BillDetailRequest();
		billDetailRequest.setWuyeId(user.getWuyeId());
		billDetailRequest.setStmtId(stmtId);
		billDetailRequest.setBillId(anotherbillIds);
		
		TypeReference<HexieResponse<PaymentInfo>> typeReference = new TypeReference<HexieResponse<PaymentInfo>>(){};
		HexieResponse<PaymentInfo> hexieResponse = wuyeRest(requestUrl, billDetailRequest, typeReference);
		BaseResult<PaymentInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
	}
	
	/**
	 * 账单快捷缴费
	 * @param user
	 * @param stmtId
	 * @param currPage
	 * @param totalCount
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public BaseResult<BillListVO> quickPayInfo(User user, String stmtId, String currPage, String totalCount) throws Exception {
		
		String requestUrl = getRequestUrl(user, "");
		requestUrl += QUICK_PAY_URL;
		
		QuickPayRequest quickPayRequest = new QuickPayRequest();
		quickPayRequest.setStmtId(stmtId);
		quickPayRequest.setCurrPage(currPage);
		quickPayRequest.setTotalCount(totalCount);
		
		TypeReference<HexieResponse<BillListVO>> typeReference = new TypeReference<HexieResponse<BillListVO>>(){};
		HexieResponse<BillListVO> hexieResponse = wuyeRest(requestUrl, quickPayRequest, typeReference);
		BaseResult<BillListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
	}
	

	/**
	 * 专业版缴费
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<WechatPayInfo> getPrePayInfo(PrepayRequestDTO prepayRequestDTO) throws Exception {

		
		User user = prepayRequestDTO.getUser();
		String appid = user.getAppId();
		String fromSys = wechatPropConfig.getSysName();
		if (!StringUtils.isEmpty(appid)) {
			//TODO 下面静态引用以后改注入
			fromSys = SystemConfigServiceImpl.getSysMap().get(appid);
		}
		String requestUrl = getRequestUrl(user, prepayRequestDTO.getRegionName());
		requestUrl += WX_PAY_URL;
		
		PrepayRequest prepayRequest = new PrepayRequest(prepayRequestDTO);
		prepayRequest.setFromSys(fromSys);
		prepayRequest.setAppid(user.getAppId());
		
		TypeReference<HexieResponse<WechatPayInfo>> typeReference = new TypeReference<HexieResponse<WechatPayInfo>>(){};
		HexieResponse<WechatPayInfo> hexieResponse = wuyeRest(requestUrl, prepayRequest, typeReference);
		BaseResult<WechatPayInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}

	/**
	 * 获取优惠支付明细
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<Discounts> getDiscounts(DiscountViewRequestDTO discountViewRequestDTO) throws Exception {
		
		User user = discountViewRequestDTO.getUser();
		String requestUrl = getRequestUrl(user, discountViewRequestDTO.getRegionName());
		requestUrl += DISCOUNT_URL;
		
		DiscountViewRequest discountViewRequest = new DiscountViewRequest(discountViewRequestDTO);
		TypeReference<HexieResponse<Discounts>> typeReference = new TypeReference<HexieResponse<Discounts>>(){};
		HexieResponse<Discounts> hexieResponse = wuyeRest(requestUrl, discountViewRequest, typeReference);
		BaseResult<Discounts> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 获取优惠支付明细
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> queryOrder(User user, String orderNo) throws Exception {
		
		String requestUrl = getRequestUrl(user, "");
		requestUrl += QUERY_ORDER_URL;
		
		QueryOrderRequest queryOrderRequest = new QueryOrderRequest();
		queryOrderRequest.setOrderNo(orderNo);
		TypeReference<HexieResponse<String>> typeReference = new TypeReference<HexieResponse<String>>(){};
		HexieResponse<String> hexieResponse = wuyeRest(requestUrl, queryOrderRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 获取优惠支付明细
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> getPaySmsCode(User user, BankCard bankCard) throws Exception {
		
		String requestUrl = getRequestUrl(user, "");
		requestUrl += CARD_PAY_SMS_URL;
		
		PaySmsCodeRequest paySmsCodeRequest = new PaySmsCodeRequest();
		paySmsCodeRequest.setMobile(bankCard.getPhoneNo());
		paySmsCodeRequest.setQuickToken(bankCard.getQuickToken());
		TypeReference<HexieResponse<String>> typeReference = new TypeReference<HexieResponse<String>>(){};
		HexieResponse<String> hexieResponse = wuyeRest(requestUrl, paySmsCodeRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 其他收入支付
	 * @param otherPayDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<WechatPayInfo> requestOtherPay(OtherPayDTO otherPayDTO) throws Exception {
		
		String requestUrl = getRequestUrl(otherPayDTO.getUser(), "");
		requestUrl += OTHER_PAY_URL;
		OtherPayRequest otherPayRequest = new OtherPayRequest(otherPayDTO);
		
		TypeReference<HexieResponse<WechatPayInfo>> typeReference = new TypeReference<HexieResponse<WechatPayInfo>>(){};
		HexieResponse<WechatPayInfo> hexieResponse = wuyeRest(requestUrl, otherPayRequest, typeReference);
		BaseResult<WechatPayInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
	}
	
	public BaseResult<QrCodePayService> getQrCodePayService(User user) throws Exception {
		
		String requestUrl = getRequestUrl(user, "");
		requestUrl += QRCODE_PAY_SERVICE_URL;

		QrCodePayServiceRequest qrCodePayServiceRequest = new QrCodePayServiceRequest();
		qrCodePayServiceRequest.setOpenid(user.getOpenid());
		qrCodePayServiceRequest.setTel(user.getTel());
		
		TypeReference<HexieResponse<QrCodePayService>> typeReference = new TypeReference<HexieResponse<QrCodePayService>>(){};
		HexieResponse<QrCodePayService> hexieResponse = wuyeRest(requestUrl, qrCodePayServiceRequest, typeReference);
		BaseResult<QrCodePayService> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
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
	private <T extends WuyeRequest, V> HexieResponse<V> wuyeRest(String requestUrl, T jsonObject, TypeReference<HexieResponse<V>> typeReference)
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
			throw new BizValidateException("支付请求失败！ code : " + respEntity.getStatusCodeValue());
		}
		
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		HexieResponse<V> hexieResponse = objectMapper.readValue(respEntity.getBody(), typeReference);
		if (!"00".equals(hexieResponse.getResult())) {
			String errMsg = hexieResponse.getErrMsg();
			throw new BizValidateException(errMsg);
		}
		return hexieResponse;
	}
	
	/**
	 * 获取需要请求的服务器地址
	 * 给wuyeUtil2用的，以后都调用这个
	 * @param user
	 * @param regionName
	 * @return
	 */
	private String getRequestUrl(User user, String regionName) {
	
		//1.先从用户的自动定位取
		String targetUrl = "";
		if (!StringUtils.isEmpty(regionName)) {
			RegionUrl regionurl = locationService.getRegionUrlByName(regionName);
			if (regionurl == null) {
				logger.info("regionName : " + regionName + " 未能找到相应的配置链接。");
			}else {
				targetUrl = regionurl.getRegionUrl();
			}
			
		}
		//2.如果自动定位的地区在区域配置表中没有，则根据用户所属的公众号 取配置文件中默认的请求地址
		if (StringUtils.isEmpty(targetUrl)) {
			//TODO 下面2个静态引用以后改注入形式
			String userSysCode = SystemConfigServiceImpl.getSysMap().get(user.getAppId());	//获取用户所属的公众号
			RegionUrl regionUrl = LocationServiceImpl.getCodeUrlMap().get(userSysCode);	//根据公众号 获取请求地址
			targetUrl = wechatPropConfig.getRequestUrl();
			if (regionUrl!=null) {
				String urlLink = regionUrl.getRegionUrl();
				if (!StringUtils.isEmpty(urlLink)) {
					targetUrl = urlLink;
				}
			}
		}
		return targetUrl;
		
	}
	
	/**
	 * 对象转LinkedMultiValueMap，如果对象有jsonProperty注解，则取注解的value值
	 * @param fromObject
	 * @param destMap
	 */
	private void convertObject2Map(Object fromObject, LinkedMultiValueMap<String, String> destMap) {
		
		if (destMap == null) {
			return;
		}
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
				destMap.add(fieldName, field.get(fromObject)==null?"":String.valueOf(field.get(fromObject)));

			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	
	
}
