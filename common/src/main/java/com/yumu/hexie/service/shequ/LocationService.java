package com.yumu.hexie.service.shequ;

import com.yumu.hexie.integration.baidu.vo.RegionVo;
import com.yumu.hexie.model.region.RegionUrl;

public interface LocationService {

	/**
	 * 根据经纬度查询地址
	 * @param coordinate
	 * @return
	 */
	RegionVo getRegionUrl(String coordinate);
	
	void updateRegionUrlCache();

	RegionUrl getRegionUrlByName(String regionName);
}
