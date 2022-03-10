package com.yumu.hexie.service.shequ;

import java.util.List;
import java.util.Map;

import com.yumu.hexie.integration.wuye.req.CommunityRequest;
import com.yumu.hexie.model.community.Notice;
import com.yumu.hexie.model.user.User;

public interface NoticeService {

	List<Notice> getNotice(User user, int page);

	String addOutSidNotice(CommunityRequest request);

	void delOutSidNotice(long noticeId);

	List<Notice> getNoticeByOutSidKey(String outSidKey);

}
