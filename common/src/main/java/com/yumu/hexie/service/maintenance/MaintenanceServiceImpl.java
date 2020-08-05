package com.yumu.hexie.service.maintenance;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.maintenance.vo.MaintenanceVO;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

	private static Logger logger = LoggerFactory.getLogger(MaintenanceServiceImpl.class);
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate; 
	
	@Override
	public Map<Object, Object> updateQueueSwitch(MaintenanceVO maintenanceVO) {

		String server = maintenanceVO.getServer();
		String queueSwitch = maintenanceVO.getQueueSwitch();
		String currentServer = getHostName();
		logger.info("currSever : " + currentServer + ", maintenanceVO : " + maintenanceVO);
		if (StringUtils.isEmpty(server) || StringUtils.isEmpty(queueSwitch)) {
			throw new BizValidateException("server or switch is null ! ");
		}
		
		if (!"on".equals(queueSwitch) && !"off".equals(queueSwitch)) {
			throw new BizValidateException("invalid switch value !");
		}
		if (server.equalsIgnoreCase(currentServer)) {
			stringRedisTemplate.opsForHash().put(ModelConstant.KEY_MAINTANANCE_SWITCH, currentServer, queueSwitch);
		}
		Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(ModelConstant.KEY_MAINTANANCE_SWITCH);
		return map;
		
	}


	@Override
	public Map<Object, Object> getQueueSwitch() {

		Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(ModelConstant.KEY_MAINTANANCE_SWITCH);;
		return map;
	}


	/**
	 * 队列开关是否开启
	 * on 开启
	 * off 关闭
	 * 默认空值 代表开启
	 */
	@Override
	public boolean isQueueSwitchOn() {
		
		boolean isOn = false;
		String currentServer = getHostName();
		Object status = stringRedisTemplate.opsForHash().get(ModelConstant.KEY_MAINTANANCE_SWITCH, currentServer);
		String statusStr = (String) status;
		isOn = !"off".equals(statusStr);
		return isOn;
	}
	
	
	private String getHostName() {
		
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostname = inetAddress.getHostName();
			return hostname;
		} catch (UnknownHostException e) {
			throw new BizValidateException(e.getMessage() ,e);
		}
		
	}


}
