package com.yumu.hexie.service.shequ.impl;

import java.util.List;

import com.yumu.hexie.integration.wuye.req.CommunityRequest;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.*;
import com.yumu.hexie.service.notify.impl.NotifyQueueTaskImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.NoticeService;

import javax.transaction.Transactional;

@Service
public class NoticeServiceImpl implements NoticeService {

	private static Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

	private final static int PAGE_SIZE = 10;

	@Autowired
	private NoticeRepository noticeRepository;

	@Autowired
	private NoticeSectRepository noticeSectRepository;
	
	@Override
	public List<Notice> getNotice(User user, int page) {
		
		String sectId = user.getSectId();
		if (StringUtils.isEmpty(user.getSectId())) {
			sectId = "0";
		}
		Sort sort = Sort.by(Direction.DESC, "top", "createDate");
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
		return noticeRepository.getNoticeList(ModelConstant.MESSAGE_STATUS_VALID, user.getAppId(), sectId, user.getOpenid(), pageable);
	}

	@Override
	@Transactional
	public String addOutSidNotice(CommunityRequest request) {
		Notice notice = new Notice();
		logger.info("addOutSidNotice :" + request);
		BeanUtils.copyProperties(request, notice);
		Notice n = noticeRepository.save(notice);

		String sectIds = request.getSectIds();
		if(!StringUtils.isEmpty(sectIds)) {
			String[] ids = sectIds.split(",");
			for(String key: ids) {
				NoticeSect noticeSect = new NoticeSect();
				noticeSect.setNoticeId(n.getId());
				noticeSect.setSectId(Long.parseLong(key));
				noticeSectRepository.save(noticeSect);
			}
		}
		return String.valueOf(n.getId());
	}

	@Override
	@Transactional
	public void delOutSidNotice(long noticeId) {
		Assert.hasText(String.valueOf(noticeId), "信息ID不能为空");
		noticeRepository.deleteById(noticeId);
		noticeSectRepository.deleteByNoticeId(noticeId);
	}
}
