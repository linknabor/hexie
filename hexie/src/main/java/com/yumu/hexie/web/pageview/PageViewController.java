package com.yumu.hexie.web.pageview;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.service.page.PageViewService;
import com.yumu.hexie.service.page.pojo.dto.PageViewDTO;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
public class PageViewController extends BaseController {

	private final PageViewService pageViewService;
	

	public PageViewController(PageViewService pageViewService) {
		this.pageViewService = pageViewService;
	}

	@SuppressWarnings("unchecked")
	@PutMapping("/pageView")
	public BaseResult<String> updatePageView(@RequestBody PageViewDTO pageViewDTO) {
		pageViewService.incrView(pageViewDTO);
		return BaseResult.successResult("success");
	}
}
