package com.yumu.hexie.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.service.user.UserMngService;
import com.yumu.hexie.vo.req.QueryWuyeUserReq;
import com.yumu.hexie.web.BaseController;

@RestController
@RequestMapping(value = "/usermng")
public class UserMngController extends BaseController {

	@Autowired
	private UserMngService userMngService;
	
	/**
	 * 预约服务人员列表
	 * 
	 * @param baseRequestDTO
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public CommonResponse<Object> getUserList(@RequestBody QueryWuyeUserReq queryWuyeUserReq) {
		CommonResponse<Object> commonResponse = userMngService.getUserList(queryWuyeUserReq);
		return commonResponse;
	}
	
	@RequestMapping(value = "/getByWuyeId", method = RequestMethod.POST)
	public CommonResponse<Object> getUserListByWuyeId(@RequestBody QueryWuyeUserReq queryWuyeUserReq) {
		CommonResponse<Object> commonResponse = userMngService.getByWuyeIds(queryWuyeUserReq);
		return commonResponse;
	}

}
