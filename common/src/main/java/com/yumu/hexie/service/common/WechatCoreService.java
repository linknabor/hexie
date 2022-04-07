package com.yumu.hexie.service.common;

import com.yumu.hexie.integration.wechat.entity.UserMiniprogram;
import com.yumu.hexie.integration.wechat.entity.common.CloseOrderResp;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wechat.entity.common.PaymentOrderResult;
import com.yumu.hexie.integration.wechat.entity.common.PrePaymentOrder;
import com.yumu.hexie.integration.wechat.entity.common.WxRefundOrder;
import com.yumu.hexie.integration.wechat.entity.common.WxRefundResp;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.payment.RefundOrder;

public interface WechatCoreService {

	public JsSign getJsSign(String url, String appId);

	public UserWeiXin getUserInfo(String appId, String openid);
	public UserWeiXin getByOAuthAccessToken(String code, String oriApp);
	

	public PrePaymentOrder createOrder(PaymentOrder payOrder);
	public CloseOrderResp closeOrder(PaymentOrder payOrder);
	public PaymentOrderResult queryOrder(String out_trade_no);
	public JsSign getPrepareSign(String prepay_id) ;
	
	public WxRefundResp requestRefund(RefundOrder refund);
	public WxRefundOrder refundQuery(String outTradeNo);

	UserMiniprogram getMiniUserSessionKey(String code) throws Exception;
	
}