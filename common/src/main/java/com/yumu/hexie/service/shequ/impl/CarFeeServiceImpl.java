package com.yumu.hexie.service.shequ.impl;

import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.MyHttpClient;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.CarFeeInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.service.shequ.CarFeeService;

@Service("carFeeService")
public class CarFeeServiceImpl implements CarFeeService{

	@Value("${requestUrl}")
	private String requestUrl;
	
	private static final String STOPCAR_FEE_URL = "getStopCarFeeByDriveNoSDO.do?device_no=%s"; // 停车费用
	private static final String STOPCAR_PAY_URL = "wechatPayTempCarSDO.do?user_id=%s&water_id=%s&openid=%s"; // 停车费用支付
	
	@Override
	public CarFeeInfo getCarFeeByDriveNo(String device_no) {
		String url = requestUrl + String.format(STOPCAR_FEE_URL, device_no);
		BaseResult<CarFeeInfo> carFeeInfo = httpGet(url,CarFeeInfo.class);
		return carFeeInfo.getData();
	}

	@Override
	public WechatPayInfo getCarPayByWaterId(String userId,String water_id,String openid) {
		String url = requestUrl + String.format(STOPCAR_PAY_URL, userId,water_id,openid);
		BaseResult<WechatPayInfo> wechatPayInfo = httpGet(url,WechatPayInfo.class);
		return wechatPayInfo.getData();
	}
	
	
	private static BaseResult httpGet(String reqUrl, Class c){
		HttpGet get = new HttpGet(reqUrl);
		get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
		String resp;
		BaseResult v = null;
		try {
			resp = MyHttpClient.getStringFromResponse(MyHttpClient.execute(get),"GBK");
			v =WuyeUtil.jsonToBeanResult(resp, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

}
