package com.yumu.hexie.web.user;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.localservice.HomeCart;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.promotion.coupon.CouponView;
import com.yumu.hexie.model.redis.Keys;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.dto.GainCouponDTO;
import com.yumu.hexie.vo.CouponsSummary;
import com.yumu.hexie.vo.GetValidCouponReq;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.user.resp.CouponSeedVO;

import io.swagger.annotations.ApiOperation;

@Controller(value = "couponController")
public class CouponController extends BaseController{
	
    @Inject
    private CouponService couponService;
    @Inject
    private RedisRepository redisRepository;
    @Inject
    private BaseOrderService baseOrderService;

    @RequestMapping(value = "/coupon/draw/{seedStr}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Coupon> drawCoupon(@PathVariable String seedStr,@ModelAttribute(Constants.USER)User user) throws Exception {
		return new BaseResult<Coupon>().success(couponService.addCouponFromSeed(seedStr, user));
	}

    @RequestMapping(value = "/couponSummary", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<CouponsSummary> coupons(@ModelAttribute(Constants.USER)User user) throws Exception {
		return new BaseResult<CouponsSummary>().success(couponService.findCouponSummary(user.getId()));
	}
    
    @ApiOperation(value = "获取个人已失效的红包")
    @RequestMapping(value = "/invalidCoupons/{page}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<List<Coupon>> coupons(@ModelAttribute(Constants.USER)User user,@PathVariable int page) throws Exception {
        return new BaseResult<List<Coupon>>().success(couponService.findInvalidCoupons(user.getId(), page));
    }
    
    @RequestMapping(value = "/order/coupon/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Coupon> orderCoupon(@PathVariable Long orderId,@ModelAttribute(Constants.USER)User user) throws Exception {
    	List<Coupon> coupons = couponService.findCouponsFromOrder(orderId);
    	if(coupons!=null&&coupons.size() > 0) {
    		Coupon c = coupons.get(0);
    		if(!c.isEmpty()&& c.getUserId()==user.getId()) {
    			return new BaseResult<Coupon>().success(c);
    		}
    	}
    	return new BaseResult<Coupon>().success(null);
	}

    @RequestMapping(value = "/couponSeed/{seedStr}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<CouponSeedVO> couponSeed(@PathVariable String seedStr,@ModelAttribute(Constants.USER)User user) throws Exception {
    	CouponSeedVO vo = new CouponSeedVO();
    	CouponSeed seed = couponService.findSeedByStr(seedStr);
    	if(seed == null){
    		return new BaseResult<CouponSeedVO>().failMsg("对不起，现金券不存在！");
    	}
    	vo.setSeed(seed);
    	vo.setCoupons(couponService.findCouponsBySeedStr(seedStr));
    	Coupon myCoupon = couponService.findCouponBySeedAndUser(seed.getId(), user.getId());
		vo.setFetched(myCoupon != null);
		vo.setCoupon(myCoupon);
    	return new BaseResult<CouponSeedVO>().success(vo);
	}
    @RequestMapping(value = "/coupon/validByOrder/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Coupon>> findValidCoupons(@PathVariable long orderId,@ModelAttribute(Constants.USER)User user) throws Exception {
		ServiceOrder order = baseOrderService.findOne(orderId);
		if(order.getUserId() != user.getId()) {
			throw new BizValidateException("无法查看该订单信息！");
		}
		return new BaseResult<List<Coupon>>().success(couponService.findAvaibleCoupon(order));
	}
    
    @ApiOperation(value = "特卖、团购获取可以使用的红包", notes = "salePlanType --> 特卖3, 团购4,  核销券12")
    @RequestMapping(value = "/coupon/valid", method = RequestMethod.POST)
   	@ResponseBody
   	public BaseResult<List<Coupon>> findValidCoupons(@ModelAttribute(Constants.USER)User user, 
   			@RequestBody GetValidCouponReq getValidCouponReq) throws Exception {
   		
    	List<Coupon> couponList = couponService.findAvaibleCoupon(user.getId(), getValidCouponReq.getItemList(), getValidCouponReq.getSalePlanType());
    	return new BaseResult<List<Coupon>>().success(couponList);
   	}
    
    @ApiOperation(value = "自定义服务获取可以使用的红包")
    @RequestMapping(value = "/coupon/valid4service/{serviceId}/{agentNo}", method = RequestMethod.GET)
   	@ResponseBody
   	public BaseResult<List<Coupon>> findValidCoupons4Service(@PathVariable long serviceId, @PathVariable String agentNo, 
   				@ModelAttribute(Constants.USER)User user) throws Exception {
   	
    	SalePlan salePlan = new SalePlan();
    	salePlan.setProductId(serviceId);
    	List<Coupon> couponList = couponService.findAvaibleCoupon4CustomService(user.getId(), serviceId, agentNo);
    	return new BaseResult<List<Coupon>>().success(couponList);
   	}
    
    @RequestMapping(value = "/coupon/valid4Cart", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Coupon>> findValidCoupons(@ModelAttribute(Constants.USER)User user) throws Exception {
    	Cart cart = redisRepository.getCart(Keys.uidCardKey(user.getId()));
		return new BaseResult<List<Coupon>>().success(couponService.findAvaibleCoupon(user.getId(),cart));
	}
    

    @RequestMapping(value = "/coupon/valid4HomeCart", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<List<Coupon>> valid4HomeCart(@ModelAttribute(Constants.USER)User user) throws Exception {
        HomeCart cart = redisRepository.getHomeCart(Keys.uidHomeCardKey(user.getId()));
        return new BaseResult<List<Coupon>>().success(couponService.findAvaibleCoupon(user.getId(),cart));
    }
    
    @ApiOperation(value = "获取红包种子列表")
    @RequestMapping(value = "/coupon/v2/seedList", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<List<CouponView>> getSeedList(@ModelAttribute(Constants.USER)User user) throws Exception {

    	List<CouponView> viewList = couponService.getSeedList(user);
        return new BaseResult<List<CouponView>>().success(viewList);
    }
    
    @ApiOperation(value = "根据种子领取红包")
    @RequestMapping(value = "/coupon/v2/gain/{seedStr}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Coupon> gainCoupon(HttpSession session, @ModelAttribute(Constants.USER)User user, @PathVariable String seedStr) throws Exception {
    	
    	GainCouponDTO dto = couponService.gainCouponFromSeed(user, seedStr);
    	if (!dto.isSuccess()) {
			throw new BizValidateException(dto.getErrMsg());
		}
    	session.setAttribute(Constants.USER, user);
		return new BaseResult<Coupon>().success(dto.getCoupon());
	}
    
}
