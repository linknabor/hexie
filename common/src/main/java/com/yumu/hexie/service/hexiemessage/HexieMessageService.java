package com.yumu.hexie.service.hexiemessage;

import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.user.User;

public interface HexieMessageService {
	
	boolean sendMessage(HexieMessage exr);
	
	HexieMessage getMessage(long messageId);

	boolean saveHexieMessage(HexieMessage exr, User user);

}
