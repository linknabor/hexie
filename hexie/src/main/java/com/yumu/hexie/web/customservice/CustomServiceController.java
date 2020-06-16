package com.yumu.hexie.web.customservice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.resp.CreateOrderResponseVO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.customservice.CustomService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.customservice.vo.CustomServiceOrderVO;
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
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/service", method = RequestMethod.GET)
	public BaseResult<List<CustomServiceVO>> getService(@ModelAttribute(Constants.USER) User user) throws Exception {
		
		List<CustomServiceVO> data = customService.getService(user);
		return BaseResult.successResult(data);
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	public BaseResult<CreateOrderResponseVO> createOrder(@ModelAttribute(Constants.USER) User user, @RequestBody CustomServiceOrderVO customServiceOrderVO) throws Exception {
		
		logger.info("customServiceOrderVO : " + customServiceOrderVO);
		CustomerServiceOrderDTO dto = new CustomerServiceOrderDTO();
		BeanUtils.copyProperties(customServiceOrderVO, dto);
		dto.setUser(user);
		logger.info("customerServiceOrderDTO : " + dto);
		
		CreateOrderResponseVO vo = customService.createOrder(dto);
		return BaseResult.successResult(vo);
	}
	
	/**
	 * 确认订单
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	public BaseResult<String> confirmOrder(@ModelAttribute(Constants.USER) User user, @RequestParam String orderId) throws Exception {
		
		logger.info("user : " + user);
		logger.info("confirmOrder orderId : " + orderId);
		customService.confirmOrder(user, orderId);
		return BaseResult.successResult(Constants.SUCCESS);
	}
	
}
