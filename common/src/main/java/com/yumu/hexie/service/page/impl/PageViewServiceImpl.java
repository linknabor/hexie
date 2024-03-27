package com.yumu.hexie.service.page.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.service.page.PageViewService;
import com.yumu.hexie.service.page.pojo.dto.PageViewDTO;

@Service
public class PageViewServiceImpl implements PageViewService {

	private final static Logger log = LoggerFactory.getLogger(PageViewServiceImpl.class);
	
	private final static String DATE_FORMAT = "yyyyMMdd";
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public void incrView(PageViewDTO pageViewDTO) {
		if (pageViewDTO.getCount() == 0) {
			return;
		}
		if (StringUtils.isEmpty(pageViewDTO.getPage())) {
			log.info("page is undefined, will skip ! ");
			return;
		}
		String countDate = DateUtil.dtFormat(new Date(), DATE_FORMAT);
		String cacheKey = ModelConstant.KEY_PAGE_VIEW_COUNT + pageViewDTO.getAppid() + ":" + countDate;
		stringRedisTemplate.opsForHash().increment(cacheKey, pageViewDTO.getPage(), pageViewDTO.getCount());

	}

}
