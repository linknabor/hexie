package com.yumu.hexie.service.user;

import java.util.Date;
import java.util.List;

import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.localservice.HomeCart;
import com.yumu.hexie.model.localservice.basemodel.BaseO2OService;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.promotion.coupon.CouponRule;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.promotion.coupon.CouponView;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.user.dto.CheckCouponDTO;
import com.yumu.hexie.service.user.dto.GainCouponDTO;
import com.yumu.hexie.vo.CouponsSummary;

/**
 * 现金券服务
 * 
 * @author ouyezi
 */
public interface CouponService {

	CouponSeed createOrderSeed(long userId, ServiceOrder order);
	
	CouponRule saveRule(CouponRule rule);
	
	void addCoupon4Member(User user);//会员
	Coupon addCouponFromSeed(String seedStr, User user);
	Coupon addCouponFromSeed(CouponSeed seed, User user);

	List<Coupon> findCouponsFromOrder(long orderId);
	Coupon findCouponBySeedAndUser(long seedId, long userId);
	List<Coupon> findCouponsBySeedStr(String seedStr);
	//是否可用
	boolean isAvaible(Cart cart, Coupon coupon);
    boolean isAvaible(HomeCart cart, Coupon coupon);
    boolean isAvaible(ServiceOrder order, Coupon coupon, boolean withLocked);
    boolean isAvaible(int itemType, Long subItemType, Long serviceType, Long productId,
					  Float amount, Coupon coupon, boolean locked);
    
	List<Coupon> findAvaibleCoupon(ServiceOrder order);
	List<Coupon> findAvaibleCoupon(long userId, List<OrderItem> itemList, int salePlanType);
	
    List<Coupon> findAvaibleCoupon(long userId, Cart cart);
    List<Coupon> findAvaibleCoupon(long userId, HomeCart cart);
    List<Coupon> findAvaibleCoupon4CustomService(long userId, long serviceId, String agentNo);
    
    //查看服务类型是否支持红包
	List<Coupon> findAvaibleCoupon4ServiceType(long userId, long homeServiceType, Long parentType, Long itemId);
	
	List<Coupon> findInvalidCoupons(long userId, int page);
	CouponsSummary findCouponSummary(long userId);
	CouponSeed findSeedByStr(String seedStr);

    void lock(ServiceOrder order, Coupon coupon);//锁定现金券
    boolean lock(BaseO2OService bill, Coupon coupon);//使用现金券
    
    void comsume(ServiceOrder order);//使用现金券
    void comsume(BaseO2OService bill);//使用现金券
    
	void unlock(Long couponId);//解锁现金券
	void timeout(Coupon coupon);//现金券过期
	
	Coupon findOne(long couponId);
	List<Coupon> findTop100TimeoutCoupon();
	List<Coupon> findTimeoutCouponByDate(Date fromDate, Date toDate);

	/********v2 新版 start ************/
	List<CouponView> getSeedList(User user);

	GainCouponDTO gainCouponFromSeed(User user, String seedStr) throws Exception;
	
	Coupon updateCouponReceived(Coupon coupon);

	Coupon findById(long couponId);
	
	boolean checkAvailable4Service(ServiceOrder order, Coupon coupon, boolean withLocked);

	CheckCouponDTO checkAvailable4Service(int itemType, Product product, Float amount, Coupon coupon, boolean locked);

	boolean checkAvailable4Sales(ServiceOrder order, Coupon coupon, boolean withLocked);
	
	CheckCouponDTO checkAvailable4Sales(int itemType, List<OrderItem> itemList, Coupon coupon, boolean locked);

	CheckCouponDTO checkCouponAvailable(int itemType, Product product, Coupon coupon, boolean locked);

	boolean checkCouponUsageCondition(Float amount, Coupon coupon);

	List<Coupon> findAvaibleCouponForWuye(User user, String payType, String amount, String agentNo);

	void consume(String orderNo, String couponId);
}
