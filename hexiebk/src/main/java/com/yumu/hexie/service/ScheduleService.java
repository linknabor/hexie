package com.yumu.hexie.service;

public interface ScheduleService {

	//1. 支付单超时未支付
    public void executeOrderTimeoutJob();
	//2. 退款状态查询更新
    public void executeRefundStatusJob();
	//3. 支付状态更新
    public void executePayOrderStatusJob();
    //4. 拼单团超时
    //public void executeGroupTimeoutJob();
    //5. 团购团超时
    public void executeRGroupTimeoutJob();
    //6. 红包超时
    public void executeCouponTimeoutJob();
    //7.优惠券到期提醒
    public void executeCouponTimeoutHintJob();
    //8.会员定时 订单状态查询  及会员日期判断
    public void executeMemberTimtout();
    //9.保洁超时
	void executeBaojieTimeoutJob();
	//10.洗衣超时
	void executeXiyiTimeoutJob();
    //11.商品规则超时自动下架
	void executeOnsaleRuleTimeoutJob();
	//12.核销券超时自动修改状态
	void executeEvoucherTimeoutJob();
	//13.刷新缓存中的库存
	void initStockAndFreeze();
	//14.更新团购发货状态
	void executeRGroupDeliveryJob();
	
}
