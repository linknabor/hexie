package com.yumu.hexie.service.user.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.community.Message;
import com.yumu.hexie.model.community.MessageRepository;
import com.yumu.hexie.model.user.Feedback;
import com.yumu.hexie.model.user.FeedbackRepository;
import com.yumu.hexie.service.user.MessageService;

@Service(value = "messageService")
public class MessageServiceImpl implements MessageService {


	@Inject
	private MessageRepository messageRepository;
	@Inject
	private FeedbackRepository feedbackRepository;
	@Override
	public List<Message> queryMessages(int type, long provinceId, long cityId,
			long countyId, long xiaoquId,int page, int pageSize) {
		page = page<0?0:page;
		pageSize = pageSize<0?10:pageSize;
		return messageRepository.queryMessageByRegions(type, provinceId, cityId, countyId, xiaoquId, new PageRequest(page,pageSize));
	}
	
	@Override
	public List<Message> queryMessages(int page, int pageSize){
		
		return messageRepository.queryMessagesByStatus(new PageRequest(page,pageSize));
	}
	
	@Override
	public Message findOne(long messageId) {
		return messageRepository.findOne(messageId);
	}
	@Override
	public Feedback reply(long userId,String userName,String userHeader, long messageId, String content) {
		Feedback f = new Feedback(userId, userName,userHeader,messageId, content);
		return feedbackRepository.save(f);
	}
	@Override
	public List<Feedback> queryReplays(long messageId, int page, int pageSize) {
		return feedbackRepository.findAllByArticleId(messageId, new PageRequest(page,pageSize));
	}

}
