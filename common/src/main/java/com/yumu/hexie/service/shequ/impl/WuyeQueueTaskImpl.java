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
					
					BaseResult<HexieHouses> baseResult = WuyeUtil.bindByTrade(user, queue.getTradeWaterId(), queue.getBindType());
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
				if (user == null) {
					user = new User();
				}

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
				if (user == null) {
					user = new User();
				}

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
	
	/**
	 * 注册&绑定房屋
	 * 申请电子发票后的异步操作:
	 * 1.完成用户注册（如果未注册的话）
	 * 2.帮助用户绑定房屋（根据申请发票关联交易的房屋）
	 * 
	 * @throws InterruptedException 
	 */
	@Override
	@Async("taskExecutor")
	public void registerAndBind() {
		
		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_REGISER_AND_BIND_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				BindHouseQueue queue = objectMapper.readValue(json, new TypeReference<BindHouseQueue>(){});
				
				logger.info("strat to consume registerAndBind queue : " + queue);
				User user = queue.getUser();	//这里的user有3种情况：1）没有wuyeId的，从来没登陆过的。2）没有手机号码的，已生成用户但未注册的。3）有手机的
				
				User dbUser = userService.multiFindByOpenId(user.getOpenid());	//这时候可能已经产生了user或者没有user,因为跟开票是异步进行的
				User savedUser = null;
				boolean isSuccess = false;
				try {
					if (dbUser == null) {	//对于没有wuyeId的，需要从community获取新的wuyeId
						BaseResult<HexieUser> baseResult = WuyeUtil.userLogin(user);
						if (baseResult.isSuccess()) {
							String wuyeId = baseResult.getData().getUser_id();
							user.setWuyeId(wuyeId);
							String mobile = user.getTel();
							String prefix = mobile.substring(0, 3);
							String suffix = mobile.substring(7, mobile.length());
							String name = prefix + "***" + suffix;	//使用手机号虚构一个名称，中间打码
							user.setName(name);
							savedUser = userService.simpleRegister(user);
							isSuccess = true;
						}
					} else if (StringUtils.isEmpty(dbUser.getWuyeId())) {
						BaseResult<HexieUser> baseResult = WuyeUtil.userLogin(user);
						if (baseResult.isSuccess()) {
							String wuyeId = baseResult.getData().getUser_id();
							String mobile = user.getTel();
							dbUser.setWuyeId(wuyeId);
							dbUser.setTel(mobile);
							savedUser = userService.simpleRegister(dbUser);
							isSuccess = true;
						}
						
					} else if (StringUtils.isEmpty(dbUser.getTel())) {
						dbUser.setTel(user.getTel());
						savedUser = userService.simpleRegister(dbUser);
						isSuccess = true;
					} else if (!StringUtils.isEmpty(dbUser.getTel())){	//这种情况只要绑定房子即可
						isSuccess = true;
					}
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}
				
				if (isSuccess) {
					wuyeService.bindHouseByTradeAsync("1", savedUser, queue.getTradeWaterId(), "5");	//绑定房屋队列
				}
				if (!isSuccess) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_REGISER_AND_BIND_QUEUE, json);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
