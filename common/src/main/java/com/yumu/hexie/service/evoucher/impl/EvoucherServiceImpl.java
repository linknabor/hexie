package com.yumu.hexie.service.evoucher.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.OrderNoUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.EvoucherRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.evoucher.EvoucherService;
import com.yumu.hexie.service.exception.BizValidateException;

public class EvoucherServiceImpl implements EvoucherService {

	@Autowired
	private EvoucherRepository evoucherRepository;
	@Autowired
	private EshopUtil eshopUtil;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private ProductRepository productRepository;
	
	/**
	 * 创建优惠券
	 * @param serviceOrder
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void createEvoucher(ServiceOrder serviceOrder) {
		
		if (ModelConstant.ORDER_TYPE_EVOUCHER != serviceOrder.getOrderType()) {
			return;
		}
		Product product = productRepository.findOne(serviceOrder.getProductId());
		float totalPrice = 0;
		for (int i = 0; i < serviceOrder.getCount(); i++) {
			
			Evoucher evoucher = new Evoucher();
			evoucher.setCode(OrderNoUtil.generateEvoucherNo());
			evoucher.setOrderId(serviceOrder.getId());
			evoucher.setActualPrice(product.getSinglePrice());	//实际销售价
			evoucher.setOriPrice(product.getOriPrice());	//原价
			evoucher.setProductId(serviceOrder.getProductId());
			evoucher.setProductName(serviceOrder.getProductName());
			evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_INIT);
			evoucher.setUserId(serviceOrder.getUserId());
			evoucher.setTel(serviceOrder.getTel());
			evoucher.setOpenid(serviceOrder.getOpenId());
			evoucher.setMerchantId(serviceOrder.getMerchantId());
			evoucher.setMerchantName(serviceOrder.getMerchantName());
			evoucher.setAgentId(serviceOrder.getAgentId());
			evoucher.setAgentName(serviceOrder.getAgentName());
			evoucher.setAgentNo(serviceOrder.getAgentNo());
			evoucherRepository.save(evoucher);
			
			totalPrice += evoucher.getActualPrice();	//校验每张券的价格总和是否和订单支付金额一致
		}
		
		if (totalPrice != serviceOrder.getPrice()) {
			throw new BizValidateException("实际售卖价格有订单价格不符。");
		}
		
	}
	
	/**
	 * 启用优惠券
	 * @param serviceOrder
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void enable(ServiceOrder serviceOrder) {
		
		if (ModelConstant.ORDER_TYPE_EVOUCHER != serviceOrder.getOrderType()) {
			return;
		}
		
		List<Evoucher> evoucherList = evoucherRepository.findByOrderId(serviceOrder.getId());
		for (Evoucher evoucher : evoucherList) {
			evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_NORMAL);
			evoucher.setBeginDate(new Date());
			Date endDate = DateUtil.addDate(evoucher.getBeginDate(), 31);	//过期时间默认往后加一个月
			evoucher.setEndDate(endDate);
			evoucherRepository.save(evoucher);
		}
		
	}
	
	/**
	 * 使用优惠券
	 * @param serviceOrder
	 * @throws Exception 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void consume(User operator, String code, String evouchers) throws Exception {
		
		Assert.hasText(code, "核销券码不能为空。");
		
		String[]ids = evouchers.split(",");
		long orderId = 0;
		for (String id : ids) {
			if (StringUtil.isEmpty(id)) {
				continue;
			}
			Evoucher evoucher = evoucherRepository.findOne(Long.valueOf(id));
			evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_USED);
			evoucher.setCosumeDate(new Date());
			evoucher.setOperatorName(operator.getName());
			evoucher.setOperatorId(operator.getId());
			evoucherRepository.save(evoucher);
			orderId = evoucher.getOrderId();
		}
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(orderId);
		
		List<Evoucher> list = evoucherRepository.findByOrderIdAndStatus(serviceOrder.getId(), ModelConstant.EVOUCHER_STATUS_USED);
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			Evoucher evoucher = list.get(i);
			bf.append(evoucher.getCode());
			if (i!=(list.size()-1)) {
				bf.append(",");
			}
		}
		
		eshopUtil.notifyConsume(operator, serviceOrder.getOrderNo(), bf.toString());
		
//		List<Evoucher> evoucherList = evoucherRepository.findByCode(code);
//		for (Evoucher evoucher : evoucherList) {
//			evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_USED);
//			evoucher.setEndDate(new Date());
//			evoucher.setOperatorName(operator.getName());
//			evoucher.setOperatorId(operator.getId());
//			evoucherRepository.save(evoucher);
//		}
		
	}
	
	@Override
	public List<Evoucher> getEvoucher(String code){

		Assert.hasText(code, "核销券码不能为空。");
		return  evoucherRepository.findByCode(code);
	}

	@Override
	public List<Evoucher> getByUser(User user) {
		
		return evoucherRepository.findByUserId(user.getId());
	}
	
	@Override
	public List<ServiceOrder> getEvoucherOrders(User user, List<Integer>status) {
		
		return serviceOrderRepository.findByUserAndStatusAndType(user.getId(),status,ModelConstant.ORDER_TYPE_EVOUCHER);
	}

	@Override
	public List<Evoucher> getByOrder(long orderId) {
		
		return evoucherRepository.findByOrderId(orderId);
	}
	
	
}
