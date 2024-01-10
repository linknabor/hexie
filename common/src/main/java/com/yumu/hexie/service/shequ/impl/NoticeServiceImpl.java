package com.yumu.hexie.service.shequ.impl;

import java.util.ArrayList;
import java.util.List;

import com.yumu.hexie.integration.wuye.req.CommunityRequest;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.*;
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

	private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

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

		List<Integer> list = new ArrayList<>();
		list.add(ModelConstant.NOTICE_TYPE2_NOTIFICATIONS);
		list.add(ModelConstant.NOTICE_TYPE2_BIll);
		list.add(ModelConstant.NOTICE_TYPE2_ARREARS_BILL);
		list.add(ModelConstant.NOTICE_TYPE2_THREAD);
		list.add(ModelConstant.NOTICE_TYPE2_ORDER);
		String appid = user.getAppId();
		String openid = user.getOpenid();
		if (StringUtils.isEmpty(appid) || StringUtils.isEmpty(openid)) {
			appid = user.getMiniAppId();
			openid = user.getMiniopenid();
		}
		return noticeRepository.getNoticeList(ModelConstant.MESSAGE_STATUS_VALID, appid, sectId, openid, list, pageable);

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

	@Override
	public List<Notice> getNoticeByOutSidKey(String outSidKey) {
		Assert.hasText(outSidKey, "关联ID不能为空");
		return noticeRepository.findByOutsideKey(Long.parseLong(outSidKey));
	}
	
	@Override
	@Transactional
	public void saveNotice(Notice notice) {
		noticeRepository.save(notice);
	}
}
