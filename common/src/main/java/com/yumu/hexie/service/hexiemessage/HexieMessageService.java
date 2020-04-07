package com.yumu.hexie.service.hexiemessage;

import com.yumu.hexie.model.hexiemessage.HexieMessage;

public interface HexieMessageService {
	
	void sendMessage(HexieMessage exr);
	
	HexieMessage getMessage(long messageId);
}
