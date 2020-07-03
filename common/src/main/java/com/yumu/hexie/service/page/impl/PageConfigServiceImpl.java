package com.yumu.hexie.service.page.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.Banner;
import com.yumu.hexie.model.view.BannerRepository;
import com.yumu.hexie.model.view.BgImage;
import com.yumu.hexie.model.view.BgImageRepository;
import com.yumu.hexie.model.view.BottomIcon;
import com.yumu.hexie.model.view.BottomIconRepository;
import com.yumu.hexie.model.view.PageConfigView;
import com.yumu.hexie.model.view.PageConfigViewRepository;
import com.yumu.hexie.model.view.QrCode;
import com.yumu.hexie.model.view.QrCodeRepository;
import com.yumu.hexie.model.view.WuyePayTabs;
import com.yumu.hexie.model.view.WuyePayTabsRepository;
import com.yumu.hexie.service.page.PageConfigService;

@Service("pageConfigService")
public class PageConfigServiceImpl implements PageConfigService {
	
	private static Logger logger = LoggerFactory.getLogger(PageConfigServiceImpl.class);

	@Inject
	private PageConfigViewRepository pageConfigViewRepository;
	@Inject
	private BannerRepository bannerRepository;

	@Autowired
	private BottomIconRepository bottomIconRepository;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private QrCodeRepository qrCodeRepository;
	@Autowired
	private BgImageRepository bgImageRepository;
	@Autowired
	private WuyePayTabsRepository wuyePayTabsRepository;
	
	private static Map<String, Map<String, Object>> pageConfigMap = new HashMap<>();

	/**
	 * 根据banner类型动态获取
	 */
	@Override
	public List<Banner> queryBannerType(User user, int bannerType) {
		if (user.getProvinceId() != 0) {
			return bannerRepository.queryByBannerTypeAndUser(user.getProvinceId(), user.getCityId(), user.getCountyId(),
					user.getXiaoquId(), bannerType);
		} else {
			return bannerRepository.queryByBannerTypeAndUser(19, user.getCityId(), user.getCountyId(),
					user.getXiaoquId(), bannerType);
		}

	}

	/**
	 * 到家、洗衣、红包页面配置
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String findByTempKey(String key, String appId) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		
		if (StringUtil.isEmpty(appId)) {
			appId = ConstantWeChat.APPID;
		}
		final String sysAppId = appId;
		TypeReference typeReference = new TypeReference<PageConfigView>() {};
		String field = appId + "_" + key;
		PageConfigView pageConfigView = (PageConfigView) getConfigFromCache(ModelConstant.KEY_TYPE_PAGECONFIG, field, typeReference);
		if (pageConfigView == null || pageConfigView.getId() == 0) {
			Supplier<PageConfigView> supplier = ()-> pageConfigViewRepository.findByTempKeyAndAppId(key, sysAppId);
			pageConfigView = (PageConfigView)setConfigCache(ModelConstant.KEY_TYPE_PAGECONFIG, field, supplier);
		}
		if (pageConfigView != null) {
			return pageConfigView.getPageConfig();
		}
		return "";
	}

	/**
	 * 根据不同sys动态获取底部icon。
	 * @param appId
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<BottomIcon> getBottomIcon(String appId) throws JsonParseException, JsonMappingException, IOException {

		if (StringUtil.isEmpty(appId)) {
			appId = ConstantWeChat.APPID;
		}
		final String sysAppId = appId;
		TypeReference typeReference = new TypeReference<List<BottomIcon>>() {};
		List<BottomIcon> iconList = (List<BottomIcon>) getConfigFromCache(ModelConstant.KEY_TYPE_BOTTOM_ICON, appId, typeReference);
		if (iconList == null || iconList.isEmpty()) {
			Sort sort = new Sort(Direction.ASC, "sort");
			Supplier<List<BottomIcon>> supplier = ()-> bottomIconRepository.findByAppId(sysAppId, sort);
			iconList = (List<BottomIcon>)setConfigCache(ModelConstant.KEY_TYPE_BOTTOM_ICON, appId, supplier);
		}
		return iconList;
	}

	
	/**
	 * 根据不同sys动态获取空白背景地图
	 * @param appId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<BgImage> getBgImage(String appId) throws JsonParseException, JsonMappingException, IOException {

		if (StringUtil.isEmpty(appId)) {
			appId = ConstantWeChat.APPID;
		}
		final String sysAppId = appId;
		TypeReference typeReference = new TypeReference<List<BgImage>>() {};
		List<BgImage> imageList = (List<BgImage>) getConfigFromCache(ModelConstant.KEY_TYPE_BGIMAGE, appId, typeReference);
		if (imageList == null || imageList.isEmpty()) {
			Sort sort = new Sort(Direction.ASC, "type");
			Supplier<List<BgImage>> supplier = ()-> bgImageRepository.findByAppId(sysAppId, sort);
			imageList = (List<BgImage>)setConfigCache(ModelConstant.KEY_TYPE_BGIMAGE, appId, supplier);
		}
		return imageList;
	}
	
	/**
	 * 动态获取公众号二维码
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public QrCode getQrCode(String appId) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {

		if (StringUtil.isEmpty(appId)) {
			appId = ConstantWeChat.APPID;
		}
		final String sysAppId = appId;
		TypeReference typeReference = new TypeReference<QrCode>() {};
		QrCode qrCode = (QrCode) getConfigFromCache(ModelConstant.KEY_TYPE_QRCODE, appId, typeReference);
		if (qrCode == null || qrCode.getId() == 0) {
			Supplier<QrCode> supplier = ()->qrCodeRepository.findByFromSys(sysAppId);
			qrCode = (QrCode) setConfigCache(ModelConstant.KEY_TYPE_QRCODE, appId, supplier);
		}
		return qrCode;
	}
	
	/**
	 * 根据banner类型和appId获取banner
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Banner> queryByBannerTypeAndAppId(int bannerType, String appId) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {

		if (StringUtils.isEmpty(appId)) {
			appId = ConstantWeChat.APPID;
		}
		final String sysAppId = appId;
		String field = appId + "_" + bannerType;
		TypeReference typeReference = new TypeReference<List<Banner>>() {};
		List<Banner> bannerList = (List<Banner>) getConfigFromCache(ModelConstant.KEY_TYPE_BANNER, field, typeReference);
		if (bannerList == null || bannerList.isEmpty()) {
			Sort sort = new Sort(Direction.ASC, "sortNo");
			Supplier<List<Banner>> supplier = ()-> bannerRepository.findByBannerTypeAndStatusAndRegionTypeAndAppId(bannerType, ModelConstant.BANNER_STATUS_VALID, 
					ModelConstant.REGION_ALL, sysAppId, sort);
			bannerList = (List<Banner>) setConfigCache(ModelConstant.KEY_TYPE_BANNER, field, supplier);
		}
		return bannerList;
		
	}
	
	/**
	 * 根据appId获取相应的图标或者背景图，每个公众号有自己不同的图标和背景图
	 * @param appId	公众号的appId
	 * @param typeReference	泛型类型
	 * @param function	查询数据库函数，外面自己实现好传进来，这里只是调用
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	private <T> Object getConfigFromCache(String redisKey, String field, TypeReference<T> typeReference)
			throws IOException, JsonParseException, JsonMappingException, JsonProcessingException {
		
		Object object = null;
		Map<String, Object> map = pageConfigMap.get(redisKey);
		if (map != null) {
			object = map.get(field);
		}
		return object;
	}
	
	private <T> Object setConfigCache(String redisKey, String field, Supplier<T> supplier) throws JsonProcessingException {
		
		Object object = supplier.get();
		Map<String, Object> configMap = new HashMap<>();
		configMap.put(field, object);
		pageConfigMap.put(redisKey, configMap);
		return object;
		
	}

	/**
	 * 获取物业缴费选款卡配置
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<WuyePayTabs> getWuyePayTabs(String appId) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		
		if (StringUtils.isEmpty(appId)) {
			appId = ConstantWeChat.APPID;
		}
		
		final String sysAppId = appId;
		TypeReference typeReference = new TypeReference<List<WuyePayTabs>>() {};
		List<WuyePayTabs> tabList = (List<WuyePayTabs>) getConfigFromCache(ModelConstant.KEY_TYPE_WUYEPAY_TABS, appId, typeReference);
		if (tabList == null) {
			Sort sort = new Sort(Direction.ASC, "sort");
			Supplier<List<WuyePayTabs>> supplier = ()-> wuyePayTabsRepository.findByAppId(sysAppId, sort);
			tabList = (List<WuyePayTabs>) setConfigCache(ModelConstant.KEY_TYPE_WUYEPAY_TABS, appId, supplier);
		}
		return tabList;
	}

	/**
	 * 更新物业缴费选款卡配置
	 */
	@Override
	public void updatePageConfig() {

		pageConfigMap.clear();
	}

	@Override
	public List<BottomIcon> filterBottomIcon(User user, List<BottomIcon>iconList) {
		
		List<BottomIcon> showList = new ArrayList<>();
		showList.addAll(iconList);
		
		String sectId = user.getSectId();
		
		Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(ModelConstant.KEY_CS_SERVED_SECT + sectId);
		logger.info("filterBottomIcon , map : " + map);
		
		if (map.size()>0) {
			return showList;
		}
		
		int index = Integer.MAX_VALUE;
		for (int i = 0; i < showList.size(); i++) {
			BottomIcon bottomIcon = showList.get(i);
			if ("customService".equals(bottomIcon.getIconKey())) {
				index = i;
				break;
			}
		}
		if (index != Integer.MAX_VALUE) {
			showList.remove(index);
		}
		return showList;
	}
	
	

}
