package com.yumu.hexie.web.sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.sales.CartService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
@RequestMapping(value = "/cart")
public class CartController extends BaseController {

	@Autowired
	private CartService cartService;
	
	/**
	 * 添加商品至购物车
	 * @param user
	 * @param goods
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Integer> add2cart(@ModelAttribute(Constants.USER)User user, @RequestBody OrderItem orderItem) throws Exception {
		
		int updated = cartService.add2cart(user, orderItem);
		return new BaseResult<Integer>().success(updated);
	}
	
	/**
	 * 删除商品从购物车
	 * @param user
	 * @param goods
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Integer> delFromCart(@ModelAttribute(Constants.USER)User user, @RequestBody OrderItem orderItem) throws Exception {
		
		int updated = cartService.delFromCart(user, orderItem);
		return new BaseResult<Integer>().success(updated);
	}
	
	/**
	 * 清空购物车
	 * @param user
	 * @param goods
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/clear", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Integer> clearCart(@ModelAttribute(Constants.USER)User user) throws Exception {
		
		int updated = cartService.clearCart(user);
		return new BaseResult<Integer>().success(updated);
	}
	
	/**
	 * 清空购物车
	 * @param user
	 * @param goods
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Cart> getItems(@ModelAttribute(Constants.USER)User user) throws Exception {
		
		Cart cart = cartService.getCart(user);
		return new BaseResult<Cart>().success(cart);
	}
	
	/**
	 * 添加商品至购物车
	 * @param user
	 * @param orderItem
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroup/add", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Integer> add2RgroupCart(@ModelAttribute(Constants.USER)User user, @RequestBody OrderItem orderItem) throws Exception {
		
		int updated = cartService.add2RgroupCart(user, orderItem);
		return new BaseResult<Integer>().success(updated);
	}
	
	/**
	 * 清空购物车
	 * @param user
	 * @param goods
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/v2/getByRule", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Cart> getCartItemsByRule(@ModelAttribute(Constants.USER)User user, @RequestParam(required = false) long ruleId) throws Exception {
		
		Cart cart = cartService.getCart(user);
		return new BaseResult<Cart>().success(cart);
	}
	
}
