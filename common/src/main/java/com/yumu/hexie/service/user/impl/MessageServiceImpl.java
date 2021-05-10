package com.yumu.hexie.service.user.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.yumu.hexie.model.community.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.Feedback;
import com.yumu.hexie.model.user.FeedbackRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.user.MessageService;

@Service(value = "messageService")
public class MessageServiceImpl implements MessageService {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	@Inject
	private MessageRepository messageRepository;
	@Inject
	private FeedbackRepository feedbackRepository;
	@Autowired
	private MessageSectRepository messageSectRepository;
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NoticeRepository noticeRepository;
	@Autowired
	private NoticeSectRepository noticeSectRepository;

	@Value(value = "${messageUrl}")
	private String messageUrl;

	@Override
	public List<Message> queryMessages(int type, long provinceId, long cityId,
			long countyId, long xiaoquId,int page, int pageSize) {
		page = page<0?0:page;
		pageSize = pageSize<0?10:pageSize;
		return messageRepository.queryMessageByRegions(type, provinceId, cityId, countyId, xiaoquId, PageRequest.of(page,pageSize));
	}
	
	@Override
	public List<Message> queryMessages(int page, int pageSize){
		
		return messageRepository.queryMessagesByStatus(PageRequest.of(page,pageSize));
	}
	
	@Override
	public Message findOne(long messageId) {
		return messageRepository.findById(messageId).get();
	}
	@Override
	public Feedback reply(long userId,String userName,String userHeader, long messageId, String content) {
		Feedback f = new Feedback(userId, userName,userHeader,messageId, content);
		return feedbackRepository.save(f);
	}
	@Override
	public List<Feedback> queryReplays(long messageId, int page, int pageSize) {
		return feedbackRepository.findAllByArticleId(messageId, PageRequest.of(page,pageSize));
	}

	/**
	 * 管理端查询首页
	 */
	@Override
	public Page<Message> queryMessages(BaseRequestDTO<Message> baseRequestDTO) {

		List<String> sectList = baseRequestDTO.getSectList();
		Sort sort = Sort.by(Direction.DESC, "top", "createDate");
		Pageable pageable = PageRequest.of(baseRequestDTO.getCurr_page(), baseRequestDTO.getPage_size(), sort);
		Message message = baseRequestDTO.getData();
		Page<Message> page;
		if(message.getMsgType() == 9) {
			page = messageRepository.querySysMessageMutipleCons(ModelConstant.MESSAGE_STATUS_VALID, message.getId(), message.getTitle(),
					baseRequestDTO.getBeginDate(), baseRequestDTO.getEndDate(), message.getMsgType(), pageable);
		} else {
			page = messageRepository.queryMessageMutipleCons(ModelConstant.MESSAGE_STATUS_VALID, message.getId(), message.getTitle(),
					baseRequestDTO.getBeginDate(), baseRequestDTO.getEndDate(), sectList, pageable);
		}
		return page;
	
	}

	/**
	 * 新增/保存公告
	 */
	@Transactional
	@Override
	public void saveMessage(BaseRequestDTO<Message> baseRequestDTO) {

		Message message = baseRequestDTO.getData();
		message = messageRepository.save(message);
		List<MessageSect> list = messageSectRepository.findByMessageId(message.getId());
		for (MessageSect messageSect : list) {
			messageSectRepository.delete(messageSect);
		}
		
		List<String> sectIds = baseRequestDTO.getSectList();
		for (String sectId : sectIds) {
			MessageSect messageSect = new MessageSect();
			messageSect.setMessageId(message.getId());
			messageSect.setSectId(Long.parseLong(sectId));
			messageSectRepository.save(messageSect);
		}

		//在notice添加一条记录
		Notice notice = new Notice();
		BeanUtils.copyProperties(message, notice);
		notice.setNoticeType(message.getMsgType());
		notice.setOutsideKey(message.getId());
		Notice n = noticeRepository.findByOutsideKey(message.getId());
		if(n != null) {
			notice.setId(n.getId());
		}

		//这里特殊处理，如果conntext有内容，则转换成链接形式存在在notice表的url字段

		if(!ObjectUtils.isEmpty(message.getContent())) {
			String url = messageUrl + "?oriApp=" + message.getAppid() + "#/message?messageId="+ message.getId();
			notice.setUrl(url);
		}
		notice = noticeRepository.save(notice);

		List<NoticeSect> noticeSects = noticeSectRepository.findByNoticeId(notice.getId());
		for (NoticeSect noticeSect : noticeSects) {
			noticeSectRepository.delete(noticeSect);
		}

		for (String sectId : sectIds) {
			NoticeSect noticeSect = new NoticeSect();
			noticeSect.setNoticeId(notice.getId());
			noticeSect.setSectId(Long.parseLong(sectId));
			noticeSectRepository.save(noticeSect);
		}
	}

	@Override
	public List<MessageSect> queryMessageSectList(Long messageId) {

		return messageSectRepository.findByMessageId(messageId);
	}

	/**
	 * 移动端物业首页公告、资讯查询
	 */
	@Override
	public List<Message> queryMessagesByUserAndType(User user, int msgType, int page, int pageSize) {

		List<Message> messageList = new ArrayList<Message>();
		Sort sort = Sort.by(Direction.DESC, "top", "createDate");
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		User currUser = userRepository.findById(user.getId());
		switch (msgType) {
		case 9:
			messageList = messageRepository.queryMessagesByStatusAndMsgType(pageable);
			break;
		default:
			boolean isDonghu = systemConfigService.isDonghu(currUser.getAppId());
			logger.info("isDonghu:" + isDonghu + ", appid : " + currUser.getAppId() + ", sectId : " + currUser.getSectId());
			if (isDonghu && (StringUtils.isEmpty(currUser.getSectId()) || "0".equals(currUser.getSectId())) ) {
				messageList = messageRepository.queryMessagesByAppidAndRegionType(msgType, 0, currUser.getAppId(), pageable);
			}else {
				List<Message> sectList = messageRepository.queryMessagesByUserAndType(currUser.getSectId(), msgType, pageable);
				List<Message> allList = messageRepository.queryMessagesByAppidAndRegionType(msgType, 0, currUser.getAppId(), pageable);
				messageList.addAll(sectList);
				messageList.addAll(allList);

			}
			break;
		}
		
		return messageList;
	}
	
	/**
	 * 查询便民信息,msgType=3的
	 */
	@Override
	public Message queryConvenienceInfo(User user, int msgType) {

		Message message = null;
		User currUser = userRepository.findById(user.getId());
		boolean isDonghu = systemConfigService.isDonghu(currUser.getAppId());
		logger.info("isDonghu:" + isDonghu + ", appid : " + currUser.getAppId() + ", sectId : " + currUser.getSectId());
		if (isDonghu && (StringUtils.isEmpty(currUser.getSectId()) || "0".equals(currUser.getSectId())) ) {
			message = messageRepository.queryMessagesByAppidAndRegionTypeWithContent(msgType, 0, currUser.getAppId());
		}else {
			message = messageRepository.queryMessagesByUserAndTypeWithContent(currUser.getSectId(), msgType);
		}
		
		return message;
	}
	
	

}
