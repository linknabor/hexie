package com.yumu.hexie.service.user.impl;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.promotion.coupon.CouponRepository;
import com.yumu.hexie.model.promotion.coupon.CouponRule;
import com.yumu.hexie.model.promotion.coupon.CouponRuleRepository;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.promotion.coupon.CouponSeedRepository;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.user.CouponQueueTask;

@Service
public class CouponQueueTaskImpl implements CouponQueueTask {

	private static Logger logger = LoggerFactory.getLogger(CouponQueueTaskImpl.class);
	
	@Autowired
	private MaintenanceService maintenanceService;
	@Autowired
	private CouponRuleRepository couponRuleRepository;
	@Autowired
	private CouponSeedRepository couponSeedRepository;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 异步统计领取的红包数量，并且更新数据库。所有用户领取的操作都放入一个队列统一操作，这样数据库不会有脏读
	 * 实时领取的数量限制由redis操作，redis具有原子性，不会造成
	 */
	@Override
	@Async("taskExecutor")
	public void gainCouponAsync() {
		
		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String ruleId = redisTemplate.opsForList().leftPop(ModelConstant.KEY_COUPON_GAIN_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(ruleId)) {
					continue;
				}
				boolean isSuccess = false;
				try {
					logger.info("start to consume gain coupon queue, ruleId : " + ruleId);
				
					//1.更新couponRule
					Optional<CouponRule> optional = couponRuleRepository.findById(Long.valueOf(ruleId));
					CouponRule couponRule  = null;
					if (optional.isPresent()) {
						couponRule = optional.get();
						couponRule.addReceived();
						couponRuleRepository.save(couponRule);
						
						//2.更新couponSeed
						Optional<CouponSeed> seedOptional = couponSeedRepository.findById(couponRule.getSeedId());
						if (seedOptional.isPresent()) {
							CouponSeed couponSeed = seedOptional.get();
							couponSeed.setReceivedCount(couponRule.getReceivedCount());
							couponSeed.getSeedStr();
							couponSeedRepository.save(couponSeed);
							
						}
					}
					isSuccess = true;
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
						
				if (!isSuccess) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_COUPON_GAIN_QUEUE, ruleId);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

}
