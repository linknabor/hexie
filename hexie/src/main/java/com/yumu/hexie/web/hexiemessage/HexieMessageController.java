package com.yumu.hexie.web.hexiemessage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.model.community.Message;
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
	
	@RequestMapping(value = "/pullWechat", method = RequestMethod.POST)
	public String pullWechat(@RequestBody HexieMessage expr) {
		log.info("pullWechat:--wuyeId:"+expr.getWuyeId()+"---type:"+expr.getType());
		messageService.pullWechat(expr);
		return "ok";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getMessage", method = RequestMethod.POST)
	public BaseResult<List<Message>> getMessage(@RequestParam(required=false) String userId) {
		
		return BaseResult.successResult(messageService.getMessage(Long.parseLong(userId)));
	}
}
