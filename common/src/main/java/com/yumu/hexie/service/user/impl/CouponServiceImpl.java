package com.yumu.hexie.service.user.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.RedisUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRule;
import com.yumu.hexie.model.localservice.HomeCart;
import com.yumu.hexie.model.localservice.ServiceType;
import com.yumu.hexie.model.localservice.basemodel.BaseO2OService;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.Collocation;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.saleplan.OnSaleRule;
import com.yumu.hexie.model.market.saleplan.OnSaleRuleRepository;
import com.yumu.hexie.model.promotion.PromotionConstant;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.promotion.coupon.CouponCfg;
import com.yumu.hexie.model.promotion.coupon.CouponCombination;
import com.yumu.hexie.model.promotion.coupon.CouponCombinationRepository;
import com.yumu.hexie.model.promotion.coupon.CouponHis;
import com.yumu.hexie.model.promotion.coupon.CouponHisRepository;
import com.yumu.hexie.model.promotion.coupon.CouponRepository;
import com.yumu.hexie.model.promotion.coupon.CouponRule;
import com.yumu.hexie.model.promotion.coupon.CouponRuleRepository;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.promotion.coupon.CouponSeedRepository;
import com.yumu.hexie.model.promotion.coupon.CouponView;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.o2o.HomeItemService;
import com.yumu.hexie.service.sales.CollocationService;
import com.yumu.hexie.service.sales.impl.BaseOrderServiceImpl;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.dto.CheckCouponDTO;
import com.yumu.hexie.vo.CouponsSummary;

/**
 * 
 * @author ouyezi
 *
 */
@Transactional
@Service("couponService")
public class CouponServiceImpl implements CouponService {

	protected static final Logger log = LoggerFactory.getLogger(BaseOrderServiceImpl.class);

    @Inject
    private CouponRepository couponRepository;
	@Inject
	private CollocationService collocationService;
	@Inject
	private SystemConfigService systemConfigService;
	@Inject
	private CouponRuleRepository couponRuleRepository;
	@Inject
	private CouponSeedRepository couponSeedRepository;
	@Inject
	private ProductRepository productRepository;
	@Inject 
	private UserRepository userRepository;
	@Inject 
	private CouponCombinationRepository couponCombinationRepository;
    @Inject
    private OnSaleRuleRepository onSaleRuleRepository;
    @Inject
    private HomeItemService homeItemService;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private AgentRepository agentRepository;
	@Autowired
	private CouponHisRepository couponHisRepository;
	
	@Override
	public Coupon findById(Long couponId) {
		Coupon coupon = null;
		Optional<Coupon> optional = couponRepository.findById(couponId);
		if (optional.isPresent()) {
			coupon = optional.get();
		}
		return coupon;
	}

	@Override
	public CouponSeed findSeedByStr(String seedStr){
		return couponSeedRepository.findBySeedStr(seedStr);
	}
	
	@Async
	public void updateSeedForRuleUpdate(long seedId){
		CouponSeed oriSeed = couponSeedRepository.findById(seedId).get();
		oriSeed.getSeedStr();
		oriSeed.updateTotal(couponRuleRepository.findBySeedId(seedId));
		couponSeedRepository.save(oriSeed);
	}

	public void updateSeedAndRuleForCouponReceive(CouponRule rule){
		rule.addReceived();
		saveRule(rule);
	}
	
	public void updateSeedAndRuleForCouponUse(Coupon coupon){
		CouponRule rule = couponRuleRepository.findById(coupon.getRuleId()).get();
		rule.addUsed();
		saveRule(rule);
	}

	@Override
	public CouponSeed createOrderSeed(long userId,ServiceOrder order) {
		log.error("CREATE SEED:" + userId + " -- " +order.getId());
		
		if(StringUtils.isEmpty(order.getOrderNo())){	//拆单的交易，只产生一个种子。拆单的交易也只有一个serviceOrder有orderNo
            return null;
        }
		
		//查询类型为订单分裂模板类型的优惠卷种子
//		List<CouponSeed> templates = couponSeedRepository.findBySeedType(ModelConstant.COUPON_SEED_ORDER_BUY_TEMPLATE);
		List<Integer> seedTypes = new ArrayList<>();
		seedTypes.add(ModelConstant.COUPON_SEED_ORDER_BUY_TEMPLATE);
		seedTypes.add(ModelConstant.COUPON_SEED_ORDER_BUY2_TEMPLATE);
		
		List<CouponSeed> templates = couponSeedRepository.findBySeedType(seedTypes);
		for(CouponSeed template : templates) {
			log.error("CREATE SEED:templateId:" + template.getId());
			if(template == null||!template.isCanUse()||!canUse(template,order)) {
			} else {
				//根据种子id查询优惠卷规则表
				List<CouponRule> rules = couponRuleRepository.findBySeedId(template.getId());
				if(rules.isEmpty()) {
					continue;
				}

				log.error("CREATE SEED: rules:" + rules.size());
				User user = userRepository.findById(userId);
				CouponSeed cs = new CouponSeed();
				cs.update(template);
				cs.setUserId(userId);
				cs.setUserImgUrl(user.getHeadimgurl());
				cs.setBizId(order.getId());
				int seedType = ModelConstant.COUPON_SEED_ORDER_BUY;
				if (ModelConstant.COUPON_SEED_ORDER_BUY2_TEMPLATE == template.getSeedType()) {
					seedType = ModelConstant.COUPON_SEED_ORDER_BUY2;
				}
				cs.setSeedType(seedType);
				cs.setSeedStr(cs.getGeneratedCouponSeedStr());
				cs = couponSeedRepository.save(cs);
				for(CouponRule rule: rules) {
					CouponRule r = rule.copy(cs.getId());
					couponRuleRepository.save(r);
				}
				updateSeedForRuleUpdate(cs.getId());
				return cs;
			}
		}
		return null;
	}

	//只为某商户订单生产现金券
	private boolean canUse(CouponSeed template, ServiceOrder order) {
		return template.getMerchantId() == null || template.getMerchantId() == 0
				|| template.getMerchantId() == order.getMerchantId();
	}

	//修改规则
	@Override
	public CouponRule saveRule(CouponRule rule) {
		rule = couponRuleRepository.save(rule);
		updateSeedForRuleUpdate(rule.getSeedId());
		return rule;
	}

    @Override
	public Coupon findCouponBySeedAndUser(long seedId,long userId) {
        List<Coupon> coupons = couponRepository.findByUserIdAndSeedId(userId, seedId);
        return (coupons!=null &&coupons.size()>0) ?  coupons.get(0) : null;
	}
    
	@Override
	public Coupon addCouponFromSeed(CouponSeed seed,User user){
		if(seed == null || !seed.isCanUse()){
			return null;
		}
		Coupon coupon  = findCouponBySeedAndUser(seed.getId(), user.getId());
		
		List<CouponRule> rules = couponRuleRepository.findBySeedIdAndStatusDuration(
				seed.getId(), ModelConstant.COUPON_RULE_STATUS_AVAILABLE,new Date(), new Date());
		CouponRule chosedRule = null;
		int couponCount = 0;
		for(CouponRule rule : rules) {
			couponCount+=(rule.getTotalCount()-rule.getReceivedCount());
		}
		if(couponCount <= 0) {
			return null;//现金券已领完
		}
		if(seed.getRate() < Math.random()){
			//没有抽中现金券
			coupon = Coupon.emptyCoupon(seed, user);
			coupon = couponRepository.save(coupon);
			return coupon;
		}
		int random = (int)(couponCount*Math.random());
		couponCount = 0;//从0开始算
		for(CouponRule rule : rules) {
			couponCount+=(rule.getTotalCount()-rule.getReceivedCount());
			if(couponCount>=random) {
				chosedRule = rule; 
				break;
			}
		}
		if(chosedRule == null) {
			return null;
		}
		
		coupon = new Coupon(seed, chosedRule,user);
		coupon = couponRepository.save(coupon);
		try {
            log.error("红包发放："+JacksonJsonUtil.beanToJson(coupon));
        } catch (Exception e) {
        }

		//更新统计数据
		updateSeedAndRuleForCouponReceive(chosedRule);

		List<Integer> status = new ArrayList<Integer>();
		status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
		status.add(ModelConstant.COUPON_STATUS_LOCKED);
		//status.add(ModelConstant.COUPON_STATUS_USED);
		//status.add(ModelConstant.COUPON_STATUS_TIMEOUT);
		int validNum = couponRepository.countByUserIdAndStatusIn(user.getId(),status);
		user.setCouponCount(validNum);
		userRepository.save(user);
		return coupon;
	}
	@Override
	public Coupon addCoupon4Regist(User user) {
		if(couponRepository.countByUserAndSeedType(user.getId(), ModelConstant.COUPON_SEED_USER_REGIST) > 0){
			return null;
		}
		List<CouponSeed> cs = couponSeedRepository.findBySeedType(ModelConstant.COUPON_SEED_USER_REGIST);
		for(CouponSeed c:cs){
			Coupon coupon = addCouponFromSeed(c, user);
			if(coupon != null) {
	            log.warn("添加注册红包 User["+user.getId()+"]Coupon["+coupon.getId()+"]");
				return coupon;
			}
		}
		return null;
	}
	/**
	 * 会员
	 */
	@Override
	public Coupon addCoupon4Member(User user) {
		List<CouponSeed> cs = couponSeedRepository.findBySeedType(ModelConstant.COUPON_SEED_MEMBER);
		for(CouponSeed c:cs){
			Coupon coupon = addCouponFromSeed(c, user);
			if(coupon != null) {
	            log.warn("添加会员红包 User["+user.getId()+"]Coupon["+coupon.getId()+"]");
			}
		}
		return null;
	}

	@Override
	public Coupon addCoupon4Subscribe(User user) {
		
		if(couponRepository.countByUserAndSeedType(user.getId(), ModelConstant.COUPON_SEED_USER_SUBSCRIB) > 0){
			return null;
		}
		List<CouponSeed> cs = couponSeedRepository.findBySeedType(ModelConstant.COUPON_SEED_USER_SUBSCRIB);
		
		log.error("cs size is : " + cs.size());
		
		Coupon coupon = null;
		for(CouponSeed c:cs){
			
			coupon = addCouponFromSeed(c, user);

            log.warn("添加关注红包 User["+user.getId()+"]Coupon["+coupon.getId()+"]");
		}
		//此处返回最后一个红包，调用处需要根据返回值 空/非空 做业务处理
		return coupon;
	}

	@Override
	public Coupon addCouponFromSeed(String seedStr,User user) {
		CouponSeed cs = couponSeedRepository.findBySeedStr(seedStr);
		return addCouponFromSeed(cs, user);
	}
	//不管是不是有效现金券
	@Override
	public List<Coupon> findCouponsFromOrder(long orderId) {
		CouponSeed cs = couponSeedRepository.findBySeedTypeAndBizId(ModelConstant.COUPON_SEED_ORDER_BUY, orderId);
		if(cs != null) {
			return couponRepository.findBySeedIdOrderByIdDesc(cs.getId(), PageRequest.of(0,20));
		} else {
			return new ArrayList<Coupon>();
		}
	}
	

	//不管是不是有效现金券
	public List<Coupon> findCouponsBySeedStr(String seedStr){
		CouponSeed cs = couponSeedRepository.findBySeedStr(seedStr);
		if(cs != null) {
			return couponRepository.findBySeedIdOrderByIdDesc(cs.getId(), PageRequest.of(0,20));
		} else {
			return new ArrayList<Coupon>();
		}
	}
	@Override
	public List<Coupon> findAvaibleCoupon(long userId,Cart cart) {
		List<Integer> status = new ArrayList<Integer>();
		status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
		List<Coupon> coupons = couponRepository.findByUserIdAndStatusIn(userId,status, PageRequest.of(0,200));
		
		List<Coupon> result = new ArrayList<Coupon>();
		for(Coupon coupon : coupons) {
			if(isAvaible(cart, coupon)){
				result.add(coupon);
			}
		}
		return result;
	}

    @Override
	public List<Coupon> findAvaibleCoupon(long userId,HomeCart cart) {
        if(cart == null) {
            return new ArrayList<Coupon>();
        }
        List<Integer> status = new ArrayList<Integer>();
        status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
        List<Coupon> coupons = couponRepository.findByUserIdAndStatusIn(userId,status, PageRequest.of(0,200));
        
        List<Coupon> result = new ArrayList<Coupon>();
        for(Coupon coupon : coupons) {
            if(isAvaible(cart, coupon)){
                result.add(coupon);
            }
        }
        return result;
	}
    @Override//FIXME 红包使用范围需要限定更小值
    public List<Coupon> findAvaibleCoupon4ServiceType(long userId,long homeServiceType,Long parentType, Long itemId){
        List<Integer> status = new ArrayList<Integer>();
        status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
        List<Coupon> coupons = couponRepository.findByUserIdAndStatusIn(userId,status, PageRequest.of(0,200));
        
        List<Coupon> result = new ArrayList<Coupon>();
        for(Coupon coupon : coupons) {
            if(isAvaible(PromotionConstant.COUPON_ITEM_TYPE_SERVICE, homeServiceType,
                parentType, itemId, null, coupon, false)){
                result.add(coupon);
            }
        }
        return result;
    }
    
    /**
     * 获取可用的红包
     */
	@Override
	public List<Coupon> findAvaibleCoupon(long userId, List<OrderItem> itemList, int salePlanType){
		
		List<Coupon> result = new ArrayList<Coupon>();
		List<Coupon> couponList = new ArrayList<>();
		if(itemList == null || itemList.isEmpty()) {
			return result;
		}
		int itemType = 0;
		if (ModelConstant.ORDER_TYPE_EVOUCHER == salePlanType) {
			itemType = PromotionConstant.COUPON_ITEM_TYPE_EVOUCHER;
		}else if (ModelConstant.ORDER_TYPE_ONSALE == salePlanType) {
			itemType = PromotionConstant.COUPON_ITEM_TYPE_MARKET;
		}else if (ModelConstant.ORDER_TYPE_RGROUP == salePlanType) {
			itemType = PromotionConstant.COUPON_ITEM_TYPE_MARKET;
		}
		
		List<Integer> status = new ArrayList<Integer>();
		status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
		List<Coupon> coupons = couponRepository.findByUserIdAndStatusIn(userId,status, PageRequest.of(0,200));
		
		long currTime = System.currentTimeMillis();
		for(Coupon coupon : coupons) {
			
			CheckCouponDTO check = checkAvailable4Sales(itemType, itemList, coupon, false);
			if (!check.isValid()) {
				continue;
			}
			if (coupon.getExpiredDate().getTime() > currTime ) {
				result.add(coupon);
			}
		}
		if (!result.isEmpty()) {
			Set<Coupon> couponSet = new TreeSet<>((c1, c2)-> c1.getId()==c2.getId()?0:1);
			couponSet.addAll(result);	//根据ID去重
			
			couponList.addAll(couponSet);
			Comparator<Coupon> comparator = (c1, c2)-> (int)(c2.getAmount() - c1.getAmount());
			Collections.sort(couponList, comparator);	//根据金额排序
		}
		return couponList;
	}
	
	@Override
    public List<Coupon> findAvaibleCoupon4CustomService(long userId, long serviceId, String agentNo){
        
		List<Integer> status = new ArrayList<Integer>();
        status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
        List<Coupon> coupons = couponRepository.findByUserIdAndStatusIn(userId, status, PageRequest.of(0,200));
        
        Agent agent = agentRepository.findByAgentNo(agentNo);
        Product product = new Product();
        product.setId(serviceId);
        product.setService(true);
        product.setAgentId(agent.getId());
        
        List<Coupon> result = new ArrayList<Coupon>();
        for(Coupon coupon : coupons) {
        	CheckCouponDTO dto = checkAvailable4Service(PromotionConstant.COUPON_ITEM_TYPE_SERVICE, product, null, coupon, false);
        	if (dto.isValid()) {
        		result.add(coupon);
			}
        }
        return result;
    }
	
	//服务-洗衣-服务项父类型-服务项 商户ID
	public boolean isAvaible(int itemType, Long subItemType, Long serviceType, Long productId, 
	                          Float amount, Coupon coupon, boolean locked) {
	    if(coupon == null) {
	        return false;
	    }
	    log.warn("Check Coupon["+itemType+"]["+subItemType+"]["+serviceType+"]["+productId
	        +"]["+amount+"]["+coupon.getId()+"]["+locked+"]");
	    //状态验证
        if(!locked && coupon.getStatus() != ModelConstant.COUPON_STATUS_AVAILABLE){
            log.warn("不可用（状态验证）");
            return false;
        }
        if(locked && coupon.getStatus() != ModelConstant.COUPON_STATUS_LOCKED 
                && coupon.getStatus() != ModelConstant.COUPON_STATUS_AVAILABLE){
            log.warn("不可用（锁定状态）");
            return false;
        }
        if(amount != null && coupon.getUsageCondition()-0.009 > amount) {
            log.warn("不可用（金额不支持）");
            return false;
        }

        if(coupon.isAvailableForAll()){
            log.warn("可以用（面向全部可用）");
            return true;
        }
        
	    //系统参数 FIXME
	    if(itemType != PromotionConstant.COUPON_ITEM_TYPE_ALL
                && productId != null && productId != 0) {
            if(systemConfigService.getUnCouponItems().contains(itemType+"-"+productId)){
                log.warn("不可以用（系统配置）");
                return false;
            }
        }

        /*可用商户校验*/
		if(coupon.getMerchantId() != null && coupon.getMerchantId() != 0 ){
		    Long merchantId = getMerchatId(new Long(itemType), serviceType, productId);
		    log.error("merchantId:" + merchantId);
		    if(merchantId == null || merchantId != coupon.getMerchantId()) {
                log.warn("不可用（商户正向验证）");
                return false;
		    }
		}
		
		/*不可用商户校验*/
		if(coupon.getuMerchantId() != null && coupon.getuMerchantId() != 0 ){
		    Long merchantId = getMerchatId(new Long(itemType), serviceType, productId);
		    log.error("merchantId:" + merchantId);
		    if(merchantId == coupon.getuMerchantId()) {
                log.warn("不可用（商户逆向验证）");
                return false;
		    }
		}
		
        log.warn("可以用（全部通过）");
		return true;
	}
	
	
	
	/**
	 * 根据卡库指定策略 TODO
	 * @param payType
	 * @return
	 */
	@Override
	public boolean isAvaible(String appId, String payType) {
		//TODO
		return true;
	}

	//itemType:1, serviceType:-0, productId:10
	public Long getMerchatId(Long mainType, Long subType, Long itemId) {
		
		log.error("mainType:"+mainType+",subType:"+subType+",itemId:"+itemId);
        if(new Long(PromotionConstant.COUPON_ITEM_TYPE_MARKET).equals(mainType) && itemId != null && itemId != 0){
            Product product = productRepository.findById(itemId).get();
            return product == null ? 0 : product.getMerchantId();
        }
        if(new Long(PromotionConstant.COUPON_ITEM_TYPE_SERVICE).equals(mainType) && subType != null && subType!=0) {
            ServiceType type = homeItemService.queryTypeById(subType);
            return type == null ? 0 : type.getMerchantId();
        }
        return 0l;
	}

	@Override//FIXME 如果特卖类型是0，如何处理
	public boolean isAvaible(Cart cart, Coupon coupon) {
	    Float amount = null;
        if(cart.getCollocationId()>0) {
            Collocation coll = collocationService.findOne(cart.getCollocationId());
	        amount = coll.getSatisfyAmount() <= cart.getTotalAmount() ? cart.getTotalAmount() - coll.getDiscountAmount() : cart.getTotalAmount();
        }
	    for(long productId : cart.getProductIds()) {
	        if(isAvaible(PromotionConstant.COUPON_ITEM_TYPE_MARKET,new Long(ModelConstant.ORDER_TYPE_ONSALE), 0l, 
	            productId, 
	            amount, coupon,false)){
	            return true;
	        }
	    }
		return false;
	}

    @Override
    public boolean isAvaible(HomeCart cart, Coupon coupon) {
        return isAvaible(PromotionConstant.COUPON_ITEM_TYPE_SERVICE, cart.getBaseType(),
            cart.getItemType(),  cart.getItems().get(0).getServiceId(), cart.getAmount().floatValue(), coupon, false);
    }
	@Override
    public boolean isAvaible(String feePrice, Coupon coupon) {
	    if(coupon.getItemType() != PromotionConstant.COUPON_ITEM_TYPE_ALL 
                && coupon.getItemType() != PromotionConstant.COUPON_ITEM_TYPE_WUYE){
            return false;
        }
    	if(coupon.getStatus() != ModelConstant.COUPON_STATUS_AVAILABLE){
    		return false;
    	}
    	if (new BigDecimal(feePrice).compareTo(new BigDecimal("0.01"))<0){
    		return false;
    	}
    	if(coupon.isAvailableForAll()){
    		return true;
    	}
    	return true;
    }

    @Override
    public boolean isAvaible(ServiceOrder order, Coupon coupon, boolean withLocked) {
    	if(withLocked) {
    		if(coupon.getOrderId() != 0&& coupon.getOrderId() != order.getId()) {
    			return false;
    		}
    	}
    	if(order.getItems() != null) {
    	    for(OrderItem item : order.getItems()) {
    	        Integer onsaleType = 0;
    	        if(item.getOrderType() == ModelConstant.ORDER_TYPE_ONSALE) {
    	            OnSaleRule plan = onSaleRuleRepository.findById(item.getRuleId()).get();
    	            onsaleType = plan.getProductType();
    	        }
                if(isAvaible(PromotionConstant.COUPON_ITEM_TYPE_MARKET, new Long(item.getOrderType()),new Long(onsaleType),item.getProductId(), 
                     order.getTotalAmount(), coupon,withLocked)){
                    return true;
                }
            }
    	}
        
    	return false;
    }

    @Override
	public List<Coupon> findAvaibleCoupon(ServiceOrder order) {
		List<Integer> status = new ArrayList<Integer>();
		status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
		List<Coupon> coupons = couponRepository.findByUserIdAndStatusIn(order.getUserId(),status, PageRequest.of(0,200));
		
		List<Coupon> result = new ArrayList<Coupon>();
		for(Coupon coupon : coupons) {
			if(isAvaible(order, coupon,false)){
				result.add(coupon);
			}
		}
		return result;
	}
	
	private static final int COUPON_PAGE_SIZE = 20;
	@Override
	public List<Coupon> findInvalidCoupons(long userId,int page){
		List<Integer> invalidStatus = new ArrayList<Integer>();
		invalidStatus.add(ModelConstant.COUPON_STATUS_USED);
		invalidStatus.add(ModelConstant.COUPON_STATUS_TIMEOUT);
		return couponRepository.findByUserIdAndStatusIn(userId, invalidStatus, PageRequest.of(page, COUPON_PAGE_SIZE));
 	}
	
	@Override
    public CouponsSummary findCouponSummary(long userId){
        CouponsSummary r = new CouponsSummary();
        List<Integer> validStatus = new ArrayList<Integer>();
        validStatus.add(ModelConstant.COUPON_STATUS_AVAILABLE);
        validStatus.add(ModelConstant.COUPON_STATUS_LOCKED);
        int validNum = couponRepository.countByUserIdAndStatusIn(userId,validStatus);
        
        List<Integer> invalidStatus = new ArrayList<Integer>();
        invalidStatus.add(ModelConstant.COUPON_STATUS_USED);
        invalidStatus.add(ModelConstant.COUPON_STATUS_TIMEOUT);
        int invalidNum = couponRepository.countByUserIdAndStatusIn(userId,invalidStatus);
        
        r.setInvalidCount(invalidNum);
        r.setValidCount(validNum);
        r.setValidCoupons(couponRepository.findByUserIdAndStatusIn(userId, validStatus, PageRequest.of(0, 40)));
        r.setInvalidCoupons(couponRepository.findByUserIdAndStatusIn(userId, invalidStatus, PageRequest.of(0, 2)));;
        
        return r;
    }
	
	@Override
	public void lock(ServiceOrder order, Coupon coupon){

        log.warn("lock红包["+order.getId()+"]Coupon["+coupon.getId()+"]");
		if(!checkAvailable4Service(order, coupon, false)){
			throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_COUPON,coupon.getId(),"该现金券不可用于本订单");
		}
		coupon.lock(order.getId());
		couponRepository.save(coupon);
	}
    @Override
	public boolean lock(BaseO2OService bill, Coupon coupon){
        log.warn("lock红包["+bill.getId()+"]Coupon["+bill.getId()+"]");
        if(!isAvaible(PromotionConstant.COUPON_ITEM_TYPE_SERVICE,
            new Long(bill.getOrderType()), bill.getItemType(), bill.getItemId(),bill.getAmount().floatValue(), coupon, true)){
            return false;
        }
        coupon.lock(bill.getId());
        couponRepository.save(coupon);
        return true;
	}
	@Override
    public void comsume(ServiceOrder order) {
        log.warn("comsume红包["+order.getId()+"], orderType : " + order.getOrderType());
        if(order.getCouponId() == null || order.getCouponId() == 0){
            return;
        }
        Coupon coupon = couponRepository.findById(order.getCouponId()).get();
        boolean canConsume = false;
        if (ModelConstant.ORDER_TYPE_SERVICE == order.getOrderType()) {
        	canConsume = checkAvailable4Service(order, coupon, false);	//服务类交易不锁券，所以这里传false
		}else {
			canConsume = checkAvailable4Sales(order, coupon, true);
		}
        if (!canConsume) {
			return;
		}
        log.warn("comsume红包before["+order.getId()+"]Coupon["+coupon.getId()+"]");
        coupon.cousume(order.getId());
        couponRepository.save(coupon);
        
        User user = userRepository.findById(coupon.getUserId());
        if(user.getCouponCount()>0) {
            user.setCouponCount(user.getCouponCount()-1);
            userRepository.save(user);
        }
        updateSeedAndRuleForCouponUse(coupon);
        
        log.warn("comsume红包END["+order.getId()+"]Coupon["+coupon.getId()+"]");
    }
	
	@Override
	public boolean comsume(BaseO2OService bill) {
	    if(bill.getCouponId() == null || bill.getCouponId() == 0){
            return true;
        }
        log.warn("comsume红包Bill[BEG]["+bill.getId()+"]Coupon["+bill.getId()+"]");
	    Coupon coupon = couponRepository.findById(bill.getCouponId()).get();
		if(!isAvaible(PromotionConstant.COUPON_ITEM_TYPE_SERVICE,
            new Long(bill.getOrderType()), bill.getItemType(), bill.getItemId(), bill.getAmount().floatValue(), coupon, true)){
			//throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_COUPON,coupon.getId(),"该现金券不可用于本订单");
		    return false;
		}
        log.warn("comsume红包Bill[END]["+bill.getId()+"]Coupon["+bill.getId()+"]");
		coupon.cousume(bill.getId());
		couponRepository.save(coupon);
		
		User user = userRepository.findById(coupon.getUserId());
		if(user.getCouponCount()>0) {
			user.setCouponCount(user.getCouponCount()-1);
			userRepository.save(user);
		}
		
		updateSeedAndRuleForCouponUse(coupon);
		return true;
	}

	@Override
	public void unlock(Long couponId){
        log.warn("unlock红包Coupon["+couponId+"]");
	    if(couponId ==null || couponId == 0) {
	        return;
	    }
	    Coupon coupon = couponRepository.findById(couponId).get();
	    if(coupon == null) {
	        return;
	    }
		if(coupon.getStatus() == ModelConstant.COUPON_STATUS_TIMEOUT
				|| coupon.getStatus() == ModelConstant.COUPON_STATUS_USED) {
			throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_COUPON,coupon.getId(),"该现金券已使用，无法解锁");
		}
		coupon.unlock();
		couponRepository.save(coupon);

        log.warn("unlock红包Bill[BEG]Coupon["+coupon.getId()+"]");
	}
	
	@Override
	public Coupon findOne(long id) {
	    return couponRepository.findById(id).get();
	}
	@Override
	public void timeout(Coupon coupon){
		if(coupon.getStatus() == ModelConstant.COUPON_STATUS_LOCKED
				|| coupon.getStatus() == ModelConstant.COUPON_STATUS_USED) {
			throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_COUPON,coupon.getId(),"该现金券已被使用或正在被使用，无法超时失效");
		}
		log.error("timeout红包已过期["+coupon.getId()+"]["+coupon.getTitle()+"]");
		coupon.timeout();
		couponRepository.save(coupon);
		
		User user = userRepository.findById(coupon.getUserId());
		if(user!=null && user.getCouponCount()>0) {
			user.setCouponCount(user.getCouponCount()-1);
			userRepository.save(user);
		}
	}
	
	public List<Coupon> findTop100TimeoutCoupon(){
		return couponRepository.findTimeoutByPage(new Date(), PageRequest.of(0, 100));
	}

	@Override
	public List<Coupon> findAvaibleCouponForWuye(User user, String payType) {
		
		List<Coupon> result = new ArrayList<Coupon>();
        List<Integer> status = new ArrayList<Integer>();
        status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
        List<Coupon> coupons = couponRepository.findByUserIdAndStatusIn(user.getId(), status, PageRequest.of(0,200));
        for(Coupon coupon : coupons) {
            if(isAvaible(PromotionConstant.COUPON_ITEM_TYPE_WUYE, 0l, 0l, 
                0l, null, coupon,false)){
            	
            	if (isAvaible(user.getAppId(), payType)) {
            		result.add(coupon);
            		
            	}
            }
        }
        return result;
    
	}

	@Override
	public List<CouponCombination> findCouponCombination(int combinationType) {
		
		return couponCombinationRepository.findByCombinationType(combinationType);
	}

	@Override
	public void comsume(String feePrice, long couponId) {

		Coupon coupon = couponRepository.findById(couponId).get();

        log.info("comsume红包Bill[BEG]feePrice["+feePrice+"]["+couponId+"]");
		if(!isAvaible(feePrice, coupon)){
			throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_COUPON,coupon.getId(),"该现金券不可用于本订单");
		}
		log.info("consume coupon:" + coupon.getId());
		coupon.cousume(0);
		couponRepository.save(coupon);

        log.info("comsume红包Bill[END]feePrice["+feePrice+"]["+couponId+"]");
		User user = userRepository.findById(coupon.getUserId());
		if(user.getCouponCount()>0) {
			userRepository.updateUserCoupon(user.getCouponCount()-1, user.getId(), user.getCouponCount());
		}
		
		updateSeedAndRuleForCouponUse(coupon);
		
	}

	@Override
	public List<Coupon> findTimeoutCouponByDate(Date fromDate, Date toDate) {

		return couponRepository.findTimeoutCouponByDate(fromDate, toDate, PageRequest.of(0, 10000));
	
	}
	
	/**
	 * 锁定物业优惠券
	 * @param orderId传tradeWaterId
	 * @param coupon
	 */
	@Override
	public void lockWuyeCoupon(String orderId, Coupon coupon) {
		
		log.warn("lock红包["+orderId+"]Coupon["+coupon.getId()+"]");
		log.info("coupon : " + coupon);
		if(!isAvaible(String.valueOf(coupon.getAmount()), coupon)){
			throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_COUPON, coupon.getId(),"该现金券不可用于本订单");
		}
		couponRepository.lockWuyeCoupon(Long.valueOf(orderId), ModelConstant.COUPON_STATUS_LOCKED, coupon.getId(), ModelConstant.COUPON_STATUS_AVAILABLE);
		
	}

	@Override
	public void updateWuyeCouponOrderId(long orderId, long couponId) {
		couponRepository.updateWuyeCouponOrderId(orderId, couponId);
	}

	/**
	 * 根据orderId消费优惠券
	 */
	@Override
	public void consume(long orderId) {
		
		Coupon coupon = couponRepository.findByOrderId(orderId);
		couponRepository.consumeWuyeCoupon(ModelConstant.COUPON_STATUS_USED, coupon.getId(), orderId, ModelConstant.COUPON_STATUS_AVAILABLE);
	}
	
	/**
	 * 根据orderId查找优惠券
	 * @param orderId
	 * @return
	 */
	@Override
	public Coupon findByOrderId(long orderId) {
		return couponRepository.findByOrderId(orderId);
	}
	
	/**
	 * 获取红包种子列表
	 * @param user
	 * @return
	 */
	@Override
	public List<CouponView> getSeedList(User user){
		
		log.info("getSeedList, user : " + user);
		String sectId = user.getSectId();
		if (StringUtils.isEmpty(sectId) || "0".equals(sectId)) {
			throw new BizValidateException("当前用户未绑定房屋");
		}
		String gainedSeedKey = ModelConstant.KEY_USER_COUPON_SEED + user.getId();
		String gainedSeedStr = redisTemplate.opsForValue().get(gainedSeedKey);
		
		String seedKeyPattern = ModelConstant.KEY_COUPON_RULE + "*";	//这里如果操作的数据量较大，需要改成 scan操作。keys操作会阻塞其他redis操作，导致redis宕机。
		List<String> ruleList = RedisUtil.scanKeys(redisTemplate, seedKeyPattern);
		List<CouponView> availableList = new ArrayList<>();
		for (String ruleKey : ruleList) {
			CouponCfg couponCfg = redisRepository.getCouponCfg(ruleKey);
			String supportSects = couponCfg.getSectIds();	//支持的小区
			if (!StringUtils.isEmpty(supportSects)) {
				if (supportSects.indexOf(user.getSectId()) > -1) {
					CouponView couponView = new CouponView(couponCfg);
					String stockKey = ModelConstant.KEY_COUPON_TOTAL + couponView.getId();
					String stock = redisTemplate.opsForValue().get(stockKey);	//这里只是粗略判断一下，可能存在多个用户同时显示可领，但真正领的时候其实只有一个红包，在领取的方法里会校验
					if (!StringUtils.isEmpty(stock) && Integer.valueOf(stock) <= 0) {
						couponView.setUsedup(true);	//已领完
					}
					if (!StringUtils.isEmpty(gainedSeedStr) && gainedSeedStr.indexOf(couponCfg.getSeedStr())>-1) {
						couponView.setGained(true);
					}
					
					Date endDate = couponView.getEndDate();
					if (endDate!=null && endDate.getTime() <= System.currentTimeMillis()) {
						continue;
					}
					availableList.add(couponView);
				}
			}
		}
		return availableList;
		
	}
	
	/**
	 * 根据种子领取红包
	 * @param seedStr
	 * @throws JsonProcessingException 
	 */
	@Override
	public Coupon gainCouponFromSeed(User user, String seedStr) throws Exception {
		
		Assert.hasText(seedStr, "种子不能为空。");
		
		log.info("当前用户:" + user.getId() + ", session中拥有红包：" + user.getCouponCount());
		String couponKey = ModelConstant.KEY_USER_COUPON_SEED + user.getId();
		String gainedSeeds = redisTemplate.opsForValue().get(couponKey);
		if (!StringUtils.isEmpty(gainedSeeds)) {
			if (gainedSeeds.indexOf(seedStr) > -1) {
				throw new BizValidateException("已经领过红包啦。");
			}
		}
		
		String seedStrKey = ModelConstant.KEY_COUPON_SEED + seedStr;
		String ruleId = redisTemplate.opsForValue().get(seedStrKey);
		if (StringUtils.isEmpty(ruleId)) {
			throw new BizValidateException("未找到当前种子对应的规则ID， 种子：" + seedStr);
		}
		String ruleKey = ModelConstant.KEY_COUPON_RULE + ruleId;
		CouponCfg couponCfg = redisRepository.getCouponCfg(ruleKey);
		
		String stockKey = ModelConstant.KEY_COUPON_TOTAL + ruleId;
		Long stock = redisTemplate.opsForValue().decrement(stockKey);	//直接减，不要用get获取一遍值，非原子性操作，有脏读
		if (stock < 0) {
			log.info("红包已领完, ruleId : " + ruleId);
			throw new BizValidateException("现金券已领完。");
		}
		CouponSeed seed = new CouponSeed();
		seed.setId(couponCfg.getSeedId());
		seed.setSeedType(couponCfg.getSeedType());
		
		CouponRule rule = new CouponRule();
		BeanUtils.copyProperties(couponCfg, rule);
		
		Coupon coupon = new Coupon(seed, rule, user);
		Optional<Agent> optional = agentRepository.findById(coupon.getAgentId());
		if (optional.isPresent()) {
			Agent agent = optional.get();
			if (agent != null) {
				coupon.setAgentName(agent.getName());
				coupon.setAgentNo(agent.getAgentNo());
			}
		}
		
		//更新数据库。放到队列，以免多人同时领取时脏读
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		String value = objectMapper.writeValueAsString(coupon);
		redisTemplate.opsForList().rightPush(ModelConstant.KEY_COUPON_GAIN_QUEUE, value);
		
		//塞红包到缓存
		user.setCouponCount(user.getCouponCount()+1);
		redisTemplate.opsForValue().append(couponKey, seedStr + ",");	//后面加逗号分割
		return coupon;
	}
	
	@Transactional
	@Override
	public Coupon updateCouponReceived(Coupon coupon) {
		
		//1.更新couponRule
		Optional<CouponRule> optional = couponRuleRepository.findById(Long.valueOf(coupon.getRuleId()));
		CouponRule couponRule  = null;
		if (optional.isPresent()) {
			couponRule = optional.get();
			couponRule.addReceived();
			couponRuleRepository.save(couponRule);
			
			//2.更新couponSeed
			Optional<CouponSeed> seedOptional = couponSeedRepository.findById(couponRule.getSeedId());
			if (seedOptional.isPresent()) {
				CouponSeed couponSeed = seedOptional.get();
				couponSeed.setReceivedCount(couponRule.getReceivedCount());
				couponSeed.getSeedStr();
				couponSeedRepository.save(couponSeed);
				
			}
		}
		//3.保存红包到数据库
		coupon = couponRepository.save(coupon);
		log.info("红包发放："+ coupon.getId() + ", title: " + coupon.getTitle());
		
		//4.更新用户红包数量
		User user = userRepository.findById(coupon.getUserId());
		log.info("当前用户:" + user.getId() + ", db中拥有红包：" + user.getCouponCount());
		
		List<Integer> status = new ArrayList<Integer>();
		status.add(ModelConstant.COUPON_STATUS_AVAILABLE);
		status.add(ModelConstant.COUPON_STATUS_LOCKED);
		int validNum = couponRepository.countByUserIdAndStatusIn(user.getId(),status);
		log.info("当前用户:" + user.getId() + ", 实际拥有红包：" + validNum);
		userRepository.updateUserCoupon(validNum, user.getId(), user.getCouponCount());
		
		//5.记录红包领取记录
		CouponHis couponHis = new CouponHis();
		couponHis.setUserId(user.getId());
		couponHis.setUserName(user.getName());
		couponHis.setSeedStr(coupon.getSeedStr());
		couponHis.setCouponId(coupon.getId());
		couponHis.setTitle(coupon.getTitle());
		couponHisRepository.save(couponHis);
		return coupon;
		
	}
	
	/**
	 * 根据订单验证红包是否可用,服务
	 */
	@Override
    public boolean checkAvailable4Service(ServiceOrder order, Coupon coupon, boolean withLocked) {
    	if(withLocked) {
    		if(coupon.getOrderId() != 0&& coupon.getOrderId() != order.getId()) {
    			log.info("coupon orderId : " + coupon.getOrderId() + ", 与订单ID["+order.getId()+"]不符");
    			return false;
    		}
    	}
    	
    	log.info("orderItems : " + order.getItems());
    	if(order.getItems() != null) {
    	    for(OrderItem item : order.getItems()) {
    	    	Product product = new Product();
    	    	product.setId(item.getProductId());
    	    	Float totalPrice = order.getTotalAmount() + order.getShipFee(); //理论上应该跟price字段是一样的。但price字段在使用红包后会修改为减免后的金额。这个函数会再最后消费红包时调用，所以price的值已经是减免了红包的金额
    	    	CheckCouponDTO check = checkAvailable4Service(coupon.getItemType(), product, totalPrice, coupon, withLocked);
    	    	if (!check.isValid()) {
					return false;
				}
    	    }
    	}
        
    	return true;
    }
	
	/**
	 * 根据订单验证红包是否可用,商品
	 */
	@Override
    public boolean checkAvailable4Sales(ServiceOrder order, Coupon coupon, boolean withLocked) {
    	if(withLocked) {
    		if(coupon.getOrderId() != 0&& coupon.getOrderId() != order.getId()) {
    			log.info("coupon orderId : " + coupon.getOrderId() + ", 与订单ID["+order.getId()+"]不符");
    			return false;
    		}
    	}
    	log.info("orderItems : " + order.getItems());
    	CheckCouponDTO check = checkAvailable4Sales(coupon.getItemType(), order.getItems(), coupon, withLocked);
    	return check.isValid();
    }
	
	/**
	 * 验证红包是否可用
	 * @param itemType	0全部，1特卖团购商品，2电子核销券,3服务，4物业缴费
	 * @param productId	需要验证的产品ID，如果是服务传serviceId
	 * @param amount	此次购买金额
	 * @param coupon	此次使用的红包
	 * @param locked	是否锁
	 * @param type	0商品 1服务
	 * @return
	 */
	@Override
	public CheckCouponDTO checkAvailable4Service(int itemType, Product product, Float amount, Coupon coupon, boolean locked) {
	    
		log.warn("Check Coupon, couponId : " + coupon.getId() + ", ["+itemType+"]["+product.getId()+"]["+amount+"]["+coupon.getId()+"]["+locked+"]");
		CheckCouponDTO dto = checkCouponAvailable(itemType, product, coupon, locked);
		boolean isAmountValid = checkCouponUsageCondition(amount, coupon);
		if (!isAmountValid) {
			dto.setErrMsg("优惠券：" + coupon.getId() + ", 商品最小使用金额：" + coupon.getUsageCondition() + ", 不可用。");
		}
        return dto;
        
	}

	/**
	 * 红包校验，除了优惠券以外
	 * @param itemType
	 * @param product
	 * @param coupon
	 * @param locked
	 * @return
	 */
	private CheckCouponDTO checkCouponAvailable(int itemType, Product product, Coupon coupon, boolean locked) {
		
		CheckCouponDTO dto = new CheckCouponDTO();
		if(coupon == null) {
			dto.setErrMsg("优惠券为空。");
	        return dto;
	    }
		
		long productId = product.getId();
	    
	    //1.状态验证
        if(!locked && coupon.getStatus() != ModelConstant.COUPON_STATUS_AVAILABLE){
            log.warn("coupon " + coupon.getId() + ", 不可用（状态验证）");
            dto.setErrMsg("优惠券：" + coupon.getId() + ", 状态：" + coupon.getStatus() + ", 不可用。");
            return dto;
        }
        //2.是否锁定验证
        if(locked && coupon.getStatus() != ModelConstant.COUPON_STATUS_LOCKED && coupon.getStatus() != ModelConstant.COUPON_STATUS_AVAILABLE){
        	log.warn("coupon " + coupon.getId() + ", 不可用（锁定状态）");
        	dto.setErrMsg("优惠券：" + coupon.getId() + ", 已被其他商品锁定，不可用。");
        	return dto;
        }
        
	    //4.支持产品类型验证
        if (PromotionConstant.COUPON_ITEM_TYPE_ALL != coupon.getItemType() && itemType != coupon.getItemType()) {
        	dto.setErrMsg("优惠券：" + coupon.getId() + ", 当前商品不可用。");
        	return dto;
        }
        
        //5.支持(或不支持)的商品验证
	    if(PromotionConstant.COUPON_ITEM_TYPE_ALL != coupon.getItemType()&& productId > 0) {
	    	if (coupon.getSupportType() == 1) {	//0全部支持，1部分支持，2部分不支持
    			if (!StringUtils.isEmpty(coupon.getProductId()) && coupon.getProductId().indexOf(String.valueOf(productId))>-1) {
    				//do nothing
    			} else {
					log.warn("coupon " + coupon.getId() + ", 商品productId : " + productId + ", 不在支持的列表中。");
					dto.setErrMsg("优惠券：" + coupon.getId() + ", 当前商品不可用。");
					return dto;
    			}
			}
    		if (coupon.getSupportType() == 2) {
    			if (!StringUtils.isEmpty(coupon.getuProductId()) && coupon.getuProductId().indexOf(String.valueOf(productId)) >-1) {
					log.warn("coupon " + coupon.getId() + ", 商品productId : " + productId + ", 在不支持的列表中。");
					dto.setErrMsg("优惠券：" + coupon.getId() + ", 当前商品不可用。");
					return dto;
				}
    		}
        }
	    
	    //5.验证代理商
	    if (coupon.getAgentId() > 0) {
	    	if (coupon.getAgentId() != product.getAgentId() ) {
				log.warn("coupon " + coupon.getId() + ", 商品productId : " + productId + ", 非指定代理商, agentId : " + product.getAgentId() + ", 不能使用。");
				dto.setErrMsg("优惠券：" + coupon.getId() + ", 当前商品不可用。");
				return dto;
			}
			
	    }
	    //TODO 商户校验
        log.warn("coupon " + coupon.getId()+ " 可以用（全部通过）");
        dto.setValid(true);
		return dto;
	}

	/**
	 * 校验金额是否可用
	 * @param amount
	 * @param coupon
	 * @param dto
	 */
	private boolean checkCouponUsageCondition(Float amount, Coupon coupon) {
		//3.金额验证
        if (amount != null) {
            if(coupon.getUsageCondition() > amount) {		//coupon.getUsageCondition()-0.009 > amount 原来的逻辑
            	log.warn("coupon " + coupon.getId() + ", 不可用（金额不支持）");
            	return false;
            }
		}
        return true;
	}
	
	/**
	 * 验证红包是否可用（购物车种商品，一般是多个，也有可能单个）
	 * @param itemType	0全部，1特卖团购商品，2电子核销券,3服务，4物业缴费
	 * @param proList	需要验证的产品ID的list
	 * @param amount	此次购买金额
	 * @param coupon	此次使用的红包
	 * @param locked	是否锁
	 * @param type	0商品 1服务
	 * @return
	 */
	@Override
	public CheckCouponDTO checkAvailable4Sales(int itemType, List<OrderItem> itemList, Coupon coupon, boolean locked) {
	    
		CheckCouponDTO dto = new CheckCouponDTO();
		if(coupon == null) {
			dto.setErrMsg("优惠券为空。");
	        return dto;
	    }
		List<Long> productList = new ArrayList<>();
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (OrderItem orderItem : itemList) {
			String key = ModelConstant.KEY_PRO_RULE_INFO + orderItem.getRuleId();
			ProductRule productRule = redisRepository.getProdcutRule(key);
			if (productRule == null) {
				throw new BizValidateException("未查询到商品规则：" + orderItem.getRuleId());
			}
			
			BigDecimal amt = new BigDecimal(String.valueOf(productRule.getPrice())).multiply(new BigDecimal(String.valueOf(orderItem.getCount())));
			orderItem.setAmount(amt.floatValue());
			
			Product product = new Product();
			product.setId(orderItem.getProductId());
			product.setAgentId(productRule.getAgentId());
			
			dto = checkCouponAvailable(itemType, product, coupon, locked);
			if (!dto.isValid()) {	//不可用的商品跳过，不作总价格累计
				continue;
			}
			productList.add(product.getId());
			totalAmount = totalAmount.add(amt);
		}
			
		boolean isAmountValid = checkCouponUsageCondition(totalAmount.floatValue(), coupon);
		if (!isAmountValid) {
			dto.setValid(false);
			dto.setErrMsg("优惠券：" + coupon.getId() + ", 商品[" + productList+ "], 商品最小使用金额：" + coupon.getUsageCondition() + ", 不可用。");
		}
		dto.setValid(true);
		dto.setErrMsg("");
		return dto;
	}
	
}
