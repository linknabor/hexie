package com.yumu.hexie.service.page;

import com.yumu.hexie.service.page.pojo.dto.PageViewDTO;
import com.yumu.hexie.service.page.pojo.vo.PageViewSumVO;

public interface PageViewService {

	public void incrView(PageViewDTO pageViewDTO);

	PageViewSumVO getPageViewSummary(String appid, String startDate, String endDate);
	
}
