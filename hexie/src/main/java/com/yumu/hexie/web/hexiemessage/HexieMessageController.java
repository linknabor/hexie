package com.yumu.hexie.web.hexiemessage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.hexiemessage.HexieMessageService;
import com.yumu.hexie.vo.req.MessageReq;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
public class HexieMessageController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(HexieMessageController.class);
	
	@Autowired
	private HexieMessageService messageService;
	
	/**
	 * 公众号通知群发
	 * @param expr
	 * @return
	 */
	@RequestMapping(value = "/servplat/hexiemessage/send", method = RequestMethod.POST)
	public String pullWechat(@RequestBody HexieMessage hexieMessage) {
		logger.info("sendMessage:--hexieMessage:"+hexieMessage);	//TODO expr中的wuyeId如果拼接过能，可能超长。
		boolean success = messageService.sendMessage(hexieMessage);
		return Boolean.toString(success);
	}
	
	/**
	 * 群发通知查询
	 * @param messageId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/hexiemessage/get", method = RequestMethod.POST)
	public BaseResult<HexieMessage> getMessage(@RequestParam(required=false) String messageId) {
		Assert.hasText(messageId, "消息id不能为空。");
		return BaseResult.successResult(messageService.getMessage(Long.parseLong(messageId)));
	}
	
	/**
	 * 移动端发送消息
	 * @param user
	 * @param messageReq
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/hexiemessage", method = RequestMethod.POST)
	public BaseResult<String> addMessage(@ModelAttribute(Constants.USER) User user, @RequestBody MessageReq messageReq) throws Exception {
		
		messageService.sendMessageMobile(user, messageReq);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	}
	
	/**
	 * 移动端发送消息
	 * @param user
	 * @param messageReq
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/testUpload", method = RequestMethod.POST)
	public BaseResult<String> addMessageTest(@RequestBody MessageReq messageReq) throws Exception {
		
		logger.info("messageReq : " + messageReq);
		messageService.sendMessageMobile(null, messageReq);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	}
	
	/**
	 * 群发通知查询
	 * @param messageId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/hexiemessage/getByBatch", method = RequestMethod.GET)
	public BaseResult<HexieMessage> getMessageH5(@RequestParam(required=false) String batchNo) {
		Assert.hasText(batchNo, "短信批号不能为空。");
		return BaseResult.successResult(messageService.getMessageByBatchNo(batchNo));
	}
	
	
	
}
