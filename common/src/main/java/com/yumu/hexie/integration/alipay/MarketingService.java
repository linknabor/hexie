package com.yumu.hexie.integration.alipay;

import org.springframework.stereotype.Service;

import com.alipay.v3.util.GenericExecuteApi;
import com.alipay.v3.util.model.OpenApiGenericRequest;

@Service(value = "alipayMarketingService")
public class MarketingService {

	public void getMarketingConsult(String userid, String appid) {
		
		GenericExecuteApi api = new GenericExecuteApi();
		OpenApiGenericRequest request = new OpenApiGenericRequest();
//		api.execute(path, method, request);
		
	}
}
