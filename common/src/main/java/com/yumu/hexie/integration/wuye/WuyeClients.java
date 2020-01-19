package com.yumu.hexie.integration.wuye;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;

@FeignClient(name = "communityms", fallback = WuyeFallBack.class)
public interface WuyeClients {

	@RequestMapping(value = "/communityms/mobile/getHoseInfoSDO.do", method = RequestMethod.GET)
	BaseResult<HouseListVO> getHouse(@RequestParam(value = "user_id") String userId);
	
	
}
