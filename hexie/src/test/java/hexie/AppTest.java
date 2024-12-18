package hexie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yumu.hexie.common.config.AppConfig;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.alipay.service.AliTemplateMsgService;
import com.yumu.hexie.integration.baidu.BaiduMapUtil;
import com.yumu.hexie.integration.baidu.resp.GeoCodeRespV2;
import com.yumu.hexie.integration.beyondsoft.BeyondSoftUtil;
import com.yumu.hexie.integration.beyondsoft.resp.BeyondSoftToken;
import com.yumu.hexie.integration.notify.Operator;
import com.yumu.hexie.integration.notify.WorkOrderNotification;
import com.yumu.hexie.integration.wechat.constant.ConstantAlipay;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.cache.CacheService;
import com.yumu.hexie.service.notify.NotifyService;
import com.yumu.hexie.service.user.UserService;

import hexie.CreateCommReq.CommunityPropertyCompany;
import hexie.CreateCommReq.CommunityService;
import junit.framework.TestCase;

/**
 * Unit test for simple App.
 * 
 * @param <T>
 * @param <T>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppConfig.class)
public class AppTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(AppTest.class);

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Test
	public void testRedisTemplate() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("tom", "cat");
		map.put("jerry", "mouse");
		
		
		Map<String, Map<String, String>> map2 = new HashMap<String, Map<String, String>>();
		
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("aa", "a");
		map3.put("bb", "b");
		
		map2.put("222", map3);
		
		map.put("tom", "cat");
		map.put("jerry", "mouse");
		
		redisTemplate.opsForHash().putAll("test1", map);
		redisTemplate.opsForHash().putAll("test2", map2);
		
	}
	
	@Autowired
	private BaiduMapUtil baiduMapUtil;
	
	@Test
	public void testBaiduMap() {
		
//		String str = baiduMapUtil.findByBaiduGetCity("106.74590655179391,26.671247860481976");
//		System.out.println("str:" + str);
//		
//		String str2 = baiduMapUtil.findByCoordinateGetBaidu("121.4737,31.23037");
//		System.out.println("str2:" + str2);
//		
//		String str3 = baiduMapUtil.findByBaiduGetCity("");
//		System.out.println("str3:" + str3);
//		
//		String str4 = baiduMapUtil.findByCoordinateGetBaidu("");
//		System.out.println("str4:" + str4);
//		
//		String str5 = baiduMapUtil.findByBaiduGetCity("null");
//		System.out.println("str5:" + str5);
//		
//		String str6 = baiduMapUtil.findByCoordinateGetBaidu("null");
//		System.out.println("str6:" + str6);
		
		String coordinate = "121.55054432318816,31.22738971073461";
		String covertedCoord = baiduMapUtil.findByCoordinateGetBaidu(coordinate);
		log.info("covertedCoord : " + covertedCoord);
		GeoCodeRespV2 geoCodeResp = baiduMapUtil.getLocationByCoordinateV2(covertedCoord);
		log.info("geoCodeResp: {}", geoCodeResp.getResult());
		
	}

	@Test
	public void readPropFile () throws IOException{
		
		Properties sysProp = System.getProperties();
		String password = sysProp.getProperty("jasypt.encryptor.password");
		if (StringUtils.isEmpty(password)) {
			password = "x5VGvitCcPMa4ZXcl1vYkG2LOzviOLa7";
		}
		System.out.println("password : " + password);
		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);
        
        String appid = "DYjSXu8Ish/vULsbsphsInHNiAaU+9XDL9b8d0/i8yY=";
        String dencryptValue = textEncryptor.decrypt(appid);
        System.out.println("appid:" + dencryptValue);
        
        String testAppid = "3hrvJGP/qx09pQtUS7F/R5dWrwGc2cgtib77w3IxySI=";
        String testAppidDecrypt = textEncryptor.decrypt(testAppid);
        System.out.println("test appid:" + testAppidDecrypt);
        
        String appSecret = "4GTATCg3Q3FLZ6GctXKg7hWQ5Y6QZ9Nr/VnHK0GQeBmlsjoDAuT8TA==";
        String dencryptValue2 = textEncryptor.decrypt(appSecret);
        System.out.println("appSecret:" + dencryptValue2);
        
        String mapKey = "wiao2YKi8DRc27UNfhkpeFZPR7xNPjkiQEF5bvzhZiWHkELv5JUMrO2X0+kxp+bs";
        String dencryptValue3 = textEncryptor.decrypt(mapKey);
        System.out.println("mapKey:" + dencryptValue3);
		
	}
	
	@Test
	public void encrptProps() {
		
		//below for test
		String baseUrl = "http://xibu-devel.omniview.pro:30721";
		String loginUri = "/api/v2/public/shareData/login";
		String statisticUri = "/api/v2/service-special-xb-ddd/westernSmallProgramStatistics";
		String username = "xibushare";
		String password = "123adxiqwoj!qqq..";
		String appkey = "70523a6315f9e192e7ab9d11ea2017e1";
		
		//below for prod
		baseUrl = "http://www.westgroup.com.cn";
		loginUri = "/api/v2/public/shareData/login";
		statisticUri = "/api/v2/service-special-xb-ddd/westernSmallProgramStatistics";
		username = "xibuprod";
		password = "12eddqsa!!ooo";
		appkey = "70523a6315f9e192e7ab9d11ea2017e1";
		
		String encrpytKey = "x5VGvitCcPMa4ZXcl1vYkG2LOzviOLa7";
		System.out.println("encrpytKey : " + encrpytKey);
		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(encrpytKey);
        
        String encryBaseUrl = textEncryptor.encrypt(baseUrl);
        String encryLoginUri = textEncryptor.encrypt(loginUri);
        String encryStatisticUri = textEncryptor.encrypt(statisticUri);
        String encryUsername = textEncryptor.encrypt(username);
        String encryPassword = textEncryptor.encrypt(password);
        String encryAppkey = textEncryptor.encrypt(appkey);
        
        log.info("encryBaseUrl: {}", encryBaseUrl);
        log.info("encryLoginUri: {}", encryLoginUri);
        log.info("encryStatisticUri: {}", encryStatisticUri);
        log.info("encryUsername: {}", encryUsername);
        log.info("encryPassword: {}", encryPassword);
        log.info("encryAppkey: {}", encryAppkey);
		
	}
	
	@Resource
	private BeyondSoftUtil beyondSoftUtil;

	@Test
	public void testGetToken() throws Exception {
		
		BeyondSoftToken token = beyondSoftUtil.getAccessToken();
		log.info("token : {}", token);
	}
	
	@Resource
	private AlipayClient alipayClient;
	@Test
	public void testAlipayPreCreateSign() throws AlipayApiException, JsonProcessingException {
		
		AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        
        Map<String, String> reqMap = new HashMap<>();
        String orderId = "241012115752987280";
		reqMap.put("out_trade_no", orderId);
		reqMap.put("product_code", "FAST_INSTANT_TRADE_PAY");
		reqMap.put("code_type", "share_code");
		reqMap.put("subject", "物业缴费");
		reqMap.put("total_amount", "0.02");
        
		String bizContent = JacksonJsonUtil.getMapperInstance(false).writeValueAsString(reqMap);
		request.setBizContent(bizContent);
		log.info("alipay appid : " + ConstantAlipay.APPID);
		AlipayTradePrecreateResponse alipayResp = alipayClient.execute(request);
		log.info("alipayResp : {} : " + alipayResp);
		
	}
	
	@Test
	public void testAlipayRespVerify() throws AlipayApiException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg", "Business Failed");
		jsonObject.put("code", "40004");
		jsonObject.put("sub_msg", "未知的错误码ISV_NOT_EXIST");
		jsonObject.put("sub_code", "unknown-sub-code");
		
		Map<String, String> params = new HashMap<>();
		params.put("alipay_ebpp_community_thirdpartycommunity_create_response", jsonObject.toString());

		params.put("sign", "T139Dn2TTNEDYZEBiRtijxZjdwq6GElcrdq6xX7sEgdjbcX6UaQS/sztMfZfH2h3BGL3zxkIYeAGjNfDNYfnJaG+GPMC4pjJxqeLoY1hcDnld4l7arq7ij2M466F1rnP/STLLg0MnhPHvO2MvcwdJAEPBzI81WEgHOg0vlj5cgWu3WwXiBUkL4QdS5c9ZPP96Emd8gS2CMNMIs3pt4WQss0hzM7yUhUW0UoIU63e9SZsxMjdaU8kC1sZiueE53+4BPil5/c/xtuzOv4kEZHgAGCpcGNgzoPkEnH8JAjaZoAH3BFJWp3cxj/HPendkx8BVXX3s/Q6m5CDL2BCEHGSQA==");
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuT+DS868MtV6nb9p57/R/Mg3bzG/30vwxQrCFhkULmT1/WRtPy3JVPDHIEZy27rEzyY4BN7zyGPDG1aULAzn3Poun8/cF/qb9yRlHXRt5ptPM++9euULhvraqzCB3dapkI3AZVs3+d71T+edBeepazB/4zxSf3GumQUbkzGxr2BTFM2ocfJn8iQtSdsJ6uCBWMFGY0XZn3jxeANbLDzfEaSbMhmpO3g8ipv0stbtx4RPTDTtiYK3cY+eDy0a257OBnrL3JYd4846PBfYUFQy3qgRLTzPKzhsACdYzHkBJ5Fvy1AoHVBAnyqTCV9kdEWa1xA+NgaZbSzXzgdSAV+HMwIDAQAB";
		boolean valid = AlipaySignature.verifyV1(params, publicKey, "utf-8", "RSA2");
		System.out.println(valid);
	}
	
	@Test
	public void testAlipayJsonWriter() {
		CreateCommReq req = new CreateCommReq();
		req.setName("锦绣花苑二期");
		req.setOut_community_id("180427100113842987");
		req.setAlias("");
		req.setSupport_type("EXTERNAL_BIND_BILL");
		req.setAddress("嘉定区白银路1200弄1号");
		req.setLatitude("31.224333");
		req.setLongitude("121.46895");
		req.setVerify_type("verify_type");
		req.setProvince("");
		req.setCity("");
		
		CommunityPropertyCompany company = new CommunityPropertyCompany();
		company.setName("测试物业z");
		company.setPid("2088001297005270");
		company.setOpen_id("open_id");
		company.setScale("7");
		req.setCommunity_property_company(company);
		
		CommunityService service = new CommunityService();
		service.setServce_type("THIRD_PARTY_COMMUNITY_JIAOFEI");
		service.setDaily_start("00:00");
		service.setDaily_end("24:00");
		service.setBillkey_url("alipays://platformapi/startapp?appId=XXX&page=%2Fpages%2Fcommunity%2Fhouseaccountquery%2Fhouseaccountout-");
		service.setOut_bill_url("alipays://platformapi/startapp?appId=XXX&page=%2Fpages%2Fcommunity%2Fhouseaccountquery%2Fhouseaccount-");
		
		CommunityService[]services = new CommunityService[1];
		services[0] = service;
		req.setCommunity_service(services);
		
		com.alipay.api.internal.util.json.JSONWriter jsonWriter = new com.alipay.api.internal.util.json.JSONWriter();
		String str = jsonWriter.write(req, true);
		System.out.println(str);
	}
	
	@Resource
	private UserRepository userRepository;
	
	@Test
	public void testFindByReturn() {
		List<User> userList = userRepository.findByAliuseridAndAliappid("0", "123");
		log.info("userList : {}", userList);
		
		User user = userRepository.findById(99900999L);
		log.info("user : {}", user);
	}
	
	@Test
	public void testFindEmptyFirst() {
		List<User> userList = userRepository.findByAliuserid("111");
//		log.info("size : {} ", userList.size());
////		User user = userList.stream().findFirst().orElse(null);
////		log.info("user : {}", user);
//		
//		User user = userList.stream().filter(u -> !StringUtils.isEmpty(u.getTel())).findFirst().orElse(null);
//		log.info("user : {}", user);
		
		
		User dbUser = userList.stream().filter(u -> u.getOpenid().equals("567")).findFirst().orElse(null);
		log.info("dbUser : {}", dbUser );
	}
	
	@Resource
	private CacheService cacheService;
	
	@Test
	public void testClearCache() {
		User user = new User();
		user.setAliappid("2021004116648237");
		user.setAliuserid("2088312129787880");
//		cacheService.clearUserCache(user);
	}
	
	@Resource
	private UserService userService;
	
	@Test
	public void testCacheable() {
		userService.getUserByAliUserIdAndAliAppid("2088312129787880", "2021004116648237");
		
	}
	
	@Resource
	private AliTemplateMsgService aliTemplateMsgService;
	
	@Test
	public void testAliMsg() throws UnsupportedEncodingException {
		WorkOrderNotification workOrderNotification = new WorkOrderNotification();
		Operator operator = new Operator();
		operator.setAppid("2021004116648237");
//		operator.setAppid("2021001161682727");
//		operator.setOpenid("2088402083298963");
		operator.setOpenid("2088312129787880");
		List<Operator> opList = new ArrayList<>();
		opList.add(operator);
		workOrderNotification.setOperatorList(opList);
		workOrderNotification.setContent("");
		workOrderNotification.setOperation("05");
		workOrderNotification.setOrderType("维修工单");
		workOrderNotification.setContent("320-201的车辆占用主干道需要挪车");
		workOrderNotification.setOrderStatus("已受理");
		aliTemplateMsgService.sendWorkOrderMsg(workOrderNotification);
	}
	
	
	@Test
	public void testAliMsg2() {
		
		WorkOrderNotification woNotification = new WorkOrderNotification();
		woNotification.setOrderId("241211100742675709");
		woNotification.setOrderType("维修");
		woNotification.setOrderStatus("已完工");
		woNotification.setOperateDate("2024-12-02 08:35:59");
		woNotification.setSectName("音乐广场");
		woNotification.setContent("12号楼底层0304电梯厅照明三个筒灯己坏二个，已有几个月了，请尽快修复！！！");
		woNotification.setServeAddress("12号楼底层0304室电梯厅照明己坏了二只，己有几个月了，请尽快修复！！！");
		woNotification.setOrderSource("公众号");
		woNotification.setDistType("公共");
		woNotification.setReason("工单完工");
		woNotification.setAcceptor("王仁林");
		woNotification.setFinisher("王仁林");
		woNotification.setOperation("07");
		
		List<Operator> opList = new ArrayList<>();
		
		Operator operator = new Operator();
		operator.setAppid("2021004116648237");
		operator.setOpenid("2088312129787880");
//		operator.setOpenid("2088402083298963");
		
		opList.add(operator);
		woNotification.setOperatorList(opList);
		aliTemplateMsgService.sendWorkOrderMsg(woNotification);
	}
}
