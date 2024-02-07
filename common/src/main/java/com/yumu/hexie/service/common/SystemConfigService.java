/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common;

import java.util.List;
import java.util.Set;

import com.yumu.hexie.model.system.SystemConfig;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.pojo.dto.ActiveApp;

/**
 * <pre>
 * 系统参数统一获取
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: SystemConfigService.java, v 0.1 2016年3月30日 上午11:51:34  Exp $
 */
public interface SystemConfigService {

    int querySmsChannel();
    String queryJsTickets(String appId);
    String queryWXAToken(String appId);
    
    String[] queryActPeriod();
    Set<String> getUnCouponItems();

	String getSysConfigByKey(String key);
	void reloadSysConfigCache();
	boolean isCardServiceAvailable(String appId);
	boolean coronaPreventionAvailable(String appId);
	boolean isDonghu(String appId);
	boolean registerCouponServiceAvailabe(String appId);
	boolean isCardPayServiceAvailabe(String appId);
	List<SystemConfig> getAll();
	ActiveApp getActiveApp(User user);
	String getMiniProgramMappedApp(String miniAppid);

}
