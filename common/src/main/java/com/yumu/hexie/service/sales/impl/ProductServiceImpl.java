package com.yumu.hexie.service.sales.impl;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.ProductService;

@Service("productService")
public class ProductServiceImpl implements ProductService {

	@Inject
	private ProductRepository productRepository;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public Product getProduct(long productId) {
		return productRepository.findById(productId).get();
	}

	@Override
	public void checkSalable(Product product, int count) {
		if(product.getStatus() != ModelConstant.PRODUCT_ONSALE){
			throw new BizValidateException("您晚到了一步，商品已下架！");
//		} else if(product.getCanSaleNum()<=0) {
//			throw new BizValidateException("您晚到了一步，商品已卖完！");
//		} else if(product.getCanSaleNum()<count) {
//			throw new BizValidateException("库存不足，请减少购买数量！");
		} else if(product.getEndDate()!=null&&product.getEndDate().getTime()<System.currentTimeMillis()){
			throw new BizValidateException("您晚到了一步，商品已下架！");
		}
		String freeze = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_FREEZE + product.getId());
		String stock = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + product.getId());
		int canSale = Integer.valueOf(stock) - Integer.valueOf(freeze);
		if (canSale <= 0) {
			throw new BizValidateException("您晚到了一步，商品["+product.getName()+"]已卖完！");
		}else if (canSale < count) {
			throw new BizValidateException("商品["+product.getName()+"]库存不足，请减少购买数量！");
		}
	}

	@Override
	public void freezeCount(Product product, int count) {
//		product.setFreezeNum(product.getFreezeNum() +count);
//		productRepository.save(product);
		redisTemplate.opsForValue().increment(ModelConstant.KEY_PRO_FREEZE + product.getId(), count);
	}

	@Override
	public void saledCount(long productId, int count) {
		Product product = productRepository.findById(productId).get();
		product.setSaledNum(product.getSaledNum()+count);
//		product.setFreezeNum(product.getFreezeNum()-count);
		productRepository.save(product);
		redisTemplate.opsForValue().decrement(ModelConstant.KEY_PRO_FREEZE + productId, count);
	}

	@Override
	public void unfreezeCount(long productId, int count) {
		
//		Product product = productRepository.findById(productId).get();
//		product.setFreezeNum(product.getFreezeNum()-count);
//		productRepository.save(product);
		redisTemplate.opsForValue().decrement(ModelConstant.KEY_PRO_FREEZE + productId, count);
		
	}
}
