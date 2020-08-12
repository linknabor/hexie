package com.yumu.hexie.service.health.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSect;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSectRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.health.ResvOperService;

public class ResvOperServiceImpl implements ResvOperService {
	
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ServiceOperatorSectRepository serviceOperatorSectRepository;

	@Override
	public Page<ServiceOperator> getOperList(BaseRequestDTO<ServiceOperator> baseRequestDTO) {
		
		ServiceOperator serviceOperator = baseRequestDTO.getData();
		Pageable pageable = PageRequest.of(baseRequestDTO.getCurr_page(), baseRequestDTO.getPage_size());
		Page<ServiceOperator> page = serviceOperatorRepository.getResvOper(ModelConstant.SERVICE_OPER_TYPE_STAFF, 
				serviceOperator.getName(), serviceOperator.getTel(), null, baseRequestDTO.getSectList(), pageable);
		return page;
	}

	@Override
	public List<User> getUserListByTel(BaseRequestDTO<String> baseRequestDTO) {
		
		String tel = baseRequestDTO.getData();
		Assert.hasLength(tel, "手机号不能为空。");
		return userRepository.findByTel(tel);
		
	}
	
	@Override
	@Transactional
	public void saveResvOper(BaseRequestDTO<ServiceOperator> baseRequestDTO) {
		
		ServiceOperator operator = baseRequestDTO.getData();
		List<String> sectIds = baseRequestDTO.getSectList();
		ServiceOperator so = null;
		//新增
		if(operator.getId() == 0){
			List<ServiceOperator>  operatorList = serviceOperatorRepository.findByTypeAndUserIdAndCompanyName(ModelConstant.SERVICE_OPER_TYPE_STAFF, 
					operator.getId(), operator.getCompanyName());
			if(operatorList!=null && !operatorList.isEmpty()){
				throw new BizValidateException("手机号为:"+operator.getTel()+"的服务人员已存在。");
			}
			operator.setFromWuye(true);
			operator.setType(ModelConstant.SERVICE_OPER_TYPE_STAFF);
			so = serviceOperatorRepository.save(operator);
		}else{	//编辑
			//编辑只能改姓名以及和小区的绑定关系，手机号不能改
			so = serviceOperatorRepository.findById(operator.getId()).get();
			so.setName(operator.getName());
			serviceOperatorRepository.save(so);
		}
		for (String sectid : sectIds) {
			
			ServiceOperatorSect sos = new ServiceOperatorSect();
			sos.setSectId(sectid);
			sos.setOperatorId(so.getId());
			sos.setCreateDate(System.currentTimeMillis());
			serviceOperatorSectRepository.save(sos);
		}
		
	}

	@Override
	public List<String> getOperServedSect(String operatorId) {
		
		Assert.hasLength(operatorId, "员工ID不能为空。");
		return serviceOperatorSectRepository.findByOperatorId(Long.valueOf(operatorId));
	}
	
	@Override
	@Transactional
	public void deleteOperator(BaseRequestDTO<String> baseRequestDTO) {
		
		String operatorId=baseRequestDTO.getData();
		List<String> sectList = baseRequestDTO.getSectList();
		for (String sectid : sectList) {
			serviceOperatorSectRepository.deleteByOperatorIdAndSectId(Long.valueOf(operatorId), sectid);
		}
    	serviceOperatorRepository.deleteById(Long.valueOf(operatorId));
	}
	

}
