package com.yumu.hexie.service.expressdelivery;

import java.util.List;

import javax.transaction.Transactional;

import com.yumu.hexie.model.express.Express;

public interface ExpressDeliveryService {
	
	@Transactional
	void pullWechat(Express exr);
	
	public List<Express> getExpress(long userId);
}
