package com.yumu.hexie.service.page;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.Banner;
import com.yumu.hexie.model.view.BgImage;
import com.yumu.hexie.model.view.BottomIcon;
import com.yumu.hexie.model.view.QrCode;

public interface PageConfigService {
	public List<Banner> queryBannerType(User user, int bannerType);
    public String findByTempKey(String key);
	List<BottomIcon> getBottomIcon(String appId) throws JsonParseException, JsonMappingException, IOException;
	void updateBottomIcon() throws JsonProcessingException;
	public QrCode getQrCode(String fromSys);
	BgImage getBgImage(String imageType, String fromSys) throws JsonParseException, JsonMappingException, IOException;
}
