package com.yumu.hexie.integration.wuye;

import org.springframework.stereotype.Component;

import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;

@Component
public class WuyeFallBack implements WuyeClients {

	@Override
	public BaseResult<HouseListVO> getHouse(String userId) {
		
		BaseResult<HouseListVO> baseResult = new BaseResult<>();
		baseResult.setResult("出错了。");
		return baseResult;
	}

}
