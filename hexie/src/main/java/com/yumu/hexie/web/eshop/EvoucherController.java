package com.yumu.hexie.web.eshop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.eshop.EvoucherService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
@RequestMapping(value = "/evoucher")
public class EvoucherController extends BaseController {

	@Autowired
	private EvoucherService evoucherService;
	
	@RequestMapping(value = "/get/{type}", method = RequestMethod.GET)
	public BaseResult<Object> getEvoucher(@ModelAttribute(Constants.USER) User user, @PathVariable(required = false) int type) {
		
		if (StringUtils.isEmpty(type)) {
			type = ModelConstant.EVOUCHER_TYPE_VERIFICATION;
		}
		BaseResult<Object> baseResult = new BaseResult<>();
		baseResult.setResult(evoucherService.getByUserAndType(user, type));
		return baseResult;
	}
	
	@RequestMapping(value = "/getByCode/{code}", method = RequestMethod.GET)
	public BaseResult<Object> getByCode(@PathVariable String code) {
		
		BaseResult<Object> baseResult = new BaseResult<>();
		baseResult.setResult(evoucherService.getEvoucher(code));
		return baseResult;
	}
	
	@RequestMapping(value = "/getByOrder/{orderId}", method = RequestMethod.GET)
	public BaseResult<Object> getByOrder(@ModelAttribute(Constants.USER) User user, @PathVariable long orderId) {
		
		BaseResult<Object> baseResult = new BaseResult<>();
		baseResult.setResult(evoucherService.getByOrder(orderId));
		return baseResult;
	}
	
	@RequestMapping(value = "/getByOperator", method = RequestMethod.GET)
	public BaseResult<Object> getByOperator(@ModelAttribute(Constants.USER) User user) throws Exception {
		
		BaseResult<Object> baseResult = new BaseResult<>();
		baseResult.setResult(evoucherService.getByOperator(user));
		return baseResult;
	}
	
	/**
	 * 核销
	 * @param user
	 * @param code
	 * @param evouchers
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/consume/{code}", method = RequestMethod.POST)
	public BaseResult<String> consume(@ModelAttribute(Constants.USER) User user, 
			@PathVariable String code, @RequestParam(required = false) String evouchers) throws Exception {
		
		evoucherService.consume(user, code, evouchers);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	}
	
	/**
	 * 查看核销券订单，可筛状态
	 * @param user
	 * @param statusType
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/orders/{statusType}", method = RequestMethod.GET)

	public BaseResult<List<ServiceOrder>> getEvoucherOrders(@ModelAttribute(Constants.USER)User user,@PathVariable String statusType) throws Exception {
		List<Integer> status = new ArrayList<Integer>();
		if("NEEDPAY".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_INIT);
		}else if("NEEDRECEIVE".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_APPLYREFUND);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
		}else if("CANCELD".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_BACKEND);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
		}else if("PAYED".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_PAYED);
		}else if("PREPARE".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
		}else{//if("ALL".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_INIT);
			status.add(ModelConstant.ORDER_STATUS_PAYED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_APPLYREFUND);
			status.add(ModelConstant.ORDER_STATUS_REFUNDING);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
			status.add(ModelConstant.ORDER_STATUS_RECEIVED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_BACKEND);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
			status.add(ModelConstant.ORDER_STATUS_RETURNED);
			status.add(ModelConstant.ORDER_STATUS_REFUNDED);
		}
		List<ServiceOrder> orderList = evoucherService.getEvoucherOrders(user, status);
		return new BaseResult<List<ServiceOrder>>().success(orderList);
    }
	
	
}
