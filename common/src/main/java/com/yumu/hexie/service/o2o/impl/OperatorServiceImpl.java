/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.o2o.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.o2o.OperatorDefinition;
import com.yumu.hexie.service.o2o.OperatorService;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: OperatorServiceImpl.java, v 0.1 2016年4月8日 下午8:09:51  Exp $
 */
@Service("operatorService")
public class OperatorServiceImpl implements OperatorService {

    @Inject
    private ServiceOperatorRepository serviceOperatorRepository;
    /** 
     * @param type
     * @param userId
     * @return
     * @see com.yumu.hexie.service.o2o.OperatorService#findByTypeAndUserId(int, long)
     */
    @Override
    public ServiceOperator findByTypeAndUserId(int type, long userId) {
        List<ServiceOperator> o = serviceOperatorRepository.findByTypeAndUserId(type,userId);
        if(o.size() > 0) {
            return o.get(0);
        }
        return null;
    }
    @Override
    public boolean isOperator(int type,long userId) {
        List<ServiceOperator> o = serviceOperatorRepository.findByTypeAndUserId(type,userId);
        return o.size()>0;
    }
    /** 
     * @param ids
     * @return
     * @see com.yumu.hexie.service.o2o.OperatorService#findByIds(java.util.List)
     */
    @Override
    public List<ServiceOperator> findByIds(List<Long> ids) {
        return serviceOperatorRepository.findOperators(ids);
    }
    /** 
     * @param type
     * @return
     * @see com.yumu.hexie.service.o2o.OperatorService#findByType(int)
     */
    @Override
    public List<ServiceOperator> findByType(int type) {
        return serviceOperatorRepository.findByType(type);
    }
    
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_USER_SERVE_ROLE, key = "#user.id")
	public OperatorDefinition defineOperator(User user) {
		
		OperatorDefinition oDefinition = new OperatorDefinition();
		List<ServiceOperator> opList = serviceOperatorRepository.findByUserId(user.getId());
		for (ServiceOperator serviceOperator : opList) {
			
			if (ModelConstant.SERVICE_OPER_TYPE_WEIXIU == serviceOperator.getType()) {
				oDefinition.setRepairOperator(true);;
			}
			if (ModelConstant.SERVICE_OPER_TYPE_SERVICE == serviceOperator.getType()) {
				oDefinition.setServiceOperator(true);
			}
			if (ModelConstant.SERVICE_OPER_TYPE_EVOUCHER == serviceOperator.getType()) {
				oDefinition.setEvoucherOperator(true);
			}
			if (ModelConstant.SERVICE_OPER_TYPE_ONSALE_TAKER == serviceOperator.getType()) {
				oDefinition.setOnsaleTaker(true);
			}
			if (ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER == serviceOperator.getType()) {
				oDefinition.setRgroupTaker(true);
			}
			if (ModelConstant.SERVICE_OPER_TYPE_MSG_SENDER == serviceOperator.getType()) {
				oDefinition.setMsgSender(true);
			}
		}
		return oDefinition;
	}
}
