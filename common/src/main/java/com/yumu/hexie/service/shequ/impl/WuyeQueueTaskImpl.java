package com.yumu.hexie.service.shequ.impl;

import java.util.List;
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
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.WuyeQueueTask;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.vo.BindHouseQueue;

@Service
public class WuyeQueueTaskImpl implements WuyeQueueTask {
	
	private static Logger logger = LoggerFactory.getLogger(WuyeQueueTaskImpl.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private WuyeService wuyeService;
	
	/**
	 * 绑定房屋队列。在缴费后调用，做异步绑定，缴费完了只要显示缴费金额即可，绑定在后台操作
	 * @throws InterruptedException 
	 */
	@Override
	@Async
	public void bindHouseByQueue() {
		
		while(true) {
			try {
	
				String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_BIND_HOUSE_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				BindHouseQueue queue = objectMapper.readValue(json, new TypeReference<BindHouseQueue>(){});
				
				logger.info("strat to consume to queue : " + queue);
				
				User user = queue.getUser();
				int totalFailed = 0;
				boolean isSuccess = false;
				
				while(!isSuccess && totalFailed < 3) {
					
					BaseResult<HexieHouses> baseResult = WuyeUtil.bindByTrade(user.getWuyeId(), queue.getTradeWaterId());
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
}
