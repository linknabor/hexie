package com.yumu.hexie.service.sales.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.service.sales.CacheableService;
import com.yumu.hexie.service.sales.RgroupService;
import com.yumu.hexie.vo.RgroupOrder;

@Service("rgroupService")
public class RgroupServiceImpl implements RgroupService {
	@Inject
	private CacheableService cacheableService;
    @Inject
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
	@Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

	/**
	 * 前端显示进度和限制库存
	 * @param result
	 * @return
	 */
	public List<RgroupAreaItem> addProcessStatus(List<RgroupAreaItem> result) {
		//TODO
        for(RgroupAreaItem item : result){
            RgroupRule rule = findSalePlan(item.getRuleId());
            if(rule!=null) {
                item.setProcess(rule.getProcess());
            } else {
                item.setProcess(0);
            }
            String stock = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + item.getProductId());
    		String freeze = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_FREEZE + item.getProductId());
    		int canSale = Integer.parseInt(stock) - Integer.parseInt(freeze);
			item.setTotalCount(canSale);
        }
        return result;
    }
	
	//FIXME
	public RgroupRule findSalePlan(long ruleId) {
		return cacheableService.findRgroupRule(ruleId);
	}

	@Override
	public List<RgroupOrder> queryMyRgroupOrders(long userId,List<Integer> status) {
		List<ServiceOrder> orders = serviceOrderRepository.findByUserAndStatusAndType(userId,status, ModelConstant.ORDER_TYPE_RGROUP);
		List<RgroupOrder> result = new ArrayList<>();
		for(ServiceOrder so : orders) {
			result.add(new RgroupOrder(cacheableService.findRgroupRule(so.getGroupRuleId()), so));
		}
		return result;
	}
}


