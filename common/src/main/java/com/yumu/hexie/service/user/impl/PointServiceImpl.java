package com.yumu.hexie.service.user.impl;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.PointRecord;
import com.yumu.hexie.model.user.PointRecordRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.vo.PointQueue;

@Service("pointService")
public class PointServiceImpl implements PointService {
	
	private static Logger logger = LoggerFactory.getLogger(PointServiceImpl.class);

	@Inject
	private PointRecordRepository pointRecordRepository;
	@Inject
	private UserRepository userRepository;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public void addLvdou(User user, int point, String key) {
		if(StringUtil.isNotEmpty(key)) {
			List<PointRecord> rs = pointRecordRepository.findAllByKeyStr(key);
			if(rs != null&&rs.size()>0) {
				return;
			}
		}
		PointRecord pr = new PointRecord();
		pr.setType(ModelConstant.POINT_TYPE_LVDOU);
		pr.setUserId(user.getId());
		pr.setPoint(point);
		pr.setKeyStr(key);
		pointRecordRepository.save(pr);
		user.setLvdou(user.getLvdou()+point);
		userRepository.save(user);
	}

	@Override
	public void addZhima(User user, int point, String key) {
		if(StringUtil.isNotEmpty(key)) {
			List<PointRecord> rs = pointRecordRepository.findAllByKeyStr(key);
			if(rs != null&&rs.size()>0) {
				return;
			}
		}
		PointRecord pr = new PointRecord();
		pr.setType(ModelConstant.POINT_TYPE_ZIMA);
		pr.setUserId(user.getId());
		pr.setPoint(point);
		pr.setKeyStr(key);
		pointRecordRepository.save(pr);
		user.setZhima(user.getZhima()+point);
		userRepository.save(user);
	}

	/**
	 * 异步添加芝麻积分
	 */
	@Override
	public void addZhimaAsync(User user, int point, String key) {

		int pointRetryTimes = 0;
		boolean pointSuccess = false;
		
		while(!pointSuccess && pointRetryTimes < 3) {
			
			try {
//				pointService.addZhima(user, 10, "zhima-bill-" + user.getId() + "-" + billId);
				PointQueue pointQueue = new PointQueue();
				pointQueue.setUser(user);
				pointQueue.setPoint(10);
				pointQueue.setType(ModelConstant.POINT_TYPE_ZIMA);
				pointQueue.setKey(key);
				
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(pointQueue);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_POINT_QUEUE, value);
				pointSuccess = true;
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				pointRetryTimes++;
			}
		}
	}

}
