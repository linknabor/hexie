package com.yumu.hexie.service.shequ.impl;

import java.beans.Transient;
import java.util.List;
import java.util.Map;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.*;
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

@Service
public class NoticeServiceImpl implements NoticeService {
	
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
		return noticeRepository.getNoticeList(ModelConstant.MESSAGE_STATUS_VALID, user.getAppId(), sectId, pageable);
	}

	@Override
	@Transient
	public String addOutSidNotice(Map<String, String> map) {
		Notice notice = new Notice();
		notice.setNoticeType(Integer.parseInt(map.get("noticeType")));
		notice.setTitle(map.get("title"));
		notice.setSummary(map.get("summary"));
		notice.setContent(map.get("content"));
		notice.setImage(map.get("image"));
		notice.setAppid(map.get("appid"));
		notice.setOutsideKey(Long.parseLong(map.get("outsideKey")));
		notice.setPublishDate(map.get("publishDate"));
		Notice n = noticeRepository.save(notice);

		String sectIds = map.get("sectIds");
		String[] ids = sectIds.split(",");
		for(String key: ids) {
			NoticeSect noticeSect = new NoticeSect();
			noticeSect.setNoticeId(n.getId());
			noticeSect.setSectId(Long.parseLong(key));
			noticeSectRepository.save(noticeSect);
		}
		return String.valueOf(n.getId());
	}

	@Override
	@Transient
	public void delOutSidNotice(long noticeId) {
		noticeRepository.deleteById(noticeId);
		noticeSectRepository.deleteByNoticeId(noticeId);
	}
}
