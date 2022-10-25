package com.yumu.hexie.service.sales.impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.ProductService;
import org.springframework.util.StringUtils;

@Service("productService")
public class ProductServiceImpl implements ProductService {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Inject
	private ProductRepository productRepository;
	@Autowired
	@Qualifier("stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public Product getProduct(long productId) {
		return productRepository.findById(productId);
	}

	@Override
	public void checkSalable(Product product, int count) {
		
		if (product.getUserLimitCount() < count) {
			throw new BizValidateException("当前商品每用户限购"+count+"份！");
		}
		if(product.getStatus() != ModelConstant.PRODUCT_ONSALE){
			throw new BizValidateException("您晚到了一步，商品已下架！");
		} else if(product.getEndDate()!=null&&product.getEndDate().getTime()<System.currentTimeMillis()){
			throw new BizValidateException("您晚到了一步，商品已下架！");
		}
		String freeze = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_FREEZE + product.getId());
		String stock = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + product.getId());
//		if(StringUtils.isEmpty(stock) || StringUtils.isEmpty(freeze)) {
//			throw new BizValidateException("您晚到了一步，商品["+product.getName()+"]已卖完！");
//		}
		if(StringUtils.isEmpty(stock)) {
			stock = String.valueOf(Integer.MAX_VALUE);
		}
		if(StringUtils.isEmpty(freeze)) {
			stock = "0";
		}
		int canSale = Integer.parseInt(stock) - Integer.parseInt(freeze);
		if (canSale <= 0) {
			throw new BizValidateException("您晚到了一步，商品["+product.getName()+"]已卖完！");
		}else if (canSale < count) {
			throw new BizValidateException("商品["+product.getName()+"]库存不足，请减少购买数量！");
		}
	}

	@Override
	public void freezeCount(Product product, int count) {
		redisTemplate.opsForValue().increment(ModelConstant.KEY_PRO_FREEZE + product.getId(), count);
	}

	@Override
	public void saledCount(long productId, int count) {

		Product product = productRepository.findById(productId);
		if (product == null) {
			logger.warn("cant not find product, id : " + productId);
			return;
		}
		product.setSaledNum(product.getSaledNum()+count);
		productRepository.save(product);
		if (count >= 0) {	//退款不需要解冻库存
			redisTemplate.opsForValue().decrement(ModelConstant.KEY_PRO_FREEZE + productId, count);
		}
	}

	@Override
	public void unfreezeCount(long productId, int count) {
		redisTemplate.opsForValue().decrement(ModelConstant.KEY_PRO_FREEZE + productId, count);
		
	}
}
