package com.yumu.hexie.web.shequ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.baidu.vo.RegionVo;
import com.yumu.hexie.service.shequ.LocationService;
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
	
	@RequestMapping(value = "/regionUrl", method = RequestMethod.PUT)
	public String regionUrlCache(@RequestParam String code){
		
		if (StringUtils.isEmpty(code)) {
			return "";
		}else {
			
			return "success";
		}
		
	}
}
