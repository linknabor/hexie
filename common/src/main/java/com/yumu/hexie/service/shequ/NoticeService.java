package com.yumu.hexie.service.shequ;

import java.util.List;
import java.util.Map;

import com.yumu.hexie.model.community.Notice;
import com.yumu.hexie.model.user.User;

public interface NoticeService {

	List<Notice> getNotice(User user, int page);

	String addOutSidNotice(Map<String, String> map);

	void delOutSidNotice(long noticeId);
}
