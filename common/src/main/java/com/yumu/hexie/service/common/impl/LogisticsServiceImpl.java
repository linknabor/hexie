package com.yumu.hexie.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.kuaidi100.Kuaidi100Util;
import com.yumu.hexie.integration.kuaidi100.resp.LogisticCompanyQueryResp;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.logistics.LogisticCompany;
import com.yumu.hexie.model.commonsupport.logistics.LogisticCompanyRepository;
import com.yumu.hexie.model.commonsupport.logistics.Logistics;
import com.yumu.hexie.model.commonsupport.logistics.LogisticsRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.service.common.LogisticsService;
import com.yumu.hexie.service.common.req.LogisticsInfoReq;
import com.yumu.hexie.service.exception.BizValidateException;



/**
 * Created by Administrator on 2014/12/1.
 */
@Service(value = "logisticsService")
public class LogisticsServiceImpl implements LogisticsService {
	
	private static final Logger Log = LoggerFactory.getLogger(LogisticsServiceImpl.class);
	
	@Inject
	private LogisticsRepository logisticsItemRepository;
	@Autowired
	private LogisticCompanyRepository logisticCompanyRepository;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private Kuaidi100Util kuaidi100Util;
	
	private static Map<String,String> map = null;
	
	@PostConstruct
	public void init() {
		
		if (ConstantWeChat.isMainServer()) {	//BK程序不跑下面的队列轮询
    		return;
    	}
		if(map == null){
			initComMapping();
		}
	}
	
	private void initComMapping(){
		
		if(map==null){
			map=new HashMap<>();
			List<LogisticCompany> comList= logisticCompanyRepository.findAll();
			for (LogisticCompany com : comList) {
				map.put(com.getCode(), com.getName());
			}
		}
	}
	
	@Override
	public Logistics queryLogisticsInfo(String nu , String com) {
		Logistics  logistics = logisticsItemRepository.findByLogistics(nu);	
		Log.error("logistice:"+ logistics);
		if(logistics == null){
			logistics = new Logistics();
			logistics.setLogisticsno(nu);
			logistics.setLogisticsname(com);
			logistics.setSignstatus("7");
		}else if (logistics.getDescription() == null) {
			logistics.setLogisticsno(nu);
			logistics.setLogisticsname(com);
			logistics.setSignstatus("8");
		}
		return logistics;
	}

	@Override
	public List<LogisticCompany> queryByTrackingNo(String trackingNo) {

		List<LogisticCompany> returnList = new ArrayList<>();
		List<LogisticCompanyQueryResp> queryList = kuaidi100Util.queryByTrackingNo(trackingNo);
		if (queryList == null) {
			return returnList;
		}
		for (LogisticCompanyQueryResp logisticCompanyQueryResp : queryList) {
			LogisticCompany com = new LogisticCompany();
			com.setCode(logisticCompanyQueryResp.getComCode());
			com.setName(map.get(logisticCompanyQueryResp.getComCode()));
			returnList.add(com);
		}
		return returnList;
	
	}
	
	@Override
	public void refreshExpressCom() {
		
		map = null;
		initComMapping();
		
	}

	@Override
	@Transactional
	public void saveLogisticsInfo(LogisticsInfoReq logisticsInfoReq) {

		ServiceOrder serviceOrder = serviceOrderRepository.findById(logisticsInfoReq.getOrderId()).get();
		if (ModelConstant.ORDER_STATUS_PAYED != serviceOrder.getStatus()) {
			throw new BizValidateException("订单状态不允许当前操作，订单ID：" + serviceOrder.getId());
		}
		serviceOrder.setLogisticCode(logisticsInfoReq.getLogisticCode());
		serviceOrder.setLogisticName(logisticsInfoReq.getLogisticName());
		serviceOrder.setLogisticNo(logisticsInfoReq.getLogisticNo());
		serviceOrder.setLogisticType(logisticsInfoReq.getLogisticType());
		serviceOrder.setStatus(ModelConstant.ORDER_STATUS_SENDED);
		serviceOrderRepository.save(serviceOrder);
		
	}
}
