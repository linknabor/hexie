package com.yumu.hexie.service.sales.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.cache.ProductRuleCache;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.RgroupCart;
import com.yumu.hexie.model.redis.Keys;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.CartService;

@Service
public class CartServiceImpl implements CartService {
	
	private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
	
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	@Qualifier("stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 添加商品至购物车
	 */
	@Override
	public int add2cart(User user, OrderItem orderItem){
		
		ProductRuleCache productRule = redisRepository.getProdcutRule(ModelConstant.KEY_PRO_RULE_INFO + orderItem.getRuleId());
		if (productRule == null) {
			throw new BizValidateException("未找到当前商品规则配置，ruleId: " + orderItem.getRuleId());
		}
		String stock = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + productRule.getProductId());
		String cartKey = Keys.uidCardKey(user.getId());
		Cart cart = redisRepository.getCart(cartKey);
		if (cart == null) {
			cart = new Cart();
		}
		if(StringUtils.isEmpty(stock)) {
			throw new BizValidateException("商品库存为空，productId：" + productRule.getProductId());
		}
		cart.add(orderItem, productRule, Integer.parseInt(stock));
		redisRepository.setCart(cartKey, cart);
		return cart.getTotalCount();
	
	}
	
	/**
	 * 从购物车删除商品
	 */
	@Override
	public int delFromCart(User user, OrderItem orderItem){
		
		ProductRuleCache productRule = redisRepository.getProdcutRule(ModelConstant.KEY_PRO_RULE_INFO + orderItem.getRuleId());
		if (productRule == null) {
			throw new BizValidateException("未找到当前商品规则配置，ruleId: " + orderItem.getRuleId());
		}
		String cartKey = Keys.uidCardKey(user.getId());
		Cart cart = redisRepository.getCart(cartKey);
		if (cart == null) {
			return 0;
		}else {
			cart.del(orderItem, productRule);
			redisRepository.setCart(cartKey, cart);
			return cart.getTotalCount();
		}
		
	}
	
	
	/**
	 * 清空购物车
	 */
	@Override
	public int clearCart(User user){
		
		String cartKey = Keys.uidCardKey(user.getId());
		Cart cart = redisRepository.getCart(cartKey);
		if (cart != null) {
			cart.clear();
			redisRepository.setCart(cartKey, cart);
		}
		return 0;
	}
	
	@Override
	public Cart getCart(User user){
		
		String cartKey = Keys.uidCardKey(user.getId());
		return redisRepository.getCart(cartKey);
	}

	
	/**
	 * 从购物车删除商品
	 */
	@Override
	public void delFromCart(Long userId, List<OrderItem> itemList){
		
		String cartKey = Keys.uidCardKey(userId);
		Cart cart = redisRepository.getCart(cartKey);
		if (cart == null) {
			logger.info("cart is empty, userId : " + userId);
			return;
		}
		
		for (OrderItem orderItem : itemList) {
			ProductRuleCache productRule = redisRepository.getProdcutRule(ModelConstant.KEY_PRO_RULE_INFO + orderItem.getRuleId());
			if (productRule == null) {
				logger.info("未找到当前商品规则配置，ruleId: " + orderItem.getRuleId());
			}else {
				cart.del(orderItem, productRule);
			}
				
		}
		redisRepository.setCart(cartKey, cart);
	}
	
	/**
	 * 添加商品至购物车
	 */
	@Override
	public int add2RgroupCart(User user, OrderItem orderItem){
		
		String key = orderItem.getRuleId() + "_" + orderItem.getProductId();
		ProductRuleCache productRule = redisRepository.getProdcutRule(ModelConstant.KEY_PRO_RULE_INFO + key);
		if (productRule == null) {
			throw new BizValidateException("未找到当前商品规则配置，ruleId: " + orderItem.getRuleId());
		}
		String stock = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + productRule.getProductId());
		String cartKey = Keys.uidRgroupCartKey(user.getId());
		RgroupCart cart = redisRepository.getRgroupCart(cartKey);
		if (cart == null) {
			cart = new RgroupCart();
		}
		Integer updated = cart.add(orderItem, productRule, Integer.parseInt(stock));
		redisRepository.setRgroupCart(cartKey, cart);
		return updated;
	
	}
	
//	@Override
//	public Map<Long, > getCartItemsByRule(User user, long ruleId){
//		
//		String cartKey = Keys.uidRgroupCartKey(user.getId());
//		RgroupCart cart = redisRepository.getRgroupCart(cartKey);
//		cart.getItemsMap();
//	}

	
	
}
