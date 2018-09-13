package com.yumu.hexie.service.home.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.daojia.haojiaan.HaoJiaAnReq;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.region.Merchant;
import com.yumu.hexie.model.distribution.region.MerchantRepository;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrder;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrderRepository;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnOrder;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnOrderRepository;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.AddressRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.home.HaoJiaAnService;
import com.yumu.hexie.service.user.UserNoticeService;
import com.yumu.hexie.vo.YuyueQueryOrder;
@Service("haoJiaAnService")
public class HaoJiaAnServiceImpl implements HaoJiaAnService{
	@Inject 
	private AddressRepository addressRepository;
	@Inject
	private HaoJiaAnOrderRepository haoJiaAnOrderRespository;
	@Inject
	private YuyueOrderRepository yuyueOrderRepository;
	@Inject
	private UserNoticeService userNoticeService;
	@Inject
	private MerchantRepository merchantRepository;
	@Inject
	private SystemConfigService systemConfigService;

	@Override
	public Long addNoNeedPayOrder(User user, HaoJiaAnReq haoJiaAnReq,
			long addressId) {
		Address address = addressRepository.findOne(addressId);
		haoJiaAnReq.setStrMobile(address.getTel());
		haoJiaAnReq.setStrName(address.getReceiveName());
		haoJiaAnReq.setStrWorkAddr(address.getRegionStr()+address.getDetailAddress());
		
		Merchant merchant = merchantRepository.findMerchantByProductType(ModelConstant.YUYUE_PRODUCT_TYPE_HAOJIAAN);
		
		//新增YuyueOrder
		YuyueOrder yOrder = new YuyueOrder();
		yOrder.setStatus(ModelConstant.ORDER_STAUS_YUYUE_SUCCESS);
		yOrder.setProductType(ModelConstant.YUYUE_PRODUCT_TYPE_HAOJIAAN);
		yOrder.setMerchantId(merchant.getId());
		yOrder.setProductName(haoJiaAnReq.getServiceTypeName());
		yOrder.setPrice(haoJiaAnReq.getPrices());
		yOrder.setPaymentType(haoJiaAnReq.getPaymentType());
		yOrder.setAddress(haoJiaAnReq.getStrWorkAddr());
		yOrder.setTel(haoJiaAnReq.getStrMobile());
		yOrder.setReceiverName(haoJiaAnReq.getStrName());
		yOrder.setWorkTime(haoJiaAnReq.getExpectedTime());
		yOrder.setUserId(user.getId());
		yOrder.setMemo(haoJiaAnReq.getCustomerMemo());
		yOrder = yuyueOrderRepository.save(yOrder);
		
		//新增HaoJiaAnOrder
		HaoJiaAnOrder hOrder = new HaoJiaAnOrder();
		hOrder.setyOrderId(yOrder.getId());
		hOrder.setServiceTypeName(haoJiaAnReq.getServiceTypeName());
		hOrder.setUserId(user.getId());
		hOrder.setPaymentType(haoJiaAnReq.getPaymentType());
		hOrder.setPayStatus(ModelConstant.YUYUE_PAYSTATUS_INIT);
		hOrder.setPrices(haoJiaAnReq.getPrices());
		hOrder.setExpectedTime(haoJiaAnReq.getExpectedTime());
		hOrder.setStrMobile(haoJiaAnReq.getStrMobile());
		hOrder.setStrName(haoJiaAnReq.getStrName());
		hOrder.setStrWorkAddr(haoJiaAnReq.getStrWorkAddr());
		hOrder.setMemo(haoJiaAnReq.getCustomerMemo());
		hOrder.setServiceStatus(ModelConstant.YUYUE_SERVICE_STATUS_UNUSED);
		hOrder = haoJiaAnOrderRespository.save(hOrder);
		
		userNoticeService.yuyueSuccess(user.getId(), yOrder.getTel(), yOrder.getReceiverName(), yOrder.getId(), yOrder.getProductName(), ModelConstant.YUYUE_PAYMENT_TYPE_OFFLINE, 0);
		String accessToken = systemConfigService.queryWXAToken();
		TemplateMsgService.sendHaoJiaAnAssignMsg(hOrder, user.getOpenid(), accessToken);//发送模板消息
		return yOrder.getId();
	}

	@Override
	public YuyueQueryOrder queryYuYueOrder(User user, long orderId) {

		YuyueOrder yuyueOrder = yuyueOrderRepository.findOne(orderId);
		YuyueQueryOrder yuyueQueryOrder = new YuyueQueryOrder();
		yuyueQueryOrder.setOrderId(yuyueOrder.getId());
		yuyueQueryOrder.setAddress(yuyueOrder.getAddress());
		yuyueQueryOrder.setMemo(yuyueOrder.getMemo());
		yuyueQueryOrder.setReceiverName(yuyueOrder.getReceiverName());
		yuyueQueryOrder.setServiceTypeName(yuyueOrder.getProductName());
		yuyueQueryOrder.setTel(yuyueOrder.getTel());
		yuyueQueryOrder.setWorkTime(yuyueOrder.getWorkTime());
		return yuyueQueryOrder;
	}
	
}
