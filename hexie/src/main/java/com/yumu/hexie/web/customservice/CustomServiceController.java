package com.yumu.hexie.web.customservice;

import java.util.Arrays;
import java.util.List;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.customservice.req.HeXieServiceOrderReq;
import com.yumu.hexie.integration.eshop.vo.QueryEvoucherVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.common.CommonPayResponse;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.dto.OrderQueryDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCommentDTO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.integration.customservice.resp.ServiceOrderPrepayVO;
import com.yumu.hexie.integration.customservice.resp.ServiceOrderQueryVO;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.customservice.CustomService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.customservice.vo.CustomServiceOrderVO;
import com.yumu.hexie.web.customservice.vo.OrderQueryVO;
import com.yumu.hexie.web.customservice.vo.ServiceCommentVO;

import io.swagger.annotations.ApiOperation;
/**
 * 自定义服务
 * @author david
 *
 */
@RestController
@RequestMapping(value = "/customService")
public class CustomServiceController extends BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomServiceController.class);
	
	@Autowired
	private CustomService customService;

	/**
	 * 获取服务列表
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "获取自定义服务列表")
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/service", method = RequestMethod.GET)
	public BaseResult<List<CustomServiceVO>> getService(@ModelAttribute(Constants.USER) User user) throws Exception {
		
		logger.info("getService, user : " + user);
		List<CustomServiceVO> data = customService.getService(user);
		return BaseResult.successResult(data);
		
	}
	
	/**
	 * 创建自定义服务订单
	 * @param user
	 * @param customServiceOrderVO
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "自定义服务创建订单")
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	public BaseResult<CommonPayResponse> createOrder(@ModelAttribute(Constants.USER) User user, @RequestBody CustomServiceOrderVO customServiceOrderVO) throws Exception {
		
		long begin = System.currentTimeMillis();
		logger.info("customServiceOrderVO : " + customServiceOrderVO);
		CustomerServiceOrderDTO dto = new CustomerServiceOrderDTO();
		BeanUtils.copyProperties(customServiceOrderVO, dto);
		dto.setUser(user);
		logger.info("customerServiceOrderDTO : " + dto);
		
		long end = System.currentTimeMillis();
		logger.info("createOrderController location 1 : " + (end-begin)/1000);
		
		CommonPayResponse cvo = customService.createOrder(dto);
		customService.assginOrder(cvo);	//异步分派消息
		
		end = System.currentTimeMillis();
		logger.info("createOrderController location 2 : " + (end-begin)/1000);
		
		ServiceOrderPrepayVO vo = new ServiceOrderPrepayVO(cvo);
		return BaseResult.successResult(vo);
	}
	
	/**
	 * 确认订单完工--用户
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/confirm", method = RequestMethod.POST)
	public BaseResult<String> confirmOrder(@ModelAttribute(Constants.USER) User user, @RequestParam String orderId) throws Exception {
		
		logger.info("confirmOrder, user : " + user);
		logger.info("confirmOrder, orderId : " + orderId);
		String operType = "0";
		customService.confirmOrder(user, orderId, operType);	//用户自己确认operType填0
		return BaseResult.successResult(Constants.PAGE_SUCCESS);

	}
	
	/**
	 * 确认订单完工--维修工
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/confirmByOper", method = RequestMethod.POST)
	public BaseResult<String> confirmByOper(@ModelAttribute(Constants.USER) User user, @RequestParam String orderId) throws Exception {
		
		logger.info("confirmByOper, user : " + user);
		logger.info("confirmByOper orderId : " + orderId);
		String operType = "1";
		customService.confirmOrder(user, orderId, operType);	//维修工确认operType填1
		return BaseResult.successResult(Constants.PAGE_SUCCESS);

	}
	
	/**
	 * 查询订单
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public BaseResult<ServiceOrder> queryOrder(@ModelAttribute(Constants.USER) User user, @RequestParam String orderId) throws Exception {
		
		logger.info("queryOrder, user : " + user);
		logger.info("queryOrder orderId : " + orderId);
		ServiceOrder serviceOrder = customService.queryOrder(user, orderId);
		return BaseResult.successResult(serviceOrder);
	}

	/**
	 * 查询订单
	 * @param user
	 * @param orderStatus
	 * @param serivceId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/queryByStatus", method = RequestMethod.GET)
	public BaseResult<List<ServiceOrder>> queryOrderByStatus(@ModelAttribute(Constants.USER) User user, 
			@RequestParam String orderStatus, @RequestParam(required = false, value = "service_id") String serivceId) throws Exception {
		
		logger.info("queryOrderByStatus, user : " + user);
		logger.info("queryOrder orderStatus : " + orderStatus);
		List<ServiceOrder> orderList = customService.queryOrderByStatus(user, orderStatus, serivceId);
		return BaseResult.successResult(orderList);
	}

	/**
	 * 查询订单
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/queryByUser", method = RequestMethod.GET)
	public BaseResult<List<ServiceOrder>> queryOrderByUser(@ModelAttribute(Constants.USER) User user) throws Exception {
		
		logger.info("queryOrderByUser, user : " + user);
		List<ServiceOrder> orderList = customService.queryOrderByUser(user);
		return BaseResult.successResult(orderList);
	}
	
	/**
	 * 接单
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/accept", method = RequestMethod.POST)
	public BaseResult<String> acceptOrder(@ModelAttribute(Constants.USER) User user, @RequestParam String orderId) throws Exception {
		
		logger.info("acceptOrder user : " + user);
		logger.info("acceptOrder orderId : " + orderId);
		customService.acceptOrder(user, orderId);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);

	}
	
	/**
	 * 撤销订单
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/reverse", method = RequestMethod.POST)
	public BaseResult<String> reverseOrder(@ModelAttribute(Constants.USER) User user, @RequestParam String orderId) throws Exception {
		
		logger.info("reverseOrder, user : " + user);
		logger.info("acceptOrder orderId : " + orderId);
		customService.reverseOrder(user, orderId);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);

	}
	
	/**
	 * 前端微信支付成功
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/notifyPay", method = RequestMethod.POST)
	public BaseResult<String> notifyPay(@ModelAttribute(Constants.USER) User user, @RequestParam String orderId) throws Exception {
		
		logger.info("notifyPay, user : " + user);
		logger.info("notifyPay orderId : " + orderId);
		customService.notifyPay(user, orderId);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);

	}
	
	/**
	 * 非一口价支付订单
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "自定义服务，非一口价支付")
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/pay", method = RequestMethod.POST)
	public BaseResult<CommonPayResponse> orderPay(@ModelAttribute(Constants.USER) User user, 
			@RequestParam String orderId, @RequestParam String amount, @RequestParam(required = false) String couponId) throws Exception {
		
		logger.info("orderPay, user : " + user);
		logger.info("orderPay orderId : " + orderId + ", amout : " + amount + ", couponId : " + couponId);
		ServiceOrderPrepayVO vo = customService.orderPay(user, orderId, amount, couponId);
		return BaseResult.successResult(vo);
	}

	/**
	 * 服务评价
	 * @param user
	 * @param serviceCommentVO
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/comment", method = RequestMethod.POST)
	public BaseResult<String> comment(@ModelAttribute(Constants.USER) User user, 
			@RequestBody ServiceCommentVO serviceCommentVO) throws Exception {
		
		logger.info("comment, user : " + user);
		logger.info("comment serviceCommentVO : " + serviceCommentVO);
		
		ServiceCommentDTO dto = new ServiceCommentDTO();
		BeanUtils.copyProperties(serviceCommentVO, dto);
		dto.setUser(user);
		customService.comment(dto);
		
		String imgUrls = serviceCommentVO.getCommentImgUrls();
		if (!StringUtils.isEmpty(imgUrls)) {
			String[]imgArr = imgUrls.split(",");
			List<String> imgList = Arrays.asList(imgArr);
			customService.saveCommentImages(user.getAppId(), Long.parseLong(serviceCommentVO.getOrderId()), imgList);	//异步保存上传的图片
		}
		
		return BaseResult.successResult(Constants.PAGE_SUCCESS);

	}
	
	/**
	 * 取消唤起支付
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/cancelPay", method = RequestMethod.POST)
	public BaseResult<String> cancelPay(@ModelAttribute(Constants.USER) User user, 
			String orderId) throws Exception {
		
		logger.info("cancelPay, user : " + user);
		logger.info("cancelPay orderId : " + orderId);
		
		customService.cancelPay(user, orderId);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);

	}
	
	@RequestMapping(value = "/cfg", method = RequestMethod.POST)
	public String updateCustomServiceCfg(@RequestBody ServiceCfgDTO serviceCfgDTO) throws Exception {
		
		logger.info("cfg : " + serviceCfgDTO);
		customService.updateServiceCfg(serviceCfgDTO);
		return "SUCCESS";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/queryByFeeType", method = RequestMethod.POST)
	public BaseResult<ServiceOrderQueryVO> queryOrderBySect(@ModelAttribute(Constants.USER) User user, 
			@RequestBody OrderQueryVO orderQueryVO) throws Exception{
		
		logger.info("orderQueryVO : " + orderQueryVO);
		OrderQueryDTO orderQueryDTO = new OrderQueryDTO();
		BeanUtils.copyProperties(orderQueryVO, orderQueryDTO);
		orderQueryDTO.setUser(user);
		ServiceOrderQueryVO serviceOrderQueryVO = customService.queryOrderByFeeType(orderQueryDTO);
		return BaseResult.successResult(serviceOrderQueryVO);
	}

	/**
	 * 运营端调用，抢单或完工
	 * @param heXieServiceOrderReq
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/outsid/serviceOrder/update", method = RequestMethod.POST)
	public BaseResult<ServiceOrderQueryVO> updateServiceOrder(@RequestBody HeXieServiceOrderReq heXieServiceOrderReq) throws Exception{
		logger.info("heXieServiceOrderReq : " + heXieServiceOrderReq);
		customService.updateServiceOrderByOutSid(heXieServiceOrderReq);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	}

}
