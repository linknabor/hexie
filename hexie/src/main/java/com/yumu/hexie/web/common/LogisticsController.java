package com.yumu.hexie.web.common;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.integration.kuaidi100.resp.LogisticCompanyQueryResp;
import com.yumu.hexie.model.commonsupport.logistics.LogisticCompany;
import com.yumu.hexie.model.commonsupport.logistics.Logistics;
import com.yumu.hexie.service.common.LogisticsService;
import com.yumu.hexie.service.common.req.LogisticsInfoReq;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@Controller(value = "logisticsController")
public class LogisticsController extends BaseController{
	
	private static Logger logger = LoggerFactory.getLogger(LogisticsController.class);
	
	@Inject
    private LogisticsService logisticsService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/logistics/{nu}/{com}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Logistics>> queryLogisticsInfo( @PathVariable String nu , @PathVariable String com) throws Exception {
		List<Logistics> queryKuaidi = new ArrayList<Logistics>();
		String n[] = nu.split(",");
		String c[] = com.split(",");
		for(int i=0 ; i<n.length ; i++){
				Logistics logistics =logisticsService.queryLogisticsInfo(n[i] , c[i]);
				if(logistics.getDescription()!=null){
					logistics.setDescription(logistics.getDescription());	
				}
		    	queryKuaidi.add(logistics);	
		}
		BaseResult<List<Logistics>> result =  BaseResult.successResult(queryKuaidi);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/logistics/{logisticNo}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<LogisticCompanyQueryResp>> queryByTrackingNo( @PathVariable(name = "logisticNo") String trackingNo) throws Exception {
		
		List<LogisticCompany> list = logisticsService.queryByTrackingNo(trackingNo);
		BaseResult<List<LogisticCompanyQueryResp>> result =  BaseResult.successResult(list);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/logistics/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<String> saveLogisticsInfo(@RequestBody LogisticsInfoReq logisticsInfoReq) throws Exception {
		
		logger.info("saveLogisticsInfo : " + logisticsInfoReq);
		logisticsService.saveLogisticsInfo(logisticsInfoReq);
		BaseResult<String> result =  BaseResult.successResult(com.yumu.hexie.common.Constants.PAGE_SUCCESS);
		return result;
	}
}
