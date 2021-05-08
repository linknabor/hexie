package com.yumu.hexie.service.shequ;

import java.util.List;

import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.community.Notice;
import com.yumu.hexie.model.user.User;
import org.springframework.data.domain.Page;

public interface NoticeService {

	List<Notice> getNotice(User user, int page);

	Page<Notice> queryNotice(BaseRequestDTO<Notice> baseRequestDTO);

	Notice findOne(long id);

	void saveNotice(BaseRequestDTO<Notice> baseRequestDTO);
}
