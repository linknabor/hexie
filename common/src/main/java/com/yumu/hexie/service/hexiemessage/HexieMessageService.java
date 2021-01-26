package com.yumu.hexie.service.hexiemessage;

import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.req.MessageReq;

public interface HexieMessageService {
	
	boolean sendMessage(HexieMessage exr);
	
	boolean saveMessage(HexieMessage exr, User user);
	
	void sendMessageMobile(User user, MessageReq messageReq) throws Exception;

	HexieMessage getMessage(long messageId);
	
	HexieMessage getMessageByBatchNo(String batchNo);
}
