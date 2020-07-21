package com.yumu.hexie.service.eshop;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.eshop.vo.QueryOperVO;
import com.yumu.hexie.integration.eshop.vo.QueryProductVO;
import com.yumu.hexie.integration.eshop.vo.SaveOperVO;
import com.yumu.hexie.integration.eshop.vo.SaveProductVO;

public interface EshopSerivce {
	
	CommonResponse<Object> getProduct(QueryProductVO queryProductVO);
	
	CommonResponse<Object> getProductById(QueryProductVO queryProductVO);

	void saveProduct(SaveProductVO saveProductVO) throws Exception;

	void updateStatus(SaveProductVO saveProductVO);

	CommonResponse<Object> getOper(QueryOperVO queryOperVO);

	void saveOper(SaveOperVO saveOperVO);

	
	


}
