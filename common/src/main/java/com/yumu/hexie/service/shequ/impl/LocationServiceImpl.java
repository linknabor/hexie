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
import com.yumu.hexie.integration.baidu.resp.GeoCodeRespV2;
import com.yumu.hexie.integration.baidu.vo.RegionVo;
import com.yumu.hexie.integration.baidu.vo.RegionVo.RegionSelection;
import com.yumu.hexie.integration.wuye.WuyeUtil2;
import com.yumu.hexie.integration.wuye.resp.RadiusSect;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.region.RegionUrlRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.LocationService;
import com.yumu.hexie.service.shequ.req.RadiusSectReq;
import com.yumu.hexie.service.shequ.vo.LocationVO;

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
	@Autowired
	private WuyeUtil2 wuyeUtil2;
	
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
		
		logger.info("location cache init finished !, regionUrlMap : " + regionUrlMap);
		logger.info("codeUrlMap : " + codeUrlMap);
	}
	
	@Override
	public RegionVo getRegionUrl(String coordinate) {
		
		RegionVo vo = new RegionVo();
		if (!StringUtils.isEmpty(coordinate)) {
			String bdCoordinate = convertWGS842Bd(coordinate);
			String name = baiduMapUtil.findByBaiduGetCity(bdCoordinate);
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
	
	/**
	 * 根据用户地理位置获取附近的小区
	 * @param coordinate
	 * @throws Exception 
	 */
	@Override
	public List<RadiusSect> querySectNearby(User user, RadiusSectReq radiusSectReq) throws Exception {
	
		//先将WGS84转换百度地图坐标系
		String converted = convertWGS842Bd(radiusSectReq.getCoordinate());	
		radiusSectReq.setBdCoordinate(converted);
		return wuyeUtil2.querySectNearby(user, radiusSectReq).getData();
	}
	
	/**
	 * 将WGS84坐标系转换成BD09II
	 * @return
	 */
	private String convertWGS842Bd(String wgsCoordinate) {
		return baiduMapUtil.findByCoordinateGetBaidu(wgsCoordinate);
	}
	
	/**
	 * 获取用户当前位置信息和附近小区信息
	 * @param user
	 * @param radiusSectReq
	 * @return
	 * @throws Exception
	 */
	@Override
	public LocationVO getLocationInfo(User user, RadiusSectReq radiusSectReq) throws Exception {
		
		//先将WGS84转换百度地图坐标系
		String bdCoordinate = convertWGS842Bd(radiusSectReq.getCoordinate());
		radiusSectReq.setBdCoordinate(bdCoordinate);
		//获取附近小区
		List<RadiusSect> sectList = wuyeUtil2.querySectNearby(user, radiusSectReq).getData();
		GeoCodeRespV2 geoCodeResp = baiduMapUtil.getLocationByCoordinateV2(bdCoordinate);
		String province = "";
		String formattedAddress = "";
		if (geoCodeResp != null) {
			if (geoCodeResp.getResult().getAddressComponent() != null) {
				province = geoCodeResp.getResult().getAddressComponent().getProvince();
			}
			formattedAddress = geoCodeResp.getResult().getFormatted_address();
		}
		RegionVo regionVo = getRegionUrlFromCache(province);
		LocationVO locationVO = new LocationVO();
		locationVO.setProvince(regionVo.getAddress());
		locationVO.setProvinceAbbr(regionVo.getShowAddress());
		locationVO.setSectList(sectList);
		locationVO.setFormattedAddress(formattedAddress);
		return locationVO;
	}

}
