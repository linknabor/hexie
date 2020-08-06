package com.yumu.hexie.service.evoucher.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.common.util.OrderNoUtil;
import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorItem;
import com.yumu.hexie.model.localservice.ServiceOperatorItemRepository;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.EvoucherRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.evoucher.EvoucherService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.vo.EvoucherPageMapper;
import com.yumu.hexie.vo.EvoucherView;

@Service
public class EvoucherServiceImpl implements EvoucherService {
	
	private static Logger logger = LoggerFactory.getLogger(EvoucherServiceImpl.class);
	
	@Value("${evoucher.qrcode.url}")
	private String QRCODE_URL;

	@Autowired
	private EvoucherRepository evoucherRepository;
	@Autowired
	private EshopUtil eshopUtil;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	@Autowired
	private ServiceOperatorItemRepository serviceOperatorItemRepository;
	
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
//		BigDecimal totalPrice = BigDecimal.ZERO;
		for (int i = 0; i < serviceOrder.getCount(); i++) {
			
			Evoucher evoucher = new Evoucher();
			evoucher.setCode(OrderNoUtil.generateEvoucherNo());
			evoucher.setOrderId(serviceOrder.getId());
			evoucher.setActualPrice(product.getSinglePrice());	//实际销售价
			evoucher.setOriPrice(product.getOriPrice());	//原价
			evoucher.setProductId(serviceOrder.getProductId());
			evoucher.setProductName(serviceOrder.getProductName());
			evoucher.setSmallPicture(product.getSmallPicture());
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
			
//			totalPrice  = totalPrice.add(new BigDecimal(evoucher.getActualPrice()));	//校验每张券的价格总和是否和订单支付金额一致
		}
		
//		if (totalPrice.compareTo(new BigDecimal(serviceOrder.getPrice()))!=0) {
//			throw new BizValidateException("实际售卖价格有订单价格不符。");
//		}
		
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
			
			Date beginDate = new Date();
			String bd = DateUtil.dtFormat(beginDate, DateUtil.dSimple);
			bd += " 00:00:00";
			Date formatBd = DateUtil.parse(bd, DateUtil.dttmSimple);
			
			evoucher.setBeginDate(formatBd);
			Date endDate = DateUtil.addDate(evoucher.getBeginDate(), 30);	//过期时间默认往后加一个月
			String ed = DateUtil.dtFormat(endDate, DateUtil.dSimple);
			ed += " 23:59:59";
			Date formatEd = DateUtil.parse(ed, DateUtil.dttmSimple);
			evoucher.setEndDate(formatEd);
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
		
//		String[]ids = evouchers.split(",");
//		long orderId = 0;
//		for (String id : ids) {
//			if (StringUtil.isEmpty(id)) {
//				continue;
//			}
//			Evoucher evoucher = evoucherRepository.findOne(Long.valueOf(id));
//			evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_USED);
//			evoucher.setCosumeDate(new Date());
//			evoucher.setOperatorName(operator.getName());
//			evoucher.setOperatorId(operator.getId());
//			evoucherRepository.save(evoucher);
//			orderId = evoucher.getOrderId();
//		}
//		ServiceOrder serviceOrder = serviceOrderRepository.findOne(orderId);
//		
//		List<Evoucher> list = evoucherRepository.findByOrderIdAndStatus(serviceOrder.getId(), ModelConstant.EVOUCHER_STATUS_USED);
//		StringBuffer bf = new StringBuffer();
//		for (int i = 0; i < list.size(); i++) {
//			Evoucher evoucher = list.get(i);
//			bf.append(evoucher.getCode());
//			if (i!=(list.size()-1)) {
//				bf.append(",");
//			}
//		}
		
		List<ServiceOperator> opList = serviceOperatorRepository.findByTypeAndUserId(ModelConstant.SERVICE_OPER_TYPE_EVOUCHER, operator.getId());
		if (opList == null || opList.isEmpty()) {
			logger.warn("用户不能进行当前操作。用户id: " + operator.getId());
			throw new BizValidateException("您没有权限核销该券码，请确认该券码详细信息。");
		}
		
		long orderId = 0;
		StringBuffer bf = new StringBuffer();
		Evoucher e = evoucherRepository.findByCode(code);
		
		ServiceOperator serviceOperator = opList.get(0);
		ServiceOperatorItem serviceOperatorItem = serviceOperatorItemRepository.findByOperatorIdAndServiceId(serviceOperator.getId(), e.getProductId());
		if (serviceOperatorItem == null) {
			logger.warn("用户不能进行当前操作。用户id: " + operator.getId());
			throw new BizValidateException("您没有权限核销该券码，请确认该券码详细信息。");
		}
		
		List<Evoucher> evoucherList = evoucherRepository.findByOrderId(e.getOrderId());
		
		for (int i =0; i < evoucherList.size(); i ++) {
			
			Evoucher evoucher = evoucherList.get(i);
			if (ModelConstant.EVOUCHER_STATUS_NORMAL != evoucher.getStatus()) {
				throw new BizValidateException("当前核销券不可用。状态码：" + evoucher.getStatus());
			}
			evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_USED);
			evoucher.setConsumeDate(new Date());
			evoucher.setOperatorName(serviceOperator.getName());
			evoucher.setOperatorUserId(operator.getId());
			evoucherRepository.save(evoucher);
			bf.append(evoucher.getCode());
			if (i!=(evoucherList.size()-1)) {
				bf.append(",");
			}
			orderId = evoucher.getOrderId();
		}
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(orderId);
		eshopUtil.notifyConsume(operator, serviceOrder.getOrderNo(), bf.toString());
		
	}
	
	@Override
	public EvoucherView getEvoucher(String code){

		Assert.hasText(code, "核销券码不能为空。");
		logger.info("code is : " + code);
		Evoucher evoucher =  evoucherRepository.findByCode(code);
		List<Evoucher> list = new ArrayList<>();
		if (evoucher!=null) {
			list = evoucherRepository.findByOrderId(evoucher.getOrderId());
		}
		return new EvoucherView(QRCODE_URL, list);
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
	public EvoucherView getByOrder(long orderId) {
		
		List<Evoucher> list = evoucherRepository.findByOrderId(orderId);
		return new EvoucherView(QRCODE_URL, list);
	}
	
	@Override
	public List<EvoucherPageMapper> getByOperator(User operator) throws Exception {
		
		List<Object[]> objList = evoucherRepository.findByOperator(operator.getId());
		List<EvoucherPageMapper> list = ObjectToBeanUtils.objectToBean(objList, EvoucherPageMapper.class);
		return list;
	}
	

}
