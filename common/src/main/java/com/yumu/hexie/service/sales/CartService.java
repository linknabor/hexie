package com.yumu.hexie.service.sales;

import java.util.List;

import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.user.User;

public interface CartService {

	// 添加物品到购物车
	int add2cart(User user, OrderItem orderItem);

	// 从购物车删除商品
	int delFromCart(User user, OrderItem orderItem);

	// 清空购物车
	int clearCart(User user);

	Cart getCart(User user);

	void delFromCart(Long userId, List<OrderItem> itemList);

	//添加团购购物车
	int add2RgroupCart(User user, OrderItem orderItem);

//	Cart getCartItemsByRule(User user, long ruleId);
}
