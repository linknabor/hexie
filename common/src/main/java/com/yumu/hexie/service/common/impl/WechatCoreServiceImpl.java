package com.yumu.hexie.service.common.impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.AccessToken;
import com.yumu.hexie.integration.wechat.entity.AccessTokenOAuth;
import com.yumu.hexie.integration.wechat.entity.MiniAccessToken;
import com.yumu.hexie.integration.wechat.entity.MiniUserPhone;
import com.yumu.hexie.integration.wechat.entity.UserMiniprogram;
import com.yumu.hexie.integration.wechat.entity.common.CloseOrderResp;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wechat.entity.common.PaymentOrderResult;
import com.yumu.hexie.integration.wechat.entity.common.PrePaymentOrder;
import com.yumu.hexie.integration.wechat.entity.common.WxRefundOrder;
import com.yumu.hexie.integration.wechat.entity.common.WxRefundResp;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.integration.wechat.service.FundService;
import com.yumu.hexie.integration.wechat.service.MiniprogramAuthService;
import com.yumu.hexie.integration.wechat.service.OAuthService;
import com.yumu.hexie.integration.wechat.service.RefundService;
import com.yumu.hexie.integration.wechat.service.UserService;
import com.yumu.hexie.integration.wechat.util.WeixinUtil;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.payment.RefundOrder;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.exception.WechatException;

@Service(value = "wechatCoreService")
public class WechatCoreServiceImpl implements WechatCoreService {

	private static final Logger SCHEDULE_LOG = LoggerFactory.getLogger("com.yumu.hexie.schedule");
	public AccessToken at;
	public String jsTicket = "";
	private static final Logger LOGGER = LoggerFactory.getLogger(WechatCoreServiceImpl.class);
	@Inject
	private SystemConfigService systemConfigService;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private MiniprogramAuthService miniprogramAuthService;
	
	@Override
	public JsSign getPrepareSign(String prepay_id) {
		try {
			return FundService.getPrepareSign(prepay_id);
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	private void processError(Exception e) {
		LOGGER.error("微信异常----------------------------------------------！！！！！！！！！！！", e);
		if (e instanceof WechatException) {
			SCHEDULE_LOG.error("process error" , e);
		}
	}

	@Override
	public JsSign getJsSign(String url, String appId) {
		try {
			return WeixinUtil.getJsSign(url, appId, systemConfigService.queryJsTickets(appId));
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	@Override
	public UserWeiXin getUserInfo(String appId, String openid) {
		try {
			return UserService.getUserInfo(openid, systemConfigService.queryWXAToken(appId));
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	@Override
	public UserWeiXin getByOAuthAccessToken(String code, String oriApp) {
		
		try {
			AccessTokenOAuth auth = null;
			LOGGER.info("is main app : " + AppUtil.isMainApp(oriApp));
			if (!AppUtil.isMainApp(oriApp)) {
				String componentAccessToken = redisRepository.getComponentAccessToken(ConstantWeChat.KEY_COMPONENT_ACESS_TOKEN);
				auth = OAuthService.getOAuthAccessToken(code, oriApp, componentAccessToken);
			}else {
				auth =  OAuthService.getOAuthAccessToken(code);
			}
	        return OAuthService.getUserInfoOauth(auth.getAccessToken(),auth.getOpenid());
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	@Override
	public PrePaymentOrder createOrder(PaymentOrder payOrder) {
		try {
			return FundService.createOrder(payOrder);
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	@Override
	public CloseOrderResp closeOrder( PaymentOrder payOrder) {
		try {
			return FundService.closeOrder(payOrder.getPaymentNo());
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	

	@Override
	public PaymentOrderResult queryOrder(String out_trade_no) {
		try {
			return FundService.queryOrder(out_trade_no);
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	@Override
	public WxRefundResp requestRefund(RefundOrder refund) {
		try {
			return RefundService.requestRefund(refund);
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	@Override
	public WxRefundOrder refundQuery(String outTradeNo) {
		try {
			return RefundService.refundQuery(outTradeNo);
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	
	/**
     * 通过code获取微信小程序用户信息
     *
     * @param code
     * @return
     * @throws Exception
     */
    @Override
    public UserMiniprogram getMiniUserSessionKey(String code) throws Exception {
        return miniprogramAuthService.getMiniUserSessionKey(code);
    }
    
    /**
     * 通过code获取微信小程序用户信息
     * @param miniAppid
     * @param code
     * @return
     * @throws Exception
     */
    @Override
    public UserMiniprogram getMiniUserSessionKey(String miniAppid, String code) throws Exception {
        return miniprogramAuthService.getMiniUserSessionKeyByAppid(miniAppid, code);
    }
    
    /**
     * 通过code获取微信小程序accessToken
     * @return
     * @throws Exception
     */
    @Override
    public MiniAccessToken getMiniAccessToken() throws Exception {
        return miniprogramAuthService.getMiniAccessToken();
    }
    
    /**
     * 通过code获取微信小程序accessToken
     * @return
     * @throws Exception
     */
    @Override
    public MiniAccessToken getMiniAccessToken(String miniAppid, String appSecret) throws Exception {
        return miniprogramAuthService.getMiniAccessToken(miniAppid, appSecret);
    }
    
    /**
     * 通过code获取微信小程序用户手机号，此code跟login的code不是同一个
     * @param code
     * @return
     * @throws Exception
     */
    @Override
    public MiniUserPhone getMiniUserPhone(String miniAppid, String code) throws Exception {
        return miniprogramAuthService.getPhoneNumber(miniAppid, code);
    }

	@Override
	public String getUnlimitedQrcode(User user, String path, String param) throws Exception {
		return miniprogramAuthService.getUnlimitedQrcode(user.getMiniAppId(),path, param);
	}


}
