package com.yumu.hexie.service.sales.impl;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.saleplan.OnSaleRule;
import com.yumu.hexie.model.market.saleplan.OnSaleRuleRepository;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.service.common.DistributionService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.ProductService;

@Service("customOnSaleService")
public class CustomOnSaleServiceImpl extends CustomOrderServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomOnSaleServiceImpl.class);
	
	@Inject
	private OnSaleRuleRepository onSaleRuleRepository;
    @Inject
    private DistributionService distributionService;
    @Inject
    private ProductService productService;
    @Autowired
    private OrderItemRepository orderItemRepository;

	@Override
	public void validateRule(ServiceOrder order,SalePlan rule, OrderItem item, Address address) {
		if(!rule.valid(item.getCount())){
            throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_ONSALE,rule.getId(),"商品信息已过期，请重新下单！").setError();
        }
        distributionService.validOnSalePlan((OnSaleRule)rule, address);
	}

    @Override
    public void postOrderConfirm(ServiceOrder order) {
        
    }
    
    @Transactional
	@Override
	public void postPaySuccess(ServiceOrder so) {
		//支付成功订单为配货中状态，改商品库存
		if (ModelConstant.ORDER_TYPE_EVOUCHER != so.getOrderType() && ModelConstant.ORDER_TYPE_PROMOTION != so.getOrderType()
				&& ModelConstant.ORDER_TYPE_SAASSALE != so.getOrderType()) {
			so.confirm();
			serviceOrderRepository.save(so);
		}
		List<OrderItem> orderItems = orderItemRepository.findByServiceOrder(so);
		for(OrderItem item : orderItems){
			productService.saledCount(item.getProductId(), item.getCount());
		}
	}

	@Override
	public SalePlan findSalePlan(long ruleId) {
		SalePlan salePlan = onSaleRuleRepository.findById(ruleId);
		if (salePlan != null) {
			return salePlan;
		}
		return new OnSaleRule();
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
