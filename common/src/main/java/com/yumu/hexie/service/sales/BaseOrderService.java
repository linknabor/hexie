package com.yumu.hexie.service.sales;

import java.util.List;

import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wechat.entity.common.WxRefundOrder;
import com.yumu.hexie.model.commonsupport.comment.Comment;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.sales.req.PromotionOrder;
import com.yumu.hexie.vo.CreateOrderReq;
import com.yumu.hexie.vo.SingleItemOrder;

public interface BaseOrderService {
    //创建维修单
    ServiceOrder createRepairOrder(RepairOrder order, float amount);

    //创建订单
    ServiceOrder createOrder(SingleItemOrder order);

    //创建订单
    ServiceOrder createOrder(User user, CreateOrderReq req, Cart cart);

    //发起支付
    JsSign requestPay(ServiceOrder order) throws Exception;

    //支付状态变更
    void update4Payment(PaymentOrder payment);

    //通知支付成功
    void notifyPayed(long orderId);

    //取消订单
    ServiceOrder cancelOrder(ServiceOrder order);

    //确认订单
    void confirmOrder(ServiceOrder order);

    //确认或签收
    ServiceOrder signOrder(ServiceOrder order);

    //评价
    void comment(ServiceOrder order, Comment comment);

    //退款
    ServiceOrder refund(ServiceOrder order) throws Exception;

    //查询订单
    ServiceOrder findOne(long orderId);

    //唤起支付取消
    void cancelPay(User user, String orderId) throws Exception;

    //购物车页面选择商品后支付
    ServiceOrder createOrderFromCart(User user, CreateOrderReq req);

    //推广支付
    JsSign promotionPay(User user, PromotionOrder promotionOrder) throws Exception;

    //推广支付
    JsSign promotionPayV2(User user, PromotionOrder promotionOrder) throws Exception;

    //查询是否购买过推广商品
    List<ServiceOrder> queryPromotionOrder(User user, List<Integer> statusList, List<Integer> typeList);

    //购物车结算页面
    ServiceOrder orderCheck(User user, CreateOrderReq req);

    //查询订单明细
    List<OrderItem> getOrderDetail(User user, long orderId);

    //拆单支付
    JsSign requestGroupPay(long orderId) throws Exception;

    //订单支付
    JsSign requestOrderPay(User user, long orderId) throws Exception;

    //查询订单（兼容拆分的交易）
    ServiceOrder getOrder(User user, long orderId);

    //退款处理
    void finishRefund(ServiceOrder serviceOrder);

    //退款完成
    public void finishRefund(WxRefundOrder wxRefundOrder);

    //订单支付成功回调处理，包括消费红包，改状态等操作
    void finishOrder(String tradeWaterId);


}
