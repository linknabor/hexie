package com.yumu.hexie.service.user.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.user.UserMngService;
import com.yumu.hexie.vo.WuyeUserVO;
import com.yumu.hexie.vo.req.QueryWuyeUserReq;

public class UserMngServiceImpl implements UserMngService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public CommonResponse<Object> getUserList(QueryWuyeUserReq queryWuyeUserReq) {

		Assert.hasText(queryWuyeUserReq.getCspId(), "物业公司id不能为空。");
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "id");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			Pageable pageable = PageRequest.of(queryWuyeUserReq.getCurrentPage(), queryWuyeUserReq.getPageSize(), sort);
			
			Page<User> page = userRepository.findByMultiCondition(queryWuyeUserReq.getCspId(), queryWuyeUserReq.getTel(), 
					queryWuyeUserReq.getName(), queryWuyeUserReq.getSectIds(), pageable);
			
			QueryListDTO<List<WuyeUserVO>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			
			List<WuyeUserVO> voList = new ArrayList<>();
			List<User> userList = page.getContent();
			for (User user : userList) {
				WuyeUserVO vo = new WuyeUserVO();
				BeanUtils.copyProperties(user, vo);
				vo.setShowName(user.getRealName());
				vo.setShowTel(user.getTel());
				voList.add(vo);
			}
			responsePage.setContent(voList);
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
		
	}

	@Override
	public CommonResponse<Object> getByWuyeIds(QueryWuyeUserReq queryWuyeUserReq) {

		Assert.hasText(queryWuyeUserReq.getCspId(), "物业公司id不能为空。");
		Assert.hasText(queryWuyeUserReq.getWuyeIds(), "用户id不能为空。");
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			String[]wuyeIdArr = queryWuyeUserReq.getWuyeIds().split(",");
			List<String> wuyeIdList = Arrays.asList(wuyeIdArr);
			
			List<User> userList = userRepository.findByCspIdAndWuyeIdIn(queryWuyeUserReq.getCspId(), wuyeIdList);
			Map<String, WuyeUserVO> map = new HashMap<>();
			for (User user : userList) {
				WuyeUserVO vo = new WuyeUserVO();
				BeanUtils.copyProperties(user, vo);
				vo.setShowName(user.getRealName());
				vo.setShowTel(user.getTel());
				map.put(vo.getWuyeId(), vo);
			}
			commonResponse.setData(map);
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
		
	}
	
	
}
