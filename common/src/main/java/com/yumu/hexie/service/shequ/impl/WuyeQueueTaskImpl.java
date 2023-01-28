package com.yumu.hexie.service.shequ.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieHouses;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.yanji.YanjiUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.event.dto.BaseEventDTO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.shequ.WuyeQueueTask;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.shequ.req.ReceiptApplicationReq;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.BindHouseQueue;

@Service
public class WuyeQueueTaskImpl implements WuyeQueueTask {
	
	private static Logger logger = LoggerFactory.getLogger(WuyeQueueTaskImpl.class);
	
	@Autowired
	@Qualifier("stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private WuyeService wuyeService;
	@Autowired
	private UserService userService;
	@Autowired
	private MaintenanceService maintenanceService;
	@Value("${yanji.appid:wxb2e5b64025a1a5f8}")
	private String yanjiAppid;
	@Autowired
	private YanjiUtil yanjiUtil;
	
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
						logger.error("用户：" + user.getId() + " 交易[" + queue.getTradeWaterId() + "]，绑定房屋失败！");
						totalFailed++;
						Thread.sleep(10000);
					}
				}
				
//				if (!isSuccess && totalFailed >= 3) {
//					redisTemplate.opsForList().rightPush(ModelConstant.KEY_BIND_HOUSE_QUEUE, json);
//				}
			
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
	public void eventScanSubscribe() {
		
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
				logger.info("strat to consume eventScanSubscribe queue : " + map);

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
				
				String type = "01";
				if (StringUtils.isEmpty(eventKey)) {
					type = "99";
				} else if (eventKey.startsWith("01") || eventKey.startsWith("qrscene_01")) {
					//donothing
				} else if (eventKey.startsWith("02") || eventKey.startsWith("qrscene_02")) {
					type = "02";
				}
				
				boolean isSuccess = false;
				WechatResponse wechatResponse = null;
				try {
					logger.info("event type : " + type + ", common subscribe . " );
					
					if (yanjiAppid.equals(appId)) {
						logger.info("subscribe event 4 yanji. ");
						try {
							yanjiUtil.subsribeEventRequest(json);
							isSuccess = true;
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
						
					} else {
						try {
							//所有关注“合协社区”公众号的用户，为其关联小程序用户。如果没有合协用户的，为其新建一个带unionid的用户
							userService.bindMiniUser(baseEventDTO);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
						
						if ("01".equals(type)) {
							logger.info("event type : " + type + ", apply invoice . " );
							wechatResponse = wuyeService.scanEvent4Invoice(baseEventDTO);
							
							if (wechatResponse.getErrcode() == 0) {
								isSuccess = true;
							}
							if (wechatResponse.getErrcode() == 40037) {
								logger.error("invalid template_id, 请联系系统管理员！");
								isSuccess = true;
							}
							if (wechatResponse.getErrcode() == 45009) {
								logger.error("reach max api daily quota limit, 请联系系统管理员！");
								isSuccess = true;
							}
							
						} else if ("02".equals(type)) {
							logger.info("event type : " + type + ", apply receipt . " );
					    	String[]eventKeyArr = eventKey.split("\\|");
					    	if (eventKeyArr == null || eventKeyArr.length < 6) {
								logger.error("illegal event key : " + eventKey);
							}
					    	String tradeWaterId = "";
					    	try {
								tradeWaterId = eventKeyArr[1];
								ReceiptApplicationReq receiptApplicationReq = new ReceiptApplicationReq();
								receiptApplicationReq.setAppid(appId);
								receiptApplicationReq.setOpenid(openid);
								receiptApplicationReq.setTradeWaterId(tradeWaterId);
								
								user.setAppId(appId);
								user.setOpenid(openid);
								wuyeService.applyReceipt(user, receiptApplicationReq);
								if (!StringUtils.isEmpty(user.getOpenid())) {
									wuyeService.registerAndBind(user, tradeWaterId, "6");	//队列，异步执行
								}
								
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
					    	isSuccess = true;
						} else {
							isSuccess = true;
						}
						
					}
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}
				
				if (!isSuccess) {
					logger.info("eventScanSubscribe queue consume failed !, repush into the queue. json : " + json);
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
	public void eventScan() {
		
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
				logger.info("strat to consume eventScan queue : " + map);

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
				
				String type = "01";
				if (eventKey.startsWith("01") || eventKey.startsWith("qrscene_01")) {
					//donothing
				} else if (eventKey.startsWith("02") || eventKey.startsWith("qrscene_02")) {
					type = "02";
				}
				
				boolean isSuccess = false;
				WechatResponse wechatResponse = null;
				try {
					if ("01".equals(type)) {
						logger.info("event type : " + type + ", apply invoice . " );
						wechatResponse = wuyeService.scanEvent4Invoice(baseEventDTO);
						
						if (wechatResponse.getErrcode() == 0) {
							isSuccess = true;
						}
						if (wechatResponse.getErrcode() == 40037) {
							logger.error("invalid template_id, 请联系系统管理员！");
							isSuccess = true;
						}
						if (wechatResponse.getErrcode() == 45009) {
							logger.error("reach max api daily quota limit, 请联系系统管理员！");
							isSuccess = true;
						}
						
					} else if ("02".equals(type)) {
						
						logger.info("event type : " + type + ", apply receipt . " );
				    	String[]eventKeyArr = eventKey.split("\\|");
				    	if (eventKeyArr == null || eventKeyArr.length < 6) {
							logger.error("illegal event key : " + eventKey);
						}
				    	String tradeWaterId = "";
				    	try {
							tradeWaterId = eventKeyArr[1];
							ReceiptApplicationReq receiptApplicationReq = new ReceiptApplicationReq();
							receiptApplicationReq.setAppid(appId);
							receiptApplicationReq.setOpenid(openid);
							receiptApplicationReq.setTradeWaterId(tradeWaterId);
							
							user.setAppId(appId);
							user.setOpenid(openid);
							wuyeService.applyReceipt(user, receiptApplicationReq);
							if (!StringUtils.isEmpty(user.getOpenid())) {
								wuyeService.registerAndBind(user, tradeWaterId, "6");	//队列，异步执行
							}
							
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
				    	isSuccess = true;
						
					}
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}
				
				if (!isSuccess) {
					logger.info("eventScan queue consume failed !, repush into the queue. json : " + json);
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
							String name = "";
							if (!StringUtils.isEmpty(mobile)) {
								String prefix = mobile.substring(0, 3);
								String suffix = mobile.substring(7, mobile.length());
								name = prefix + "***" + suffix;	//使用手机号虚构一个名称，中间打码
							} else {
								name = user.getOpenid().substring(0, 11);	//申请电子收据存在没有手机号的情况，因此截取openid作为用户名
							}
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
							if (!StringUtils.isEmpty(mobile)) {
								dbUser.setTel(mobile);	//申请电子收据存在没有手机号的情况。因此有值才填
							}
							savedUser = userService.simpleRegister(dbUser);
							isSuccess = true;
						}
						
					} else if (StringUtils.isEmpty(dbUser.getTel())) {
						dbUser.setTel(user.getTel());
						savedUser = userService.simpleRegister(dbUser);
						isSuccess = true;
					} else if (!StringUtils.isEmpty(dbUser.getTel())){	//这种情况只要绑定房子即可
						savedUser = dbUser;
						isSuccess = true;
					}
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}
				
				if (isSuccess) {
					try {
						wuyeService.bindHouseByTradeAsync("1", savedUser, queue.getTradeWaterId(), queue.getBindType());	//绑定房屋队列
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}	
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
