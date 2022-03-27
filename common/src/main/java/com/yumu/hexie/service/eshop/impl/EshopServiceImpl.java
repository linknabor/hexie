package com.yumu.hexie.service.eshop.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.eshop.dto.QueryCouponCfgDTO;
import com.yumu.hexie.integration.eshop.dto.QueryProductDTO;
import com.yumu.hexie.integration.eshop.mapper.EvoucherMapper;
import com.yumu.hexie.integration.eshop.mapper.OperatorMapper;
import com.yumu.hexie.integration.eshop.mapper.QueryCouponCfgMapper;
import com.yumu.hexie.integration.eshop.mapper.QueryCouponMapper;
import com.yumu.hexie.integration.eshop.mapper.QueryOrderMapper;
import com.yumu.hexie.integration.eshop.mapper.QueryProductMapper;
import com.yumu.hexie.integration.eshop.mapper.QueryRgroupMapper;
import com.yumu.hexie.integration.eshop.mapper.QuerySupportProductMapper;
import com.yumu.hexie.integration.eshop.mapper.RgroupOperatorMapper;
import com.yumu.hexie.integration.eshop.mapper.SaleAreaMapper;
import com.yumu.hexie.integration.eshop.resp.OrderDetailResp;
import com.yumu.hexie.integration.eshop.resp.QueryRgoupsResp;
import com.yumu.hexie.integration.eshop.resp.QueryRgoupsResp.RgroupSummaryResp;
import com.yumu.hexie.integration.eshop.resp.QueryRgroupOrdersResp;
import com.yumu.hexie.integration.eshop.resp.QueryRgroupSummaryResp;
import com.yumu.hexie.integration.eshop.vo.OrderSummaryVO;
import com.yumu.hexie.integration.eshop.vo.QueryCouponCfgVO;
import com.yumu.hexie.integration.eshop.vo.QueryCouponVO;
import com.yumu.hexie.integration.eshop.vo.QueryEvoucherVO;
import com.yumu.hexie.integration.eshop.vo.QueryOperVO;
import com.yumu.hexie.integration.eshop.vo.QueryOrderVO;
import com.yumu.hexie.integration.eshop.vo.QueryProductVO;
import com.yumu.hexie.integration.eshop.vo.QueryRgroupsVO;
import com.yumu.hexie.integration.eshop.vo.SaveCategoryVO;
import com.yumu.hexie.integration.eshop.vo.SaveCopyRgroupVo;
import com.yumu.hexie.integration.eshop.vo.SaveCouponCfgVO;
import com.yumu.hexie.integration.eshop.vo.SaveCouponVO;
import com.yumu.hexie.integration.eshop.vo.SaveLogisticsVO;
import com.yumu.hexie.integration.eshop.vo.SaveLogisticsVO.LogisticInfo;
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
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.OnSaleRule;
import com.yumu.hexie.model.market.saleplan.OnSaleRuleRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.promotion.PromotionConstant;
import com.yumu.hexie.model.promotion.coupon.CouponCfg;
import com.yumu.hexie.model.promotion.coupon.CouponRepository;
import com.yumu.hexie.model.promotion.coupon.CouponRule;
import com.yumu.hexie.model.promotion.coupon.CouponRuleRepository;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.promotion.coupon.CouponSeedRepository;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.eshop.EshopSerivce;
import com.yumu.hexie.service.eshop.EvoucherService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.dto.GainCouponDTO;

/**
 * 商品上、下架
 * @author david
 *
 */
@Service
public class EshopServiceImpl implements EshopSerivce {
	
	private final Logger logger = LoggerFactory.getLogger(EshopServiceImpl.class);
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
	private EvoucherRepository evoucherRepository;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private ProductCategoryRepository productCategoryRepository;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	@Qualifier("stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private EvoucherService evoucherService;
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GotongService gotongService;
	@Autowired
	private CouponRuleRepository couponRuleRepository;
	@Autowired
	private CouponSeedRepository couponSeedRepository;
	@Autowired
	private CouponRepository couponRepository;
	@Autowired
	private CouponService couponService;
	@Autowired
	private OrderItemRepository orderItemRepository;
	@Autowired
	private ServiceOperatorItemRepository serviceOperatorItemRepository;

	@Value("${promotion.qrcode.url}")
	private String PROMOTION_QRCODE_URL;
	
	@Override
	public CommonResponse<Object> getProduct(QueryProductVO queryProductVO) {

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Long> agentList = null;
			if (!(StringUtils.isEmpty(queryProductVO.getAgentNo())
					&& StringUtils.isEmpty(queryProductVO.getAgentName()))) {
				agentList = agentRepository.findByAgentNoOrName(1, queryProductVO.getAgentNo(),
						queryProductVO.getAgentName());
			}
			if (agentList != null ) {
				if (agentList.isEmpty()) {
					agentList.add(0L);
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
			if(page == null) {
				throw new BizValidateException("商品类型不正确");
			}
			
			List<ServiceOperator> opList = new ArrayList<>();
			int operatorType = 0;
			switch (productType) {
				case "1001":
					operatorType = ModelConstant.SERVICE_OPER_TYPE_ONSALE_TAKER;
					break;
				case "1002":
					operatorType = ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER;
					break;
				case "1003":
					operatorType = ModelConstant.SERVICE_OPER_TYPE_PROMOTION;
					break;
				case "1004":
					operatorType = ModelConstant.SERVICE_OPER_TYPE_SAASSALE;
					break;
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
			if(page == null) {
				throw new BizValidateException("商品类型不正确");
			}

			List<QueryProductMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryProductMapper.class);
			List<Object[]> regionList;
			if ("1000".equals(productType) || "1001".equals(productType) || "1003".equals(productType) || "1004".equals(productType)) {
				regionList = regionRepository.findByProductId(queryProductVO.getProductId());
			} else {
				regionList = regionRepository.findByProductId4Rgroup(queryProductVO.getProductId());
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
	 * 获取团购团长
	 */
	@Override
	public CommonResponse<Object> getRgroupLeader(QueryOperVO queryOperVO) {
		
		Assert.notNull(queryOperVO.getServiceId(), "商品ID不能为空。");
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Object[]> regionList = regionRepository.findRgroupLeaderByProduct(queryOperVO.getServiceId());
			if(regionList == null) {
				throw new BizValidateException("商品类型不正确");
			}
			List<RgroupOperatorMapper> areaList = ObjectToBeanUtils.objectToBean(regionList, RgroupOperatorMapper.class);
			commonResponse.setData(areaList);
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
			product = productRepository.findById(Long.parseLong(saveProductVO.getId()));
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
		product.setMiniPrice(Float.parseFloat(saveProductVO.getMiniPrice()));
		product.setSinglePrice(Float.parseFloat(saveProductVO.getSinglePrice()));
		product.setOriPrice(Float.parseFloat(saveProductVO.getOriPrice()));
		product.setServiceDesc(saveProductVO.getContext());
		product.setPostageFee(Float.parseFloat(saveProductVO.getPostageFee()));
		if (ModelConstant.RULE_STATUS_ON == Integer.parseInt(saveProductVO.getStatus())) {
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
			product.setProductCategoryId(Integer.parseInt(saveProductVO.getProductCategoryId()));
		}
		product = productRepository.save(product);
		
		int salePlanType = Integer.parseInt(saveProductVO.getSalePlanType());
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
			onSaleRule.setProductId(Integer.parseInt(product.getProductType()));
			onSaleRule.setProductName(product.getName());
			onSaleRule.setProductType(Integer.parseInt(saveProductVO.getType()));
			onSaleRule.setCreateDate(product.getCreateDate());
			onSaleRule.setDescription(product.getServiceDesc());
			onSaleRule.setProductId(product.getId());
			onSaleRule.setName(product.getName());
			onSaleRule.setLimitNumOnce(Integer.parseInt(saveProductVO.getLimitNumOnce()));
			onSaleRule.setStartDate(product.getStartDate());
			onSaleRule.setEndDate(product.getEndDate());
			onSaleRule.setOriPrice(product.getOriPrice());
			onSaleRule.setPrice(product.getSinglePrice());
			onSaleRule.setDescription(product.getServiceDesc());
			onSaleRule.setTimeoutForPay(30*60*1000);
			onSaleRule.setFreeShippingNum(Integer.parseInt(saveProductVO.getFreeShippingNum()));
			onSaleRule.setPostageFee(Float.parseFloat(saveProductVO.getPostageFee()));
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
				onSaleAreaItem.setRegionId(1L);	//全国
				onSaleAreaItem.setRuleId(onSaleRule.getId());
				long ruleCloseTime = onSaleRule.getEndDate().getTime();
				onSaleAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
				onSaleAreaItem.setSortNo(Integer.parseInt(saveProductVO.getSortNo()));
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
					onSaleAreaItem.setSortNo(Integer.parseInt(saveProductVO.getSortNo()));
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
		} else {
			rgroupRule = new RgroupRule();
			if ("edit".equals(saveProductVO.getOperType())) {
				List<RgroupRule> ruleList = rgroupRuleRepository.findAllByProductId(product.getId());
				if (ruleList == null || ruleList.isEmpty()) {
					throw new BizValidateException("未查询到商品上架规则，product id : " + saveProductVO.getId());
				}
				rgroupRule = ruleList.get(0);
			}
			rgroupRule.setProductId(Integer.parseInt(product.getProductType()));
			rgroupRule.setProductName(product.getName());
			rgroupRule.setProductType(Integer.parseInt(saveProductVO.getType()));
			rgroupRule.setCreateDate(product.getCreateDate());
			rgroupRule.setDescription(product.getServiceDesc());
			rgroupRule.setProductId(product.getId());
			rgroupRule.setName(product.getName());
			rgroupRule.setLimitNumOnce(Integer.parseInt(saveProductVO.getLimitNumOnce()));
			rgroupRule.setStartDate(product.getStartDate());
			rgroupRule.setEndDate(product.getEndDate());
			rgroupRule.setOriPrice(product.getOriPrice());
			rgroupRule.setPrice(product.getSinglePrice());
			rgroupRule.setDescription(product.getServiceDesc());
			rgroupRule.setTimeoutForPay(30*60*1000);
			rgroupRule.setFreeShippingNum(Integer.parseInt(saveProductVO.getFreeShippingNum()));
			rgroupRule.setPostageFee(Float.parseFloat(saveProductVO.getPostageFee()));
			rgroupRule.setGroupMinNum(Integer.parseInt(saveProductVO.getGroupMinNum()));
			
			if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
				rgroupRule.setStatus(ModelConstant.RULE_STATUS_ON);
			}else {
				rgroupRule.setStatus(ModelConstant.RULE_STATUS_OFF);
			}
			rgroupRule = rgroupRuleRepository.save(rgroupRule);
			
			Map<Long, RgroupAreaItem> areaLeaderMap = new HashMap<>();	//如果是编辑，需要先将之前保存的团长信息取出来暂存,key:regionId, value: RgroupAreaItem
			if ("edit".equals(saveProductVO.getOperType())) {
				List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByRuleId(rgroupRule.getId());
				if (areaList == null || areaList.isEmpty()) {
					throw new BizValidateException("未查询到商品上架区域，product id : " + saveProductVO.getId());
				}
				for (RgroupAreaItem rgroupAreaItem : areaList) {
					areaLeaderMap.put(rgroupAreaItem.getRegionId(), rgroupAreaItem);
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
				rgroupAreaItem.setSortNo(Integer.parseInt(saveProductVO.getSortNo()));
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
				//团长信息，如果是编辑的话，要将上次保存的存到本次编辑后的数据中去
				RgroupAreaItem leaderItem = areaLeaderMap.get(region.getId());
				if (leaderItem!=null) {
					rgroupAreaItem.setAreaLeader(leaderItem.getAreaLeader());
					rgroupAreaItem.setAreaLeaderAddr(leaderItem.getAreaLeaderAddr());
					rgroupAreaItem.setAreaLeaderId(leaderItem.getAreaLeaderId());
					rgroupAreaItem.setAreaLeaderTel(leaderItem.getAreaLeaderTel());
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
		
		ProductRule productRule;
		if (onSaleRule != null) {
			productRule = new ProductRule(product, onSaleRule);
		}else {
			productRule = new ProductRule(product, rgroupRule);
		}
		String key = ModelConstant.KEY_PRO_RULE_INFO + productRule.getId();
		redisRepository.setProdcutRule(key, productRule);
		
		redisTemplate.opsForValue().set(ModelConstant.KEY_PRO_STOCK + product.getId(), String.valueOf(product.getTotalCount()));
		
		//只有第一次新增时才将冻结商品数量置0
		String freezeCount = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_FREEZE + product.getId());
		if (StringUtils.isEmpty(freezeCount)) {
			redisTemplate.opsForValue().set(ModelConstant.KEY_PRO_FREEZE + product.getId(), "0");	//初始化冻结数量
		}
		
	}
	
	@Override
	@Transactional
	public void updateStatus(SaveProductVO saveProductVO) {
		
		Assert.hasText(saveProductVO.getId(), "商品ID不能为空。");
		Assert.hasText(saveProductVO.getOperType(), "操作类型不能为空。 ");
		
		String productId = saveProductVO.getId();
		String operType = saveProductVO.getOperType();
		
		int productStatus;
		int ruleStatus;
		int itemStatus;
		if ("on".equals(operType)) {
			productStatus = ModelConstant.PRODUCT_ONSALE;
			ruleStatus = ModelConstant.RULE_STATUS_ON;
			itemStatus = ModelConstant.DISTRIBUTION_STATUS_ON;
		}else {
			productStatus = ModelConstant.PRODUCT_OFF;
			ruleStatus = ModelConstant.RULE_STATUS_OFF;
			itemStatus = ModelConstant.DISTRIBUTION_STATUS_OFF;
		}
		Product product = productRepository.findById(Long.parseLong(productId));
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
					if ("on".equals(operType)) {	//上架时校验是否每个小区都配备了团长
						if (StringUtils.isEmpty(item.getAreaLeader()) || StringUtils.isEmpty(item.getAreaLeaderAddr())) {
							throw new BizValidateException("当前商品仍有上架区域未配置团长，请检查。");
						}
					}
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
			productRepository.updateDemo(1, Long.parseLong(productId));
		}else if ("0".equals(operType)) {
			productRepository.updateDemo(0, Long.parseLong(productId));
		}
	}

	/**
	 * 获取平台小区对应的region
	 * @param saleArea
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
					for (Region currRegion : regionList) {
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
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_SERVE_ROLE, allEntries = true)
	public void saveOper(SaveOperVO saveOperVO) {
		Assert.notNull(saveOperVO.getServiceId(), "服务ID或产品ID不能为空。");
		Agent agent = new Agent();
		if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
			agent = agentRepository.findByAgentNo(saveOperVO.getAgentNo());
		}
		
		if (ModelConstant.SERVICE_OPER_TYPE_EVOUCHER == saveOperVO.getOperatorType()) {
			Assert.notNull(saveOperVO.getServiceId(), "服务ID或产品ID不能为空。");
			serviceOperatorItemRepository.deleteByServiceId(saveOperVO.getServiceId());
		} else if (ModelConstant.SERVICE_OPER_TYPE_PROMOTION == saveOperVO.getOperatorType() ||
				ModelConstant.SERVICE_OPER_TYPE_SAASSALE == saveOperVO.getOperatorType() ||
				ModelConstant.SERVICE_OPER_TYPE_ONSALE_TAKER == saveOperVO.getOperatorType() ||
				ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER == saveOperVO.getOperatorType()) {
			
			if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
				serviceOperatorRepository.deleteByTypeAndAgentId(saveOperVO.getOperatorType(), agent.getId());
			}else {
				serviceOperatorRepository.deleteByTypeAndNullAgent(saveOperVO.getOperatorType());
			}
		} else if(ModelConstant.SERVICE_OPER_TYPE_SERVICE == saveOperVO.getOperatorType()) {
			if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
				serviceOperatorRepository.deleteByTypeAndAgentId(saveOperVO.getOperatorType(), agent.getId());
			}else {
				serviceOperatorRepository.deleteByTypeAndNullAgent(saveOperVO.getOperatorType());
			}
		}

		List<Oper> operList = saveOperVO.getOpers();
		for (Oper oper : operList) {
			
			ServiceOperator serviceOperator;
			if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
				serviceOperator = serviceOperatorRepository.findByTypeAndUserIdAndAgentId(saveOperVO.getOperatorType(), oper.getUserId(), agent.getId());
			}else {
				serviceOperator = serviceOperatorRepository.findByTypeAndUserIdAndAgentIdIsNull(saveOperVO.getOperatorType(), oper.getUserId());
			}
			
			if (serviceOperator == null) {
				serviceOperator = new ServiceOperator();
			}
			
			serviceOperator.setName(oper.getName());
			serviceOperator.setType(saveOperVO.getOperatorType());
			serviceOperator.setUserId(oper.getUserId());
			serviceOperator.setRegionId(oper.getRegionId());
			if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
				agent = agentRepository.findByAgentNo(saveOperVO.getAgentNo());
				serviceOperator.setAgentId(agent.getId());
			}
			serviceOperator.setLatitude(0d);
			serviceOperator.setLongitude(0d);
			serviceOperatorRepository.save(serviceOperator);
			
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
	 * 保存服务人员
	 */
	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_SERVE_ROLE, allEntries = true)
	public void saveRgroupLeader(SaveOperVO saveOperVO) {
		
		Assert.notNull(saveOperVO.getServiceId(), "服务ID或产品ID不能为空。");
		
		Agent agent = new Agent();
		if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
			agent = agentRepository.findByAgentNo(saveOperVO.getAgentNo());
		}
		
		if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
			serviceOperatorRepository.deleteByTypeAndAgentId(saveOperVO.getOperatorType(), agent.getId());
		}else {
			serviceOperatorRepository.deleteByTypeAndNullAgent(saveOperVO.getOperatorType());
		} 

		List<Oper> operList = saveOperVO.getOpers();
		for (Oper oper : operList) {
			
			ServiceOperator serviceOperator;
			if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
				serviceOperator = serviceOperatorRepository.findByTypeAndUserIdAndAgentId(saveOperVO.getOperatorType(), oper.getUserId(), agent.getId());
			}else {
				serviceOperator = serviceOperatorRepository.findByTypeAndUserIdAndAgentIdIsNull(saveOperVO.getOperatorType(), oper.getUserId());
			}
			
			if (serviceOperator == null) {
				serviceOperator = new ServiceOperator();
			}
			serviceOperator.setName(oper.getName());
			serviceOperator.setType(saveOperVO.getOperatorType());
			serviceOperator.setUserId(oper.getUserId());
			serviceOperator.setOpenId(oper.getLeaderOpenid());
			serviceOperator.setTel(oper.getMobile());
			if (!StringUtils.isEmpty(saveOperVO.getAgentNo())) {
				agent = agentRepository.findByAgentNo(saveOperVO.getAgentNo());
				serviceOperator.setAgentId(agent.getId());
			}
			serviceOperatorRepository.save(serviceOperator);
			
			List<RgroupAreaItem> rgroupAreaItems = rgroupAreaItemRepository.findByProductIdAndRegionId(saveOperVO.getServiceId(), oper.getRegionId());
			for (RgroupAreaItem rgroupAreaItem : rgroupAreaItems) {
				rgroupAreaItem.setAreaLeader(oper.getName());
				rgroupAreaItem.setAreaLeaderAddr(oper.getLeaderAddr());
				rgroupAreaItem.setAreaLeaderId(oper.getUserId());
				rgroupAreaItem.setAreaLeaderOpenid(oper.getLeaderOpenid());
				rgroupAreaItem.setAreaLeaderTel(oper.getMobile());
				rgroupAreaItemRepository.save(rgroupAreaItem);
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
			List<String> listSect = null;
			if(queryEvoucherVO.getSectIds() != null && queryEvoucherVO.getSectIds().size() > 0) {
				//查询小区对应的ID
				listSect = regionRepository.getRegionBySectid(queryEvoucherVO.getSectIds());
			}

			Page<Evoucher> page = evoucherRepository.findByMultipleConditions(queryEvoucherVO.getStatus(), queryEvoucherVO.getTel(), queryEvoucherVO.getAgentNo(), queryEvoucherVO.getAgentName(), queryEvoucherVO.getType(), queryEvoucherVO.getUserid(), listSect, pageable);

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
				order.setStatus(ModelConstant.ORDER_STATUS_REFUNDING);
				serviceOrderRepository.save(order);
				
			}else if ("1".equals(operType)) {
				fromStatus = ModelConstant.EVOUCHER_STATUS_INVALID;
				toStatus = ModelConstant.EVOUCHER_STATUS_NORMAL;
				
				order.setStatus(ModelConstant.ORDER_STATUS_PAYED);
				if (!StringUtils.isEmpty(order.getSendDate())) {
					order.setStatus(ModelConstant.ORDER_STATUS_SENDED);
				}else if (!StringUtils.isEmpty(order.getConfirmDate())) {
					order.setStatus(ModelConstant.ORDER_STATUS_CONFIRM);
				}
				serviceOrderRepository.save(order);
				
			}
		}
		//TODO 考虑放到异步通知里去，但是时效性较差
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
				ProductCategory category = productCategoryRepository.findById(Long.parseLong(id));
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
					if (!orderList.isEmpty()) {
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

	/**
	 * 订单查询
	 */
	@Override
	public CommonResponse<Object> getOrder(QueryOrderVO queryOrderVO) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			List<Integer> typeList = new ArrayList<>();
			String orderType = queryOrderVO.getOrderType();
			if (StringUtils.isEmpty(orderType)) {
				typeList.add(ModelConstant.ORDER_TYPE_ONSALE);
				typeList.add(ModelConstant.ORDER_TYPE_RGROUP);
			} else {
				typeList.add(Integer.valueOf(orderType));
			}
			List<Integer> statusList = new ArrayList<>();
			String status = queryOrderVO.getStatus();
			if (StringUtils.isEmpty(status)) {
				statusList.add(ModelConstant.ORDER_STATUS_PAYED);
				statusList.add(ModelConstant.ORDER_STATUS_REFUNDED);
				statusList.add(ModelConstant.ORDER_STATUS_REFUNDING);
				statusList.add(ModelConstant.ORDER_STATUS_RETURNED);
				statusList.add(ModelConstant.ORDER_STATUS_SENDED);
				statusList.add(ModelConstant.ORDER_STATUS_CONFIRM);
			} else {
				statusList.add(Integer.valueOf(status));
			} 	
			List<Order> sortList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "id");
	    	sortList.add(order);
	    	Sort sort = Sort.by(sortList);
			
			Pageable pageable = PageRequest.of(queryOrderVO.getCurrentPage(), queryOrderVO.getPageSize(), sort);
			
			String sDate = queryOrderVO.getSendDateBegin();
			String eDate = queryOrderVO.getSendDateEnd();
			Page<Object[]> page;

			if(!"1".equals(queryOrderVO.getQueryFlag())) {
				if (!StringUtils.isEmpty(sDate)) {
					Date startDate = DateUtil.parse(sDate + " 00:00:00", DateUtil.dttmSimple);
					sDate = startDate.toString();
				}
				if (!StringUtils.isEmpty(eDate)) {
					Date endDate = DateUtil.parse(eDate + " 23:59:59", DateUtil.dttmSimple);
					eDate = endDate.toString();
				}

				page = serviceOrderRepository.findByMultiCondition(typeList, statusList, queryOrderVO.getId(),
						queryOrderVO.getProductName(), queryOrderVO.getOrderNo(), queryOrderVO.getReceiverName(), queryOrderVO.getTel(),
						queryOrderVO.getLogisticNo(), sDate, eDate, queryOrderVO.getAgentNo(),
						queryOrderVO.getAgentName(), queryOrderVO.getSectName(), queryOrderVO.getGroupStatus(), pageable);
			} else { //从运营端过来

				List<String> listSect = null;
				if(queryOrderVO.getSectIds() != null && queryOrderVO.getSectIds().size() > 0) {
					//查询小区对应的ID
					listSect = regionRepository.getRegionBySectid(queryOrderVO.getSectIds());
				}

				page = serviceOrderRepository.findByOrder(typeList, statusList, queryOrderVO.getId(),
						queryOrderVO.getProductName(), queryOrderVO.getOrderNo(), queryOrderVO.getReceiverName(), queryOrderVO.getTel(),
						queryOrderVO.getLogisticNo(), sDate, eDate, queryOrderVO.getAgentNo(),
						queryOrderVO.getAgentName(), queryOrderVO.getSectName(), queryOrderVO.getGroupStatus(), queryOrderVO.getUserid(), listSect, pageable);
			}

			logger.error("page.getContent():" + page.getContent());

			List<QueryOrderMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryOrderMapper.class);
			QueryListDTO<List<QueryOrderMapper>> responsePage = new QueryListDTO<>();
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

	@Override
	public String getOrderSummary(OrderSummaryVO orderSummaryVO) {
		List<String> listSect = null;
		if(orderSummaryVO.getSectIds() != null && orderSummaryVO.getSectIds().size() > 0) {
			//查询小区对应的ID
			listSect = regionRepository.getRegionBySectid(orderSummaryVO.getSectIds());
		}

		List<Integer> typeList = new ArrayList<>();
		typeList.add(ModelConstant.ORDER_TYPE_ONSALE);
		typeList.add(ModelConstant.ORDER_TYPE_RGROUP);
		typeList.add(ModelConstant.ORDER_TYPE_SERVICE);

		List<Integer> statusList = new ArrayList<>();
		statusList.add(ModelConstant.ORDER_STATUS_PAYED);
		statusList.add(ModelConstant.ORDER_STATUS_REFUNDED);
		statusList.add(ModelConstant.ORDER_STATUS_REFUNDING);
		statusList.add(ModelConstant.ORDER_STATUS_RETURNED);
		statusList.add(ModelConstant.ORDER_STATUS_SENDED);
		statusList.add(ModelConstant.ORDER_STATUS_CONFIRM);

		String date = DateUtil.dtFormat(new Date());
		Date startDate = DateUtil.parse(date + " 00:00:00", DateUtil.dttmSimple);
		long sDate = startDate.getTime();

		Date endDate = DateUtil.parse(date + " 23:59:59", DateUtil.dttmSimple);
		long eDate = endDate.getTime();

		List<ServiceOrder> list = serviceOrderRepository.findOrderSummary(typeList, statusList, sDate, eDate, orderSummaryVO.getAgentNo(), orderSummaryVO.getUserid(), listSect);
		return String.valueOf(list.size());
	}

	@Override
	public OrderDetailResp getOrderDetail(String orderId) {
		
		Assert.hasText(orderId, "订单编号不能为空。");
		List<OrderItem> itemList = new ArrayList<>();
		ServiceOrder order = serviceOrderRepository.findById(Long.parseLong(orderId));
		if (order == null) {
			order = serviceOrderRepository.findByOrderNo(orderId);
		}
		if (order != null) {
			itemList = orderItemRepository.findByServiceOrder(order);
		} else {
			List<ServiceOrder> orderList = serviceOrderRepository.findByGroupOrderId(Long.parseLong(orderId));
			if (!orderList.isEmpty()) {
				order = orderList.get(0);
				for (ServiceOrder serviceOrder : orderList) {
					List<OrderItem> oList = orderItemRepository.findByServiceOrder(serviceOrder);
					itemList.addAll(oList);
				}
			}
		}
		
		OrderDetailResp resp = new OrderDetailResp();

		if(order == null) {
			return resp;
		}

		OrderDetailResp.OrderResp orderResp = new OrderDetailResp.OrderResp(order);
		resp.setOrder(orderResp);

		List<OrderDetailResp.OrderSubResp> listSub = new ArrayList<>();
		for(OrderItem item : itemList) {
			OrderDetailResp.OrderSubResp sub = new OrderDetailResp.OrderSubResp(item);
			listSub.add(sub);
		}
		resp.setDetails(listSub);

		return resp;
	}

	/**
	 * 保存物流信息
	 * @param saveLogisticsVO
	 */
	@Transactional
	@Override
	public void saveLogistics(SaveLogisticsVO saveLogisticsVO) {
		
		Assert.hasText(saveLogisticsVO.getOrderId(), "订单id不能为空。");
		
		Long orderId = Long.valueOf(saveLogisticsVO.getOrderId());
		Optional<ServiceOrder> optional = serviceOrderRepository.findById(orderId);
		if (optional.isPresent()) {
			
			List<LogisticInfo> logisticList = saveLogisticsVO.getLogistics();
			StringBuilder codeBf  = new StringBuilder();
			StringBuilder comBf = new StringBuilder();
			StringBuilder noBf = new StringBuilder();
			
			for (int i=0; i<logisticList.size(); i++) {
				
				codeBf.append(logisticList.get(i).getLogisticCode());
				comBf.append(logisticList.get(i).getLogisticName());
				noBf.append(logisticList.get(i).getLogisticNo());
				
				if (i!= logisticList.size()-1) {
					codeBf.append(",");
					comBf.append(",");
					noBf.append(",");
				}
			}
			
			ServiceOrder order = optional.get();
			order.setLogisticCode(codeBf.toString());
			order.setLogisticName(comBf.toString());
			order.setLogisticNo(noBf.toString());
			order.setLogisticType(3);	//第三方配送
			order.setStatus(ModelConstant.ORDER_STATUS_SENDED);//改状态为已发货
			order.setSendDate(new Date());
			
			//提醒用户已发货
			User user = userRepository.findById(order.getUserId());
			gotongService.sendCustomerDelivery(user, order);
			
		}
	}
	
	/**
	 * 查询优惠券配置列表
	 * @param queryCouponCfgVO
	 */
	@Override
	public CommonResponse<Object> getCouponCfg(QueryCouponCfgVO queryCouponCfgVO) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Long> agentList = null;
			if (!(StringUtils.isEmpty(queryCouponCfgVO.getAgentNo()) && StringUtils.isEmpty(queryCouponCfgVO.getAgentName()))) {
				agentList = agentRepository.findByAgentNoOrName(1, queryCouponCfgVO.getAgentNo(),
						queryCouponCfgVO.getAgentName());
			}
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "id");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			
			Pageable pageable = PageRequest.of(queryCouponCfgVO.getCurrentPage(), queryCouponCfgVO.getPageSize(), sort);
			Page<Object[]> page = couponRuleRepository.findByMultiCondition(queryCouponCfgVO.getRuleId(), queryCouponCfgVO.getSeedId(), 
					queryCouponCfgVO.getSeedType(), queryCouponCfgVO.getStatus(), agentList, queryCouponCfgVO.getTitle(), pageable);
			
			List<QueryCouponCfgMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryCouponCfgMapper.class);
			QueryListDTO<List<QueryCouponCfgMapper>> responsePage = new QueryListDTO<>();
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
	 * 根据优惠券规则ID查询优惠券及其支持（不支持）的商品
	 */
	@Override
	public CommonResponse<Object> getCouponCfgByRuleId(QueryCouponCfgVO queryCouponCfgVO) {
		
		Assert.hasText(queryCouponCfgVO.getRuleId(), "规则ID不能为空。");

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			Pageable pageable = PageRequest.of(0, 1);
			Page<Object[]> page = couponRuleRepository.findByMultiCondition(queryCouponCfgVO.getRuleId(), "", "", 
					null, null, "", pageable);
				
			List<QueryCouponCfgMapper> cfgList = ObjectToBeanUtils.objectToBean(page.getContent(), QueryCouponCfgMapper.class);
			
			QueryCouponCfgDTO<QueryCouponCfgMapper, Product> queryProductDTO = new QueryCouponCfgDTO<>();
			queryProductDTO.setContent(cfgList.get(0));
			
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
	 * 选择支持优惠券的商品
	 * @param queryProductVO
	 */
	@Override
	public CommonResponse<Object> getSupportProduct(QueryProductVO queryProductVO) {

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Long> agentList = null;
			if (!(StringUtils.isEmpty(queryProductVO.getAgentNo()) && StringUtils.isEmpty(queryProductVO.getAgentName()))) {
				agentList = agentRepository.findByAgentNoOrName(1, queryProductVO.getAgentNo(),
						queryProductVO.getAgentName());
			}
			
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "id");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			
			Pageable pageable = PageRequest.of(queryProductVO.getCurrentPage(), queryProductVO.getPageSize(), sort);
			String productType = queryProductVO.getProductType();
			List<String> typeList = new ArrayList<>();
			if ("9999".equals(productType)) {
				typeList.add("1001");
				typeList.add("1002");
			}else {
				typeList.add(productType);
			}
			
			Page<Object[]>	page = productRepository.getSupportProduct(typeList, queryProductVO.getProductStatus(), 
					queryProductVO.getProductName(), agentList, pageable);
			
			List<QuerySupportProductMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QuerySupportProductMapper.class);
			QueryListDTO<List<QuerySupportProductMapper>> responsePage = new QueryListDTO<>();

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
	 * 保存商品上架内容
	 */
	@Override
	@Transactional
	public void saveCouponCfg(SaveCouponCfgVO saveCouponCfgVO) throws Exception {
	
		Agent agent;
		if ("1".equals(saveCouponCfgVO.getSupportAllAgent())) {	//全平台通用
			agent = new Agent();
			agent.setId(0L);
		} else {	
			agent = agentRepository.findByAgentNo(saveCouponCfgVO.getAgentNo());
		}
		
		if ("add".equals(saveCouponCfgVO.getOperType())) {
			if (agent == null) {
				agent = new Agent();
				agent.setName(saveCouponCfgVO.getAgentName());
				agent.setAgentNo(saveCouponCfgVO.getAgentNo());
				agent.setStatus(1);
				agent = agentRepository.save(agent);
			}
		}
		
		/*保存红包种子 start */
		CouponSeed couponSeed = new CouponSeed();
		if ("edit".equals(saveCouponCfgVO.getOperType())) {
			couponSeed = couponSeedRepository.findById(Long.parseLong(saveCouponCfgVO.getSeedId()));
			if (couponSeed == null) {
				throw new BizValidateException("未查询到商品，id : " + saveCouponCfgVO.getSeedId());
			}
		}
		couponSeed.setTitle(saveCouponCfgVO.getTitle());
		couponSeed.setSeedType(Integer.parseInt(saveCouponCfgVO.getSeedType()));
		
		int seedStatus = ModelConstant.COUPON_SEED_STATUS_AVAILABLE;
		if (!"1".equals(saveCouponCfgVO.getStatus())) {
			seedStatus = ModelConstant.COUPON_SEED_STATUS_INVALID;
		}
		int oriCount = couponSeed.getTotalCount();	//原来的库存，新增情况下，这个值是0
		int addedCount = Integer.parseInt(saveCouponCfgVO.getTotalCount()) - oriCount; //编辑后增加的数量，跟当前有多少库存无关
		couponSeed.setTotalCount(Integer.valueOf(saveCouponCfgVO.getTotalCount()));	//总数，以种子的总数为统计单位，规则里的总数分享时用。
		BigDecimal unitAmt = new BigDecimal(saveCouponCfgVO.getAmount());
		BigDecimal count = new BigDecimal(couponSeed.getTotalCount());
		BigDecimal totalAmt = unitAmt.multiply(count);
		couponSeed.setTotalAmount(totalAmt.floatValue());
		couponSeed.setStatus(seedStatus);
		couponSeed.setRate(1d);	//固定1.0
		
		//生效时间
		Date startDate = DateUtil.parse(saveCouponCfgVO.getStartDate(), DateUtil.dttmSimple);
		couponSeed.setStartDate(startDate);
		Date endDate = DateUtil.parse(saveCouponCfgVO.getEndDate(), DateUtil.dttmSimple);
		couponSeed.setEndDate(endDate);
		
		couponSeed.setSeedImg(saveCouponCfgVO.getSeedImg());
		couponSeed.setRuleDescription(saveCouponCfgVO.getCouponDesc());
		couponSeed.setDescription(saveCouponCfgVO.getCouponDesc());
		
		if ("add".equals(saveCouponCfgVO.getOperType())) {	//不可修改项，生成后唯一
			String seedStr = DigestUtils.md5Hex((UUID.randomUUID().toString()));//唯一标识
			couponSeed.setSeedStr(seedStr);	//种子
		}
		couponSeed = couponSeedRepository.save(couponSeed);
		/*保存红包种子 end */
		
		/*保存红包规则 start */
		/*添加支持的小区，领券时判断 start*/
		String supportType = saveCouponCfgVO.getSupportType();	//0全部支持，1支持部分商品，2不支持部分商品
		if (StringUtils.isEmpty(supportType)) {
			supportType = "0";
		}
		CouponRule couponRule = new CouponRule();
		if ("add".equals(saveCouponCfgVO.getOperType())) {
			couponRule.setAgentId(agent.getId());
		}
		if ("edit".equals(saveCouponCfgVO.getOperType())) {
			couponRule = couponRuleRepository.findById(Long.parseLong(saveCouponCfgVO.getRuleId()));
			if (couponRule == null) {
				throw new BizValidateException("未查询到商品，id : " + saveCouponCfgVO.getRuleId());
			}
			if ("1".equals(saveCouponCfgVO.getSupportAllAgent())) {	//全平台通用
				couponRule.setAgentId(0L);
			} else {
				if (couponRule.getAgentId() == 0L) {
					couponRule.setAgentId(1L);	//奈博的
				}
				agent = agentRepository.findById(couponRule.getAgentId());
			}
		}
		
		List<String> marketRegions = null;
		List<String> serviceRegions = null;
		if (PromotionConstant.COUPON_ITEM_TYPE_ALL.equals(Integer.valueOf(saveCouponCfgVO.getItemType()))) {
			marketRegions = getMarketRegions(saveCouponCfgVO.getSupported(), saveCouponCfgVO.getUnsupported(), agent, supportType);
			serviceRegions = getServiceRegions(saveCouponCfgVO.getServiceSupportedSect());
		} else if (PromotionConstant.COUPON_ITEM_TYPE_EVOUCHER.equals(Integer.valueOf(saveCouponCfgVO.getItemType())) ||
				PromotionConstant.COUPON_ITEM_TYPE_MARKET.equals(Integer.valueOf(saveCouponCfgVO.getItemType()))) {
			marketRegions = getMarketRegions(saveCouponCfgVO.getSupported(), saveCouponCfgVO.getUnsupported(), agent, supportType);
		} else if (PromotionConstant.COUPON_ITEM_TYPE_SERVICE.equals(Integer.valueOf(saveCouponCfgVO.getItemType()))) {
			serviceRegions = getServiceRegions(saveCouponCfgVO.getServiceSupportedSect());
		}
		if (marketRegions == null) {
			marketRegions = new ArrayList<>();
		}
		if (serviceRegions == null) {
			serviceRegions = new ArrayList<>();
		}
		marketRegions.removeAll(serviceRegions);
		marketRegions.addAll(serviceRegions);
		
		StringBuilder bf = new StringBuilder();
		for (String sectId : marketRegions) {
			if (StringUtils.isEmpty(sectId)) {
				continue;
			}
			bf.append(sectId).append(",");
		}
		String serviceSupported = bf.substring(0, bf.length()-1);
		couponRule.setSectIds(serviceSupported);
		/*添加支持的小区，领券时判断 end*/
		
		couponRule.setTitle(couponSeed.getTitle());	//名称
		couponRule.setSeedId(couponSeed.getId());
		couponRule.setTitle(couponSeed.getTitle());
		couponRule.setTotalCount(couponSeed.getTotalCount());
		couponRule.setAmount(Float.parseFloat(saveCouponCfgVO.getAmount()));
		couponRule.setUsageCondition(Float.parseFloat(saveCouponCfgVO.getUsageCondition()));
		couponRule.setItemType(Integer.parseInt(saveCouponCfgVO.getItemType()));	//适用模块

		switch (supportType) {
			case "0":
				couponRule.setProductId("");
				couponRule.setuProductId("");
				break;
			case "1":
				couponRule.setProductId(saveCouponCfgVO.getSupported());
				couponRule.setuProductId("");
				break;
			case "2":
				couponRule.setuProductId(saveCouponCfgVO.getUnsupported());
				couponRule.setProductId("");
				break;
		}
		couponRule.setSupportType(Integer.parseInt(supportType));
		couponRule.setStartDate(couponSeed.getStartDate());
		couponRule.setEndDate(couponSeed.getEndDate());
		int expiredDays = 0;
		if (!StringUtils.isEmpty(saveCouponCfgVO.getExpiredDays())) {
			expiredDays = Integer.parseInt(saveCouponCfgVO.getExpiredDays());
		}
		if (expiredDays > 0) {
			couponRule.setExpiredDays(Integer.parseInt(saveCouponCfgVO.getExpiredDays()));
			couponRule.setUseStartDate(null);
			couponRule.setUseEndDate(null);
		}else {
			//可用日期
			Date useStartDate = DateUtil.parse(saveCouponCfgVO.getUseStartDate(), DateUtil.dttmSimple);
			couponRule.setUseStartDate(useStartDate);
			Date useEndDate = DateUtil.parse(saveCouponCfgVO.getUseEndDate(), DateUtil.dttmSimple);
			couponRule.setUseEndDate(useEndDate);
			couponRule.setExpiredDays(0);
		}
		
		int ruleStatus = ModelConstant.COUPON_RULE_STATUS_AVAILABLE;
		if (!"1".equals(saveCouponCfgVO.getStatus())) {
			ruleStatus = ModelConstant.COUPON_RULE_STATUS_INVALID;
		}
		couponRule.setSuggestUrl(saveCouponCfgVO.getSuggestUrl());
		couponRule.setStatus(ruleStatus);
		couponRule.setCouponDesc(saveCouponCfgVO.getCouponDesc());
		couponRule = couponRuleRepository.save(couponRule);
		
		CouponCfg couponCfg = new CouponCfg(couponRule, couponSeed);
		String key = ModelConstant.KEY_COUPON_RULE + couponRule.getId();
		redisRepository.setCouponCfg(key, couponCfg);
		
		int currValue;
		String currCount = redisTemplate.opsForValue().get(ModelConstant.KEY_COUPON_TOTAL + couponRule.getId());
		if (!StringUtils.isEmpty(currCount)) {
			currValue = Integer.parseInt(currCount);
			if (currValue < 0) {
				addedCount = addedCount - currValue;	//先要平成0，因为redis里面是一直减的，会有负值
			}
		}
		
		redisTemplate.opsForValue().increment(ModelConstant.KEY_COUPON_TOTAL + couponRule.getId(), addedCount);
		long expire = couponRule.getEndDate().getTime() - couponRule.getStartDate().getTime();
		redisTemplate.opsForValue().set(ModelConstant.KEY_COUPON_SEED + couponSeed.getSeedStr(), String.valueOf(couponRule.getId()), expire, TimeUnit.SECONDS);
	}

	/**
	 * 获取自定义服务支持的区域列表
	 * @param serviceSupportSect
	 * @return
	 */
	private List<String> getServiceRegions(String serviceSupportSect) {
		
		List<String> serviceRegions = new ArrayList<>();
		if (!StringUtils.isEmpty(serviceSupportSect)) {
			String[]sects = serviceSupportSect.split(",");
			serviceRegions = Arrays.asList(sects);
		}
		return serviceRegions;
	}

	/**
	 * 获取核销券、特卖和团购商品所支持的区域列表
	 * @param supported
	 * @param unsupported
	 * @param agent
	 * @param supportType
	 * @return
	 */
	private List<String> getMarketRegions(String supported, String unsupported, Agent agent, String supportType) {
		
		String agentId = String.valueOf(agent.getId());
		if (agent.getId() == 0) {	//全平台通用券
			agentId = "";
		}
		
		List<Region> onsaleList = null;
		List<Region> rgroupList = null;
		if ("0".equals(supportType) || StringUtils.isEmpty(supportType)) {
			onsaleList = regionRepository.findByAgentIdOrProductId(ModelConstant.DISTRIBUTION_STATUS_ON, null, null, agentId);
			rgroupList = regionRepository.findByAgentIdOrProductId4Rgroup(ModelConstant.DISTRIBUTION_STATUS_ON, null, null, agentId);
		}else if ("1".equals(supportType)) {
			String[]products = supported.split(",");
			List<String> productList = Arrays.asList(products);
			onsaleList = regionRepository.findByAgentIdOrProductId(ModelConstant.DISTRIBUTION_STATUS_ON, productList, null, agentId);
			rgroupList = regionRepository.findByAgentIdOrProductId4Rgroup(ModelConstant.DISTRIBUTION_STATUS_ON, productList, null, agentId);
		}else if ("2".equals(supportType)) {
			String[]uproducts = unsupported.split(",");
			List<String> uproductList = Arrays.asList(uproducts);
			onsaleList = regionRepository.findByAgentIdOrProductId(ModelConstant.DISTRIBUTION_STATUS_ON, null, uproductList, agentId);
			rgroupList = regionRepository.findByAgentIdOrProductId4Rgroup(ModelConstant.DISTRIBUTION_STATUS_ON, null, uproductList, agentId);
		}
		List<String> sectIds = null;
		if(rgroupList != null) {
			onsaleList.removeAll(rgroupList);	//去重
			onsaleList.addAll(rgroupList);	//取并集
			sectIds = new ArrayList<>(onsaleList.size());
			for (Region region : onsaleList) {
				sectIds.add(region.getSectId());
			}
		}
		return sectIds;
	}
	
	/**
	 * 查询红包列表
	 */
	@Override
	public CommonResponse<Object> getCouponList(QueryCouponVO queryCouponVO){
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		
		try {
			List<Long> agentList = null;
			if (!(StringUtils.isEmpty(queryCouponVO.getAgentNo()) && StringUtils.isEmpty(queryCouponVO.getAgentName()))) {
				agentList = agentRepository.findByAgentNoOrName(1, queryCouponVO.getAgentNo(),
						queryCouponVO.getAgentName());
			}
			List<Order> orderList = new ArrayList<>();
			Order order = new Order(Direction.DESC, "id");
			orderList.add(order);
			Sort sort = Sort.by(orderList);
			
			Pageable pageable = PageRequest.of(queryCouponVO.getCurrentPage(), queryCouponVO.getPageSize(), sort);
			
			List<Integer> statusList = new ArrayList<>();
			if (StringUtils.isEmpty(queryCouponVO.getStatus())) {
				statusList.add(ModelConstant.COUPON_STATUS_AVAILABLE);
				statusList.add(ModelConstant.COUPON_STATUS_LOCKED);
				statusList.add(ModelConstant.COUPON_STATUS_TIMEOUT);
				statusList.add(ModelConstant.COUPON_STATUS_USED);
			}else {
				statusList.add(Integer.valueOf(queryCouponVO.getStatus()));
			}
			
			Page<Object[]> page = couponRepository.findByMultiCondition(statusList, queryCouponVO.getTitle(), queryCouponVO.getSeedType(), 
					queryCouponVO.getTel(), agentList, pageable);
			
			List<QueryCouponMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryCouponMapper.class);
			QueryListDTO<List<QueryCouponMapper>> responsePage = new QueryListDTO<>();
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
	 * 保存商品上架内容
	 */
	@Override
	public CommonResponse<Object> saveCoupon(SaveCouponVO saveCouponVO) {
	
		Assert.hasText(saveCouponVO.getUserId(), "用户id不能为空。");
		Assert.hasText(saveCouponVO.getSeedStr(), "优惠券种子不能为空。");
		
		long userId = Long.parseLong(saveCouponVO.getUserId());
		User user = userRepository.findById(userId);

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			GainCouponDTO dto = couponService.gainCouponFromSeed(user, saveCouponVO.getSeedStr());
			if (dto.isSuccess()) {
				commonResponse.setResult("00");
			}else {
				throw new BizValidateException(dto.getErrMsg());
			}
			
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
		
	}
	
	/**
	 * 团购订单查询
	 */
	@Override
	public CommonResponse<Object> getRgroupOrders(QueryOrderVO queryOrderVO) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Integer> typeList = new ArrayList<>();
			String orderType = queryOrderVO.getOrderType();
			if (StringUtils.isEmpty(orderType)) {
				typeList.add(ModelConstant.ORDER_TYPE_RGROUP);
			} else {
				typeList.add(Integer.valueOf(orderType));
			}
			List<Integer> statusList = new ArrayList<>();
			String status = queryOrderVO.getOrderStatus();
			if (StringUtils.isEmpty(status)) {
//				statusList.add(ModelConstant.ORDER_STATUS_PAYED);
				statusList.add(ModelConstant.ORDER_STATUS_CONFIRM);
				statusList.add(ModelConstant.ORDER_STATUS_SENDED);
				statusList.add(ModelConstant.ORDER_STATUS_RECEIVED);
			} else {
				Integer currStatus = Integer.valueOf(status);
				statusList.add(currStatus);
				if (ModelConstant.ORDER_STATUS_SENDED == currStatus) {
					statusList.add(ModelConstant.ORDER_STATUS_RECEIVED);
				}
			} 	
			List<Order> sortList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "createDate");
	    	sortList.add(order);
	    	Sort sort = Sort.by(sortList);
			
			Pageable pageable = PageRequest.of(queryOrderVO.getCurrentPage(), queryOrderVO.getPageSize(), sort);
			
			List<String> sectList = null;
			if(queryOrderVO.getSectIds() != null && queryOrderVO.getSectIds().size() > 0) {
				//查询小区对应的ID
				sectList = regionRepository.getRegionBySectid(queryOrderVO.getSectIds());
			}
			
			//1.先查询列表和页数
			Page<Object[]> page = serviceOrderRepository.findByMultiConditionAndLeaderId(typeList, statusList, queryOrderVO.getId(),
					queryOrderVO.getProductName(), queryOrderVO.getOrderNo(), queryOrderVO.getReceiverName(), queryOrderVO.getUserTel(),
					queryOrderVO.getUserAddr(), queryOrderVO.getLogisticNo(), "", "", queryOrderVO.getAgentNo(), queryOrderVO.getAgentName(),
					queryOrderVO.getSectName(),  queryOrderVO.getRuleId(), queryOrderVO.getGroupStatus(), 
					queryOrderVO.getUserid(), queryOrderVO.getCreateDateBegin(), queryOrderVO.getCreateDateEnd(), sectList, pageable);

			List<QueryOrderMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryOrderMapper.class);
			if (list == null) {
				list = new ArrayList<>();
			}
			QueryRgroupOrdersResp queryRgroupOrdersResp = new QueryRgroupOrdersResp();
			queryRgroupOrdersResp.setOrderList(list);
			
			//2.再查询各种状态的合计
			statusList = new ArrayList<>();
//			statusList.add(ModelConstant.ORDER_STATUS_PAYED);
			statusList.add(ModelConstant.ORDER_STATUS_CONFIRM);
			statusList.add(ModelConstant.ORDER_STATUS_SENDED);
			statusList.add(ModelConstant.ORDER_STATUS_RECEIVED);
			
			//分页数最大,先写10000条 TODO
			Pageable sumamryPage = PageRequest.of(0, 10000, sort);
			page = serviceOrderRepository.findByMultiConditionAndLeaderId(typeList, statusList, queryOrderVO.getId(),
					queryOrderVO.getProductName(), queryOrderVO.getOrderNo(), queryOrderVO.getReceiverName(), queryOrderVO.getUserTel(),
					queryOrderVO.getUserAddr(), queryOrderVO.getLogisticNo(), "", "", queryOrderVO.getAgentNo(), queryOrderVO.getAgentName(),
					queryOrderVO.getSectName(), queryOrderVO.getRuleId(), queryOrderVO.getGroupStatus(), 
					queryOrderVO.getUserid(), queryOrderVO.getCreateDateBegin(), queryOrderVO.getCreateDateEnd(), sectList, sumamryPage);
			
			QueryRgroupSummaryResp querySummary = new QueryRgroupSummaryResp();
			List<QueryOrderMapper> summarylist = ObjectToBeanUtils.objectToBean(page.getContent(), QueryOrderMapper.class);
			int delivered = 0;
			int undelivered = 0;
			if (summarylist != null) {
				for (QueryOrderMapper queryOrderMapper : summarylist) {
					if (ModelConstant.ORDER_STATUS_PAYED == queryOrderMapper.getStatus() || ModelConstant.ORDER_STATUS_CONFIRM == queryOrderMapper.getStatus()) {
						undelivered++;
					} else if (ModelConstant.ORDER_STATUS_SENDED == queryOrderMapper.getStatus() || ModelConstant.ORDER_STATUS_RECEIVED == queryOrderMapper.getStatus()) {
						delivered++;
					} else {
						logger.info("unknonw rgroup status : " + queryOrderMapper.getStatus());
					}
				}
			}
			querySummary.setDelivered(delivered);
			querySummary.setUndelivered(undelivered);
			queryRgroupOrdersResp.setOrderSummary(querySummary);
			
			commonResponse.setData(queryRgroupOrdersResp);
			commonResponse.setResult("00");
		
		} catch (Exception e) {

			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}

		return commonResponse;
	}
	
	/**
	 * 团购信息查询
	 */
	@Override
	public CommonResponse<Object> getRgroups(QueryRgroupsVO queryRgroupsVO) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
				
			List<Order> sortList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "createDate");
	    	sortList.add(order);
	    	Sort sort = Sort.by(sortList);
			
			Pageable pageable = PageRequest.of(queryRgroupsVO.getCurrentPage(), queryRgroupsVO.getPageSize(), sort);
			
			List<String> sectList = null;
			if(queryRgroupsVO.getSectList() != null && queryRgroupsVO.getSectList().size() > 0) {
				//查询小区对应的ID
				sectList = regionRepository.getRegionBySectid(queryRgroupsVO.getSectList());
			}
			
			List<Integer> allStatus = new ArrayList<>();
			allStatus.add(ModelConstant.RGROUP_STAUS_GROUPING);	//进行中的
			allStatus.add(ModelConstant.RGROUP_STAUS_FINISH);	//已成团
			allStatus.add(ModelConstant.RGROUP_STAUS_DELIVERING);	//发货中
			allStatus.add(ModelConstant.RGROUP_STAUS_DELIVERED);	//发货完成
			
			List<Integer> statusList = null;
			String groupStatus = queryRgroupsVO.getGroupStatus();
			if (!StringUtils.isEmpty(groupStatus)) {
				statusList = new ArrayList<>();
				statusList.add(Integer.valueOf(groupStatus));
			} else {
				statusList = allStatus;
			}
			String productType = "1002";
			Page<Object[]> page = rgroupRuleRepository.findByMultiCondRgroup(productType, queryRgroupsVO.getRuleId(), queryRgroupsVO.getRuleName(), 
					statusList, queryRgroupsVO.getStartDate(), queryRgroupsVO.getEndDate(), queryRgroupsVO.getAgentNo(), Boolean.FALSE.toString(), 
					queryRgroupsVO.getUserid(), sectList, new Date(), pageable);
			

			List<QueryRgroupMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryRgroupMapper.class);
			if (list == null) {
				list = new ArrayList<>();
			}
			QueryRgoupsResp queryRgoupsResp = new QueryRgoupsResp();
			queryRgoupsResp.setGroupList(list);
			
			//2.再查询各种状态的合计
			statusList = null;
			
			//分页数最大,先写10000条 TODO
			Pageable sumamryPage = PageRequest.of(0, 10000, sort);
			page = rgroupRuleRepository.findByMultiCondRgroup(productType, queryRgroupsVO.getRuleId(), queryRgroupsVO.getRuleName(), 
					allStatus, queryRgroupsVO.getStartDate(), queryRgroupsVO.getEndDate(), queryRgroupsVO.getAgentNo(), Boolean.FALSE.toString(), 
					queryRgroupsVO.getUserid(), sectList, new Date(), sumamryPage);
			
			List<QueryRgroupMapper> summarylist = ObjectToBeanUtils.objectToBean(page.getContent(), QueryRgroupMapper.class);
			int grouping = 0;
			int grouped = 0;
			int delivering = 0;
			int delivered = 0;
			if (summarylist != null) {
				for (QueryRgroupMapper queryRgroupMapper : summarylist) {
					if (ModelConstant.RGROUP_STAUS_GROUPING == queryRgroupMapper.getGroupStatus()) {
						grouping++;
					} else if (ModelConstant.RGROUP_STAUS_FINISH == queryRgroupMapper.getGroupStatus()) {
						grouped++;
					} else if (ModelConstant.RGROUP_STAUS_DELIVERING == queryRgroupMapper.getGroupStatus()) {
						delivering++; 
					} else if (ModelConstant.RGROUP_STAUS_DELIVERED == queryRgroupMapper.getGroupStatus()) {
						delivered++; 
					} else {
						logger.info("unknonw groupStatus : " + queryRgroupMapper.getGroupStatus());
					}
				}
			}
			RgroupSummaryResp rgroupSummaryResp = new RgroupSummaryResp();
			rgroupSummaryResp.setGrouping(grouping);
			rgroupSummaryResp.setGrouped(grouped);
			rgroupSummaryResp.setDelivered(delivered);
			rgroupSummaryResp.setDelivering(delivering);
			queryRgoupsResp.setGroupSummary(rgroupSummaryResp);
			
			commonResponse.setData(queryRgoupsResp);
			commonResponse.setResult("00");
		
		} catch (Exception e) {

			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}

		return commonResponse;
	}
	
	/**
	 * 团购信息明细查询
	 */
	@Override
	public CommonResponse<Object> getRroupDetail(QueryRgroupsVO queryRgroupsVO) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
				
			List<Order> sortList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "createDate");
	    	sortList.add(order);
	    	Sort sort = Sort.by(sortList);
			
			Pageable pageable = PageRequest.of(0, 10, sort);
			
			List<Integer> statusList = null;
			String productType = "1002";
			Page<Object[]> page = rgroupRuleRepository.findByMultiCondRgroup(productType, queryRgroupsVO.getRuleId(), "", 
					statusList, "", "", "", Boolean.FALSE.toString(), 
					queryRgroupsVO.getUserid(), null, new Date(), pageable);
			

			List<QueryRgroupMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryRgroupMapper.class);
			if (list == null) {
				list = new ArrayList<>();
			}
			QueryRgroupMapper rgroupDetail = list.get(0);
			BigInteger delivered = BigInteger.ZERO;
			if (rgroupDetail != null) {
				List<ServiceOrder> orderList = serviceOrderRepository.findByRGroupAndGroupStatusAndLeaderId(queryRgroupsVO.getRuleId(), queryRgroupsVO.getUserid(), null);
				if (orderList!=null) {
					for (ServiceOrder serviceOrder : orderList) {
						if (ModelConstant.ORDER_STATUS_SENDED == serviceOrder.getStatus() || 
								ModelConstant.ORDER_STATUS_RECEIVED == serviceOrder.getStatus()) {
							delivered = delivered.add(BigInteger.ONE);
						}
					}
				}
				rgroupDetail.setDelivered(delivered);
			}
			commonResponse.setData(rgroupDetail);
			commonResponse.setResult("00");
		
		} catch (Exception e) {

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
	public void saveCopyRgroup(SaveCopyRgroupVo saveCopyRgroupVo) throws Exception {
	
		Product srcProduct = new Product();
		srcProduct = productRepository.findById(Long.parseLong(saveCopyRgroupVo.getSrcProductId()));
		if (srcProduct == null) {
			throw new BizValidateException("未查询到被复制商品，id : " + saveCopyRgroupVo.getSrcProductId());
		}
		
		Product product = new Product();
		BeanUtils.copyProperties(srcProduct, product, "id", "createDate", "updateDate", "updateUser");
		product.setName(saveCopyRgroupVo.getDestName());
		product.setStatus(ModelConstant.PRODUCT_OFF);
		Date startDate = DateUtil.parse(saveCopyRgroupVo.getStartDate(), DateUtil.dttmSimple);
		product.setStartDate(startDate);
		Date endDate = DateUtil.parse(saveCopyRgroupVo.getEndDate(), DateUtil.dttmSimple);
		product.setEndDate(endDate);
		product.setShortName(saveCopyRgroupVo.getDestName());
		product.setTitleName(saveCopyRgroupVo.getDestName());
		product = productRepository.save(product);
		
		/*保存团购规则*/
		List<RgroupRule> ruleList = rgroupRuleRepository.findAllByProductId(srcProduct.getId());
		if (ruleList == null || ruleList.isEmpty()) {
			throw new BizValidateException("未查询到被复制商品上架规则，product id : " + srcProduct.getId());
		}
		RgroupRule srcRule = ruleList.get(0);
		RgroupRule rgroupRule = new RgroupRule();
		BeanUtils.copyProperties(srcRule, rgroupRule, "id", "groupStatus", "currentNum", "groupFinishDate");
		
		rgroupRule.setProductName(product.getName());
		rgroupRule.setCreateDate(product.getCreateDate());
		rgroupRule.setProductId(product.getId());
		rgroupRule.setName(product.getName());
		rgroupRule.setStartDate(product.getStartDate());
		rgroupRule.setEndDate(product.getEndDate());
		rgroupRule.setCurrentNum(0);	//当前团人数置为0
		if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
			rgroupRule.setStatus(ModelConstant.RULE_STATUS_ON);
		}else {
			rgroupRule.setStatus(ModelConstant.RULE_STATUS_OFF);
		}
		rgroupRule = rgroupRuleRepository.save(rgroupRule);
		
		/*保存团购上架区域*/
		List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByRuleId(srcRule.getId());
		if (areaList == null || areaList.isEmpty()) {
			throw new BizValidateException("未查询到被复制商品上架区域，product id : " + srcProduct.getId());
		}
		
		for (RgroupAreaItem srcAreaItem : areaList) {
			
			RgroupAreaItem rgroupAreaItem = new RgroupAreaItem();
			BeanUtils.copyProperties(srcAreaItem, rgroupAreaItem, "id");
			rgroupAreaItem.setRuleId(rgroupRule.getId());
			long ruleCloseTime = rgroupRule.getEndDate().getTime();
			rgroupAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
			rgroupAreaItem.setRuleName(rgroupRule.getName());
			rgroupAreaItem.setProductId(product.getId());
			rgroupAreaItem.setProductName(product.getName());
			if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
				rgroupAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_ON);
			}else {
				rgroupAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_OFF);
			}
			rgroupAreaItemRepository.save(rgroupAreaItem);
		}
		
		ProductPlat productPlat = new ProductPlat();
		productPlat.setAppId("");
		productPlat.setProductId(product.getId());
		productPlatRepository.save(productPlat);
		
		ProductRule productRule = new ProductRule(product, rgroupRule);
		String key = ModelConstant.KEY_PRO_RULE_INFO + productRule.getId();
		redisRepository.setProdcutRule(key, productRule);
		
		redisTemplate.opsForValue().set(ModelConstant.KEY_PRO_STOCK + product.getId(), String.valueOf(product.getTotalCount()));
		
		//只有第一次新增时才将冻结商品数量置0
		String freezeCount = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_FREEZE + product.getId());
		if (StringUtils.isEmpty(freezeCount)) {
			redisTemplate.opsForValue().set(ModelConstant.KEY_PRO_FREEZE + product.getId(), "0");	//初始化冻结数量
		}
		
	}
	
}
