package com.yumu.hexie.service.shequ.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayEbppCommunityDeliveryidentityDetectModel;
import com.alipay.api.request.AlipayEbppCommunityDeliveryidentityDetectRequest;
import com.alipay.api.response.AlipayEbppCommunityDeliveryidentityDetectResponse;
import com.yumu.hexie.integration.alipay.service.AliSDKService;
import com.yumu.hexie.integration.wuye.WuyeUtil2;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.UserAccessSpotResp;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.UserAccessService;
import com.yumu.hexie.service.shequ.req.UserAccessRecordReq;

@Service
public class UserAccessServiceImpl implements UserAccessService {

	private static Logger log = LoggerFactory.getLogger(UserAccessServiceImpl.class);
	
	@Resource
	private WuyeUtil2 wuyeUtil2;
	@Resource
	private AliSDKService aliSDKService;
	
	/**
	 * 获取门禁点信息
	 */
	@Override
	public UserAccessSpotResp getAccessSpot(User user, String spotId) throws Exception {
		BaseResult<UserAccessSpotResp> baseResult = wuyeUtil2.getAccessSpot(user, spotId);
		if (!StringUtils.isEmpty(baseResult.getMessage())) {
			throw new BizValidateException(baseResult.getMessage());
		}
		return baseResult.getData();
	}
	
	/**
	 * 保存门禁记录
	 * @param saveAccessRecordReq
	 * @throws Exception 
	 */
	@Override
	public void saveAccessRecord(User user, UserAccessRecordReq userAccessRecordReq) throws Exception {
		userAccessRecordReq.setUid(user.getAliuserid());
		userAccessRecordReq.setAppid(user.getAliappid());
		if ("外卖".equals(userAccessRecordReq.getReason())) {
			Boolean dectResult = detectDelivery(user);
			if (Boolean.TRUE.equals(dectResult)) {
				userAccessRecordReq.setRole("外卖员");
			}
		}
		BaseResult<String> baseResult = wuyeUtil2.saveUserAccessRecord(user, userAccessRecordReq);
		if (!StringUtils.isEmpty(baseResult.getMessage())) {
			throw new BizValidateException(baseResult.getMessage());
		}
	}

	/**
	 * 鉴定外卖员身份以及是否有外卖在该小区
	 * @param user
	 */
	private Boolean detectDelivery(User user) {
		Boolean dectResult = Boolean.FALSE;
		try {
			// 需要调用支付宝接口获取访客角色
			AlipayEbppCommunityDeliveryidentityDetectRequest request = new AlipayEbppCommunityDeliveryidentityDetectRequest();
			AlipayEbppCommunityDeliveryidentityDetectModel model = new AlipayEbppCommunityDeliveryidentityDetectModel();
			// uid参数未来计划废弃，存量商户可继续使用，新商户请使用openid。请根据应用-开发配置-openid配置选择支持的字段。
			model.setDeliveryUserId(user.getAliuserid());
			// 设置骑手支付宝的openId
			// model.setDeliveryOpenId("074a1CcTG1LelxKe4xQC0zgNdId0nxi95b5lsNpazWYoCo5");
			// 设置小区ID
			model.setCommunityId("0658f6a8-6a85-11ef-87f2-043f72e50312");	//TODO
			// 设置物业编码
			model.setCompanyCode("CHENGYUZHUFANG");	//TODO
			// 设置物业公司名
			model.setCompanyName("绿城");
			// 设置城市名
			model.setCityName("杭州");
			// 设置小区名
			model.setCommunityName("浙江杭州西溪雅苑");
			request.setBizModel(model);
			
			AlipayClient alipayClient = aliSDKService.getClient(user.getAliappid());
			AlipayEbppCommunityDeliveryidentityDetectResponse response = alipayClient.execute(request);
			log.info(response.getBody());
			if (response.isSuccess()) {
				dectResult = response.getDetectResult();	//是否快递员并且有外卖在当前小区
			} else {
			    // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
			    // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return dectResult;
	}

}
