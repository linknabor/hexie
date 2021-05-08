package com.yumu.hexie.service.shequ.impl;

import java.util.List;

import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.NoticeService;

@Service
public class NoticeServiceImpl implements NoticeService {
	
	private final static int PAGE_SIZE = 10;

	@Autowired
	private NoticeRepository noticeRepository;
	
	@Override
	public List<Notice> getNotice(User user, int page) {
		
		String sectId = user.getSectId();
		if (StringUtils.isEmpty(user.getSectId())) {
			sectId = "0";
		}
		Sort sort = Sort.by(Direction.DESC, "top", "createDate");
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
		return noticeRepository.getNoticeList(user.getAppId(), sectId, pageable);
		
	}

	@Override
	public Page<Notice> queryNotice(BaseRequestDTO<Notice> baseRequestDTO) {
		Sort sort = Sort.by(Direction.DESC, "top", "createDate");
		Pageable pageable = PageRequest.of(baseRequestDTO.getCurr_page(), baseRequestDTO.getPage_size(), sort);
		Notice notice = baseRequestDTO.getData();
		Page<Notice> page = noticeRepository.queryNoticeMutipleCons(ModelConstant.MESSAGE_STATUS_VALID, notice.getId(), notice.getTitle(),
				baseRequestDTO.getBeginDate(), baseRequestDTO.getEndDate(), notice.getNoticeType(), pageable);
		return page;
	}

	@Override
	public Notice findOne(long id) {
		return noticeRepository.findById(id).get();
	}

	@Override
	public void saveNotice(BaseRequestDTO<Notice> baseRequestDTO) {
		Notice notice = baseRequestDTO.getData();
		noticeRepository.save(notice);
	}

}
