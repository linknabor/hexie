package com.yumu.hexie.service.eshop.impl;

import java.util.ArrayList;
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
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.eshop.dto.QueryProductDTO;
import com.yumu.hexie.integration.eshop.mapper.EvoucherMapper;
import com.yumu.hexie.integration.eshop.mapper.OperatorMapper;
import com.yumu.hexie.integration.eshop.mapper.QueryProductMapper;
import com.yumu.hexie.integration.eshop.mapper.SaleAreaMapper;
import com.yumu.hexie.integration.eshop.vo.QueryEvoucherVO;
import com.yumu.hexie.integration.eshop.vo.QueryOperVO;
import com.yumu.hexie.integration.eshop.vo.QueryProductVO;
import com.yumu.hexie.integration.eshop.vo.SaveOperVO;
import com.yumu.hexie.integration.eshop.vo.SaveOperVO.Oper;
import com.yumu.hexie.integration.eshop.vo.SaveProductVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductPlat;
import com.yumu.hexie.model.commonsupport.info.ProductPlatRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.distribution.OnSaleAreaItem;
import com.yumu.hexie.model.distribution.OnSaleAreaItemRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorItem;
import com.yumu.hexie.model.localservice.ServiceOperatorItemRepository;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.EvoucherRepository;
import com.yumu.hexie.model.market.saleplan.OnSaleRule;
import com.yumu.hexie.model.market.saleplan.OnSaleRuleRepository;
import com.yumu.hexie.service.eshop.EshopSerivce;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * 商品上、下架
 * @author david
 *
 * @param <T>
 */
public class EshopServiceImpl implements EshopSerivce {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private AgentRepository agentRepository;
	@Autowired
	private OnSaleRuleRepository onSaleRuleRepository;
	@Autowired
	private OnSaleAreaItemRepository onSaleAreaItemRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private ProductPlatRepository productPlatRepository;
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	@Autowired
	private ServiceOperatorItemRepository serviceOperatorItemRepository;
	@Autowired
	private EvoucherRepository evoucherRepository;
	
	@Override
	public CommonResponse<Object> getProduct(QueryProductVO queryProductVO) {

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Integer> agentList = null;
			if (StringUtils.isEmpty(queryProductVO.getAgentNo()) && StringUtils.isEmpty(queryProductVO.getAgentName())) {
				//do nothing
			}else {
				agentList = agentRepository.findByAgentNoOrName(1, queryProductVO.getAgentNo(), 
						queryProductVO.getAgentName());
			}
			if (agentList != null ) {
				if (agentList.isEmpty()) {
					agentList.add(0);
				}
			}
			
			Pageable pageable = new PageRequest(queryProductVO.getCurrentPage(), queryProductVO.getPageSize());
			Page<Object[]> page = productRepository.findByPageSelect(queryProductVO.getProductType(), queryProductVO.getProductId(), 
					queryProductVO.getProductName(), queryProductVO.getProductStatus(), agentList, queryProductVO.getDemo(), pageable);
			
			List<QueryProductMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryProductMapper.class);
			QueryListDTO<List<QueryProductMapper>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setContent(list);
			
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	/**
	 * 根据商品ID查询
	 */
	@Override
	public CommonResponse<Object> getProductById(QueryProductVO queryProductVO) {
		
		Assert.hasText(queryProductVO.getProductId(), "商品ID不能为空。");

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			Pageable pageable = new PageRequest(0, 1);
			Page<Object[]> page = productRepository.findByPageSelect(queryProductVO.getProductType(), queryProductVO.getProductId(), 
					"", "", null, "", pageable);
			
			List<QueryProductMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryProductMapper.class);
			List<Object[]> regionList = regionRepository.findByProductId(queryProductVO.getProductId());
			
			QueryProductDTO<QueryProductMapper> queryProductDTO = new QueryProductDTO<>();
			queryProductDTO.setContent(list.get(0));
			
			List<SaleAreaMapper> areaList = ObjectToBeanUtils.objectToBean(regionList, SaleAreaMapper.class);
			
			queryProductDTO.setSaleArea(areaList);
			commonResponse.setData(queryProductDTO);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	

	/**
	 * 保存商品上架内容
	 */
	@Override
	@Transactional
	public void saveProduct(SaveProductVO saveProductVO) throws Exception {
	
		String agentNo = saveProductVO.getAgentNo();
		Agent agent = agentRepository.findByAgentNo(agentNo);
		if ("add".equals(saveProductVO.getOperType())) {
			if (agent == null) {
				agent = new Agent();
				agent.setName(saveProductVO.getAgentName());
				agent.setAgentNo(agentNo);
				agent.setStatus(1);
				agent = agentRepository.save(agent);
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
			product.setAgentId(agent.getId());
		}
		product.setProductType(saveProductVO.getType());
		product.setTotalCount(Integer.parseInt(saveProductVO.getTotalCount()));
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
		
		if (ModelConstant.ORDER_TYPE_ONSALE != Integer.valueOf(saveProductVO.getSalePlanType())) {	//核销券规则走特卖
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
		onSaleRule.setPrice(product.getSinglePrice());
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
			onSaleAreaItem.setPrice(product.getSinglePrice());
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
		
		ProductPlat productPlat = new ProductPlat();
		if ("edit".equals(saveProductVO.getOperType())) {
			productPlat = productPlatRepository.findByProductIdAndAppId(product.getId(), saveProductVO.getAppid());
			if (productPlat == null) {
				productPlat = new ProductPlat();	//这里new一个，因为选线可能有商品没有配置appid
			}
		}
		productPlat.setAppId(saveProductVO.getAppid());
		productPlat.setProductId(product.getId());
		productPlatRepository.save(productPlat);
		
		
	}
	
	@Override
	@Transactional
	public void updateStatus(SaveProductVO saveProductVO) {
		
		Assert.hasText(saveProductVO.getId(), "商品ID不能为空。");
		Assert.hasText(saveProductVO.getOperType(), "操作类型不能为空。 ");
		
		String productId = saveProductVO.getId();
		String operType = saveProductVO.getOperType();
		
		int productStatus = 0;
		int ruleStatus = 0;
		int itemStatus = 0;
		if ("on".equals(operType)) {
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
	 * 设置成样板
	 */
	@Override
	public void updateDemo(SaveProductVO saveProductVO) {

		Assert.hasText(saveProductVO.getId(), "商品ID不能为空。");
		Assert.hasText(saveProductVO.getOperType(), "操作类型不能为空。 ");
		
		String productId = saveProductVO.getId();
		String operType = saveProductVO.getOperType();
		
		if ("1".equals(operType)) {
			productRepository.updateDemo(1, Long.valueOf(productId));
		}else if ("0".equals(operType)) {
			productRepository.updateDemo(0, Long.valueOf(productId));
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
	
	@Override
	public CommonResponse<Object> getOper(QueryOperVO queryOperVO) {

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Object[]> list = serviceOperatorRepository.findByTypeAndServiceId(queryOperVO.getType(), queryOperVO.getServiceId());
			List<OperatorMapper> operList = ObjectToBeanUtils.objectToBean(list, OperatorMapper.class);
			commonResponse.setData(operList);
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	/**
	 * 保存服务人员
	 */
	@Override
	@Transactional
	public void saveOper(SaveOperVO saveOperVO) {
		
		Assert.notNull(saveOperVO.getServiceId(), "服务ID或产品ID不能为空。");
		
		serviceOperatorItemRepository.deleteByServiceId(saveOperVO.getServiceId());
		
		List<Oper> operList = saveOperVO.getOpers();
		for (Oper oper : operList) {
			ServiceOperator serviceOperator = serviceOperatorRepository.findByTypeAndTelAndOpenId(ModelConstant.SERVICE_OPER_TYPE_EVOUCHER, oper.getTel(), oper.getOpenId());
			if (serviceOperator == null) {
				serviceOperator = new ServiceOperator();
			}
			serviceOperator.setName(oper.getName());
			serviceOperator.setOpenId(oper.getOpenId());
			serviceOperator.setTel(oper.getTel());
			serviceOperator.setType(ModelConstant.SERVICE_OPER_TYPE_EVOUCHER);	//TODO 根据service_id动态取值
			serviceOperator.setUserId(oper.getUserId());
			serviceOperator.setLongitude(0d);
			serviceOperator.setLatitude(0d);
			serviceOperator = serviceOperatorRepository.save(serviceOperator);
			
			ServiceOperatorItem serviceOperatorItem = serviceOperatorItemRepository.findByOperatorIdAndServiceId(serviceOperator.getId(), saveOperVO.getServiceId());
			if (serviceOperatorItem == null) {
				serviceOperatorItem = new ServiceOperatorItem();
				serviceOperatorItem.setOperatorId(serviceOperator.getId());
				serviceOperatorItem.setServiceId(saveOperVO.getServiceId());
				serviceOperatorItemRepository.save(serviceOperatorItem);
			}
			
		}
		//查看有哪些操作员已经没有服务项目了，没有的删除该操作员
		List<ServiceOperator> noServiceList = serviceOperatorRepository.queryNoServiceOper(ModelConstant.SERVICE_OPER_TYPE_EVOUCHER);
		for (ServiceOperator serviceOperator : noServiceList) {
			serviceOperatorRepository.delete(serviceOperator);
		}
		
		
	}

	/**
	 * 后台查询核销券信息
	 */
	@Override
	public CommonResponse<Object> getEvoucher(QueryEvoucherVO queryEvoucherVO) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			Pageable pageable = new PageRequest(queryEvoucherVO.getCurrentPage(), queryEvoucherVO.getPageSize());
			Page<Evoucher> page = evoucherRepository.findByMultipleConditions(queryEvoucherVO.getStatus(), queryEvoucherVO.getTel(), queryEvoucherVO.getAgentName(), pageable);
			List<EvoucherMapper> mapperList = new ArrayList<>();
			for (Evoucher evoucher : page.getContent()) {
				EvoucherMapper evoucherMapper = new EvoucherMapper();
				BeanUtils.copyProperties(evoucher, evoucherMapper);
				mapperList.add(evoucherMapper);
			}
			
			QueryListDTO<List<EvoucherMapper>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setContent(mapperList);
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	

}
