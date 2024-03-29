package com.yumu.hexie.service.eshop.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.common.util.OrderNoUtil;
import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.distribution.OnSaleAreaItem;
import com.yumu.hexie.model.distribution.OnSaleAreaItemRepository;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorItem;
import com.yumu.hexie.model.localservice.ServiceOperatorItemRepository;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.EvoucherRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.eshop.EvoucherService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.vo.EvoucherPageMapper;
import com.yumu.hexie.vo.EvoucherView;

@Service
public class EvoucherServiceImpl implements EvoucherService {
	
	private static final Logger logger = LoggerFactory.getLogger(EvoucherServiceImpl.class);
	
	@Value("${evoucher.qrcode.url}")
	private String EVOUCHER_QRCODE_URL;
	
	@Value("${promotion.qrcode.url}")
	private String PROMOTION_QRCODE_URL;

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
	@Autowired
	private AgentRepository agentRepository;
	@Autowired
	private OnSaleAreaItemRepository onSaleAreaItemRepository;
	@Autowired
	private SystemConfigService systemConfigService;
	
	/**
	 * 创建优惠券
	 * @param serviceOrder
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void createEvoucher(ServiceOrder serviceOrder) {
		
		if (ModelConstant.ORDER_TYPE_EVOUCHER != serviceOrder.getOrderType() && ModelConstant.ORDER_TYPE_PROMOTION != serviceOrder.getOrderType()) {
			return;
		}
		Product product = productRepository.findById(serviceOrder.getProductId());
		for (int i = 0; i < serviceOrder.getCount(); i++) {
			
			Evoucher evoucher = new Evoucher();
			if (ModelConstant.ORDER_TYPE_EVOUCHER == serviceOrder.getOrderType()) {
				
				evoucher.setType(ModelConstant.EVOUCHER_TYPE_VERIFICATION);	//核销券
				evoucher.setCode(OrderNoUtil.generateEvoucherNo());
				evoucher.setOrderId(serviceOrder.getId());
				evoucher.setActualPrice(product.getSinglePrice());	//实际销售价
				evoucher.setOriPrice(product.getOriPrice());	//原价
				evoucher.setProductId(serviceOrder.getProductId());
				evoucher.setProductName(serviceOrder.getProductName());
				evoucher.setProductType(Integer.parseInt(product.getProductType()));
				evoucher.setRuleId(serviceOrder.getGroupRuleId());
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
				
			} else if (ModelConstant.ORDER_TYPE_PROMOTION == serviceOrder.getOrderType()) {
				
				evoucher.setType(ModelConstant.EVOUCHER_TYPE_PROMOTION);	//推广券码
				evoucher.setCode(OrderNoUtil.generateEvoucherNo());
				evoucher.setOrderId(serviceOrder.getId());
				evoucher.setProductId(serviceOrder.getProductId());
				evoucher.setProductType(Integer.parseInt(product.getProductType()));
				evoucher.setProductName(serviceOrder.getProductName());
				evoucher.setRuleId(serviceOrder.getGroupRuleId());
				evoucher.setSmallPicture(product.getSmallPicture());
				evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_INIT);
				evoucher.setUserId(serviceOrder.getUserId());
				evoucher.setTel(serviceOrder.getTel());
				evoucher.setOpenid(serviceOrder.getOpenId());
				evoucher.setMerchantId(serviceOrder.getMerchantId());
				evoucher.setMerchantName(serviceOrder.getMerchantName());
				Agent agent = agentRepository.findByAgentNo(serviceOrder.getTel());	//新下单用户的手机号就是他的机构号
				evoucher.setAgentId(agent.getId());
				evoucher.setAgentName(agent.getName());
				evoucher.setAgentNo(agent.getAgentNo());
			}
			evoucherRepository.save(evoucher);
		}
	}
	
	/**
	 * 启用优惠券
	 * @param serviceOrder
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void enable(ServiceOrder serviceOrder) {
		
		if (ModelConstant.ORDER_TYPE_EVOUCHER != serviceOrder.getOrderType() && ModelConstant.ORDER_TYPE_PROMOTION != serviceOrder.getOrderType()) {
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
			if (ModelConstant.ORDER_TYPE_PROMOTION == serviceOrder.getOrderType()) {
				endDate = DateUtil.addDate(evoucher.getBeginDate(), 365);	//过期时间默认往后加一年
			}
			String ed = DateUtil.dtFormat(endDate, DateUtil.dSimple);
			ed += " 23:59:59";
			Date formatEd = DateUtil.parse(ed, DateUtil.dttmSimple);
			evoucher.setEndDate(formatEd);
			evoucherRepository.save(evoucher);
		}
		
	}

	/**
	 * 使用优惠券
	 * @param operator
	 * @param code
	 * @param evouchers
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void consume(User operator, String code, String evouchers) throws Exception {
		Assert.hasText(code, "核销券码不能为空。");

		long orderId = 0;
		StringBuilder bf = new StringBuilder();
		Evoucher e = evoucherRepository.findByCode(code);
		long agentId = e.getAgentId();
		
		ServiceOperator serviceOperator = null;
		if (agentId == 1) {	//奈博自己发的核销券
			serviceOperator = serviceOperatorRepository.findByTypeAndUserIdAndAgentIdIsNull(ModelConstant.SERVICE_OPER_TYPE_EVOUCHER, operator.getId());
		}else {	//代理商、合伙人发的和小小去按
			serviceOperator = serviceOperatorRepository.findByTypeAndUserIdAndAgentId(ModelConstant.SERVICE_OPER_TYPE_EVOUCHER, operator.getId(), agentId);
		}
		
		if (serviceOperator == null ) {
			logger.warn("用户不能进行当前操作。用户id: " + operator.getId());
			throw new BizValidateException("您没有权限核销该券码，请确认该券码详细信息。");
		}
		ServiceOperatorItem serviceOperatorItem = serviceOperatorItemRepository.findByOperatorIdAndServiceId(serviceOperator.getId(), e.getProductId());
		if (serviceOperatorItem == null) {
			logger.warn("用户不能进行当前操作。用户id: " + operator.getId());
			throw new BizValidateException("您没有权限核销该券码，请确认该券码详细信息。");
		}
		
		List<Evoucher> evoucherList = evoucherRepository.findByOrderId(e.getOrderId());
		
		for (int i =0; i < evoucherList.size(); i ++) {
			
			Evoucher evoucher = evoucherList.get(i);
			if (ModelConstant.EVOUCHER_STATUS_NORMAL != evoucher.getStatus()) {
				switch (evoucher.getStatus()) {
				case ModelConstant.EVOUCHER_STATUS_EXPIRED:
					throw new BizValidateException("当前券码已过期。");
				case ModelConstant.EVOUCHER_STATUS_INVALID:
					throw new BizValidateException("当前券码已退款。");
				case ModelConstant.EVOUCHER_STATUS_USED:
					throw new BizValidateException("当前券码已使用。");
				default:
					break;
				}
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
		ServiceOrder serviceOrder = serviceOrderRepository.findById(orderId);
		eshopUtil.notifyConsume(operator, serviceOrder.getOrderNo(), bf.toString());
		
	}

	/**
	 * 使用优惠券
	 * @param userId
	 * @param code
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, String> consume(String userId, String code) {

		Assert.hasText(code, "核销券码不能为空。");

		long orderId = 0;
		StringBuilder bf = new StringBuilder();
		Evoucher e = evoucherRepository.findByCode(code);
		long agentId = e.getAgentId();

		ServiceOperator serviceOperator = null;
		if (agentId == 1) {	//奈博自己发的核销券
			serviceOperator = serviceOperatorRepository.findByTypeAndUserIdAndAgentIdIsNull(ModelConstant.SERVICE_OPER_TYPE_EVOUCHER, Long.parseLong(userId));
		}else {	//代理商、合伙人发的和小小去按
			serviceOperator = serviceOperatorRepository.findByTypeAndUserIdAndAgentId(ModelConstant.SERVICE_OPER_TYPE_EVOUCHER, Long.parseLong(userId), agentId);
		}

		if (serviceOperator == null) {
			logger.warn("用户不能进行当前操作。用户id: " + userId);
			throw new BizValidateException("您没有权限核销该券码，请确认该券码详细信息。");
		}
		ServiceOperatorItem serviceOperatorItem = serviceOperatorItemRepository.findByOperatorIdAndServiceId(serviceOperator.getId(), e.getProductId());
		if (serviceOperatorItem == null) {
			logger.warn("用户不能进行当前操作。用户id: " + userId);
			throw new BizValidateException("您没有权限核销该券码，请确认该券码详细信息。");
		}

		List<Evoucher> evoucherList = evoucherRepository.findByOrderId(e.getOrderId());

		for (int i =0; i < evoucherList.size(); i ++) {
			Evoucher evoucher = evoucherList.get(i);
			if (ModelConstant.EVOUCHER_STATUS_NORMAL != evoucher.getStatus()) {
				switch (evoucher.getStatus()) {
					case ModelConstant.EVOUCHER_STATUS_EXPIRED:
						throw new BizValidateException("当前券码已过期。");
					case ModelConstant.EVOUCHER_STATUS_INVALID:
						throw new BizValidateException("当前券码已退款。");
					case ModelConstant.EVOUCHER_STATUS_USED:
						throw new BizValidateException("当前券码已使用。");
					default:
						break;
				}
			}

			evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_USED);
			evoucher.setConsumeDate(new Date());
			evoucher.setOperatorName(serviceOperator.getName());
			evoucher.setOperatorUserId(Long.parseLong(userId));
			evoucherRepository.save(evoucher);
			bf.append(evoucher.getCode());
			if (i!=(evoucherList.size()-1)) {
				bf.append(",");
			}
			orderId = evoucher.getOrderId();
		}
		ServiceOrder serviceOrder = serviceOrderRepository.findById(orderId);

		Map<String, String> map = new HashMap<>();
		map.put("trade_water_id", serviceOrder.getOrderNo());
		map.put("evouchers", bf.toString());
		return map;
	}


	@Override
	public EvoucherView getEvoucher(String code){

		Assert.hasText(code, "核销券码不能为空。");
		logger.info("code is : " + code);
		Evoucher evoucher =  evoucherRepository.findByCode(code);
		if(evoucher == null) {
			throw new BizValidateException("核销券码不正确。");
		}
		List<Evoucher> list = evoucherRepository.findByOrderId(evoucher.getOrderId());

		String qrCodeUrl = EVOUCHER_QRCODE_URL;
		if (ModelConstant.EVOUCHER_TYPE_PROMOTION == evoucher.getType()) {
			
			String appid = systemConfigService.getSysConfigByKey("PROMOTION_SERVICE_APPID");
			if (StringUtils.isEmpty(appid)) {
				appid = "";
			}
			qrCodeUrl = PROMOTION_QRCODE_URL;
			qrCodeUrl = qrCodeUrl.replaceAll("RULE_ID", String.valueOf(evoucher.getRuleId())).replaceAll("PRODUCT_TYPE", String.valueOf(evoucher.getProductType())).
					replaceAll("SHARE_CODE", "").replace("APP_ID", appid);
		}
		return new EvoucherView(qrCodeUrl, list);
	}

	@Override
	public List<Evoucher> getByUserAndType(User user, int type) {
		
		return evoucherRepository.findByUserIdAndType(user.getId(), type);
	}
	
	/**
	 * 获取可用的二维码
	 * 1.用户已购买合伙人，显示带有用户自己分享码的二维码
	 * 2.用户购买合伙人后退款，则显示带有奈博科技分享码的二维码
	 */
	@Override
	public EvoucherView getAvailableEvoucher(User user, int type) {
		
		EvoucherView evoucherView;
		List<Evoucher> list = evoucherRepository.findByUserIdAndTypeAndStatus(user.getId(), type, ModelConstant.EVOUCHER_STATUS_NORMAL);
		Evoucher evoucher;
		if (!list.isEmpty()) {
			evoucher = list.get(0);
			String qrCodeUrl = EVOUCHER_QRCODE_URL;
			if (ModelConstant.EVOUCHER_TYPE_PROMOTION == evoucher.getType()) {
				qrCodeUrl = PROMOTION_QRCODE_URL;
				String appid = systemConfigService.getSysConfigByKey("PROMOTION_SERVICE_APPID");
				if (StringUtils.isEmpty(appid)) {
					appid = "";
				}
				qrCodeUrl = qrCodeUrl.replaceAll("RULE_ID", String.valueOf(evoucher.getRuleId())).replaceAll("PRODUCT_TYPE", String.valueOf(evoucher.getProductType())).
						replaceAll("SHARE_CODE", "").replace("APP_ID", appid);
			}
			evoucherView = new EvoucherView(qrCodeUrl, list);
		}else {
			evoucherView = new EvoucherView();
			if (ModelConstant.EVOUCHER_TYPE_PROMOTION == type) {
				evoucherView = getDefaultEvoucher4Promotion();
			}
		}
		return evoucherView;
	}
	
	@Override
	public List<ServiceOrder> getEvoucherOrders(User user, List<Integer>status) {
		
		return serviceOrderRepository.findByUserAndStatusAndType(user.getId(),status,ModelConstant.ORDER_TYPE_EVOUCHER);
	}

	@Override
	public EvoucherView getByOrder(long orderId) {
		
		List<Evoucher> list = evoucherRepository.findByOrderId(orderId);
		Evoucher evoucher = new Evoucher();
		if (!list.isEmpty()) {
			evoucher = list.get(0);
		}
		String qrCodeUrl = EVOUCHER_QRCODE_URL;
		if (ModelConstant.EVOUCHER_TYPE_PROMOTION == evoucher.getType()) {
			
			String appid = systemConfigService.getSysConfigByKey("PROMOTION_SERVICE_APPID");
			if (StringUtils.isEmpty(appid)) {
				appid = "";
			}
			qrCodeUrl = PROMOTION_QRCODE_URL;
			qrCodeUrl = qrCodeUrl.replaceAll("RULE_ID", String.valueOf(evoucher.getRuleId())).replaceAll("PRODUCT_TYPE", String.valueOf(evoucher.getProductType())).
					replaceAll("SHARE_CODE", "").replace("APP_ID", appid);
		}
		return new EvoucherView(qrCodeUrl, list);
	}
	
	@Override
	public List<EvoucherPageMapper> getByOperator(User operator) throws Exception {
		
		List<Object[]> objList = evoucherRepository.findByOperatorAndType(operator.getId(), ModelConstant.EVOUCHER_TYPE_VERIFICATION);
		return ObjectToBeanUtils.objectToBean(objList, EvoucherPageMapper.class);
	}
	
	@Override
	public Evoucher getEvoucherByCode(String code){

		Assert.hasText(code, "核销券码不能为空。");
		logger.info("code is : " + code);
		return evoucherRepository.findByCode(code);
	}

	@Override
	public Evoucher createSingle4Promotion(Agent agent) {
		
		Evoucher evoucher = new Evoucher();
		long current = System.currentTimeMillis();
    	List<Order> orderList = new ArrayList<>();
    	Order order = new Order(Direction.ASC, "sortNo");
    	Order order2 = new Order(Direction.DESC, "id");
    	orderList.add(order);
    	orderList.add(order2);
    	Sort sort = Sort.by(orderList);
    	Pageable pageable = PageRequest.of(0, 10, sort);
		
		List<OnSaleAreaItem> itemList = onSaleAreaItemRepository.findAllCountry(ModelConstant.DISTRIBUTION_STATUS_ON, 1003, current, 0, pageable);
		if (itemList.isEmpty()) {
			throw new BizValidateException("没有可以生成海报的推广配置，请先前往推广配置功能新建规则。");
		}
		OnSaleAreaItem onSaleAreaItem = itemList.get(0);
		
		evoucher.setProductId(onSaleAreaItem.getProductId());
		evoucher.setRuleId(onSaleAreaItem.getRuleId());
		evoucher.setProductName(onSaleAreaItem.getProductName());
		evoucher.setProductType(onSaleAreaItem.getProductType());
		evoucher.setSmallPicture(onSaleAreaItem.getProductPic());
		evoucher.setType(ModelConstant.EVOUCHER_TYPE_PROMOTION);	//推广券码
		evoucher.setCode(OrderNoUtil.generateEvoucherNo());
		evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_NORMAL);
		evoucher.setAgentId(agent.getId());
		evoucher.setAgentName(agent.getName());
		evoucher.setAgentNo(agent.getAgentNo());
		return evoucherRepository.save(evoucher);
		
	}
	
	/**
	 * 获取默认的海报
	 * @return
	 */
	@Override
	public EvoucherView getDefaultEvoucher4Promotion() {
		
		String agentNo = "000000000000";	//写死机构ID，奈博
		Evoucher evoucher = new Evoucher();
		List<Evoucher> evoucherList = evoucherRepository.findByStatusAndTypeAndAgentNo(ModelConstant.EVOUCHER_STATUS_NORMAL, ModelConstant.EVOUCHER_TYPE_PROMOTION, agentNo);
		if (!evoucherList.isEmpty()) {
			evoucher = evoucherList.get(0);
		}
		logger.info("evoucher : " + evoucher);
		
		String qrCodeUrl;
		String appid = systemConfigService.getSysConfigByKey("PROMOTION_SERVICE_APPID");
		if (StringUtils.isEmpty(appid)) {
			appid = "";
		}
		qrCodeUrl = PROMOTION_QRCODE_URL;
		qrCodeUrl = qrCodeUrl.replaceAll("RULE_ID", String.valueOf(evoucher.getRuleId())).replaceAll("PRODUCT_TYPE", String.valueOf(evoucher.getProductType())).
				replaceAll("SHARE_CODE", "").replace("APP_ID", appid);
		
		return new EvoucherView(qrCodeUrl, evoucherList);
		
	}


}
