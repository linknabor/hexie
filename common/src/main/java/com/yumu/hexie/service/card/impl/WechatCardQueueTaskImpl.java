package com.yumu.hexie.service.card.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wuye.vo.RefundDTO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.card.dto.EventGetCardDTO;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.card.dto.EventUpdateCardDTO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.card.WechatCardQueueTask;
import com.yumu.hexie.service.card.WechatCardService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.shequ.impl.WuyeQueueTaskImpl;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.vo.AddPointQueue;

@Service
public class WechatCardQueueTaskImpl implements WechatCardQueueTask {

	private static Logger logger = LoggerFactory.getLogger(WuyeQueueTaskImpl.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private WechatCardService wechatCardService;
	@Autowired
	private PointService pointService;
	@Autowired
	private SystemConfigService systemConfigService;

	/**
	 * 关注事件 1.发送关注客服消息，推送会员卡 2.记录推送出去的会员卡到表里
	 */
	@SuppressWarnings("unchecked")
	@Async
	@Override
	public void eventSubscribe() {

		while (true) {
			try {

				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_SUBSCRIBE_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				Map<String, String> map = objectMapper.readValue(json, Map.class);
				logger.info("strat to consume subscribe event queue : " + map);

				String appId = map.get("appId");
				String openid = map.get("openid");
				
				//校验当前公众号是否卡通了卡券服务
				if (!systemConfigService.isCardServiceAvailable(appId)) {
					continue;
				}
				User user = new User();
				user.setOpenid(openid);
				user.setAppId(appId);

				EventSubscribeDTO eventSubscribeDTO = new EventSubscribeDTO();
				eventSubscribeDTO.setUser(user);
				boolean isSuccess = false;
				try {
					wechatCardService.eventSubscribe(eventSubscribeDTO);
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}

				if (!isSuccess) {
					logger.info("subscribe event consume failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_SUBSCRIBE_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	/**
	 * 领卡事件 根据推送的cardCode查看卡是否已经创建，没创建的建新卡
	 */
	@Async
	@Override
	public void eventUserGetCard() {

		while (true) {
			try {

				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_GETCARD_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}

				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				EventGetCardDTO eventGetCardDTO = objectMapper.readValue(json, EventGetCardDTO.class);
				logger.info("strat to consume getCad event queue : " + eventGetCardDTO);

				boolean isSuccess = false;
				try {
					wechatCardService.eventGetCard(eventGetCardDTO);
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}

				if (!isSuccess) {
					logger.info("subscribe event failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_GETCARD_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Async
	@Override
	public void updatePointAsync() {
		
		while (true) {
			try {

				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_ADD_POINT_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}

				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				AddPointQueue addPointQueue = objectMapper.readValue(json, AddPointQueue.class);
				logger.info("strat to consume addpoint queue : " + addPointQueue);

				boolean isSuccess = false;
				try {
					pointService.updatePoint(addPointQueue.getUser(), addPointQueue.getPoint(), addPointQueue.getKey());
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}

				if (!isSuccess) {
					logger.info("add point failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_ADD_POINT_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	
	@Async
	@Override
	public void wuyeRefund() {
		
		while (true) {
			try {

				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_WUYE_REFUND_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}

				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				RefundDTO refundDTO = objectMapper.readValue(json, RefundDTO.class);
				logger.info("strat to consume wuyeRefund queue : " + refundDTO);
				String refundAmt = "-" + refundDTO.getTranAmt();	//退款传入负数
				boolean isSuccess = false;
				try {
					User user = new User();
					user.setWuyeId(refundDTO.getWuyeId());
					pointService.updatePoint(user, refundAmt, "wuyeRefund-" + refundDTO.getTradeWaterId());
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}

				if (!isSuccess) {
					logger.info("reduce point failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_WUYE_REFUND_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}

	@Override
	public void eventUpdateCard() {
		
		while (true) {
			try {

				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_UPDATECARD_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}

				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				EventUpdateCardDTO eventUpdateCardDTO = objectMapper.readValue(json, EventUpdateCardDTO.class);
				logger.info("strat to consume updateCard event queue : " + eventUpdateCardDTO);

				boolean isSuccess = false;
				try {
					wechatCardService.eventUpdateCard(eventUpdateCardDTO);
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}

				if (!isSuccess) {
					logger.info("subscribe event failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_GETCARD_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}

}
