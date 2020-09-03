package com.yumu.hexie.service.common;

import java.util.List;

import com.yumu.hexie.model.commonsupport.logistics.LogisticCompany;
import com.yumu.hexie.model.commonsupport.logistics.Logistics;
import com.yumu.hexie.service.common.req.LogisticsInfoReq;

/**
 * Created by Administrator on 2014/12/1.
 */
public interface LogisticsService {

	public Logistics queryLogisticsInfo(String nu ,String com);
	
	List<LogisticCompany> queryByTrackingNo(String trackingNo);

	void refreshExpressCom();
	
	void saveLogisticsInfo(LogisticsInfoReq logisticsInfoReq);

}
