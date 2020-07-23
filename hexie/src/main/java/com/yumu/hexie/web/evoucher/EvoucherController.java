package com.yumu.hexie.web.evoucher;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.evoucher.EvoucherService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
@RequestMapping(value = "/evoucher")
public class EvoucherController extends BaseController {

	@Autowired
	private EvoucherService evoucherService;
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public BaseResult<Object> getEvoucher(@ModelAttribute(Constants.USER) User user) {
		
		BaseResult<Object> baseResult = new BaseResult<>();
		baseResult.setResult(evoucherService.getByUser(user));
		return baseResult;
	}
	
	@RequestMapping(value = "/getByCode", method = RequestMethod.GET)
	public BaseResult<Object> getByCode(@RequestParam String code) {
		
		BaseResult<Object> baseResult = new BaseResult<>();
		baseResult.setResult(evoucherService.getEvoucher(code));
		return baseResult;
	}
	
	@RequestMapping(value = "/getByOrder", method = RequestMethod.GET)
	public BaseResult<Object> getByOrder(@ModelAttribute(Constants.USER) User user, long orderId) {
		
		BaseResult<Object> baseResult = new BaseResult<>();
		baseResult.setResult(evoucherService.getByOrder(orderId));
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
	@RequestMapping(value = "/consume", method = RequestMethod.POST)
	public BaseResult<String> consume(@ModelAttribute(Constants.USER) User user, 
			@RequestParam String code, @RequestParam String evouchers) throws Exception {
		
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
	@ResponseBody
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
