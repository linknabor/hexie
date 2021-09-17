package com.yumu.hexie.service.shequ.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieHouses;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.event.dto.BaseEventDTO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.shequ.WuyeQueueTask;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.BindHouseQueue;

@Service
public class WuyeQueueTaskImpl implements WuyeQueueTask {
	
	private static Logger logger = LoggerFactory.getLogger(WuyeQueueTaskImpl.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private WuyeService wuyeService;
	@Autowired
	private UserService userService;
	@Autowired
	private MaintenanceService maintenanceService;
	
	/**
	 * 绑定房屋队列。在缴费后调用，做异步绑定，缴费完了只要显示缴费金额即可，绑定在后台操作
	 * @throws InterruptedException 
	 */
	@Override
	@Async("taskExecutor")
	public void bindHouseByTrade() {
		
		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_BIND_HOUSE_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				BindHouseQueue queue = objectMapper.readValue(json, new TypeReference<BindHouseQueue>(){});
				
				logger.info("strat to consume bindHouseByTrade queue : " + queue);
				
				User user = queue.getUser();
				
				//该用户是某小区停车费收费员，这个用户ID统统出队，不作绑房屋操作
				if (4731 == user.getId()) {
					continue;
				}
				int totalFailed = 0;
				boolean isSuccess = false;
				
				while(!isSuccess && totalFailed < 3) {
					
					BaseResult<HexieHouses> baseResult = WuyeUtil.bindByTrade(user, queue.getTradeWaterId());
					if (baseResult.isSuccess()) {
						HexieHouses hexieHouses = baseResult.getData();
						List<HexieHouse> houseList = hexieHouses.getHouses();
						
						if (houseList != null && houseList.size() > 0) {
							for (HexieHouse hexieHouse : houseList) {
								HexieUser hexieUser = new HexieUser();
								BeanUtils.copyProperties(hexieHouse, hexieUser);
								wuyeService.setDefaultAddress(user, hexieUser);	//里面已经开了事务，外面不需要。跨类调，事务生效
							}
							isSuccess = true;
						} else {
							
							logger.info("交易[" + queue.getTradeWaterId() + "] 未查询到对应房屋，可能还未入账。");
							totalFailed++;
							Thread.sleep(10000);
						}
						
					} else if ("04".equals(baseResult.getResult())) {
						//已绑定过的，直接消耗队列，不处理
						logger.info("交易[" + queue.getTradeWaterId() + "] 已绑定房屋.");
						isSuccess = true;
					} else if ("05".equals(baseResult.getResult())) {
						logger.info("交易[" + queue.getTradeWaterId() + "] 用户当前绑定房屋与已绑定房屋不属于同个小区，暂不支持此功能。.");
						isSuccess = true;
					} else {
						logger.error("用户：" + user.getId() + " + 交易[" + queue.getTradeWaterId() + "]，绑定房屋失败！");
						totalFailed++;
						Thread.sleep(10000);
					}
				}
				
				if (!isSuccess && totalFailed >= 3) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_BIND_HOUSE_QUEUE, json);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 扫开票二维码关注事件
	 */
	@SuppressWarnings("unchecked")
	@Async("taskExecutor")
	@Override
	public void eventScanSubscribe4Invoice() {
		
		while (true) {
			try {

				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = (String) redisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_SCAN_SUBSCRIBE_QUEUE,
						30, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				Map<String, String> map = objectMapper.readValue(json, Map.class);
				logger.info("strat to consume eventScanSubscribe4Invoice queue : " + map);

				String appId = map.get("appId");
				String openid = map.get("openid");
				String eventKey = map.get("eventKey");
				
				User user = userService.multiFindByOpenId(openid);

				BaseEventDTO baseEventDTO = new BaseEventDTO();
				baseEventDTO.setAppId(appId);
				baseEventDTO.setOpenid(openid);
				baseEventDTO.setEventKey(eventKey);
				baseEventDTO.setUser(user);
				
				boolean isSuccess = false;	//投放会员卡是否成功
				try {
					wuyeService.scanEvent4Invoice(baseEventDTO);
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}
				
				if (!isSuccess) {
					logger.info("eventScanSubscribe4Invoice queue consume failed !, repush into the queue. json : " + json);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_SCAN_SUBSCRIBE_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	@Async("taskExecutor")
	@Override
	public void eventScan4Invoice() {
		
		while (true) {
			try {

				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = (String) redisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_SCAN_QUEUE, 30, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				Map<String, String> map = objectMapper.readValue(json, Map.class);
				logger.info("strat to consume eventScan4Invoice queue : " + map);

				String appId = map.get("appId");
				String openid = map.get("openid");
				String eventKey = map.get("eventKey");
				
				User user = userService.multiFindByOpenId(openid);

				BaseEventDTO baseEventDTO = new BaseEventDTO();
				baseEventDTO.setAppId(appId);
				baseEventDTO.setOpenid(openid);
				baseEventDTO.setEventKey(eventKey);
				baseEventDTO.setUser(user);
				
				boolean isSuccess = false;	//投放会员卡是否成功
				try {
					isSuccess = wuyeService.scanEvent4Invoice(baseEventDTO);
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}
				
				if (!isSuccess) {
					logger.info("eventScan4Invoice queue consume failed !, repush into the queue. json : " + json);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_SCAN_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}

}
