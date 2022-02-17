package com.yumu.hexie.service.page;

import java.util.List;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.Banner;
import com.yumu.hexie.model.view.BgImage;
import com.yumu.hexie.model.view.BottomIcon;
import com.yumu.hexie.model.view.CsHotline;
import com.yumu.hexie.model.view.Menu;
import com.yumu.hexie.model.view.QrCode;
import com.yumu.hexie.model.view.WuyePayTabs;

public interface PageConfigService {
	
	List<Banner> queryBannerType(User user, int bannerType);

	String findByTempKey(String key, String appId);

	List<BottomIcon> getBottomIcon(String appId);

	QrCode getQrCode(String appId);
	
	List<Banner> queryByBannerTypeAndAppId(int bannerType, String appId);

	List<BgImage> getBgImage(String appId);
	
	List<WuyePayTabs> getWuyePayTabs(String appId);
	
	CsHotline getCsHotline(String appId);
	
	void updatePageConfig();

	List<Menu> getMenuByCspId(String cspId);

	List<Menu> getMenuByAppidAndDefaultTypeLessThan(String appid, int defaultType);
	
	List<Menu> getMenuByDefaultTypeLessThan(int defaultType);

	List<Menu> getMenuBySectId(String sectId);

	String getSwtichSectTips(User user, String page);


}
