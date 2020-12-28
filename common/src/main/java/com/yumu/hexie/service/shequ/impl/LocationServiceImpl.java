package com.yumu.hexie.service.shequ.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.baidu.BaiduMapUtil;
import com.yumu.hexie.integration.baidu.vo.RegionVo;
import com.yumu.hexie.integration.baidu.vo.RegionVo.RegionSelection;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.region.RegionUrlRepository;
import com.yumu.hexie.service.shequ.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

	private static Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);
	
	private static List<RegionSelection> regionShowList = new ArrayList<>();	//前端显示用，里面存行政区域的缩写比如上海，江苏
	
	private static Map<String, RegionUrl> regionUrlMap = new HashMap<>();	//key为省市全程比如 上海市，广西壮族自治区,  value为对应DB的值

	public static Map<String, RegionUrl> codeUrlMap = new HashMap<>();	//key为 code, value为对应DB的值
	
	private static final String DEFAULT_REGiON = "上海";
	private static final String DEFAULT_REGiON_VALUE = "上海市";
	
	@Autowired
	private BaiduMapUtil baiduMapUtil;
	
	@Autowired
	private RegionUrlRepository regionUrlRepository;
	
	@PostConstruct
	public void initRegionUrlCache() {
		
		List<RegionUrl> list = regionUrlRepository.findAll();
		if (list != null) {
			for (RegionUrl regionUrl : list) {
				regionUrlMap.put(regionUrl.getRegionName(), regionUrl);//上海的有2条，后一条会覆盖前一条，但是value是一样的
				codeUrlMap.put(regionUrl.getRegionCode(), regionUrl);
			}
		}
		Iterator<Entry<String, RegionUrl>> it = regionUrlMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, RegionUrl> entry = it.next();
			RegionSelection selection = new RegionSelection();
			RegionUrl regionUrl = entry.getValue();
			if (regionUrl!=null) {
				selection.setRegionName(regionUrl.getRegionName());
				selection.setShowRegionName(regionUrl.getAbbr());
			}
			regionShowList.add(selection);
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
		vo.setRegionUrl(regionShowList);
		return vo;
	}
	
	/**
	 * 从缓存中取regoinUrl
	 * @param keyName
	 * @return
	 */
	private RegionVo getRegionUrlFromCache(String keyName) {
		
		RegionVo vo = new RegionVo();
		if (regionUrlMap.containsKey(keyName)) {
			vo.setAddress(regionUrlMap.get(keyName).getRegionName());
			vo.setShowAddress(regionUrlMap.get(keyName).getAbbr());
		}else {
			vo.setAddress(DEFAULT_REGiON);
			vo.setShowAddress(DEFAULT_REGiON_VALUE);
		}
		return vo;
		
	}
	
	@Override
	public RegionUrl getRegionUrlByName(String regionName) {
		
		return regionUrlMap.get(regionName);
		
	}

	@Override
	public void refreshCache() {
		
		regionShowList = new ArrayList<>();
		regionUrlMap = new HashMap<>();
		codeUrlMap = new HashMap<>();
		initRegionUrlCache();
		
	}

	public static Map<String, RegionUrl> getCodeUrlMap() {
		return codeUrlMap;
	}
	
	
	

}
