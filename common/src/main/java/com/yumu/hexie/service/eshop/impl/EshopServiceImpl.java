package com.yumu.hexie.service.eshop.impl;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.eshop.dto.QueryProductDTO;
import com.yumu.hexie.integration.eshop.mapper.EvoucherMapper;
import com.yumu.hexie.integration.eshop.mapper.OperatorMapper;
import com.yumu.hexie.integration.eshop.mapper.QueryProductMapper;
import com.yumu.hexie.integration.eshop.mapper.SaleAreaMapper;
import com.yumu.hexie.integration.eshop.vo.QueryEvoucherVO;
import com.yumu.hexie.integration.eshop.vo.QueryOperVO;
import com.yumu.hexie.integration.eshop.vo.QueryProductVO;
import com.yumu.hexie.integration.eshop.vo.SaveCategoryVO;
import com.yumu.hexie.integration.eshop.vo.SaveOperVO;
import com.yumu.hexie.integration.eshop.vo.SaveOperVO.Oper;
import com.yumu.hexie.integration.eshop.vo.SaveProductVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductCategory;
import com.yumu.hexie.model.commonsupport.info.ProductCategoryRepository;
import com.yumu.hexie.model.commonsupport.info.ProductPlat;
import com.yumu.hexie.model.commonsupport.info.ProductPlatRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRule;
import com.yumu.hexie.model.distribution.OnSaleAreaItem;
import com.yumu.hexie.model.distribution.OnSaleAreaItemRepository;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorItem;
import com.yumu.hexie.model.localservice.ServiceOperatorItemRepository;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.EvoucherRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.OnSaleRule;
import com.yumu.hexie.model.market.saleplan.OnSaleRuleRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.eshop.EshopSerivce;
import com.yumu.hexie.service.eshop.EvoucherService;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * 商品上、下架
 * @author david
 *
 * @param <T>
 */
public class EshopServiceImpl implements EshopSerivce {
	
	private Logger logger = LoggerFactory.getLogger(EshopServiceImpl.class);
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private AgentRepository agentRepository;
	@Autowired
	private OnSaleRuleRepository onSaleRuleRepository;
	@Autowired
	private OnSaleAreaItemRepository onSaleAreaItemRepository;
	@Autowired
	private RgroupRuleRepository rgroupRuleRepository;
	@Autowired
	private RgroupAreaItemRepository rgroupAreaItemRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private ProductPlatRepository productPlatRepository;
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	@Autowired
	private ServiceOperatorItemRepository serviceOperatorItemRepository;
	@Autowired
	private EvoucherRepository evoucherRepository;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private ProductCategoryRepository productCategoryRepository;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private EvoucherService evoucherService;
	@Autowired
	private SystemConfigService systemConfigService;
	
	@Value("${promotion.qrcode.url}")
	private String PROMOTION_QRCODE_URL;
	
	@PostConstruct
	public void initStockAndFreeze() {
		
		List<Product> proList = productRepository.findByStatusMultiType(ModelConstant.PRODUCT_ONSALE);
		for (Product product : proList) {
			String total = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + product.getId());
			if (StringUtils.isEmpty(total)) {
				redisTemplate.opsForValue().setIfAbsent(ModelConstant.KEY_PRO_STOCK + product.getId(), String.valueOf(product.getTotalCount()));
				redisTemplate.opsForValue().setIfAbsent(ModelConstant.KEY_PRO_FREEZE + product.getId(), "0");
			}
		}
		logger.info("init stock and freeze finished .");
	}
	
	@Override
	public CommonResponse<Object> getProduct(QueryProductVO queryProductVO) {

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Integer> agentList = null;
			if (StringUtils.isEmpty(queryProductVO.getAgentNo()) && StringUtils.isEmpty(queryProductVO.getAgentName())) {
				//do nothing
			}else {
				agentList = agentRepository.findByAgentNoOrName(1, queryProductVO.getAgentNo(), 
						queryProductVO.getAgentName());
			}
			if (agentList != null ) {
				if (agentList.isEmpty()) {
					agentList.add(0);
				}
			}
			
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "id");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			
			Pageable pageable = PageRequest.of(queryProductVO.getCurrentPage(), queryProductVO.getPageSize(), sort);
			Page<Object[]> page = null;
			
			String productType = queryProductVO.getProductType();
			if ("1000".equals(productType) || "1001".equals(productType) || "1003".contentEquals(productType) || "1004".equals(productType)) {	//TODO 核销券和特卖
				page = productRepository.findByMultiCondOnsale(queryProductVO.getProductType(), queryProductVO.getProductId(), 
						queryProductVO.getProductName(), queryProductVO.getProductStatus(), agentList, queryProductVO.getDemo(), pageable);
				
			}else if ("1002".equals(productType)) {
				page = productRepository.findByMultiCondRgroup(queryProductVO.getProductType(), queryProductVO.getProductId(), 
						queryProductVO.getProductName(), queryProductVO.getProductStatus(), agentList, queryProductVO.getDemo(), pageable);
			}
			
			List<ServiceOperator> opList = new ArrayList<>();
			int operatorType = 0;
			if ("1001".equals(productType)) {
				operatorType = ModelConstant.SERVICE_OPER_TYPE_ONSALE_TAKER;
			}else if ("1002".equals(productType)) {
				operatorType = ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER;
			}else if ("1003".equals(productType)) {
				operatorType = ModelConstant.SERVICE_OPER_TYPE_PROMOTION;
			}else if ("1004".equals(productType)) {
				operatorType = ModelConstant.SERVICE_OPER_TYPE_SAASSALE;
			}
			
			if (StringUtils.isEmpty(queryProductVO.getAgentNo())) {
				opList = serviceOperatorRepository.findByType(operatorType);
			}else {
				Agent agent = agentRepository.findByAgentNo(queryProductVO.getAgentNo());
				if (agent != null) {
					opList = serviceOperatorRepository.findByTypeAndAgentId(operatorType, agent.getId());
				}
			}
			
			List<QueryProductMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryProductMapper.class);
			if (!opList.isEmpty() && (list!=null && !list.isEmpty())) {
				list.get(0).setOperCounts(BigInteger.valueOf(opList.size()));
			}
			QueryListDTO<List<QueryProductMapper>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			responsePage.setContent(list);
			
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	/**
	 * 根据商品ID查询
	 */
	@Override
	public CommonResponse<Object> getProductById(QueryProductVO queryProductVO) {
		
		Assert.hasText(queryProductVO.getProductId(), "商品ID不能为空。");

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			Pageable pageable = PageRequest.of(0, 1);
			Page<Object[]> page = null;
			String productType = queryProductVO.getProductType();
			if ("1000".equals(productType) || "1001".equals(productType) || "1003".equals(productType) || "1004".equals(productType)) {
				page = productRepository.findByMultiCondOnsale(queryProductVO.getProductType(), queryProductVO.getProductId(), 
					"", "", null, "", pageable);
			}else if ("1002".equals(productType)) {
				page = productRepository.findByMultiCondRgroup(queryProductVO.getProductType(), queryProductVO.getProductId(), 
						"", "", null, "", pageable);
			}
			
			List<QueryProductMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryProductMapper.class);
			List<Object[]> regionList = new ArrayList<>();
			if ("1000".equals(productType) || "1001".equals(productType) || "1003".equals(productType) || "1004".equals(productType)) {
				regionList = regionRepository.findByProductId(queryProductVO.getProductId());}
			else if ("1002".equals(productType)) {
				regionList = regionRepository.findByProductId4Rroup(queryProductVO.getProductId());
			}
			
			QueryProductDTO<QueryProductMapper> queryProductDTO = new QueryProductDTO<>();
			queryProductDTO.setContent(list.get(0));
			
			List<SaleAreaMapper> areaList = ObjectToBeanUtils.objectToBean(regionList, SaleAreaMapper.class);
			
			queryProductDTO.setSaleArea(areaList);
			commonResponse.setData(queryProductDTO);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			logger.info(e.getMessage(), e);
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}

	/**
	 * 保存商品上架内容
	 */
	@Override
	@Transactional
	public void saveProduct(SaveProductVO saveProductVO) throws Exception {
	
		String agentNo = saveProductVO.getAgentNo();
		Agent agent = agentRepository.findByAgentNo(agentNo);
		if ("add".equals(saveProductVO.getOperType())) {
			if (agent == null) {
				agent = new Agent();
				agent.setName(saveProductVO.getAgentName());
				agent.setAgentNo(agentNo);
				agent.setStatus(1);
				agent = agentRepository.save(agent);
			}
		}
		
		Product product = new Product();
		if ("edit".equals(saveProductVO.getOperType())) {
			product = productRepository.findById(Long.valueOf(saveProductVO.getId())).get();
			if (product == null) {
				throw new BizValidateException("未查询到商品，id : " + saveProductVO.getId());
			}
		}
		
		product.setName(saveProductVO.getName());
		
		if ("add".equals(saveProductVO.getOperType())) {
			product.setAgentId(agent.getId());
		}
		product.setProductType(saveProductVO.getType());
		product.setTotalCount(Integer.parseInt(saveProductVO.getTotalCount()) + product.getSaledNum());
		product.setMainPicture(saveProductVO.getMainPicture());
		product.setSmallPicture(saveProductVO.getSmallPicture());
		product.setPictures(saveProductVO.getPictures());
		product.setMiniPrice(Float.valueOf(saveProductVO.getMiniPrice()));
		product.setSinglePrice(Float.valueOf(saveProductVO.getSinglePrice()));
		product.setOriPrice(Float.valueOf(saveProductVO.getOriPrice()));
		product.setServiceDesc(saveProductVO.getContext());
		product.setPostageFee(Float.valueOf(saveProductVO.getPostageFee()));
		if (ModelConstant.RULE_STATUS_ON == Integer.valueOf(saveProductVO.getStatus())) {
			product.setStatus(ModelConstant.PRODUCT_ONSALE);
		}
		Date startDate = DateUtil.parse(saveProductVO.getStartDate(), DateUtil.dttmSimple);
		product.setStartDate(startDate);
		Date endDate = DateUtil.parse(saveProductVO.getEndDate(), DateUtil.dttmSimple);
		product.setEndDate(endDate);
		product.setShortName(saveProductVO.getName());
		product.setTitleName(saveProductVO.getName());
		if ("edit".equals(saveProductVO.getOperType())) {
			product.setUpdateDate(new Date());
			product.setUpdateUser(saveProductVO.getUpdateUser());
		}
		if (!StringUtils.isEmpty(saveProductVO.getProductCategoryId())) {
			product.setProductCategoryId(Integer.valueOf(saveProductVO.getProductCategoryId()));
		}
		product = productRepository.save(product);
		
		int salePlanType = Integer.valueOf(saveProductVO.getSalePlanType());
		if (ModelConstant.ORDER_TYPE_EVOUCHER != salePlanType &&
				ModelConstant.ORDER_TYPE_ONSALE != salePlanType &&
				ModelConstant.ORDER_TYPE_RGROUP != salePlanType &&
				ModelConstant.ORDER_TYPE_PROMOTION != salePlanType &&
				ModelConstant.ORDER_TYPE_SAASSALE != salePlanType) {
			throw new BizValidateException("unknow sale plan type : " + saveProductVO.getSalePlanType());
		}
		
		OnSaleRule onSaleRule = null;
		RgroupRule rgroupRule = null;
		if (ModelConstant.ORDER_TYPE_EVOUCHER == salePlanType || ModelConstant.ORDER_TYPE_ONSALE == salePlanType
				|| ModelConstant.ORDER_TYPE_PROMOTION == salePlanType || ModelConstant.ORDER_TYPE_SAASSALE == salePlanType) {	//走特卖规则
			
			onSaleRule = new OnSaleRule();
			if ("edit".equals(saveProductVO.getOperType())) {
				List<OnSaleRule> ruleList = onSaleRuleRepository.findAllByProductId(product.getId());
				if (ruleList == null || ruleList.isEmpty()) {
					throw new BizValidateException("未查询到商品上架规则，product id : " + saveProductVO.getId());
				}
				onSaleRule = ruleList.get(0);
			}
			onSaleRule.setProductId(Integer.valueOf(product.getProductType()));
			onSaleRule.setProductName(product.getName());
			onSaleRule.setProductType(Integer.valueOf(saveProductVO.getType()));
			onSaleRule.setCreateDate(product.getCreateDate());
			onSaleRule.setDescription(product.getServiceDesc());
			onSaleRule.setProductId(product.getId());
			onSaleRule.setName(product.getName());
			onSaleRule.setLimitNumOnce(Integer.valueOf(saveProductVO.getLimitNumOnce()));
			onSaleRule.setStartDate(product.getStartDate());
			onSaleRule.setEndDate(product.getEndDate());
			onSaleRule.setOriPrice(product.getOriPrice());
			onSaleRule.setPrice(product.getSinglePrice());
			onSaleRule.setDescription(product.getServiceDesc());
			onSaleRule.setTimeoutForPay(30*60*1000);
			onSaleRule.setFreeShippingNum(Integer.valueOf(saveProductVO.getFreeShippingNum()));
			onSaleRule.setPostageFee(Float.valueOf(saveProductVO.getPostageFee()));
			if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
				onSaleRule.setStatus(ModelConstant.RULE_STATUS_ON);
			}else {
				onSaleRule.setStatus(ModelConstant.RULE_STATUS_OFF);
			}
			onSaleRule = onSaleRuleRepository.save(onSaleRule);
			
			if ("edit".equals(saveProductVO.getOperType())) {
				List<OnSaleAreaItem> areaList = onSaleAreaItemRepository.findByRuleId(onSaleRule.getId());
				if (areaList == null || areaList.isEmpty()) {
					throw new BizValidateException("未查询到商品上架区域，product id : " + saveProductVO.getId());
				}
				onSaleAreaItemRepository.deleteByProductId(saveProductVO.getId());
			}
			
			if (ModelConstant.ORDER_TYPE_PROMOTION == salePlanType || ModelConstant.ORDER_TYPE_SAASSALE == salePlanType) {
				
				OnSaleAreaItem onSaleAreaItem = new OnSaleAreaItem();
				onSaleAreaItem.setRegionId(1l);	//全国
				onSaleAreaItem.setRuleId(onSaleRule.getId());
				long ruleCloseTime = onSaleRule.getEndDate().getTime();
				onSaleAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
				onSaleAreaItem.setSortNo(Integer.valueOf(saveProductVO.getSortNo()));
				onSaleAreaItem.setRuleName(onSaleRule.getName());
				onSaleAreaItem.setOriPrice(product.getOriPrice());
				onSaleAreaItem.setPrice(product.getSinglePrice());
				onSaleAreaItem.setRegionType(ModelConstant.REGION_ALL);
				onSaleAreaItem.setProductId(product.getId());
				onSaleAreaItem.setProductName(product.getName());
				onSaleAreaItem.setProductCategoryId(product.getProductCategoryId());
				onSaleAreaItem.setProductPic(product.getMainPicture());
				onSaleAreaItem.setProductType(onSaleRule.getProductType());
				onSaleAreaItem.setPostageFee(onSaleRule.getPostageFee());
				onSaleAreaItem.setFreeShippingNum(onSaleRule.getFreeShippingNum());
				if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
					onSaleAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_ON);
				}else {
					onSaleAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_OFF);
				}
				onSaleAreaItemRepository.save(onSaleAreaItem);
				
			}else {
				
				for (Region saleArea : saveProductVO.getSaleAreas()) {
					
					Region region = getRegion(saleArea);
					OnSaleAreaItem onSaleAreaItem = new OnSaleAreaItem();
					onSaleAreaItem.setRuleId(onSaleRule.getId());
					long ruleCloseTime = onSaleRule.getEndDate().getTime();
					onSaleAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
					onSaleAreaItem.setSortNo(Integer.valueOf(saveProductVO.getSortNo()));
					onSaleAreaItem.setRuleName(onSaleRule.getName());
					onSaleAreaItem.setOriPrice(product.getOriPrice());
					onSaleAreaItem.setPrice(product.getSinglePrice());
					onSaleAreaItem.setRegionId(region.getId());
					onSaleAreaItem.setRegionType(region.getRegionType());
					onSaleAreaItem.setProductId(product.getId());
					onSaleAreaItem.setProductName(product.getName());
					onSaleAreaItem.setProductCategoryId(product.getProductCategoryId());
					onSaleAreaItem.setProductPic(product.getMainPicture());
					onSaleAreaItem.setProductType(onSaleRule.getProductType());
					onSaleAreaItem.setPostageFee(onSaleRule.getPostageFee());
					onSaleAreaItem.setFreeShippingNum(onSaleRule.getFreeShippingNum());
					if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
						onSaleAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_ON);
					}else {
						onSaleAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_OFF);
					}
					onSaleAreaItemRepository.save(onSaleAreaItem);
					
				}
				
			}
			
		} else if (ModelConstant.ORDER_TYPE_RGROUP == salePlanType) {
			
			rgroupRule = new RgroupRule();
			if ("edit".equals(saveProductVO.getOperType())) {
				List<RgroupRule> ruleList = rgroupRuleRepository.findAllByProductId(product.getId());
				if (ruleList == null || ruleList.isEmpty()) {
					throw new BizValidateException("未查询到商品上架规则，product id : " + saveProductVO.getId());
				}
				rgroupRule = ruleList.get(0);
			}
			rgroupRule.setProductId(Integer.valueOf(product.getProductType()));
			rgroupRule.setProductName(product.getName());
			rgroupRule.setProductType(Integer.valueOf(saveProductVO.getType()));
			rgroupRule.setCreateDate(product.getCreateDate());
			rgroupRule.setDescription(product.getServiceDesc());
			rgroupRule.setProductId(product.getId());
			rgroupRule.setName(product.getName());
			rgroupRule.setLimitNumOnce(Integer.valueOf(saveProductVO.getLimitNumOnce()));
			rgroupRule.setStartDate(product.getStartDate());
			rgroupRule.setEndDate(product.getEndDate());
			rgroupRule.setOriPrice(product.getOriPrice());
			rgroupRule.setPrice(product.getSinglePrice());
			rgroupRule.setDescription(product.getServiceDesc());
			rgroupRule.setTimeoutForPay(30*60*1000);
			rgroupRule.setFreeShippingNum(Integer.valueOf(saveProductVO.getFreeShippingNum()));
			rgroupRule.setPostageFee(Float.valueOf(saveProductVO.getPostageFee()));
			rgroupRule.setGroupMinNum(Integer.valueOf(saveProductVO.getGroupMinNum()));
			
			if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
				rgroupRule.setStatus(ModelConstant.RULE_STATUS_ON);
			}else {
				rgroupRule.setStatus(ModelConstant.RULE_STATUS_OFF);
			}
			rgroupRule = rgroupRuleRepository.save(rgroupRule);
			
			if ("edit".equals(saveProductVO.getOperType())) {
				List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByRuleId(rgroupRule.getId());
				if (areaList == null || areaList.isEmpty()) {
					throw new BizValidateException("未查询到商品上架区域，product id : " + saveProductVO.getId());
				}
				rgroupAreaItemRepository.deleteByProductId(saveProductVO.getId());
			}
			
			for (Region saleArea : saveProductVO.getSaleAreas()) {
				
				Region region = getRegion(saleArea);
				RgroupAreaItem rgroupAreaItem = new RgroupAreaItem();
				rgroupAreaItem.setRegionId(region.getId());
				rgroupAreaItem.setRuleId(rgroupRule.getId());
				long ruleCloseTime = rgroupRule.getEndDate().getTime();
				rgroupAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
				rgroupAreaItem.setSortNo(Integer.valueOf(saveProductVO.getSortNo()));
				rgroupAreaItem.setRuleName(rgroupRule.getName());
				rgroupAreaItem.setOriPrice(product.getOriPrice());
				rgroupAreaItem.setPrice(product.getSinglePrice());
				rgroupAreaItem.setRegionId(region.getId());
				rgroupAreaItem.setRegionType(region.getRegionType());
				rgroupAreaItem.setProductId(product.getId());
				rgroupAreaItem.setProductName(product.getName());
				rgroupAreaItem.setProductCategoryId(product.getProductCategoryId());
				rgroupAreaItem.setProductPic(product.getMainPicture());
				rgroupAreaItem.setProductType(rgroupRule.getProductType());
				rgroupAreaItem.setPostageFee(rgroupRule.getPostageFee());
				rgroupAreaItem.setFreeShippingNum(rgroupRule.getFreeShippingNum());
				if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
					rgroupAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_ON);
				}else {
					rgroupAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_OFF);
				}
				rgroupAreaItemRepository.save(rgroupAreaItem);
				
			}
			
		}
		
		ProductPlat productPlat = new ProductPlat();
		if ("edit".equals(saveProductVO.getOperType())) {
			productPlat = productPlatRepository.findByProductIdAndAppId(product.getId(), saveProductVO.getAppid());
			if (productPlat == null) {
				productPlat = new ProductPlat();	//这里new一个，因为选线可能有商品没有配置appid
			}
		}
		productPlat.setAppId(saveProductVO.getAppid());
		productPlat.setProductId(product.getId());
		productPlatRepository.save(productPlat);
		
		ProductRule productRule = null;
		if (onSaleRule != null) {
			productRule = new ProductRule(product, onSaleRule);
		}else {
			productRule = new ProductRule(product, rgroupRule);
		}
		String key = ModelConstant.KEY_PRO_RULE_INFO + productRule.getId();
		redisRepository.setProdcutRule(key, productRule);
		
		redisTemplate.opsForValue().set(ModelConstant.KEY_PRO_STOCK + product.getId(), String.valueOf(product.getTotalCount()));
		redisTemplate.opsForValue().set(ModelConstant.KEY_PRO_FREEZE + product.getId(), "0");	//初始化冻结数量
		
	}
	
	@Override
	@Transactional
	public void updateStatus(SaveProductVO saveProductVO) {
		
		Assert.hasText(saveProductVO.getId(), "商品ID不能为空。");
		Assert.hasText(saveProductVO.getOperType(), "操作类型不能为空。 ");
		
		String productId = saveProductVO.getId();
		String operType = saveProductVO.getOperType();
		
		int productStatus = 0;
		int ruleStatus = 0;
		int itemStatus = 0;
		if ("on".equals(operType)) {
			productStatus = ModelConstant.PRODUCT_ONSALE;
			ruleStatus = ModelConstant.RULE_STATUS_ON;
			itemStatus = ModelConstant.DISTRIBUTION_STATUS_ON;
		}else {
			productStatus = ModelConstant.PRODUCT_OFF;
			ruleStatus = ModelConstant.RULE_STATUS_OFF;
			itemStatus = ModelConstant.DISTRIBUTION_STATUS_OFF;
		}
		
		Product product = productRepository.findById(Long.valueOf(productId)).get();
		if (product == null) {
			throw new BizValidateException("未查询到商品, id : " + productId);
		}
		productRepository.updateStatus(productStatus, product.getId());
		//特卖
		List<OnSaleRule> ruleList = onSaleRuleRepository.findAllByProductId(product.getId());
		if (!ruleList.isEmpty()) {
			for (OnSaleRule onSaleRule : ruleList) {
				onSaleRuleRepository.updateStatus(ruleStatus, onSaleRule.getId());

				List<OnSaleAreaItem> itemList = onSaleAreaItemRepository.findByRuleId(onSaleRule.getId());
				for (OnSaleAreaItem item : itemList) {
					onSaleAreaItemRepository.updateStatus(itemStatus, item.getId());
				}
			}
		//团购
		}else {
			List<RgroupRule> rgroupRuleList = rgroupRuleRepository.findAllByProductId(product.getId());
			for (RgroupRule rgroupRule : rgroupRuleList) {
				rgroupRuleRepository.updateStatus(ruleStatus, rgroupRule.getId());

				List<RgroupAreaItem> itemList = rgroupAreaItemRepository.findByRuleId(rgroupRule.getId());
				for (RgroupAreaItem item : itemList) {
					rgroupAreaItemRepository.updateStatus(itemStatus, item.getId());
				}
			}
		}
		
		
		
	}
	
	/**
	 * 设置成样板
	 */
	@Override
	public void updateDemo(SaveProductVO saveProductVO) {

		Assert.hasText(saveProductVO.getId(), "商品ID不能为空。");
		Assert.hasText(saveProductVO.getOperType(), "操作类型不能为空。 ");
		
		String productId = saveProductVO.getId();
		String operType = saveProductVO.getOperType();
		
		if ("1".equals(operType)) {
			productRepository.updateDemo(1, Long.valueOf(productId));
		}else if ("0".equals(operType)) {
			productRepository.updateDemo(0, Long.valueOf(productId));
		}
		
	}

	/**
	 * 获取平台小区对应的region
	 * @param saleArea
	 * @param region
	 * @return
	 */
	private Region getRegion(Region saleArea) {
		
		Region region = null;
		List<Region> regionList = regionRepository.findAllBySectId(saleArea.getSectId());
		if (regionList==null || regionList.isEmpty()) {
			regionList = regionRepository.findByNameAndRegionType(saleArea.getName(), saleArea.getRegionType());
			if (regionList == null || regionList.isEmpty()) {
				region = new Region();
				BeanUtils.copyProperties(saleArea, region);
				region.setLongitude(0d);
				region.setLatitude(0d);
				region = regionRepository.save(region);
			}else {
				if (regionList.size()>1) {
					for (int i = 0; i < regionList.size(); i++) {
						Region currRegion = regionList.get(i);
						if (!StringUtils.isEmpty(currRegion.getSectId())) {
							region = currRegion;
						}
					}
					if (region == null) {
						region = regionList.get(0);
					}
				}else {
					region = regionList.get(0);
				}
				region.setSectId(saleArea.getSectId());
				region = regionRepository.save(region);
			}
		}else {
			if (regionList.size()>1) {
				for (int i = 0; i < regionList.size(); i++) {
					Region currRegion = regionList.get(i);
					if (!StringUtils.isEmpty(currRegion.getSectId())) {
						region = currRegion;
					}
				}
				if (region == null) {
					region = regionList.get(0);
				}
			}else {
				region = regionList.get(0);
			}
			region = regionList.get(0);
		}
		return region;
	}
	
	@Override
	public CommonResponse<Object> getOper(QueryOperVO queryOperVO) {

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			List<Object[]> list = null;
			if (ModelConstant.SERVICE_OPER_TYPE_EVOUCHER == queryOperVO.getType()) {
				list = serviceOperatorRepository.findByTypeAndServiceId(queryOperVO.getType(), queryOperVO.getServiceId());
			}else if (ModelConstant.SERVICE_OPER_TYPE_ONSALE_TAKER == queryOperVO.getType() ||
					ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER == queryOperVO.getType()) {
			
				if (StringUtils.isEmpty(queryOperVO.getAgentNo())) {
					list = serviceOperatorRepository.findByTypeWithAppid(queryOperVO.getType());
				}else {
					Agent agent = agentRepository.findByAgentNo(queryOperVO.getAgentNo());
					list = serviceOperatorRepository.findByTypeAndAgentIdWithAppid(queryOperVO.getType(), agent.getId());
				}
			}else if (ModelConstant.SERVICE_OPER_TYPE_PROMOTION == queryOperVO.getType() || ModelConstant.SERVICE_OPER_TYPE_SAASSALE == queryOperVO.getType()) {
				list = serviceOperatorRepository.findByTypeWithAppid(queryOperVO.getType());
			}
			List<OperatorMapper> operList = ObjectToBeanUtils.objectToBean(list, OperatorMapper.class);
			commonResponse.setData(operList);
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	/**
	 * 保存服务人员
	 */
	@Override
	@Transactional
	public void saveOper(SaveOperVO saveOperVO) {
		
		Agent agent = new Agent();
		if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
			agent = agentRepository.findByAgentNo(saveOperVO.getAgentNo());
		}
		
		if (ModelConstant.SERVICE_OPER_TYPE_EVOUCHER == saveOperVO.getOperatorType()) {
			Assert.notNull(saveOperVO.getServiceId(), "服务ID或产品ID不能为空。");
			serviceOperatorItemRepository.deleteByServiceId(saveOperVO.getServiceId());
		}else if (ModelConstant.SERVICE_OPER_TYPE_PROMOTION == saveOperVO.getOperatorType() ||
				ModelConstant.SERVICE_OPER_TYPE_SAASSALE == saveOperVO.getOperatorType() ||
				ModelConstant.SERVICE_OPER_TYPE_ONSALE_TAKER == saveOperVO.getOperatorType() ||
				ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER == saveOperVO.getOperatorType()) {
			
			if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
				serviceOperatorRepository.deleteByTypeAndAgentId(saveOperVO.getOperatorType(), agent.getId());
			}else {
				serviceOperatorRepository.deleteByTypeAndNullAgent(saveOperVO.getOperatorType());
			}
			
			
		}
		List<Oper> operList = saveOperVO.getOpers();
		for (Oper oper : operList) {
			ServiceOperator serviceOperator = serviceOperatorRepository.findByTypeAndTelAndOpenId(saveOperVO.getOperatorType(), oper.getTel(), oper.getOpenId());
			if (serviceOperator == null) {
				serviceOperator = new ServiceOperator();
			}
			serviceOperator.setName(oper.getName());
			serviceOperator.setOpenId(oper.getOpenId());
			serviceOperator.setTel(oper.getTel());
			serviceOperator.setType(saveOperVO.getOperatorType());
			serviceOperator.setUserId(oper.getUserId());
			serviceOperator.setLongitude(0d);
			serviceOperator.setLatitude(0d);
			if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
				agent = agentRepository.findByAgentNo(saveOperVO.getAgentNo());
				serviceOperator.setAgentId(agent.getId());
			}
			serviceOperator = serviceOperatorRepository.save(serviceOperator);
			
			if (ModelConstant.SERVICE_OPER_TYPE_EVOUCHER == saveOperVO.getOperatorType()) {
				ServiceOperatorItem serviceOperatorItem = serviceOperatorItemRepository.findByOperatorIdAndServiceId(serviceOperator.getId(), saveOperVO.getServiceId());
				if (serviceOperatorItem == null) {
					serviceOperatorItem = new ServiceOperatorItem();
					serviceOperatorItem.setOperatorId(serviceOperator.getId());
					serviceOperatorItem.setServiceId(saveOperVO.getServiceId());
					serviceOperatorItemRepository.save(serviceOperatorItem);
				}
			}
			
		}
		if (ModelConstant.SERVICE_OPER_TYPE_EVOUCHER == saveOperVO.getOperatorType()) {
			//查看有哪些操作员已经没有服务项目了，没有的删除该操作员
			List<ServiceOperator> noServiceList = serviceOperatorRepository.queryNoServiceOper(saveOperVO.getOperatorType());
			for (ServiceOperator serviceOperator : noServiceList) {
				serviceOperatorRepository.delete(serviceOperator);
			}
		}
		
		
	}

	/**
	 * 后台查询核销券信息
	 */
	@Override
	public CommonResponse<Object> getEvoucher(QueryEvoucherVO queryEvoucherVO) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			Sort sort = new Sort(Direction.DESC, "id");
			Pageable pageable = PageRequest.of(queryEvoucherVO.getCurrentPage(), queryEvoucherVO.getPageSize(), sort);
			Page<Evoucher> page = evoucherRepository.findByMultipleConditions(queryEvoucherVO.getStatus(), queryEvoucherVO.getTel(), queryEvoucherVO.getAgentNo(), queryEvoucherVO.getAgentName(), queryEvoucherVO.getType(), pageable);

			List<EvoucherMapper> mapperList = new ArrayList<>();
			for (Evoucher evoucher : page.getContent()) {
				EvoucherMapper evoucherMapper = new EvoucherMapper();
				BeanUtils.copyProperties(evoucher, evoucherMapper);
				mapperList.add(evoucherMapper);
			}
			
			QueryListDTO<List<EvoucherMapper>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			responsePage.setContent(mapperList);
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	/**
	 * 退款处理
	 * @param orderNo
	 * @param operType 0申请，1驳回
	 */
	@Override
	@Transactional
	public void refund(String orderNo, String operType) {

		Assert.hasText(orderNo, "退款订单号不能为空。");
		
		ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(orderNo);
		if (serviceOrder == null) {
			throw new BizValidateException("为查询到订单: " + orderNo);
		}
		List<ServiceOrder> orderList = new ArrayList<>();
		if (ModelConstant.ORDER_TYPE_ONSALE == serviceOrder.getOrderType()) {
			orderList = serviceOrderRepository.findByGroupOrderId(serviceOrder.getGroupOrderId());
		} else {
			orderList.add(serviceOrder);
		}
		
		int fromStatus = 0;
		int toStatus = 0; 
		for (ServiceOrder order : orderList) {
			
			if ("0".equals(operType)) {
				fromStatus = ModelConstant.EVOUCHER_STATUS_NORMAL;
				toStatus = ModelConstant.EVOUCHER_STATUS_INVALID;
				
				order.setStatus(ModelConstant.ORDER_STATUS_REFUNDED);
				if (!StringUtils.isEmpty(order.getSendDate())) {
					order.setStatus(ModelConstant.ORDER_STATUS_RETURNED);
				}
				order.setRefundDate(new Date());
				serviceOrderRepository.save(order);
				
				if (ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType()) {
					Optional<RgroupRule> optional = rgroupRuleRepository.findById(order.getGroupRuleId());
					if (optional.isPresent()) {
						RgroupRule rule = optional.get();
						rule.setCurrentNum(rule.getCurrentNum()-order.getCount());
						rgroupRuleRepository.save(rule);
					}
				}
				
			}else if ("1".equals(operType)) {
				fromStatus = ModelConstant.EVOUCHER_STATUS_INVALID;
				toStatus = ModelConstant.EVOUCHER_STATUS_NORMAL;
				
				order.setStatus(ModelConstant.ORDER_STATUS_PAYED);
				if (!StringUtils.isEmpty(order.getSendDate())) {
					order.setStatus(ModelConstant.ORDER_STATUS_SENDED);
				}else if (!StringUtils.isEmpty(order.getConfirmDate())) {
					order.setStatus(ModelConstant.ORDER_STATUS_CONFIRM);
				}
				order.setRefundDate(null);
				serviceOrderRepository.save(order);
				
				if (ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType()) {
					Optional<RgroupRule> optional = rgroupRuleRepository.findById(order.getGroupRuleId());
					if (optional.isPresent()) {
						RgroupRule rule = optional.get();
						rule.setCurrentNum(rule.getCurrentNum()+order.getCount());
						rgroupRuleRepository.save(rule);
					}
				}
			}
		}
		
		List<Evoucher> evoucherList = evoucherRepository.findByOrderId(serviceOrder.getId());
		for (Evoucher evoucher : evoucherList) {
			if (fromStatus == evoucher.getStatus()) {
				evoucher.setStatus(toStatus);
				evoucherRepository.save(evoucher);
			}
		}
		
		
	}

	/**
	 * 保存产品分类
	 */
	@Override
	@Transactional
	public void saveCategory(SaveCategoryVO saveCategoryVo) {
		
		ProductCategory productCategory = new ProductCategory();
		BeanUtils.copyProperties(saveCategoryVo, productCategory);
		productCategoryRepository.save(productCategory);
	}

	/**
	 * 删除产品分类
	 */
	@Override
	@Transactional
	public void deleteCategory(String delIds) {
		
		Assert.hasText(delIds, "没有可以删除的分类");
		String[]delArr = delIds.split(",");
		for (String delid : delArr) {
			productCategoryRepository.deleteById(Long.valueOf(delid));
		}
		
	}
	
	/**
	 * 查询产品分类
	 */
	@Override
	public CommonResponse<Object> getCategory(String id) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		List<ProductCategory> list = new ArrayList<>();
		try {
			if (StringUtils.isEmpty(id)) {
				Sort sort = Sort.by(Direction.ASC, "sort");
				list = productCategoryRepository.findAll(sort);
			}else {
				ProductCategory category = productCategoryRepository.findById(Long.valueOf(id)).get();
				list.add(category);
			}
			commonResponse.setData(list);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
		
	}
	
	/**
	 * 生成推广海报
	 */
	@Transactional
	@Override
	public CommonResponse<Object> genPromotionQrCode(Map<String, String> requestMap) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		Map<String, String> map = new HashMap<>();
		try {
			String agentNo = requestMap.get("agentNo");
			String agentName = requestMap.get("agentName");
			
			Assert.hasText(agentNo, "agentNo不能为空。");
			
			Evoucher evoucher = null;
			List<Evoucher> evoucherList = evoucherRepository.findByStatusAndTypeAndAgentNo(ModelConstant.EVOUCHER_STATUS_NORMAL, ModelConstant.EVOUCHER_TYPE_PROMOTION, agentNo);
			if (evoucherList.isEmpty()) {
				if (agentNo.length()==11 && org.apache.commons.lang3.StringUtils.isNumeric(agentNo)) {	//合伙人是11位手机号
					//查看合伙人是否购买过推广订单。如果有，说明是合伙人
					List<ServiceOrder> orderList = serviceOrderRepository.findByTelAndStatusAndOrderType(agentNo, ModelConstant.ORDER_STATUS_PAYED, ModelConstant.ORDER_TYPE_PROMOTION);
					if (orderList.isEmpty()) {
						//do nothing
					}else {
						Agent agent = agentRepository.findByAgentNo(agentNo);
						evoucher = evoucherService.createSingle4Promotion(agent);
					}
				}else {
					Agent agent = agentRepository.findByAgentNo(agentNo);
					if (agent == null) {	//机构。如果没有需要新建，以保证二位码被分享后下单能找到该机构打标记
						agent = new Agent();
						agent.setAgentNo(agentNo);
						agent.setName(agentName);
						agent.setStatus(1);
						agent = agentRepository.save(agent);
					}
					evoucher = evoucherService.createSingle4Promotion(agent);
				}
				
			}else {
				evoucher = evoucherList.get(0);
			}
			
			if (evoucher == null) {
				throw new BizValidateException("没有可以生成的海报。 ");
			}
//			ruleId=RULE_ID&productType=PRODUCT_TYPE&shareCode=SHARE_CODE
			String appid = systemConfigService.getSysConfigByKey("PROMOTION_SERVICE_APPID");
			if (StringUtils.isEmpty(appid)) {
				appid = "";
			}
			String url = PROMOTION_QRCODE_URL;
			url = url.replaceAll("RULE_ID", String.valueOf(evoucher.getRuleId())).replaceAll("PRODUCT_TYPE", String.valueOf(evoucher.getProductType())).
					replaceAll("SHARE_CODE", evoucher.getCode()).replaceAll("APP_ID", appid);
			
			url = URLEncoder.encode(url, "utf-8");
			
			map.put("codeUrl", url);
			commonResponse.setData(map);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
		
	}
	
	

}
