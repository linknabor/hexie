package com.yumu.hexie.service.shelf.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.shelf.dto.QueryProductDTO;
import com.yumu.hexie.integration.shelf.dto.QueryProductListDTO;
import com.yumu.hexie.integration.shelf.mapper.QueryProductMapper;
import com.yumu.hexie.integration.shelf.mapper.SaleAreaMapper;
import com.yumu.hexie.integration.shelf.vo.QueryProductVO;
import com.yumu.hexie.integration.shelf.vo.SaveProductVO;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.distribution.OnSaleAreaItem;
import com.yumu.hexie.model.distribution.OnSaleAreaItemRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.market.saleplan.OnSaleRule;
import com.yumu.hexie.model.market.saleplan.OnSaleRuleRepository;
import com.yumu.hexie.model.merchant.Merchant;
import com.yumu.hexie.model.merchant.MerchantRepository;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shelf.ShelfSerivce;

/**
 * 商品上、下架
 * @author david
 *
 * @param <T>
 */
public class ShelfServiceImpl<T> implements ShelfSerivce {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private MerchantRepository merchantRepository;
	@Autowired
	private OnSaleRuleRepository onSaleRuleRepository;
	@Autowired
	private OnSaleAreaItemRepository onSaleAreaItemRepository;
	@Autowired
	private RegionRepository regionRepository;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public BaseResult<T> getProduct(QueryProductVO queryProductVO) {

		BaseResult baseResult = new BaseResult<>();
		try {
			List<Integer> merchantList = null;
			if (StringUtils.isEmpty(queryProductVO.getMerchantNo()) && StringUtils.isEmpty(queryProductVO.getMerchantName())) {
				//do nothing
			}else {
				merchantList = merchantRepository.findByMerchantNoOrName(1, queryProductVO.getMerchantNo(), 
						queryProductVO.getMerchantName());
			}
			if (merchantList != null ) {
				if (merchantList.isEmpty()) {
					merchantList.add(0);
				}
			}
			
			Pageable pageable = new PageRequest(queryProductVO.getCurrentPage(), queryProductVO.getPageSize());
			Page<Object[]> page = productRepository.findByPageSelect(queryProductVO.getProductType(), queryProductVO.getProductId(), 
					queryProductVO.getProductName(), queryProductVO.getProductStatus(), merchantList, pageable);
			
			List<QueryProductMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryProductMapper.class);
			QueryProductListDTO<List<QueryProductMapper>> responsePage = new QueryProductListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setContent(list);
			
			baseResult.setData(responsePage);
			baseResult.setResult("00");
			
		} catch (Exception e) {
			
			baseResult.setData(e.getMessage());
			baseResult.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return baseResult;
	}
	
	/**
	 * 根据商品ID查询
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public BaseResult<T> getProductById(QueryProductVO queryProductVO) {
		
		Assert.hasText(queryProductVO.getProductId(), "商品ID不能为空。");

		BaseResult baseResult = new BaseResult<>();
		try {
			
			Pageable pageable = new PageRequest(0, 1);
			Page<Object[]> page = productRepository.findByPageSelect(queryProductVO.getProductType(), queryProductVO.getProductId(), 
					"", "", null, pageable);
			
			List<QueryProductMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryProductMapper.class);
			List<Object[]> regionList = regionRepository.findByProductId(queryProductVO.getProductId());
			
			QueryProductDTO<QueryProductMapper> queryProductDTO = new QueryProductDTO<>();
			queryProductDTO.setContent(list.get(0));
			
			List<SaleAreaMapper> areaList = ObjectToBeanUtils.objectToBean(regionList, SaleAreaMapper.class);
			
			queryProductDTO.setSaleArea(areaList);
			baseResult.setData(queryProductDTO);
			baseResult.setResult("00");
			
		} catch (Exception e) {
			
			baseResult.setData(e.getMessage());
			baseResult.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return baseResult;
	}
	
	

	/**
	 * 保存商品上架内容
	 */
	@Override
	@Transactional
	public void saveProduct(SaveProductVO saveProductVO) throws Exception {
	
		String merNo = saveProductVO.getMerchantNo();
		Merchant merchant = merchantRepository.findByMerchantNo(merNo);
		if ("add".equals(saveProductVO.getOperType())) {
			if (merchant == null) {
				merchant = new Merchant();
				merchant.setName(saveProductVO.getMerchantName());
				merchant.setMerchantNo(merNo);
				merchant.setMerchantType("C");
				merchant = merchantRepository.save(merchant);
			}
		}
		
		Product product = new Product();
		if ("edit".equals(saveProductVO.getOperType())) {
			product = productRepository.findOne(Long.valueOf(saveProductVO.getId()));
			if (product == null) {
				throw new BizValidateException("未查询到商品，id : " + saveProductVO.getId());
			}
		}
		if ("add".equals(saveProductVO.getOperType())) {
			product.setName(saveProductVO.getName());
			product.setMerchantId(merchant.getId());
		}
		product.setProductType(saveProductVO.getType());
		product.setMainPicture(saveProductVO.getMainPicture());
		product.setSmallPicture(saveProductVO.getSmallPicture());
		product.setPictures(saveProductVO.getPictures());
		product.setMiniPrice(Float.valueOf(saveProductVO.getMiniPrice()));
		product.setSinglePrice(Float.valueOf(saveProductVO.getSinglePrice()));
		product.setOriPrice(Float.valueOf(saveProductVO.getOriPrice()));
		product.setServiceDesc(saveProductVO.getContext());
		if (ModelConstant.RULE_STATUS_ON == Integer.valueOf(saveProductVO.getStatus())) {
			product.setStatus(ModelConstant.PRODUCT_ONSALE);
		}
		product.setStartDate(saveProductVO.getStartDate() + " 00:00:00");
		product.setEndDate(saveProductVO.getEndDate() + " 00:00:00");
		product.setShortName(saveProductVO.getName());
		product.setTitleName(saveProductVO.getName());
		if ("edit".equals(saveProductVO.getOperType())) {
			product.setUpdateDate(new Date());
			product.setUpdateUser(saveProductVO.getUpdateUser());
		}
		product = productRepository.save(product);
		
		if (ModelConstant.ORDER_TYPE_EVOUCHER != Integer.valueOf(saveProductVO.getSalePlanType())) {	//核销券规则走特卖
			throw new BizValidateException("unknow sale plat type : " + saveProductVO.getSalePlanType());
		}
		OnSaleRule onSaleRule = new OnSaleRule();
		if ("edit".equals(saveProductVO.getOperType())) {
			List<OnSaleRule> ruleList = onSaleRuleRepository.findAllByProductId(product.getId());
			if (ruleList == null || ruleList.isEmpty()) {
				throw new BizValidateException("未查询到商品上架规则，product id : " + saveProductVO.getId());
			}
			onSaleRule = ruleList.get(0);
		}
		onSaleRule.setProductId(Integer.valueOf(product.getProductType()));
		onSaleRule.setProductName(product.getName());
		onSaleRule.setProductType(Integer.valueOf(saveProductVO.getType()));
		onSaleRule.setCreateDate(product.getCreateDate());
		onSaleRule.setDescription(product.getServiceDesc());
		onSaleRule.setProductId(product.getId());
		onSaleRule.setName(product.getName());
		onSaleRule.setLimitNumOnce(Integer.valueOf(saveProductVO.getLimitNumOnce()));
		onSaleRule.setStartDate(product.getStartDate());
		onSaleRule.setEndDate(product.getEndDate());
		onSaleRule.setOriPrice(product.getOriPrice());
		onSaleRule.setDescription(product.getServiceDesc());
		onSaleRule.setTimeoutForPay(30*60*1000);
		if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
			onSaleRule.setStatus(ModelConstant.RULE_STATUS_ON);
		}else {
			onSaleRule.setStatus(ModelConstant.RULE_STATUS_OFF);
		}
		onSaleRule = onSaleRuleRepository.save(onSaleRule);
		
		if ("edit".equals(saveProductVO.getOperType())) {
			List<OnSaleAreaItem> areaList = onSaleAreaItemRepository.findByRuleId(onSaleRule.getId());
			if (areaList == null || areaList.isEmpty()) {
				throw new BizValidateException("未查询到商品上架区域，product id : " + saveProductVO.getId());
			}
			onSaleAreaItemRepository.deleteByProductId(saveProductVO.getId());
		}
		
		for (Region saleArea : saveProductVO.getSaleAreas()) {
			
			Region region = getRegion(saleArea);
			OnSaleAreaItem onSaleAreaItem = new OnSaleAreaItem();
			onSaleAreaItem.setRegionId(region.getId());
			onSaleAreaItem.setRuleId(onSaleRule.getId());
			long ruleCloseTime = onSaleRule.getEndDate().getTime();
			onSaleAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
			onSaleAreaItem.setSortNo(Integer.valueOf(saveProductVO.getSortNo()));
			onSaleAreaItem.setRuleName(onSaleRule.getName());
			onSaleAreaItem.setOriPrice(product.getOriPrice());
			onSaleAreaItem.setRegionId(region.getId());
			onSaleAreaItem.setRegionType(region.getRegionType());
			onSaleAreaItem.setProductId(product.getId());
			onSaleAreaItem.setProductName(product.getName());
			onSaleAreaItem.setProductPic(product.getMainPicture());
			onSaleAreaItem.setProductType(onSaleRule.getProductType());
			if (ModelConstant.PRODUCT_ONSALE == product.getStatus()) {
				onSaleAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_ON);
			}else {
				onSaleAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_OFF);
			}
			onSaleAreaItemRepository.save(onSaleAreaItem);
			
		}
		
		
	}
	
	@Override
	@Transactional
	public void updateStatus(SaveProductVO saveProductVO) {
		
		Assert.hasText(saveProductVO.getId(), "商品ID不能为空。");
		Assert.hasText(saveProductVO.getStatus(), "商品状态不能为空。");
		
		String productId = saveProductVO.getId();
		String status = saveProductVO.getStatus();
		
		int productStatus = 0;
		int ruleStatus = 0;
		int itemStatus = 0;
		if (ModelConstant.RULE_STATUS_ON == Integer.valueOf(status)) {
			productStatus = ModelConstant.PRODUCT_ONSALE;
			ruleStatus = ModelConstant.RULE_STATUS_ON;
			itemStatus = ModelConstant.DISTRIBUTION_STATUS_ON;
		}else {
			productStatus = ModelConstant.PRODUCT_OFF;
			ruleStatus = ModelConstant.RULE_STATUS_OFF;
			itemStatus = ModelConstant.DISTRIBUTION_STATUS_OFF;
		}
		
		
		
		Product product = productRepository.findOne(Long.valueOf(productId));
		if (product == null) {
			throw new BizValidateException("未查询到商品, id : " + productId);
		}
		productRepository.updateStatus(productStatus, product.getId());
		
		List<OnSaleRule> ruleList = onSaleRuleRepository.findAllByProductId(product.getId());
		for (OnSaleRule onSaleRule : ruleList) {
			onSaleRuleRepository.updateStatus(ruleStatus, onSaleRule.getId());

			List<OnSaleAreaItem> itemList = onSaleAreaItemRepository.findByRuleId(onSaleRule.getId());
			for (OnSaleAreaItem item : itemList) {
				onSaleAreaItemRepository.updateStatus(itemStatus, item.getId());
			}
		}
		
		
	}

	/**
	 * 获取平台小区对应的region
	 * @param saleArea
	 * @param region
	 * @return
	 */
	private Region getRegion(Region saleArea) {
		
		Region region = null;
		List<Region> regionList = regionRepository.findAllBySectId(saleArea.getSectId());
		if (regionList==null || regionList.isEmpty()) {
			regionList = regionRepository.findByNameAndRegionType(saleArea.getName(), saleArea.getRegionType());
			if (regionList == null || regionList.isEmpty()) {
				region = new Region();
				BeanUtils.copyProperties(saleArea, region);
				region.setLongitude(0d);
				region.setLatitude(0d);
				region = regionRepository.save(region);
			}else {
				if (regionList.size()>1) {
					for (int i = 0; i < regionList.size(); i++) {
						Region currRegion = regionList.get(i);
						if (!StringUtils.isEmpty(currRegion.getSectId())) {
							region = currRegion;
						}
					}
					if (region == null) {
						region = regionList.get(0);
					}
				}else {
					region = regionList.get(0);
				}
				region.setSectId(saleArea.getSectId());
				region = regionRepository.save(region);
			}
		}else {
			if (regionList.size()>1) {
				for (int i = 0; i < regionList.size(); i++) {
					Region currRegion = regionList.get(i);
					if (!StringUtils.isEmpty(currRegion.getSectId())) {
						region = currRegion;
					}
				}
				if (region == null) {
					region = regionList.get(0);
				}
			}else {
				region = regionList.get(0);
			}
			region = regionList.get(0);
		}
		return region;
	}
	

}
