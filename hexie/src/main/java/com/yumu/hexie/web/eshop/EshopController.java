package com.yumu.hexie.web.eshop;

import java.util.Map;

import com.yumu.hexie.integration.eshop.resp.OrderDetailResp;
import com.yumu.hexie.integration.eshop.vo.*;
import com.yumu.hexie.service.eshop.EvoucherService;
import com.yumu.hexie.web.BaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.service.eshop.EshopSerivce;
import com.yumu.hexie.web.BaseController;

/**
 * 商城
 * @author david
 *
 */
@RestController
@RequestMapping(value = "/eshop")
public class EshopController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(EshopController.class);

	@Autowired
	private EshopSerivce eshopSerivce;

	@Autowired
	private EvoucherService evoucherService;
	
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
	 * @param queryOperVO
	 * @return
	 */
	@RequestMapping(value = "/operator/get", method = RequestMethod.POST)
	public CommonResponse<Object> getOperList(@RequestBody QueryOperVO queryOperVO){
		
		logger.info("queryOperVO : " + queryOperVO);
		return eshopSerivce.getOper(queryOperVO);
		
	}
	
	/**
	 * 查询团购商品涉及的小区和团长
	 * @param queryProductVO
	 * @return
	 */
	@RequestMapping(value = "/operator/rGroupLeader", method = RequestMethod.POST)
	public CommonResponse<Object> getRgroupLeader(@RequestBody QueryOperVO queryOperVO) {

		logger.info("getRgroupLeader : " + queryOperVO);
		return eshopSerivce.getRgroupLeader(queryOperVO);

	}

	/**
	 * 保存服务人员信息
	 * @param saveOperVO
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
	 * 保存服务人员信息
	 * @param saveOperVO
	 * @return
	 */
	@RequestMapping(value = "/rGroupLeader/save", method = RequestMethod.POST)
	public CommonResponse<String> saveRgroupLeader(@RequestBody SaveOperVO saveOperVO){
		
		logger.info("saveRgroupLeader : " + saveOperVO);
		eshopSerivce.saveRgroupLeader(saveOperVO);
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
		
	}

	/**
	 * 后台查询
	 * @param queryEvoucherVO
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
		return eshopSerivce.getCategory(map.get("id"));
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

	/**
	 * 查询当日订单数
	 * @return
	 */
	@RequestMapping(value = "/order/summary", method = RequestMethod.POST)
	public BaseResult<String> getOrderSummary(@RequestBody OrderSummaryVO orderSummaryVO){

		logger.info("orderSummaryVO : " + orderSummaryVO);
		String str = eshopSerivce.getOrderSummary(orderSummaryVO);
		return new BaseResult<String>().success(str);
	}
	/**
	 * 查询订单
	 * @return
	 */
	@RequestMapping(value = "/order/get", method = RequestMethod.POST)
	public CommonResponse<Object> getOrderList(@RequestBody QueryOrderVO queryOrderVO){
		
		logger.info("queryOrderVO : " + queryOrderVO);
		return eshopSerivce.getOrder(queryOrderVO);
	}

	/**
	 * 查询订单详情
	 * @return
	 */
	@RequestMapping(value = "/order/detail/{orderId}", method = RequestMethod.POST)
	public CommonResponse<OrderDetailResp> getOrderDetail(@PathVariable String orderId){

		logger.info("orderId : " + orderId);
		OrderDetailResp resp = eshopSerivce.getOrderDetail(orderId);
		CommonResponse<OrderDetailResp> commonResponse = new CommonResponse<>();
		commonResponse.setData(resp);
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	/**
	 * 查询订单
	 * @return
	 */
	@RequestMapping(value = "/logistics/save", method = RequestMethod.POST)
	public CommonResponse<String> saveLogistics(@RequestBody SaveLogisticsVO saveLogisticsVO){
		
		logger.info("saveLogisticsVO : " + saveLogisticsVO);
		eshopSerivce.saveLogistics(saveLogisticsVO);
		
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	
	}
	
	/**
	 * 查询优惠券配置
	 * @return
	 */
	@RequestMapping(value = "/coupon/cfg/get", method = RequestMethod.POST)
	public CommonResponse<Object> getCouponCfg(@RequestBody QueryCouponCfgVO queryCouponCfgVO){
		
		logger.info("queryCouponCfgVO : " + queryCouponCfgVO);
		return eshopSerivce.getCouponCfg(queryCouponCfgVO);
		
	}

	/**
	 * 根据规则ID查询优惠券配置
	 * @param queryCouponCfgVO
	 * @return
	 */
	@RequestMapping(value = "/coupon/cfg/getById", method = RequestMethod.POST)
	public CommonResponse<Object> getCouponCfgById(@RequestBody QueryCouponCfgVO queryCouponCfgVO) {
		
		logger.info("getById queryCouponCfgVO : " + queryCouponCfgVO);
		return eshopSerivce.getCouponCfgByRuleId(queryCouponCfgVO);
		
	}
	
	/**
	 * 选择支持优惠券的商品
	 * @return
	 */
	@RequestMapping(value = "/product/getSupport", method = RequestMethod.POST)
	public CommonResponse<Object> getSupportProduct(@RequestBody QueryProductVO queryProductVO){
		
		logger.info("getSupportProduct : " + queryProductVO);
		return eshopSerivce.getSupportProduct(queryProductVO);
	
	}
	
	/**
	 * 保存优惠券配置
	 * @return
	 */
	@RequestMapping(value = "/coupon/cfg/save", method = RequestMethod.POST)
	public CommonResponse<String> saveCouponCfg(@RequestBody SaveCouponCfgVO saveCouponCfgVO) throws Exception {
		
		logger.info("saveCouponCfgVO : " + saveCouponCfgVO);
		eshopSerivce.saveCouponCfg(saveCouponCfgVO);
		
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	
	}
	
	/**
	 * 查询优惠券配置
	 * @return
	 */
	@RequestMapping(value = "/coupon/get", method = RequestMethod.POST)
	public CommonResponse<Object> getCouponList(@RequestBody QueryCouponVO queryCouponVO){
		
		logger.info("queryCouponVO : " + queryCouponVO);
		return eshopSerivce.getCouponList(queryCouponVO);
		
	}
	
	/**
	 * 查询优惠券配置
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/coupon/save", method = RequestMethod.POST)
	public CommonResponse<Object> saveCoupon(@RequestBody SaveCouponVO saveCouponVO){
		
		logger.info("saveCouponVO : " + saveCouponVO);
		return eshopSerivce.saveCoupon(saveCouponVO);
	}

	/**
	 * 后台调用(运营端券码核销)
	 * @param requestMap
	 * @return
	 */
	@RequestMapping(value = "/evoucher/updateConsume", method = RequestMethod.POST)
	public CommonResponse<Map<String, String>> updateConsume(@RequestBody Map<String, String> requestMap){

		logger.info("updateConsume : " + requestMap);
		CommonResponse<Map<String, String>> commonResponse = new CommonResponse<>();
		try {
			Map<String, String> map = evoucherService.consume(requestMap.get("userId"), requestMap.get("code"));
			commonResponse.setData(map);
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");
		}
		return commonResponse;
	}
	
	/**
	 * 团购发货和未发货的订单
	 * @return
	 */
	@RequestMapping(value = "/order/rgroup/delivery", method = RequestMethod.POST)
	public CommonResponse<Object> getRgroupOrders(@RequestBody QueryOrderVO queryOrderVO){
		
		logger.info("queryOrderVO : " + queryOrderVO);
		return eshopSerivce.getRgroupOrders(queryOrderVO);
	}

}
