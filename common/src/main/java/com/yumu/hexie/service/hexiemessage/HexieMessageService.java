package com.yumu.hexie.service.hexiemessage;

import java.util.List;

import com.yumu.hexie.model.hexiemessage.HexieMessage;

public interface HexieMessageService {
	void pullWechat(HexieMessage exr);
	
	public List<HexieMessage> getMessage(long userId);

}
