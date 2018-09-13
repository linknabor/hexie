package com.yumu.hexie.web.home;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.daojia.haojiaan.HaoJiaAnReq;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.home.HaoJiaAnService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@Controller(value = "haoJiaAnController")
public class HaoJiaAnController extends BaseController{
	private static final Logger Log = LoggerFactory.getLogger(HaoJiaAnController.class);
	
	@Inject
	private HaoJiaAnService haoJiaAnService;
	
	@RequestMapping(value = "/haojiaan/createHaoJiaAnYuyueOrder/{addressId}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Long> createHaoJiaAnYuyueOrder(HaoJiaAnReq haojiaanReq,@ModelAttribute(Constants.USER)User user, @PathVariable long addressId) throws Exception {
		System.out.println("");
		Long oId = haoJiaAnService.addNoNeedPayOrder(user, haojiaanReq, addressId);
		if(oId!=null){
            return new BaseResult<Long>().success(oId);
        } else {
            return new BaseResult<Long>().failMsg("好家安预约订单提交失败，请稍后再试");
        }
    }
}
