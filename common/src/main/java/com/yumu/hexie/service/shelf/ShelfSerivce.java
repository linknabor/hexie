package com.yumu.hexie.service.shelf;

import com.yumu.hexie.integration.shelf.vo.QueryProductVO;
import com.yumu.hexie.integration.shelf.vo.SaveProductVO;
import com.yumu.hexie.integration.wuye.resp.BaseResult;

public interface ShelfSerivce {
	
	<T> BaseResult<T> getProduct(QueryProductVO queryProductVO);
	
	<T> BaseResult<T> getProductById(QueryProductVO queryProductVO);

	void saveProduct(SaveProductVO saveProductVO) throws Exception;

	void updateStatus(SaveProductVO saveProductVO);

	
	


}
