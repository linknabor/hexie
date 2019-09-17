package com.yumu.hexie.service.page.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.Banner;
import com.yumu.hexie.model.view.BannerRepository;
import com.yumu.hexie.model.view.BottomIcon;
import com.yumu.hexie.model.view.BottomIconRepository;
import com.yumu.hexie.model.view.PageConfigView;
import com.yumu.hexie.model.view.PageConfigViewRepository;
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

	@Override
	public List<Banner> queryBannerType(User user, int bannerType) {
		if(user.getProvinceId() != 0){
			return bannerRepository.queryByBannerTypeAndUser(user.getProvinceId(), user.getCityId(), user.getCountyId(), user.getXiaoquId(), bannerType);
		}else{
			return bannerRepository.queryByBannerTypeAndUser(19, user.getCityId(), user.getCountyId(), user.getXiaoquId(), bannerType);		
		}

	}

    @Override
    public String findByTempKey(String key) {
        PageConfigView v = pageConfigViewRepository.findByTempKey(key);
        if(v != null) {
            return v.getPageConfig();
        }
        return "";
    }
    
    @Override
    public List<BottomIcon> getBottomIcon(String iconSys) throws JsonParseException, JsonMappingException, IOException {
 
    	if (StringUtil.isEmpty(iconSys)) {
			iconSys = "_hxm";
		}
    	ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
    	List<BottomIcon> iconList = new ArrayList<>();
    	String iconObj = (String)redisTemplate.opsForHash().get(ModelConstant.KEY_BOTTOM_ICON, iconSys);
    	if (!StringUtils.isEmpty(iconObj)) {
        	TypeReference<List<BottomIcon>> typeReference = new TypeReference<List<BottomIcon>>() {};
        	iconList = objectMapper.readValue(iconObj, typeReference);
		}else {
			Sort sort = new Sort(Direction.ASC , "sort");
			iconList = bottomIconRepository.findByIconSys(iconSys, sort);
			String iconStr = objectMapper.writeValueAsString(iconList);
			redisTemplate.opsForHash().put(ModelConstant.KEY_BOTTOM_ICON, iconSys, iconStr);
		}
    	return iconList;
    }

	@Override
	public void updateBottomIcon() throws JsonProcessingException {

		redisTemplate.expire(ModelConstant.KEY_BOTTOM_ICON, 1l, TimeUnit.MILLISECONDS);	//先把原来的过期
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		Sort sort = new Sort(Direction.ASC , "iconSys", "sort");
		List<BottomIcon> iconList = bottomIconRepository.findAll(sort);
		if (iconList == null || iconList.isEmpty()) {
			throw new BizValidateException("尚未配置任何bottom icon.");
		}
		Map<String, List<BottomIcon>> iconMap = new HashMap<String, List<BottomIcon>>();
		for (BottomIcon bottomIcon : iconList) {
			if (!iconMap.containsKey(bottomIcon.getIconSys())) {
				List<BottomIcon> list = new ArrayList<>();
				list.add(bottomIcon);
				iconMap.put(bottomIcon.getIconSys(), list);
			}else {
				List<BottomIcon> list = iconMap.get(bottomIcon.getIconSys());
				list.add(bottomIcon);
			}
		}
		
		Map<String, String> strMap = new HashMap<>();
		Iterator<Map.Entry<String, List<BottomIcon>>> it = iconMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, List<BottomIcon>> entry = it.next();
			String key = entry.getKey();
			List<BottomIcon> value = entry.getValue();
			String valueStr = objectMapper.writeValueAsString(value);
			strMap.put(key, valueStr);
		}
		redisTemplate.opsForHash().putAll(ModelConstant.KEY_BOTTOM_ICON, strMap);
	}
    

}
