package com.yumu.hexie.model.maintenance;

import java.util.Map;

import com.yumu.hexie.model.maintenance.vo.MaintenanceVO;

public interface MaintenanceService {
	
	Map<Object, Object> getQueueSwitch();
	Map<Object, Object> updateQueueSwitch(MaintenanceVO maintenanceVO);
	boolean isQueueSwitchOn();

}
