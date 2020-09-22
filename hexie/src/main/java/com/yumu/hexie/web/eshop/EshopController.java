package com.yumu.hexie.web.eshop;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.eshop.vo.QueryEvoucherVO;
import com.yumu.hexie.integration.eshop.vo.QueryOperVO;
import com.yumu.hexie.integration.eshop.vo.QueryProductVO;
import com.yumu.hexie.integration.eshop.vo.SaveCategoryVO;
import com.yumu.hexie.integration.eshop.vo.SaveOperVO;
import com.yumu.hexie.integration.eshop.vo.SaveProductVO;
import com.yumu.hexie.service.eshop.EshopSerivce;
import com.yumu.hexie.web.BaseController;

/**
 * 商城
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
	
	@RequestMapping(value = "/product/updateDemo", method = RequestMethod.POST)
	public CommonResponse<String> updateDemo(@RequestBody SaveProductVO saveProductVO) throws Exception{
		
		logger.info("updateDemo : " + saveProductVO);
		eshopSerivce.updateDemo(saveProductVO);
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
	
	/**
	 * 后台查询
	 * @param user
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/evoucher/get")
	public CommonResponse<Object> platQuery(@RequestBody QueryEvoucherVO queryEvoucherVO) {
		
		logger.info("platQuery : " + queryEvoucherVO);
		return eshopSerivce.getEvoucher(queryEvoucherVO);
	}
	
	/**
	 * 后台调用
	 * @param requestMap
	 * @return
	 */
	@RequestMapping(value = "/order/requestRefund", method = RequestMethod.POST)
	public CommonResponse<String> requestRefund(@RequestBody Map<String, String> requestMap){
		
		logger.info("requestRefund : " + requestMap);
		eshopSerivce.refund(requestMap.get("orderNo"), "0");
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	/**
	 * 后台调用
	 * @param requestMap
	 * @return
	 */
	@RequestMapping(value = "/order/refundReject", method = RequestMethod.POST)
	public CommonResponse<String> refundReject(@RequestBody Map<String, String> requestMap){
		
		logger.info("refundReject : " + requestMap);
		eshopSerivce.refund(requestMap.get("orderNo"), "1");
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	/**
	 * 保存商品分类
	 * @param saveCategoryVo
	 * @return
	 */
	@RequestMapping(value = "/product/category/save", method = RequestMethod.POST)
	public CommonResponse<String> saveCategory(@RequestBody SaveCategoryVO saveCategoryVo){
		
		logger.info("saveCategoryVo : " + saveCategoryVo);
		eshopSerivce.saveCategory(saveCategoryVo);
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
		
	}
	
	/**
	 * 查询商品分类
	 * @return
	 */
	@RequestMapping(value = "/product/category/get", method = RequestMethod.POST)
	public CommonResponse<Object> getCategory(@RequestBody(required = false) Map<String, String> map){
		
		logger.info("getCategory, id : " + map);
		CommonResponse<Object> commonResponse = eshopSerivce.getCategory(map.get("id"));
		return commonResponse;
	}
	
	/**
	 * 查询商品分类
	 * @return
	 */
	@RequestMapping(value = "/product/category/del", method = RequestMethod.POST)
	public CommonResponse<String> delCategory(@RequestBody Map<String, String> map ){
		
		logger.info("delCategory, id : " + map);
		eshopSerivce.deleteCategory(map.get("delIds"));
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	/**
	 * 查询商品分类
	 * @return
	 */
	@RequestMapping(value = "/promotion/genQrCode", method = RequestMethod.POST)
	public CommonResponse<Object> genQrCode(@RequestBody Map<String, String> map ){
		
		logger.info("genQrCode, id : " + map);
		return eshopSerivce.genPromotionQrCode(map);
	}
	
	
	
}
