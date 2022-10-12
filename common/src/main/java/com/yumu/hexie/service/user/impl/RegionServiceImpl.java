package com.yumu.hexie.service.user.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.RegionService;
import com.yumu.hexie.vo.QQMapVO;

@Service("regionService")
public class RegionServiceImpl implements RegionService{
	
	private static Logger logger = LoggerFactory.getLogger(RegionServiceImpl.class);
	
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public List<Region> findByNameAndParentId(String name,Long parentId) {
		return regionRepository.findAllByParentIdAndName(parentId, name);
	}

	@Override
	public Region saveRegion(Region r) {
		r.setLatitude(0.0);
		r.setLongitude(0.0);
		return regionRepository.save(r);
	}

	@Override
	public List<Region> getRegionAll() {
		return regionRepository.findAll();
	}

	@Override
	public Region getRegionInfoById(long id) {
		return regionRepository.findById(id);
	}

	@Override
	public List<Region> findAllBySectId(String sectId) {
		return regionRepository.findAllBySectId(sectId);
	}

	@Override
	public List<Region> findByNameLikeAndType(String name) {
		
		Pageable pageable = PageRequest.of(0, 30);
		List<Region> regionList = regionRepository.findByRegionTypeAndNameContaining(ModelConstant.REGION_XIAOQU, name, pageable);
		return regionList;
	}
	
	@Override
	public List<Region> findByRgroupOwner(User user) {
		
		String key = ModelConstant.KEY_RGROUP_OWNER_REGION + user.getMiniopenid();	//这里用小程序的openid。如果小程序没有绑定公众号，则unionid可能是空的
		String regionStr = stringRedisTemplate.opsForValue().get(key);
		TypeReference<List<Region>> typeReference = new TypeReference<List<Region>>() {};
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		List<Region> regionList = null;
		try {
			regionList = objectMapper.readValue(regionStr, typeReference);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			regionList = new ArrayList<>();
		}
		return regionList;
	}
	
	/**
	 * 缓存团长的服务小区
	 * @param user
	 * @return
	 */
	@Override
	public void saveOwnerServiceArea(User user, Region region) {
		
		String key = ModelConstant.KEY_RGROUP_OWNER_REGION + user.getMiniopenid();
		String regionStr = stringRedisTemplate.opsForValue().get(key);
		TypeReference<List<Region>> typeReference = new TypeReference<List<Region>>() {};
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		List<Region> regionList = null;
		try {
			if (!StringUtils.isEmpty(regionStr)) {
				regionList = objectMapper.readValue(regionStr, typeReference);
			}
			if (regionList == null) {
				regionList = new ArrayList<>();
			}
			boolean cached = false;
			for (Region cachedRegion : regionList) {
				if (cachedRegion.getId() == region.getId()) {
					cached = true;
					break;
				}
			}
			if (!cached) {
				regionList.add(region);
			}
			String updatedStr = objectMapper.writeValueAsString(regionList);
			stringRedisTemplate.opsForValue().set(key, updatedStr);
			
		} catch (Exception e) {
			throw new BizValidateException(e.getMessage(), e);
		}
		
	}
	
	/**
	 * 缓存团长的服务小区
	 * @param user
	 * @return
	 */
	@Override
	public void delOwnerServiceArea(User user, long regionId) {
		
		String key = ModelConstant.KEY_RGROUP_OWNER_REGION + user.getMiniopenid();
		String regionStr = stringRedisTemplate.opsForValue().get(key);
		TypeReference<List<Region>> typeReference = new TypeReference<List<Region>>() {};
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		if (regionId == 0l) {
			return;
		}
		if (StringUtils.isEmpty(regionStr)) {
			return;
		}
		try {
			int index = -1;
			List<Region> regionList = objectMapper.readValue(regionStr, typeReference);
			for (int i = 0; i < regionList.size(); i++) {
				Region region = regionList.get(i);
				if (regionId == region.getId()) {
					index = i;
					break;
				}
			}
			if (index == -1) {
				return;
			}
			regionList.remove(index);
			String updatedStr = objectMapper.writeValueAsString(regionList);
			stringRedisTemplate.opsForValue().set(key, updatedStr);
			
		} catch (Exception e) {
			throw new BizValidateException(e.getMessage(), e);
		}
		
	}
	
	/**
	 * 团长新建小区
	 * @param user
	 * @param map
	 */
	@Transactional
	@Override
	public Region createSect(User user, QQMapVO mapVO) {
		
		logger.info("createSect : " + mapVO);
		
		Assert.hasText(mapVO.getName(), "小区名称不能为空");
		Assert.hasText(mapVO.getAddress(), "小区地址不能为空");
		
		String provinceName = mapVO.getProvince();
		Region province = null;
		if (provinceName.contains("上海")) {
			provinceName = "上海";
		}
		if (provinceName.contains("北京")) {
			provinceName = "北京";
		}
		if (provinceName.contains("天津")) {
			provinceName = "天津";
		}
		if (provinceName.contains("重庆")) {
			provinceName = "重庆";
		}
		List<Region> provinceList = regionRepository.findByNameAndRegionType(provinceName, ModelConstant.REGION_PROVINCE);
		if (provinceList == null || provinceList.isEmpty()) {
			province = new Region();
			province.setName(provinceName);
			province.setParentName("中国");
			province.setParentId(1);
			province.setRegionType(ModelConstant.REGION_PROVINCE);
			province.setDescription("created by " + user.getId());
			regionRepository.save(province);
		} else {
			province = provinceList.get(0);
		}
		
		String cityName = mapVO.getCity();
		Region city = null;
		List<Region> cityList = regionRepository.findByNameAndRegionType(cityName, ModelConstant.REGION_CITY);
		if (cityList == null || cityList.isEmpty()) {
			city = new Region();
			city.setName(cityName);
			city.setParentName(province.getName());
			city.setParentId(province.getId());
			city.setRegionType(ModelConstant.REGION_CITY);
			city.setDescription("created by " + user.getId());
			regionRepository.save(city);
		} else {
			city = cityList.get(0);
		}
		
		String distName = mapVO.getDistrict();
		Region dist = null;
		List<Region> distList = regionRepository.findByNameAndRegionType(distName, ModelConstant.REGION_COUNTY);
		if (distList == null || distList.isEmpty()) {
			dist = new Region();
			dist.setName(cityName);
			dist.setParentName(city.getName());
			dist.setParentId(city.getId());
			dist.setRegionType(ModelConstant.REGION_COUNTY);
			dist.setDescription("created by " + user.getId());
			regionRepository.save(dist);
		} else {
			dist = distList.get(0);
		}
		
		Region region = new Region();
		region.setName(mapVO.getName());
		region.setParentName(mapVO.getDistrict());
		region.setParentId(dist.getId());
		region.setRegionType(ModelConstant.REGION_XIAOQU);
		region.setLatitude(Double.valueOf(mapVO.getLatitude()));
		region.setLongitude(Double.valueOf(mapVO.getLongitude()));
		region.setXiaoquAddress(mapVO.getAddress());
		region.setDescription("created by " + user.getId());
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		String regionStr;
		try {
			regionStr = objectMapper.writeValueAsString(region);
		} catch (JsonProcessingException e) {
			logger.info(e.getMessage(), e);
			throw new BizValidateException(e.getMessage());
		}
		String regionKey = ModelConstant.KEY_CREATE_NEW_REGION_LOCK + region.getName();
		Boolean absent = stringRedisTemplate.opsForValue().setIfAbsent(regionKey, regionStr);
		if(absent) {
			regionRepository.save(region);
		} else {
			
			List<Region> regionList = regionRepository.findByNameAndRegionType(region.getName(), ModelConstant.REGION_XIAOQU);
			if (regionList == null || regionList.size() == 0) {
				throw new BizValidateException("前方拥挤，请稍后再试。");
			}
			if (regionList.size()>0) {
				for (Region currRegion : regionList) {
					if (!StringUtils.isEmpty(currRegion.getDescription())) {
						region = currRegion;
						break;
					}
				}
			}
		}
		saveOwnerServiceArea(user, region);
		return region;
		
	}

}
