package com.yumu.hexie.service.shequ;

import java.util.List;

import com.yumu.hexie.integration.baidu.vo.RegionVo;
import com.yumu.hexie.integration.wuye.resp.RadiusSect;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.req.RadiusSectReq;
import com.yumu.hexie.service.shequ.vo.LocationVO;

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

	/**
	 * 获取附近的小区
	 * @param user
	 * @param radiusSectReq
	 * @return
	 * @throws Exception
	 */
	List<RadiusSect> querySectNearby(User user, RadiusSectReq radiusSectReq) throws Exception;

	/**
	 * 获取用户位置信息和附近小区
	 * @param user
	 * @param radiusSectReq
	 * @return
	 * @throws Exception
	 */
	LocationVO getLocationInfo(User user, RadiusSectReq radiusSectReq) throws Exception;
}
