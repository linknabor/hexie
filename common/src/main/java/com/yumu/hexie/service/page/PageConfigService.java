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
import com.yumu.hexie.model.view.WuyePayTabs;

public interface PageConfigService {
	
	List<Banner> queryBannerType(User user, int bannerType);

	String findByTempKey(String key, String appId) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException;

	List<BottomIcon> getBottomIcon(String appId) throws JsonParseException, JsonMappingException, IOException;

	void updateBottomIcon() throws JsonProcessingException;

	QrCode getQrCode(String appId) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException;
	
	List<Banner> queryByBannerTypeAndAppId(int bannerType, String appId) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException;

	List<BgImage> getBgImage(String appId) throws JsonParseException, JsonMappingException, IOException;
	
	List<WuyePayTabs> getWuyePayTabs(String appId) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException;
	
	void updatePageConfig();
}
