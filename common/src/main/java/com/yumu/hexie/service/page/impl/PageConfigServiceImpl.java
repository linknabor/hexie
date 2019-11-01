package com.yumu.hexie.service.page.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.JacksonJsonUtil;
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
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.page.PageConfigService;

@Service("pageConfigService")
public class PageConfigServiceImpl implements PageConfigService {

	@Inject
	private PageConfigViewRepository pageConfigViewRepository;
	@Inject
	private BannerRepository bannerRepository;

	@Autowired
	private BottomIconRepository bottomIconRepository;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private QrCodeRepository qrCodeRepository;
	@Autowired
	private BgImageRepository bgImageRepository;

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
		Supplier<PageConfigView> supplier = ()-> pageConfigViewRepository.findByTempKeyAndAppId(key, sysAppId);
		TypeReference typeReference = new TypeReference<PageConfigView>() {};
		PageConfigView pageConfigView = (PageConfigView) getConfigFromCache(ModelConstant.KEY_TYPE_PAGECONFIG, appId, typeReference, supplier);
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
		Sort sort = new Sort(Direction.ASC, "sort");
		final String sysAppId = appId;
		Supplier<List<BottomIcon>> supplier = ()-> bottomIconRepository.findByAppId(sysAppId, sort);
		TypeReference typeReference = new TypeReference<List<BottomIcon>>() {};
		List<BottomIcon> iconList = (List<BottomIcon>) getConfigFromCache(ModelConstant.KEY_TYPE_BOTTOM_ICON, appId, typeReference, supplier);
		return iconList;
	}

	/**
	 * 更新bottomicon缓存
	 */
	@Override
	public void updateBottomIcon() throws JsonProcessingException {

		redisTemplate.expire(ModelConstant.KEY_TYPE_BOTTOM_ICON, 1l, TimeUnit.MILLISECONDS); // 先把原来的过期
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		Sort sort = new Sort(Direction.ASC, "appId", "sort");
		List<BottomIcon> iconList = bottomIconRepository.findAll(sort);
		if (iconList == null || iconList.isEmpty()) {
			throw new BizValidateException("尚未配置任何bottom icon.");
		}
		Map<String, List<BottomIcon>> iconMap = new HashMap<String, List<BottomIcon>>();
		for (BottomIcon bottomIcon : iconList) {
			if (!iconMap.containsKey(bottomIcon.getAppId())) {
				List<BottomIcon> list = new ArrayList<>();
				list.add(bottomIcon);
				iconMap.put(bottomIcon.getAppId(), list);
			} else {
				List<BottomIcon> list = iconMap.get(bottomIcon.getAppId());
				list.add(bottomIcon);
			}
		}

		Map<String, String> strMap = new HashMap<>();
		Iterator<Map.Entry<String, List<BottomIcon>>> it = iconMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<BottomIcon>> entry = it.next();
			String key = entry.getKey();
			List<BottomIcon> value = entry.getValue();
			String valueStr = objectMapper.writeValueAsString(value);
			strMap.put(key, valueStr);
		}
		redisTemplate.opsForHash().putAll(ModelConstant.KEY_TYPE_BOTTOM_ICON, strMap);
	}
	
	/**
	 * 根据不同sys动态获取空白背景地图
	 * @param appId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public BgImage getBgImage(String imageType, String appId) throws JsonParseException, JsonMappingException, IOException {

		if (StringUtil.isEmpty(appId)) {
			appId = ConstantWeChat.APPID;
		}
		Sort sort = new Sort(Direction.ASC, "type");
		final String sysAppId = appId;
		Supplier<List<BgImage>> supplier = ()-> bgImageRepository.findByAppId(sysAppId, sort);
		TypeReference typeReference = new TypeReference<List<BgImage>>() {};
		List<BgImage> imageList = (List<BgImage>) getConfigFromCache(ModelConstant.KEY_TYPE_BGIMAGE, appId, typeReference, supplier);
		return imageList;
	}
	
	//TODO
	public void updateBgImage(@ModelAttribute(Constants.USER)User user, @PathVariable String type) {
		
		
		String keyType = "0";
		switch (type) {
		case ModelConstant.BG_IMAGE_TYPE_ORDER:
			keyType = ModelConstant.KEY_TYPE_BG_IMAGE_ORDER;
			break;
		case ModelConstant.BG_IMAGE_TYPE_GROUP_ORDER:
			keyType = ModelConstant.KEY_TYPE_BG_IMAGE_GROUP_ORDER;
			break;
		case ModelConstant.BG_IMAGE_TYPE_REPAIR_ORDER:
			keyType = ModelConstant.KEY_TYPE_BG_IMAGE_REPAIR_ORDER;
			break;
		case ModelConstant.BG_IMAGE_TYPE_THREAD:
			keyType = ModelConstant.KEY_TYPE_BG_IMAGE_THREAD;
			break;
		case ModelConstant.BG_IMAGE_TYPE_BIND_HOUSE:
			keyType = ModelConstant.KEY_TYPE_BG_IMAGE_BIND_HOUSE;
			break;
		case ModelConstant.BG_IMAGE_TYPE_RESERVATION:
			keyType = ModelConstant.KEY_TYPE_BG_IMAGE_RESERVATION;
			break;
		default:
			break;
		}
		
		TypeReference<BgImage> typeReference = new TypeReference<BgImage>() {};
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		BgImage bgImage = new BgImage();
		String obj = (String) redisTemplate.opsForHash().get(keyType, appId);
		if (!StringUtils.isEmpty(obj)) {
			bgImage = objectMapper.readValue(obj, typeReference);
		}
		if (!StringUtils.isEmpty(bgImage.getId())) {
			bgImage = bgImageRepository.findByTypeAndAppId(type, appId);
			if (!StringUtils.isEmpty(bgImage.getId())) {
				savePageView2HashCache(keyType, appId, bgImage);
			}
		}
		return bgImage;
	}
	
	//TODO
	public void updateBgImage(@ModelAttribute(Constants.USER)User user, @PathVariable String type) {
		
		
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
		Supplier<QrCode> supplier = ()->qrCodeRepository.findByFromSys(sysAppId);
		
		TypeReference typeReference = new TypeReference<QrCode>() {};
		QrCode qrCode = (QrCode) getConfigFromCache(ModelConstant.KEY_TYPE_QRCODE, appId, typeReference, supplier);
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
		Sort sort = new Sort(Direction.ASC, "sortNo");
		
		final String sysAppId = appId;
		Supplier<List<Banner>> supplier = ()-> bannerRepository.findByBannerTypeAndStatusAndRegionTypeAndAppId(bannerType, ModelConstant.BANNER_STATUS_VALID, 
				ModelConstant.REGION_ALL, sysAppId, sort);

		String filed = appId + "_" + bannerType;
		TypeReference typeReference = new TypeReference<List<Banner>>() {};
		List<Banner> bannerList = (List<Banner>) getConfigFromCache(ModelConstant.KEY_TYPE_BANNER, filed, typeReference, supplier);
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
	private <T> Object getConfigFromCache(String redisKey, String filed, TypeReference<T> typeReference, Supplier<T> supplier)
			throws IOException, JsonParseException, JsonMappingException, JsonProcessingException {
		
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		Object object = null;
		String objStr = (String) redisTemplate.opsForHash().get(redisKey, filed);
		if (!StringUtils.isEmpty(objStr)) {
			String valueStr = objStr.replace("[", "").replace("]", "");
			if (!StringUtils.isEmpty(valueStr)) {
				object = objectMapper.readValue(objStr, typeReference);
			}
		}
		if (object == null) {
			object = supplier.get();
			objStr = objectMapper.writeValueAsString(object);
			redisTemplate.opsForHash().put(redisKey, filed, objStr);
		}
		return object;
	}
	
	

}
