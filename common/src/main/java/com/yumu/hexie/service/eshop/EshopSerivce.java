package com.yumu.hexie.service.eshop;

import java.util.Map;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.eshop.vo.QueryEvoucherVO;
import com.yumu.hexie.integration.eshop.vo.QueryOperVO;
import com.yumu.hexie.integration.eshop.vo.QueryOrderVO;
import com.yumu.hexie.integration.eshop.vo.QueryProductVO;
import com.yumu.hexie.integration.eshop.vo.SaveCategoryVO;
import com.yumu.hexie.integration.eshop.vo.SaveLogisticsVO;
import com.yumu.hexie.integration.eshop.vo.SaveOperVO;
import com.yumu.hexie.integration.eshop.vo.SaveProductVO;

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

	void saveLogistics(SaveLogisticsVO saveLogisticsVO);

}
