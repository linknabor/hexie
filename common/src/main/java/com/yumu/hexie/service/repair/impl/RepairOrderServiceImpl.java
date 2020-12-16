package com.yumu.hexie.service.repair.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.repair.mapper.QueryROrderMapper;
import com.yumu.hexie.integration.repair.vo.QueryROrderVO;
import com.yumu.hexie.model.localservice.repair.RepairOrderRepository;
import com.yumu.hexie.service.repair.RepairOrderService;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {
	
	@Autowired
	private RepairOrderRepository repairOrderRepository;

	@Override
	public CommonResponse<Object> getOrder(QueryROrderVO queryROrderVO) {

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "createDate");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
	    	
	    	List<String>sectIds = queryROrderVO.getSectIds();
	    	if (sectIds != null) {
	    		if (sectIds.isEmpty()) {
					sectIds = null;
				}
			}
			Pageable pageable = PageRequest.of(queryROrderVO.getCurrentPage(), queryROrderVO.getPageSize(), sort);
			Page<Object[]> page = repairOrderRepository.getROrderList(queryROrderVO.getStartDate(), queryROrderVO.getEndDate(), queryROrderVO.getAddress(), 
					queryROrderVO.getTel(), queryROrderVO.getOperatorName(), queryROrderVO.getOperatorTel(), queryROrderVO.getStatus(), queryROrderVO.getPayType(), 
					queryROrderVO.getFinishByUser(), queryROrderVO.getFinishByOperator(), sectIds, pageable);
			
			List<QueryROrderMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryROrderMapper.class);
			
			QueryListDTO<List<QueryROrderMapper>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			responsePage.setContent(list);
			
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	} 
	
	
}
