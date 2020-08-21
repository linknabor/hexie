package com.yumu.hexie.service.sales.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.ProductRule;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.redis.Keys;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.CartService;

public class CartServiceImpl implements CartService {
	
	@Autowired
	private RedisRepository redisRepository;

	/**
	 * 添加商品至购物车
	 */
	@Override
	public int add2cart(User user, OrderItem orderItem){
		
		ProductRule productRule = redisRepository.getProdcutRule(ModelConstant.KEY_PRO_RULE_INFO + orderItem.getRuleId());
		if (productRule == null) {
			throw new BizValidateException("未找到当前商品规则配置，ruleId: " + orderItem.getRuleId());
		}
		String cartKey = Keys.uidCardKey(user.getId());
		Cart cart = redisRepository.getCart(cartKey);
		if (cart == null) {
			cart = new Cart();
		}
		cart.add(orderItem, productRule);
		redisRepository.setCart(cartKey, cart);
		return cart.getTotalCount();
	
	}
	
	/**
	 * 从购物车删除商品
	 */
	@Override
	public int delFromCart(User user, OrderItem orderItem){
		
		ProductRule productRule = redisRepository.getProdcutRule(ModelConstant.KEY_PRO_RULE_INFO + orderItem.getRuleId());
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
		if (cart == null) {
		}else {
			cart.clear();
			redisRepository.setCart(cartKey, cart);
		}
		return 0;
	}
	
	@Override
	public Cart getCart(User user){
		
		String cartKey = Keys.uidCardKey(user.getId());
		Cart cart = redisRepository.getCart(cartKey);
		return cart;
		
	}
	
	
}