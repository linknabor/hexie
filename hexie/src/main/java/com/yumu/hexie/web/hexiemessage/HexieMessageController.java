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
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
public class HexieMessageController extends BaseController{
	
	private static final Logger log = LoggerFactory.getLogger(HexieMessageController.class);
	
	@Autowired
	private HexieMessageService messageService;
	
	/**
	 * 公众号通知群发
	 * @param expr
	 * @return
	 */
	@RequestMapping(value = "/servplat/hexiemessage/send", method = RequestMethod.POST)
	public String pullWechat(@RequestBody HexieMessage hexieMessage) {
		log.info("sendMessage:--hexieMessage:"+hexieMessage);	//TODO expr中的wuyeId如果拼接过能，可能超长。
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
	 * 操作员授权可以在移动端发送短信
	 * @param user
	 * @param sectIds
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/hexiemessage/authorize", method = RequestMethod.POST)
	public BaseResult<String> authorize(@ModelAttribute(Constants.USER) User user,
			@RequestParam String sectIds, @RequestParam String timestamp) throws Exception {

		log.info("authorize, sectIds : " + sectIds);
		messageService.authorize(user, sectIds, timestamp);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	} 
	
}
