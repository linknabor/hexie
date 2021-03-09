package com.yumu.hexie.integration.oper;

import java.net.URLEncoder;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.oper.req.OperAuthorizeRequest;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.OperAuthorization;

@Service
public class OperUtil {

	@Autowired
	private RestUtil restUtil;
	@Autowired
	private RequestUtil requestUtil;
	
	private static final String OPER_AUTHORIZE_URL = "oper/authorizeSDO.do";//工作人员授权
	private static final String OPER_CANCEL_AUTHORIZE_URL = "oper/cancelAuthorizeSDO.do";//取消工作人员授权
	
	/**
	 * 工作人员授权
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> operAuthorize(User user, OperAuthorization oa) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += OPER_AUTHORIZE_URL;
		
		OperAuthorizeRequest request = new OperAuthorizeRequest();
		BeanUtils.copyProperties(oa, request);
		request.setUserId(user.getWuyeId());
		request.setOpenid(user.getOpenid());
		request.setTel(user.getTel());
		
		String userName = "";
		if (!StringUtils.isEmpty(user.getName())) {
			userName = URLEncoder.encode(user.getName(),"GBK");
		}
		request.setName(userName);
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnUri(requestUrl, request, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}
	
	/**
	 * 工作人员授权
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> cancelAuthorize(User user, String type, String sectIds) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += OPER_CANCEL_AUTHORIZE_URL;
		
		OperAuthorizeRequest request = new OperAuthorizeRequest();
		request.setUserId(user.getWuyeId());
		request.setOpenid(user.getOpenid());
		request.setType(type);
		request.setSectIds(sectIds);
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnUri(requestUrl, request, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}
}
