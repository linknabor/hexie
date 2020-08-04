package com.yumu.hexie.service.shequ.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.baidu.BaiduMapUtil;
import com.yumu.hexie.integration.baidu.vo.RegionVo;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.region.RegionUrlRepository;
import com.yumu.hexie.service.shequ.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

	private static Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);
	
	private static Set<String> regionSet = new HashSet<>();
	
	private static List<RegionUrl> regionUrlList = new ArrayList<>();
	
	private static Map<String, RegionUrl> regionUrlMap = new HashMap<>();

	public static Map<String, RegionUrl> codeUrlMap = new HashMap<>();
	
	private static final String DEFAULT_REGiON = "上海市";
	
	@Autowired
	private BaiduMapUtil baiduMapUtil;
	
	@Autowired
	private RegionUrlRepository regionUrlRepository;
	
	@PostConstruct
	public void initRegionUrlCache() {
		
		if (ConstantWeChat.isMainServer()) {	//BK程序不跑下面的队列轮询
    		return;
    	}
		List<RegionUrl> list = regionUrlRepository.findAll();
		if (list != null) {
			regionUrlList = list;
			for (RegionUrl regionUrl : list) {
				regionUrlMap.put(regionUrl.getRegionName(), regionUrl);//上海的有2条，后一条会覆盖前一条，但是value是一样的
				codeUrlMap.put(regionUrl.getRegionCode(), regionUrl);
			}
		}
	}
	
	@Override
	public RegionVo getRegionUrl(String coordinate) {
		
		RegionVo vo = new RegionVo();
		if (!StringUtils.isEmpty(coordinate)) {
			coordinate = baiduMapUtil.findByCoordinateGetBaidu(coordinate);
			String name = baiduMapUtil.findByBaiduGetCity(coordinate);
			logger.info("坐标:" + coordinate + ", 对应地址："+name);
			vo = getRegionUrlFromCache(name);
		}
		vo.setRegionurl(regionUrlList);
		return vo;
	}
	
	/**
	 * 从缓存中取regoinUrl
	 * @param keyName
	 * @return
	 */
	private RegionVo getRegionUrlFromCache(String keyName) {
		
		RegionVo vo = new RegionVo();
		if (regionSet.contains(keyName)) {
			vo.setAddress(keyName);
			
		}else {
			RegionUrl regionUrl = null;
			List<RegionUrl> regionList = regionUrlRepository.findByRegionName(keyName);
			if (regionList != null && !regionUrlList.isEmpty()) {
				regionUrl = regionList.get(0);
			}
			if (regionUrl != null) {
				regionSet.add(regionUrl.getRegionName());
				vo.setAddress(regionUrl.getRegionName());
			}else {
				vo.setAddress(DEFAULT_REGiON);
			}
			
		}
		return vo;
		
	}
	
	@Override
	public RegionUrl getRegionUrlByName(String regionName) {
		
		return regionUrlMap.get(regionName);
		
	}

	@Override
	public void updateRegionUrlCache() {

		initRegionUrlCache();
		
	}

	public static Map<String, RegionUrl> getCodeUrlMap() {
		return codeUrlMap;
	}
	
	
	

}
