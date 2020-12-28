package com.yumu.hexie.service.sales.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
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
import com.yumu.hexie.service.common.GotongService;
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
    private ServiceOperatorRepository serviceOperatorRepository;
    @Autowired
    private GotongService gotongService;

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
        if (rule.getRuleLimitUserCount() == 0) {
            return;
        } else {
            if (orderItemRepository.countBuyedOrderItem(userId, rule.getId(), ModelConstant.ORDER_TYPE_RGROUP) < rule
                .getRuleLimitUserCount()) {
                return;
            } else {
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
        
        //给操作员发送发货模板消息
		int operType = ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER;
		long agentId = o.getAgentId();
		logger.info("agentId is : " + agentId);
		List<ServiceOperator> opList = new ArrayList<>();
		if (agentId > 1) {	//1是默认奈博的，奈博的操作员都是null
			opList = serviceOperatorRepository.findByTypeAndAgentId(operType, agentId);
		}else {
			opList = serviceOperatorRepository.findByTypeAndAgentIdIsNull(operType);
		}
		logger.info("oper list size : " + opList.size());
		for (ServiceOperator serviceOperator : opList) {
			logger.info("delivery user id : " + serviceOperator.getUserId());
			User sendUser = userRepository.findById(serviceOperator.getUserId());
			if (sendUser != null) {
				logger.info("send user : " + sendUser.getId());
				gotongService.sendDeliveryNotification(sendUser, o);
			}
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
