package com.yumu.hexie.service.oper.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;

import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.oper.mapper.QueryOperMapper;
import com.yumu.hexie.integration.oper.vo.QueryOperVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSect;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSectRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.oper.OperService;

public class OperServiceImpl implements OperService {
	
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	@Autowired
	private ServiceOperatorSectRepository serviceOperatorSectRepository;

	/**
	 * 获取消息发送操作员列表
	 * @param queryOperVO
	 * @return
	 */
	@Override
	public CommonResponse<Object> getOperList(QueryOperVO queryOperVO) {
		
		Assert.hasText(queryOperVO.getOperType(), "操作员类型不能为空。");
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "id");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			
			Pageable pageable = PageRequest.of(queryOperVO.getCurrentPage(), queryOperVO.getPageSize(), sort);
			
			Page<Object[]> page = serviceOperatorRepository.getServOperByType(Integer.valueOf(queryOperVO.getOperType()), 
					queryOperVO.getOperName(), queryOperVO.getOperTel(), "", queryOperVO.getSectIds(), pageable);
			
			List<QueryOperMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryOperMapper.class);
			QueryListDTO<List<QueryOperMapper>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			list = list==null?new ArrayList<>():list;
			responsePage.setContent(list);
			
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	@Override
	@Transactional
	public void authorize(User user, String sectIds, String timestamp, String type) {
		
		Assert.hasText(timestamp, "timestamp is null!");
		Assert.hasText(type, "type is null!");
		
		Long ts = Long.valueOf(timestamp);
		if (System.currentTimeMillis() - ts > 30*60*1000 ) {
			throw new BizValidateException("授权码已失效。");
		}
		
		List<ServiceOperator> operList = serviceOperatorRepository.findByTypeAndUserId(ModelConstant.SERVICE_OPER_TYPE_MSG_SENDER, user.getId());
		if (!operList.isEmpty()) {
			throw new BizValidateException("用户已授权。");
		}
		
		ServiceOperator so = new ServiceOperator();
		so.setName(user.getName());
		so.setTel(user.getTel());
		so.setUserId(user.getId());
		so.setType(Integer.valueOf(type));	//ModelConstant.SERVICE_OPER_TYPE_MSG_SENDER
		so.setOpenId(user.getOpenid());
		serviceOperatorRepository.save(so);
		
		String[]sectArr = sectIds.split(",");
		for (String sect : sectArr) {
			ServiceOperatorSect sos = new ServiceOperatorSect();
			sos.setOperatorId(so.getId());
			sos.setSectId(sect);
			serviceOperatorSectRepository.save(sos);
		}
		
	}

}
