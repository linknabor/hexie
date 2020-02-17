package com.yumu.hexie.service.expressdelivery;

import com.yumu.hexie.model.express.Express;

public interface ExpressDeliveryService {
	void pullWechat(Express exr);
	
	public Express getExpress(long userId);
}
