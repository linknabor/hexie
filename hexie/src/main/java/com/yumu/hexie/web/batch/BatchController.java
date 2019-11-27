package com.yumu.hexie.web.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.service.batch.BatchService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

/**
 * 跑批的程序
 * 
 * @author david
 *
 */
@RestController
public class BatchController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(BatchController.class);

	@Autowired
	private BatchService batchService;

	/**
	 * shareCode去重
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateRepeatUserShareCode", method = RequestMethod.GET)
	public BaseResult<String> updateRepeatUserShareCode(@RequestParam String code) throws Exception {
		if ("hexieCode".equals(code)) {
			batchService.updateRepeatUserShareCode();
			logger.error("操作完成!!!");
			return BaseResult.successResult("");
		} else {
			return BaseResult.fail("请求错误！！！");
		}

	}

	/**
	 * 没有shareCode的用户新增
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateUserShareCode", method = RequestMethod.GET)
	public BaseResult<String> updateUserShareCode(@RequestParam String code) throws Exception {

		if ("hexieCode".equals(code)) {
			batchService.updateUserShareCode();
			logger.error("操作完成!!!");
			return BaseResult.successResult("");
		} else {
			return BaseResult.fail("请求错误！！！");
		}

	}
	
	/**
	 * 手工修复绑定房屋
	 * @param userId
	 * @param tradeWaterId
	 * @param code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/fixBindHouse/{userId}/{tradeWaterId}", method = RequestMethod.GET)
	public BaseResult<String> fixBindHouse(@PathVariable String userId, 
			@PathVariable String tradeWaterId,
			@RequestParam String code){
		
		if ("hexieCode".equals(code)) {
			batchService.fixBindHouse(userId, tradeWaterId);
			logger.error("操作完成!!!");
			return BaseResult.successResult("success");
		} else {
			return BaseResult.fail("unkown request !");
		}
		
	}
	
	/**
	 * 手工修复绑定房屋
	 * @param tradeWaterId
	 * @param code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/bindHouseBatch/{appId}", method = RequestMethod.GET)
	public BaseResult<String> bindHouseBatch( @RequestParam String code, @PathVariable String appId){
		
		if ("hexieCode".equals(code)) {
			batchService.bindHouseBatch(appId);
			logger.error("操作完成!!!");
			return BaseResult.successResult("success");
		} else {
			return BaseResult.fail("unkown request !");
		}
		
	}


}
