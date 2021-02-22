package com.yumu.hexie.web.oper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.oper.mapper.QueryOperRegionMapper;
import com.yumu.hexie.integration.oper.vo.QueryOperVO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.oper.OperService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

/**
 * 所有操作员查询都放在这个类里
 * 以后改维修工，自定义服务之类的，都搬过来。TODO
 * @author huym
 *
 */
@RestController
public class OperController extends BaseController {
	
	@Autowired
	private OperService operService;
	
	private static final Logger log = LoggerFactory.getLogger(OperController.class);

	
	/**
	 * 操作员授权可以在移动端发送短信
	 * @param user
	 * @param sectIds
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/oper/authorize", method = RequestMethod.POST)
	public BaseResult<String> authorize(@ModelAttribute(Constants.USER) User user,
			@RequestParam String sectIds, @RequestParam String timestamp, @RequestParam String type) throws Exception {

		log.info("authorize, sectIds : " + sectIds);
		operService.authorize(user, sectIds, timestamp, type);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	}
	
	/**
	 * 获取操作员列表
	 * @param queryMsgOperVO
	 * @return
	 */
	@RequestMapping(value = "/servplat/oper", method = RequestMethod.POST)
	public CommonResponse<Object> getOperList(@RequestBody QueryOperVO queryOperVO) {
		
		log.info("getOperList queryMsgOperVO : " + queryOperVO);
		return operService.getOperList(queryOperVO);
	}
	
	/**
	 * 获取操作员服务区域列表
	 * @param queryMsgOperVO
	 * @return
	 */
	@RequestMapping(value = "/servplat/oper/region", method = RequestMethod.POST)
	public CommonResponse<Object> getServeRegion(@RequestBody QueryOperVO queryOperVO) {
		
		log.info("getServeRegion operId : " + queryOperVO.getOperId());
		return operService.getRegionList(queryOperVO);
	}
	
	@RequestMapping(value = "/servplat/oper/authorize/cancel", method = RequestMethod.POST)
	public CommonResponse<String> cancelAuthorize(@RequestBody QueryOperVO queryOperVO) throws Exception{
		
		log.info("cancelAuthorize : " + queryOperVO);
		operService.cancelAuthorize(queryOperVO);
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	/**
	 * 获取操作员服务区域列表 移动端
	 * @param user
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/oper/region/{type}", method = RequestMethod.GET)
	public BaseResult<Object> getServeRegionMobile(@ModelAttribute(Constants.USER) User user, @PathVariable String type) throws Exception {
		
		log.info("getServeRegionMobile, type : " + type);
		List<QueryOperRegionMapper> list = operService.getRegionListMobile(user, type);
		return BaseResult.successResult(list);
	}
	
}
