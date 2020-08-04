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
	
	void refreshCache();

	/**
	 * 根据省级单位缩写查询  江苏 -> 江苏省
	 * @param regionName 传简写
	 * @return
	 */
	RegionUrl getRegionUrlByName(String regionName);
}
