package com.yumu.hexie.service.shequ;

import java.util.List;

import com.yumu.hexie.model.community.Notice;
import com.yumu.hexie.model.user.User;

public interface NoticeService {

	List<Notice> getNotice(User user, int page);
}
