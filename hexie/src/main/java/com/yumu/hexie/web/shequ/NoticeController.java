package com.yumu.hexie.web.shequ;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.model.community.Notice;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.NoticeService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.shequ.vo.NoticeVO;

@RestController
@RequestMapping("/notice")
public class NoticeController extends BaseController {

	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private QiniuUtil qiniuUtil;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get/{page}", method = RequestMethod.GET)
	public BaseResult<List<NoticeVO>> getNotice(@ModelAttribute(Constants.USER)User user, @PathVariable int page) {
		
		List<Notice> noticeList = noticeService.getNotice(user, page);
		List<NoticeVO> noticeVoList = new ArrayList<>(noticeList.size());
		noticeList.forEach(notice->noticeVoList.add(new NoticeVO(notice, qiniuUtil)));
		return BaseResult.successResult(noticeVoList);
	}

	@RequestMapping(value = "/addOutSidNotice", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	public BaseResult<String> addOutSidNotice(@RequestParam Map<String, String> params) {
		String id = noticeService.addOutSidNotice(params);
		return BaseResult.successResult(id);
	}

	@RequestMapping(value = "/delOutSidNotice", method = RequestMethod.POST)
	public BaseResult<String> delOutSidNotice(@RequestParam(required = false) String noticeId) {
		noticeService.delOutSidNotice(Long.parseLong(noticeId));
		return BaseResult.successResult("ok");
	}
}
