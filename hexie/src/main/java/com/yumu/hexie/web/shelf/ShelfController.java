package com.yumu.hexie.web.shelf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.shelf.vo.QueryProductVO;
import com.yumu.hexie.integration.shelf.vo.SaveProductVO;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.service.shelf.ShelfSerivce;
import com.yumu.hexie.web.BaseController;

/**
 * 商品上、下架
 * @author david
 *
 * @param <T>
 */
@RestController
@RequestMapping(value = "/shelf")
public class ShelfController<T> extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ShelfController.class);

	@Autowired
	private ShelfSerivce shelfSerivce;
	
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public BaseResult<Object> getProduct(@RequestBody QueryProductVO queryProductVO) {
		
		logger.info("queryProductVO : " + queryProductVO);
		BaseResult<Object> baseResult = shelfSerivce.getProduct(queryProductVO);
		return baseResult;
		
	}
	
	@RequestMapping(value = "/getById", method = RequestMethod.POST)
	public BaseResult<Object> getProductById(@RequestBody QueryProductVO queryProductVO) {
		
		logger.info("getById queryProductVO : " + queryProductVO);
		BaseResult<Object> baseResult = shelfSerivce.getProductById(queryProductVO);
		return baseResult;
		
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public BaseResult<String> saveProduct(@RequestBody SaveProductVO saveProductVO) throws Exception{
		
		logger.info("saveProductVO : " + saveProductVO);
		shelfSerivce.saveProduct(saveProductVO);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setResult("00");
		return baseResult;
	}
	
	@RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
	public BaseResult<String> updateStatus(@RequestBody SaveProductVO saveProductVO) throws Exception{
		
		logger.info("updateStatus : " + saveProductVO);
		shelfSerivce.updateStatus(saveProductVO);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setResult("00");
		return baseResult;
	}
	
}
