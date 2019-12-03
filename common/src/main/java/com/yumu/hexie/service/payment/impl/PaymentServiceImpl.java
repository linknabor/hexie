/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.payment.impl;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.common.CloseOrderResp;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wechat.entity.common.WxRefundOrder;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.localservice.basemodel.BaseO2OService;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.payment.PaymentConstant;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.payment.PaymentOrderRepository;
import com.yumu.hexie.model.payment.RefundOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.payment.PaymentService;
import com.yumu.hexie.service.payment.RefundService;
import com.yumu.hexie.service.user.UserService;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: PaymentServiceImpl.java, v 0.1 2016年4月1日 下午3:51:22  Exp $
 */
@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {


    protected static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Inject
    protected PaymentOrderRepository paymentOrderRepository;
    @Inject
    protected WechatCoreService wechatCoreService;
    @Inject
    protected RefundService refundService;
    @Autowired
    protected UserService userService;
    /** 
     * @param order
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#fetchPaymentOrder(com.yumu.hexie.model.market.ServiceOrder)
     */
    @Override
    public PaymentOrder fetchPaymentOrder(ServiceOrder order) {
        List<PaymentOrder> pos = paymentOrderRepository.findByOrderTypeAndOrderId(PaymentConstant.TYPE_MARKET_ORDER,order.getId());
        if(pos != null && pos.size()>0) {
            return pos.get(0);
        } else {
            return new PaymentOrder(order);
        }
    }

    /** 
     * @param order
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#fetchPaymentOrder(com.yumu.hexie.model.localservice.basemodel.BaseO2OService)
     */
    @Override
    public PaymentOrder fetchPaymentOrder(BaseO2OService order,String openId) {
        List<PaymentOrder> pos = paymentOrderRepository.findByOrderTypeAndOrderIdAndOpenId(order.getPaymentOrderType(),order.getId(),openId);
        if(pos != null && pos.size()>0) {
            return pos.get(0);
        } else {
            return paymentOrderRepository.save(new PaymentOrder(order,openId));
        }
    }

    /** 
     * @param orderType
     * @param orderId
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#queryPaymentOrder(int, long)
     */
    @Override
    public PaymentOrder queryPaymentOrder(int orderType, long orderId) {
        List<PaymentOrder> pos = paymentOrderRepository.findByOrderTypeAndOrderId(orderType, orderId);
        if(pos.size() > 0){
            for(PaymentOrder p : pos) {
                if(p.getStatus() != PaymentConstant.PAYMENT_STATUS_FAIL){
                    return p;
                }
            }
        }
        return null;
    }

    /** 
     * @param order
     * @see com.yumu.hexie.service.payment.PaymentService#payOffline(com.yumu.hexie.model.localservice.basemodel.BaseO2OService)
     */
    @Override
    public void payOffline(BaseO2OService order) {
    }

    /** 
     * @param payment
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#requestPay(com.yumu.hexie.model.payment.PaymentOrder)
     */
    @Override
    public JsSign requestPay(User user, PaymentOrder pay) {
        validatePayRequest(pay);
        log.warn("[Payment-req]["+pay.getPaymentNo()+"]["+pay.getOrderId()+"]["+pay.getOrderType()+"]");
        //支付然后没继续的情景=----校验所需时间较长，是否需要如此操作
        try {
        	
            if(checkPaySuccess(user, pay.getPaymentNo())){
                throw new BizValidateException(pay.getId(),"订单已支付成功，勿重复提交！").setError();
            }
            paymentOrderRepository.save(pay);
            log.warn("获取预支付id前——————————前：");
        	WechatPayInfo payinfo = WuyeUtil.getMemberPrePayInfo(user, pay.getPaymentNo(), pay.getPrice(),ConstantWeChat.NOTIFYURL).getData();
        	log.warn("获取预支付id后——————————后：");
        	JsSign sign = new JsSign();
        	sign.setAppId(payinfo.getAppid());
        	sign.setNonceStr(payinfo.getNoncestr());
        	sign.setPkgStr(payinfo.getPackageValue());
        	sign.setSignature(payinfo.getPaysign());
        	sign.setTimestamp(payinfo.getTimestamp());
        	sign.setSignType(payinfo.getSigntype());
        	return sign;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
        return null;
    }
    
    private void validatePayRequest(PaymentOrder pay) {
        log.error("validatePayRequest:paymentNo:" +pay.getPaymentNo());
        if(PaymentConstant.PAYMENT_STATUS_SUCCESS==pay.getStatus()
                ||PaymentConstant.PAYMENT_STATUS_REFUNDED==pay.getStatus()
                ||PaymentConstant.PAYMENT_STATUS_REFUNDING==pay.getStatus()){
            throw new BizValidateException(pay.getOrderId(),"订单状态已成功支付或已退款，请重新查询确认订单状态！").setError();
        } else if(PaymentConstant.PAYMENT_STATUS_CANCEL==pay.getStatus()
                ||PaymentConstant.PAYMENT_STATUS_FAIL==pay.getStatus()){
            pay.refreshOrder();
        }
    }

    @SuppressWarnings("rawtypes")
	private boolean checkPaySuccess(User user, String paymentNo) throws Exception{
        log.warn("[Payment-check]begin["+paymentNo+"]");
        
        BaseResult baseResult = WuyeUtil.queryOrderInfo(user, paymentNo);
        log.warn("baseResult:"+baseResult.getResult());
        return baseResult.getResult().equals("SUCCESS");//表示交易成功
    }
    /** 
     * @param payment
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#refreshStatus(com.yumu.hexie.model.payment.PaymentOrder)
     */
    @SuppressWarnings("rawtypes")
	@Override
    public PaymentOrder refreshStatus(PaymentOrder payment) {
        log.warn("[Payment-refreshStatus]begin["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
        if(payment.getStatus() != PaymentConstant.PAYMENT_STATUS_INIT){
            return payment;
        }
        try {
        	User user = userService.getById(payment.getUserId());
			BaseResult baseResult = WuyeUtil.queryOrderInfo(user, payment.getPaymentNo());
	        if("USERPAYING".equals(baseResult.getResult())) {//1. 支付中
	            log.warn("[Payment-refreshStatus]isPaying["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
	            return payment;
	        } else if("FAIL".equals(baseResult.getResult())) {//2. 失败
	            log.warn("[Payment-refreshStatus]isPayFail["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
	            payment.fail();
	        } else if ("SUCCESS".equals(baseResult.getResult())) {//5. 成功
	            log.warn("[Payment-refreshStatus]isPaySuccess["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
	            payment.paySuccess("532858859");//没有此id
	        }
        } catch (Exception e) {
			log.error("err msg :" + e.getMessage());
		}
        return paymentOrderRepository.save(payment);
    }

    @Override
    public PaymentOrder cancelPayment(int orderType,long orderId) {
        log.warn("[Payment-cancelPayment]begin["+orderType+"]["+orderId+"]");
        PaymentOrder po = this.queryPaymentOrder(orderType, orderId);
        if(po == null) {
            return null;
        }
        if(po.getStatus() == PaymentConstant.PAYMENT_STATUS_SUCCESS
                ||po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDING
                ||po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDED) {
        	log.warn("[Payment-cancelPayment]begin["+orderType+"]["+orderId+"],该订单已支付成功，无法取消！");
        	return null;
            //throw new BizValidateException(po.getOrderId(),"该订单已支付成功，无法取消！").setError();
        }
        CloseOrderResp c = wechatCoreService.closeOrder(po);
		if (c==null) {
			throw new BizValidateException(po.getOrderId(), "网络异常，无法查询支付状态！").setError();
		}
        if(!c.isCloseSuccess()){
            throw new BizValidateException(po.getOrderId(),"该订单支付中，无法取消！").setError();
        } else if(c.isOrderPayed()) {
            po.setStatus(PaymentConstant.PAYMENT_STATUS_SUCCESS);
            po.setUpdateDate(System.currentTimeMillis());
            paymentOrderRepository.save(po);

            //FIXME 是否事务会回滚，需要注意
            throw new BizValidateException(po.getOrderId(),"该订单已支付成功，无法取消！").setError();
        }
        po.setStatus(PaymentConstant.PAYMENT_STATUS_CANCEL);
        po.setUpdateDate(System.currentTimeMillis());
        log.warn("[Payment-cancelPayment]end["+orderType+"]["+orderId+"]");
        return paymentOrderRepository.save(po);
    }

    /** 
     * @param paymentId
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#refundApply(long)
     */
    @Override
    public boolean refundApply(PaymentOrder po) {
        log.warn("[Payment-refundApply]begin["+po.getOrderType()+"]["+po.getId()+"]");
        if(po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDED
                || po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDING){
            return true;
        }
        validateRefundPayment(po);
        try{
            if(!wechatCoreService.queryOrder(po.getPaymentNo()).isPaySuccess()){
                log.warn("[Payment-refundApply]notsuccess["+po.getOrderType()+"]["+po.getId()+"]");
                po.setStatus(PaymentConstant.PAYMENT_STATUS_CANCEL);
                po.setUpdateDate(System.currentTimeMillis());
                paymentOrderRepository.save(po);
            }
        }catch(Exception e) {
            po.setStatus(PaymentConstant.PAYMENT_STATUS_CANCEL);
            po.setUpdateDate(System.currentTimeMillis());
            paymentOrderRepository.save(po);
        }
        RefundOrder ro = new RefundOrder(po);
        log.warn("[Payment-refundApply]end["+po.getOrderType()+"]["+po.getId()+"]");
        return refundService.refundApply(ro);
    }
    private void validateRefundPayment(PaymentOrder po) {
        if(PaymentConstant.PAYMENT_STATUS_SUCCESS != po.getStatus()
                &&PaymentConstant.PAYMENT_STATUS_INIT != po.getStatus()) {
            throw new BizValidateException(po.getOrderId(),"该支付记录无法退款！").setError();
        }
    }

    /** 
     * @param wxRefundOrder
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#updateRefundStatus(com.yumu.hexie.integration.wechat.entity.common.WxRefundOrder)
     */
    @Override
    public PaymentOrder updateRefundStatus(WxRefundOrder wxRefundOrder) {
        log.warn("[Payment-updateRefundStatus]begin["+wxRefundOrder.getOut_trade_no()+"]");
        RefundOrder ro = refundService.updateRefundStatus(wxRefundOrder);
        if(ro == null) {
            return null;
        }
        PaymentOrder po = paymentOrderRepository.findOne(ro.getPaymentId());
        if (ro.getRefundStatus() == PaymentConstant.REFUND_STATUS_SUCCESS) {
            po.refunded();
        } else if (ro.getRefundStatus() == PaymentConstant.REFUND_STATUS_APPLYED) {
            po.setStatus(PaymentConstant.PAYMENT_STATUS_REFUNDING);
        }
        return paymentOrderRepository.save(po);
    }

    /** 
     * @param paymentNo
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#findByPaymentNo(java.lang.String)
     */
    @Override
    public PaymentOrder findByPaymentNo(String paymentNo) {
        List<PaymentOrder> pos = paymentOrderRepository.findAllByPaymentNo(paymentNo);
        if(pos != null && pos.size()>=0) {
            return pos.get(0);
        }
        return null;
    }

    /** 
     * @param pay
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#save(com.yumu.hexie.model.payment.PaymentOrder)
     */
    @Override
    public PaymentOrder save(PaymentOrder pay) {
        return paymentOrderRepository.save(pay);
    }

}
