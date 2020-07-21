package com.yumu.hexie.web.eshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.eshop.vo.QueryOperVO;
import com.yumu.hexie.integration.eshop.vo.QueryProductVO;
import com.yumu.hexie.integration.eshop.vo.SaveOperVO;
import com.yumu.hexie.integration.eshop.vo.SaveProductVO;
import com.yumu.hexie.service.eshop.EshopSerivce;
import com.yumu.hexie.web.BaseController;

/**
 * 商品上、下架
 * @author david
 *
 * @param <T>
 */
@RestController
@RequestMapping(value = "/eshop")
public class EshopController<T> extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(EshopController.class);

	@Autowired
	private EshopSerivce eshopSerivce;
	
	@RequestMapping(value = "/product/get", method = RequestMethod.POST)
	public CommonResponse<Object> getProduct(@RequestBody QueryProductVO queryProductVO) {
		
		logger.info("queryProductVO : " + queryProductVO);
		return eshopSerivce.getProduct(queryProductVO);
		
	}
	
	@RequestMapping(value = "/product/getById", method = RequestMethod.POST)
	public CommonResponse<Object> getProductById(@RequestBody QueryProductVO queryProductVO) {
		
		logger.info("getById queryProductVO : " + queryProductVO);
		return eshopSerivce.getProductById(queryProductVO);
		
	}
	
	@RequestMapping(value = "/product/save", method = RequestMethod.POST)
	public CommonResponse<String> saveProduct(@RequestBody SaveProductVO saveProductVO) throws Exception{
		
		logger.info("saveProductVO : " + saveProductVO);
		eshopSerivce.saveProduct(saveProductVO);
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	@RequestMapping(value = "/product/updateStatus", method = RequestMethod.POST)
	public CommonResponse<String> updateStatus(@RequestBody SaveProductVO saveProductVO) throws Exception{
		
		logger.info("updateStatus : " + saveProductVO);
		eshopSerivce.updateStatus(saveProductVO);
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	/**
	 * 获取服务人员列表
	 * @param <T>
	 * @return
	 */
	@RequestMapping(value = "/operator/get", method = RequestMethod.POST)
	public CommonResponse<Object> getOperList(@RequestBody QueryOperVO queryOperVO){
		
		logger.info("queryOperVO : " + queryOperVO);
		return eshopSerivce.getOper(queryOperVO);
		
	}
	
	/**
	 * 保存服务人员信息
	 * @param <T>
	 * @return
	 */
	@RequestMapping(value = "/operator/save", method = RequestMethod.POST)
	public CommonResponse<String> saveOper(@RequestBody SaveOperVO saveOperVO){
		
		logger.info("saveOperVO : " + saveOperVO);
		eshopSerivce.saveOper(saveOperVO);
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
		
	}
	
}
