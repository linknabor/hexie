package com.yumu.hexie.service.sales.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.service.sales.req.NoticeServiceOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.rgroup.RgroupUser;
import com.yumu.hexie.model.market.rgroup.RgroupUserRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.DistributionService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.CacheableService;
import com.yumu.hexie.service.sales.ProductService;
import com.yumu.hexie.service.user.UserNoticeService;

@Service("customRgroupService")
public class CustomRgroupServiceImpl  extends CustomOrderServiceImpl {
	
    private static final Logger logger = LoggerFactory.getLogger(BaseOrderServiceImpl.class);

    @Inject
    private UserRepository         userRepository;
    @Inject
    private CacheableService       cacheableService;
    @Inject
    private RgroupUserRepository   rgroupUserRepository;
    @Inject
    private DistributionService    distributionService;
    @Inject
    private ProductService         productService;
    @Inject
    private UserNoticeService      userNoticeService;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private RgroupAreaItemRepository rgroupAreaItemRepository;

    @Autowired
    @Qualifier(value = "staffclientStringRedisTemplate")
    private RedisTemplate<String, String> staffclientStringRedisTemplate;

    @Override
    public void validateRule(ServiceOrder order, SalePlan plan, OrderItem item, Address address) {
        //校验
        if (!plan.valid(item.getCount())) {
            throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_RGROUP, plan.getId(), "商品信息已过期，请重新下单！")
                .setError();
        }
        //校验规则限制每个用户的数量
        RgroupRule rule = (RgroupRule) plan;
        limitRuleByUser(rule, order.getUserId());
        distributionService.validRgroupPlan(rule, address);
    }

    private void limitRuleByUser(RgroupRule rule, long userId) {
        if (rule.getRuleLimitUserCount() > 0) {
            int num = orderItemRepository.countBuyedOrderItem(userId, rule.getId(), ModelConstant.ORDER_TYPE_RGROUP);
            if (num >= rule.getRuleLimitUserCount()) {
                throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_RGROUP, rule.getId(),
                        "感谢您的参与，每个用户限购" + rule.getRuleLimitUserCount() + "份，请参与其他团购").setError();
            }
        }
    }

    @Override
    public void postPaySuccess(ServiceOrder so) {
        //支付成功订单为配货中状态，改商品库存
        so.payed();
        serviceOrderRepository.save(so);
        List<OrderItem> itemList = orderItemRepository.findByServiceOrder(so);
        for (OrderItem item : itemList) {
            productService.saledCount(item.getProductId(), item.getCount());
        }

        User u = userRepository.findById(so.getUserId());
        RgroupRule rule = findSalePlan(so.getGroupRuleId());

        logger.error("rgroup postPaySuccess:" + rule.getId());
        if (rule.getOwnerId() == 0) {
            rule.setOwnerId(so.getUserId());
            rule.setOwnerAddr(so.getAddress());
            rule.setOwnerName(u.getName());
            rule.setOwnerImg(u.getHeadimgurl());
            RgroupUser gr = new RgroupUser(so, u, true);
            rgroupUserRepository.save(gr);
        } else {
            RgroupUser gr = new RgroupUser(so, u, false);
            rgroupUserRepository.save(gr);
        }
        rule.setCurrentNum(rule.getCurrentNum() + so.getCount());
        cacheableService.save(rule);
    }


    public RgroupRule findSalePlan(long ruleId) {
        return cacheableService.findRgroupRule(ruleId);
    }

    @Override
    public void postOrderConfirm(ServiceOrder o) {
    	
    	logger.info("groupSuccess : " + o.getId());
        User u = userRepository.findById(o.getUserId());
        RgroupRule rule = cacheableService.findRgroupRule(o.getGroupRuleId());
        
        //给用户发送成团短信
        logger.info("groupSuccess, ruleId : " + rule.getId());
        userNoticeService.groupSuccess(u, u.getTel(), o.getGroupRuleId(), rule.getGroupMinNum(),
            rule.getProductName(), rule.getName());

        Region region = regionRepository.findById(o.getXiaoquId());
        //给操作员发送发货模板消息
        NoticeServiceOperator noticeServiceOperator = new NoticeServiceOperator();
        noticeServiceOperator.setSectId(region.getSectId());
        noticeServiceOperator.setAddress(o.getAddress());
        noticeServiceOperator.setCreateDate(o.getCreateDate());
        noticeServiceOperator.setReceiverName(o.getReceiverName());
        noticeServiceOperator.setId(o.getId());
        noticeServiceOperator.setProductName(o.getProductName());
        noticeServiceOperator.setOrderNo(o.getOrderNo());
        noticeServiceOperator.setSubTypeName(o.getSubTypeName());
        noticeServiceOperator.setSubType(o.getSubType());
        noticeServiceOperator.setTel(o.getTel());
        noticeServiceOperator.setOrderType(o.getOrderType());

		
		List<Long> list = new ArrayList<>();
		List<RgroupAreaItem> areaItemList = rgroupAreaItemRepository.findByProductIdAndRegionId(o.getProductId(), o.getXiaoquId());
		for (RgroupAreaItem rgroupAreaItem : areaItemList) {
			list.add(rgroupAreaItem.getAreaLeaderId());
		}
        noticeServiceOperator.setOpers(list);

		try {
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            String value = objectMapper.writeValueAsString(noticeServiceOperator);
            //放入合协管家的redis中
            staffclientStringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_DELIVERY_OPERATOR_NOTICE_MSG_QUEUE, value);
        } catch (Exception e) {
		    logger.error("custom push redis error", e);
        }

    }

    /** 
     * @param order
     * @see com.yumu.hexie.service.sales.CustomOrderService#postOrderCancel(com.yumu.hexie.model.market.ServiceOrder)
     */
    @Override
    public void postOrderCancel(ServiceOrder order) {
    	
    	if (order == null) {
    		logger.warn("order is null, will return ");
			return;
		}
    	List<OrderItem> itemList = orderItemRepository.findByServiceOrder(order);
    	for (OrderItem orderItem : itemList) {
    		logger.info("unfreeze product : " + orderItem.getProductId());
    		productService.unfreezeCount(orderItem.getProductId(), orderItem.getCount());
		}
    }

}
