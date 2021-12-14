package com.yumu.hexie.service.eshop;

import java.util.Map;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.eshop.resp.OrderDetailResp;
import com.yumu.hexie.integration.eshop.vo.*;

public interface EshopSerivce {
	
	CommonResponse<Object> getProduct(QueryProductVO queryProductVO);
	
	CommonResponse<Object> getProductById(QueryProductVO queryProductVO);

	void saveProduct(SaveProductVO saveProductVO) throws Exception;

	void updateStatus(SaveProductVO saveProductVO);
	
	void updateDemo(SaveProductVO saveProductVO);

	CommonResponse<Object> getOper(QueryOperVO queryOperVO);

	void saveOper(SaveOperVO saveOperVO);
	
	CommonResponse<Object> getEvoucher(QueryEvoucherVO queryEvoucherVO);

	void refund(String orderNo, String operType);
	
	void saveCategory(SaveCategoryVO saveCategoryVo);
	
	void deleteCategory(String delIds);

	CommonResponse<Object> getCategory(String id);

	CommonResponse<Object> genPromotionQrCode(Map<String, String> requestMap);
	
	CommonResponse<Object> getOrder(QueryOrderVO queryOrderVO);

	String getOrderSummary(OrderSummaryVO orderSummaryVO);

	OrderDetailResp getOrderDetail(String orderId);

	void saveLogistics(SaveLogisticsVO saveLogisticsVO);

	CommonResponse<Object> getCouponCfg(QueryCouponCfgVO queryCouponCfgVO);

	CommonResponse<Object> getCouponCfgByRuleId(QueryCouponCfgVO queryCouponCfgVO);

	CommonResponse<Object> getSupportProduct(QueryProductVO queryProductVO);

	void saveCouponCfg(SaveCouponCfgVO saveCouponCfgVO) throws Exception;
	
	CommonResponse<Object> getCouponList(QueryCouponVO queryCouponVO);

	CommonResponse<Object> saveCoupon(SaveCouponVO saveCouponVO);

}
