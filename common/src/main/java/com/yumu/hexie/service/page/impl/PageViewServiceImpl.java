package com.yumu.hexie.service.page.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.statistic.PageViewRepository;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.page.PageViewService;
import com.yumu.hexie.service.page.pojo.dto.PageViewDTO;
import com.yumu.hexie.service.page.pojo.vo.PageViewSumVO;

@Service
public class PageViewServiceImpl implements PageViewService {

	private final static Logger log = LoggerFactory.getLogger(PageViewServiceImpl.class);
	private final static String DATE_FORMAT = "yyyyMMdd";
	
	@Resource
	private PageViewRepository pageViewRepository;
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

	@Override
	public PageViewSumVO getPageViewSummary(String appid, String startDate, String endDate) {
		
		if (StringUtils.isEmpty(appid)) {
			throw new BizValidateException("appid不能为空");
		}
		if (StringUtils.isEmpty(startDate)) {
			throw new BizValidateException("统计起始日期不能为空");
		}
		if (StringUtils.isEmpty(endDate)) {
			throw new BizValidateException("统计结束日期不能为空");
		}
		
		List<Map<String, Object>> list = pageViewRepository.getPageViewByAppidAndDateBetween(appid, startDate, endDate);
		Map <String, Object> map = new HashMap<>(); 
		if (list != null && !list.isEmpty()) {
			map = list.get(0);
		}
		PageViewSumVO vo = new PageViewSumVO();
		vo.setAppid(appid);
		vo.setStartDate(startDate);
		vo.setEndDate(endDate);
		Integer totalCounts = 0;
		try {
			BigDecimal count = (BigDecimal) map.get("counts");
			totalCounts = count.intValue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		vo.setTotalCounts(totalCounts);
		return vo;
	}
	
}
