package com.yumu.hexie.web.hexiemessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.service.hexiemessage.HexieMessageService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
@RequestMapping(value = "/servplat/hexiemessage")
public class HexieMessageController extends BaseController{
	
	private static final Logger log = LoggerFactory.getLogger(HexieMessageController.class);
	
	@Autowired
	private HexieMessageService messageService;
	
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String pullWechat(@RequestBody HexieMessage expr) {
		log.info("sendMessage:--wuyeId:"+expr.getWuyeId()+"---type:"+expr.getType());	//TODO expr中的wuyeId如果拼接过能，可能超长。
		messageService.sendMessage(expr);
		return "ok";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public BaseResult<HexieMessage> getMessage(@RequestParam(required=false) String messageId) {
		Assert.hasLength(messageId, "消息id不能为空。");
		return BaseResult.successResult(messageService.getMessage(Long.parseLong(messageId)));
	}
}
