package com.yumu.hexie.web.shequ;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.baidu.vo.RegionVo;
import com.yumu.hexie.integration.wuye.resp.RadiusSect;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.LocationService;
import com.yumu.hexie.service.shequ.req.RadiusSectReq;
import com.yumu.hexie.service.shequ.vo.LocationVO;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
public class LocationController extends BaseController {
	
	@Autowired
	private LocationService locationService;
	
	//查询所有环境路径
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getRegionUrl", method = RequestMethod.GET)
	public BaseResult<RegionVo> getRegionUrl(@RequestParam(required=false) String coordinate) throws Exception {

		return BaseResult.successResult(locationService.getRegionUrl(coordinate));
	}
	
	/**
	 * 获取位置信息 1).用户当前定位，2)用户附近的小区
	 * @param user
	 * @param appid
	 * @param coordinate
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/location", method = RequestMethod.GET)
	public BaseResult<LocationVO> getLocationInfo(@ModelAttribute(Constants.USER)User user, 
			@RequestParam(required=false) String appid, @RequestParam(required=false) String coordinate) throws Exception {

		RadiusSectReq radiusSectReq = new RadiusSectReq();
		radiusSectReq.setAppid(appid);
		radiusSectReq.setCoordinate(coordinate);
		
		return BaseResult.successResult(locationService.getLocationInfo(user, radiusSectReq));
	}
	
	/**
	 * 获取附近的小区
	 * @param user
	 * @param appid
	 * @param coordinate
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/querySectNearby", method = RequestMethod.GET)
	public BaseResult<List<RadiusSect>> querySectNearby(@ModelAttribute(Constants.USER)User user, 
			@RequestParam(required=false) String appid, @RequestParam(required=false) String coordinate) throws Exception {

		RadiusSectReq radiusSectReq = new RadiusSectReq();
		radiusSectReq.setAppid(appid);
		radiusSectReq.setCoordinate(coordinate);
		
		return BaseResult.successResult(locationService.querySectNearby(user, radiusSectReq));
	}
}
