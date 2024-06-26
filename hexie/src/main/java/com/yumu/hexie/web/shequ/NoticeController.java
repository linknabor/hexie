package com.yumu.hexie.web.shequ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yumu.hexie.integration.wuye.req.CommunityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(NoticeController.class);
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

		for(NoticeVO vo : noticeVoList) {
			List<String> list = new ArrayList<>();
			String[] msgs = vo.getTitle().split("\\|");
			Collections.addAll(list, msgs);
			vo.setShowMsg(list);
		}
		return BaseResult.successResult(noticeVoList);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get/{appid}/{page}", method = RequestMethod.GET)
	public BaseResult<List<NoticeVO>> getNotice(@ModelAttribute(Constants.USER)User user, @PathVariable String appid, @PathVariable int page) {
		
		List<Notice> noticeList = noticeService.getNoticeByAppid(user, appid, page);
		List<NoticeVO> noticeVoList = new ArrayList<>(noticeList.size());
		noticeList.forEach(notice->noticeVoList.add(new NoticeVO(notice, qiniuUtil)));

		for(NoticeVO vo : noticeVoList) {
			List<String> list = new ArrayList<>();
			String[] msgs = vo.getTitle().split("\\|");
			Collections.addAll(list, msgs);
			vo.setShowMsg(list);
		}
		return BaseResult.successResult(noticeVoList);
	}

	@RequestMapping(value = "/addOutSidNotice", method = RequestMethod.POST)
	public BaseResult<String> addOutSidNotice(@RequestBody CommunityRequest request) {
		log.error("CommunityRequest :" + request.toString());
		String id = noticeService.addOutSidNotice(request);
		return BaseResult.successResult(id);
	}

	@RequestMapping(value = "/delOutSidNotice/{noticeId}", method = RequestMethod.GET)
	public BaseResult<String> delOutSidNotice(@PathVariable String noticeId) {
		noticeService.delOutSidNotice(Long.parseLong(noticeId));
		return BaseResult.successResult("ok");
	}
}
