package com.yumu.hexie.service.sales;

import java.util.List;
import java.util.Map;

import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.vo.RgroupCartVO;
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
	RgroupCartVO add2RgroupCart(User user, OrderItem orderItem);

	//从团购购物车删除商品
	RgroupCartVO delFromRgroupCart(User user, OrderItem orderItem);

	Map<Long, OrderItem> getRgroupCartItems(User user, long ruleId);
}
