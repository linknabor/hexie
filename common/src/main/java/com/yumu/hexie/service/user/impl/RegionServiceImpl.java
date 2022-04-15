package com.yumu.hexie.service.user.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.user.RegionService;

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
		
		List<Order> orderList = new ArrayList<>();
    	Order order = new Order(Direction.DESC, "id");
    	orderList.add(order);
    	Sort sort = Sort.by(orderList);
		Pageable pageable = PageRequest.of(1, 30, sort);
		return regionRepository.findByNameLikeAndRegionType(name, ModelConstant.REGION_XIAOQU, pageable);
	}
	
	@Override
	public List<Region> findByRgroupOwner(User user) {
		
		String key = ModelConstant.KEY_RGROUP_OWNER_REGION + user.getId();
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

}
