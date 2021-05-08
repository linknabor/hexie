package com.yumu.hexie.service.user;

import java.util.List;

import org.springframework.data.domain.Page;

import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.community.Message;
import com.yumu.hexie.model.community.MessageSect;
import com.yumu.hexie.model.user.Feedback;
import com.yumu.hexie.model.user.User;

public interface MessageService {

	List<Message> queryMessages(int type, long provinceId, long cityId,
								long countyId, long xiaoquId, int page, int pageSize);
	
	List<Message> queryMessages(int page, int pageSize);
	
	List<Message> queryMessagesByUserAndType(User user, int msgType, int page, int pageSize);
	
	Message findOne(long messageId);
	
	Feedback reply(long userId, String userName, String userHeader, long messageId, String content);
	
	List<Feedback> queryReplays(long messageId, int page, int pageSize);
	
	Page<Message> queryMessages(BaseRequestDTO<Message> baseRequestDTO);
	
	void saveMessage(BaseRequestDTO<Message> baseRequestDTO);
	
	List<MessageSect> queryMessageSectList(Long messageId);

	Message queryConvenienceInfo(User user, int msgType);

}
