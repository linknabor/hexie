package com.yumu.hexie.service.common.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.eshop.req.CreateRgroupRequest;
import com.yumu.hexie.integration.eshop.req.CreateRgroupRequest.GroupOwnerInfo;
import com.yumu.hexie.integration.eshop.req.CreateRgroupRequest.Sect;
import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.cache.ProductRuleCache;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRule;
import com.yumu.hexie.model.commonsupport.info.ProductRuleRepository;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.OrgOperatorRepository;
import com.yumu.hexie.model.user.RgroupOwner;
import com.yumu.hexie.model.user.RgroupOwnerRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.RgroupV3Service;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.RgroupVO;
import com.yumu.hexie.vo.RgroupVO.DescriptionMore;
import com.yumu.hexie.vo.RgroupVO.ProductVO;
import com.yumu.hexie.vo.RgroupVO.RgroupOwnerVO;
import com.yumu.hexie.vo.RgroupVO.Tag;
import com.yumu.hexie.vo.RgroupVO.Thumbnail;

public class RgroupV3ServiceImpl implements RgroupV3Service {
	
	private static Logger logger = LoggerFactory.getLogger(RgroupV3ServiceImpl.class);
	
	@Autowired
	private RgroupRuleRepository rgroupRuleRepository;
	@Autowired
	private OrgOperatorRepository orgOperatorRepository;
	@Autowired
	private AgentRepository agentRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductRuleRepository productRuleRepository;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RgroupAreaItemRepository rgroupAreaItemRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private RgroupOwnerRepository rgroupOwnerRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private EshopUtil eshopUtil;
	
	
	@Override
	@Transactional
	public long saveRgroup(RgroupVO rgroupVo) {
	
		logger.info("saveRgroup : " + rgroupVo);
		Assert.hasText(rgroupVo.getRgroupOwner().getOwnerTel(), "团长联系方式不能为空");
		Assert.hasText(rgroupVo.getType(), "保存类型不能为空");
		Assert.hasText(rgroupVo.getDescription(), "团购标题不能为空");
		Assert.noNullElements(rgroupVo.getDescriptionMore(), "团购活动内容不能为空");
		Assert.noNullElements(rgroupVo.getProductList(), "团购商品不能为空");
		Assert.hasText(rgroupVo.getStartDate(), "团购起始日期不能为空");
		Assert.hasText(rgroupVo.getEndDate(), "团购结束日期不能为空");
		Assert.notNull(rgroupVo.getRegion(), "团购区域不能为空");
		
		try {
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			CreateRgroupRequest createRgroupRequest = new CreateRgroupRequest();
			
			/*1.保存团长信息 start */
			RgroupOwnerVO owner = rgroupVo.getRgroupOwner();
			OrgOperator orgOperator = orgOperatorRepository.findByUserIdAndRoleId(owner.getOwnerId(), ModelConstant.USER_ROLE_RGROUPOWNER);
			Agent agent = null;
			
			if (orgOperator == null) {
				agent = agentRepository.findByAgentNo("000000000000");
				orgOperator = new OrgOperator();
				orgOperator.setOrgId(agent.getAgentNo());
				orgOperator.setOrgName(agent.getName());
				orgOperator.setOrgOperName(owner.getOwnerName());
				orgOperator.setRoleId(ModelConstant.USER_ROLE_RGROUPOWNER);
				orgOperator.setUserId(owner.getOwnerId());
				orgOperator.setOrgOperId("");		//TODO 联动backmng后回调
				orgOperator.setOrgType("");	//联动后回调
				orgOperatorRepository.save(orgOperator);
				
			} else {
				agent = agentRepository.findByAgentNo(orgOperator.getOrgId());
			}
			
			User ownerUser = userService.getById(owner.getOwnerId());;
			RgroupOwner rgroupOwner = rgroupOwnerRepository.findByUserId(owner.getOwnerId());
			if (rgroupOwner == null) {
				rgroupOwner = new RgroupOwner();
				
				rgroupOwner.setUserId(owner.getOwnerId());
				rgroupOwner.setMiniopenid(ownerUser.getMiniopenid());
				rgroupOwnerRepository.save(rgroupOwner);
				
				ownerUser.setRoleId(ModelConstant.USER_ROLE_RGROUPOWNER);
				userService.save(ownerUser);
			}
			
			if (StringUtils.isEmpty(orgOperator.getOrgOperId())) {
				GroupOwnerInfo ownerInfo = new GroupOwnerInfo();
				ownerInfo.setMiniopenid(ownerUser.getMiniopenid());
				ownerInfo.setName(ownerUser.getName());
				ownerInfo.setOpenid(ownerUser.getOpenid());	//可能为空
				ownerInfo.setTel(ownerUser.getTel());
				createRgroupRequest.setCreateOwner(Boolean.TRUE);
				createRgroupRequest.setOwner(ownerInfo);
			}
			
			/*1.保存团长信息 end */

			/*2.保存rGroupRule start */
			String ruleIdStr = rgroupVo.getRuleId();
			Long ruleId = 0L;
			RgroupRule rule = null;
			if (!StringUtils.isEmpty(ruleIdStr)) {
				ruleId = Long.valueOf(ruleIdStr);
				Optional<RgroupRule> optional = rgroupRuleRepository.findById(ruleId);
				rule = optional.get();
			}
			if (rule == null) {	//新增
				rule = new RgroupRule();
			}
			rule.setDescription(rgroupVo.getDescription());
			DescriptionMore[]descMore = rgroupVo.getDescriptionMore();
			String descStr = objectMapper.writeValueAsString(descMore);
			rule.setDescriptionMore(descStr);
			
			String startDateStr = rgroupVo.getStartDate();
			startDateStr += ":00";
			Date startDate = DateUtil.parse(startDateStr, "yyyy/MM/dd HH:mm:ss");
			rule.setStartDate(startDate);
			
			String endDateStr = rgroupVo.getEndDate();
			endDateStr += ":59";
			Date endDate = DateUtil.parse(endDateStr, "yyyy/MM/dd HH:mm:ss");
			rule.setEndDate(endDate);
			rule.setFreeShippingNum(1);
			rule.setLimitNumOnce(99);
			
			ProductVO[]productList = rgroupVo.getProductList();
			ProductVO productVo = productList[0];
			String ruleName = productVo.getName() + "等" + productList.length + "件商品";
			rule.setName(ruleName);
			rule.setProductName(ruleName);
			rule.setProductType(1002);	//团购都1002
			
			int ruleStatus = ModelConstant.RULE_STATUS_OFF;
			if ("1".equals(rgroupVo.getType())) {
				ruleStatus = ModelConstant.RULE_STATUS_ON;
			}
			rule.setStatus(ruleStatus);
			rule.setSupportRegionType(ModelConstant.REGION_ALL);
			rule.setTimeoutForPay(30*60*1000l);	//半小时支付超市
			rule.setGroupMaxNum(2000);
			rule.setGroupMinNum(rgroupVo.getGroupMinNum());
//			rule.setGroupStatus(ModelConstant.GROUP_STAUS_INIT);
			
			Region region = rgroupVo.getRegion();
			rule.setOwnerId(owner.getOwnerId());
			rule.setOwnerName(owner.getOwnerName());
			rule.setOwnerImg(owner.getOwnerImg());
			rule.setOwnerAddr(region.getName() + region.getXiaoquAddress());
			rule.setOwnerTel(owner.getOwnerTel());
			rule.setAgentId(agent.getId());
			rgroupRuleRepository.save(rule);
			/*2.保存rGroupRule end */
			
			/*3.保存product start */
			for (ProductVO productView : productList) {
				String productIdStr = productView.getId();
				Product product = null;
				Long productId = 0L;
				if (!StringUtils.isEmpty(productIdStr)) {
					productId = Long.valueOf(productIdStr);
					Optional<Product> proOptional = productRepository.findById(productId);
					product = proOptional.get();
				}
				if (product == null) {
					product = new Product();
				}
				product.setName(productView.getName());
				product.setStartDate(new Date());
				String proEndDateStr = "2099-12-31 23:59:59";
				Date proEndDate = DateUtil.parse(proEndDateStr, DateUtil.dttmSimple);
				product.setEndDate(proEndDate);
				if (!StringUtils.isEmpty(productView.getMiniPrice())) {
					product.setMiniPrice(Float.valueOf(productView.getMiniPrice()));
				}
				if (!StringUtils.isEmpty(productView.getOriPrice())) {
					product.setMiniPrice(Float.valueOf(productView.getOriPrice()));
				}
				product.setSinglePrice(Float.valueOf(productView.getSinglePrice()));
				product.setOtherDesc(productView.getDescription());
				
				Thumbnail[]images = productView.getImages();
				if (images != null && images.length > 0) {
					StringBuffer bf = new StringBuffer();
					for (Thumbnail image : images) {
						bf.append(image.getUrl()).append(",");
					}
					bf.deleteCharAt(bf.length()-1);
					product.setPictures(bf.toString());
				}
				
				product.setStatus(ModelConstant.PRODUCT_ONSALE);
				String totalStr = productView.getTotalCount();
				int totalCount = Integer.MAX_VALUE - 1;
				if (!StringUtils.isEmpty(totalStr)) {
					totalCount = Integer.parseInt(totalStr);
				}
				product.setTotalCount(totalCount);
				product.setProductType("1002");
				product.setShortName(productView.getName());
				product.setTitleName(productView.getName());
				product.setAgentId(agent.getId());
				int userLimitCount = 9999;
				String limitStr = productView.getUserLimitCount();
				if (!StringUtils.isEmpty(limitStr)) {
					userLimitCount = Integer.parseInt(limitStr);
				}
				product.setUserLimitCount(userLimitCount);
				
				if (productView.getTags() != null && productView.getTags().length > 0) {
					String tagStr = objectMapper.writeValueAsString(productView.getTags());
					product.setTags(tagStr.toString());
				}
				productRepository.save(product);
				
				
				ProductRule productRule = productRuleRepository.findByRuleIdAndProductId(rule.getId(), product.getId());
				if (productRule == null) {
					productRule = new ProductRule();
				}
				productRule.setProductId(product.getId());
				productRule.setRuleId(rule.getId());
				productRuleRepository.save(productRule);
				
				//缓存库存和商品规则
				ProductRuleCache productRuleCache = new ProductRuleCache(product, rule);
				String key = ModelConstant.KEY_PRO_RULE_INFO + productRuleCache.getId()+ ":" + product.getId();
				redisRepository.setProdcutRule(key, productRuleCache);
				stringRedisTemplate.opsForValue().set(ModelConstant.KEY_PRO_STOCK + product.getId(), String.valueOf(product.getTotalCount()));	//TODO 编辑是否要改库存？
			
			}
			/*3.保存product end */
			
			/*4.保存rgroupAreaItem 当前版本只支持单个小区 start*/
			List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByRuleId(rule.getId());
			if (areaList != null && areaList.size() > 0) {
				for (RgroupAreaItem areaItem : areaList) {
					rgroupAreaItemRepository.deleteById(areaItem.getId());
				}
			}
			RgroupAreaItem rgroupAreaItem = new RgroupAreaItem();
			rgroupAreaItem.setRegionId(region.getId());
			rgroupAreaItem.setRegionType(ModelConstant.REGION_XIAOQU);
			if (ModelConstant.RULE_STATUS_ON == rule.getStatus()) {
				rgroupAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_ON);
			}else {
				rgroupAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_OFF);
			}
			long ruleCloseTime = rule.getEndDate().getTime();
			rgroupAreaItem.setRuleId(rule.getId());
			rgroupAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
			rgroupAreaItem.setSortNo(10);	
			rgroupAreaItem.setRuleName(rule.getName());
			rgroupAreaItemRepository.save(rgroupAreaItem);
			
			Region dbSect = regionRepository.findById(region.getId());
			String regionName = "";
			if (StringUtils.isEmpty(dbSect.getSectId())) {
				List<Region> distList = regionRepository.findByNameAndRegionType(dbSect.getParentName(), ModelConstant.REGION_COUNTY);
				Region dist = distList.get(0);
				List<Region> cityList = regionRepository.findByNameAndRegionType(dist.getParentName(), ModelConstant.REGION_CITY);
				Region city = cityList.get(0);
				createRgroupRequest.setCreateSect(Boolean.TRUE);
				List<Sect> sectList = new ArrayList<>();
				Sect sect = new Sect();
				sect.setProvince(city.getParentName());
				sect.setCity(dist.getParentName());
				sect.setDistrict(dbSect.getParentName());
				sect.setSectName(dbSect.getName());
				sect.setSectAddr(dbSect.getXiaoquAddress());
				sectList.add(sect);
				createRgroupRequest.setSects(sectList);
				regionName = sect.getProvince();
			}
			
			if (createRgroupRequest.getCreateOwner() || createRgroupRequest.getCreateSect()) {
				CreateRgroupRequest response = eshopUtil.createRgroup(regionName, createRgroupRequest);
				List<Sect> sectList = response.getSects();
				
				if (createRgroupRequest.getCreateSect() && sectList != null && !sectList.isEmpty()) {
					dbSect.setSectId(sectList.get(0).getSectId());
					regionRepository.save(dbSect);
					if (StringUtils.isEmpty(ownerUser.getSectId())) {
						ownerUser.setSectId(dbSect.getSectId());
						ownerUser.setCspId(sectList.get(0).getCspId());
						ownerUser.setXiaoquName(dbSect.getName());
						userService.save(ownerUser);
					}
				}
				GroupOwnerInfo groupOwner = response.getOwner();
				if (createRgroupRequest.getCreateOwner() && groupOwner!=null) {
					orgOperator.setOrgOperId(groupOwner.getOrgOperId());
					orgOperator.setOrgType(groupOwner.getOrgType());
					orgOperatorRepository.save(orgOperator);
					
					rgroupOwner.setFeeRate(groupOwner.getFeeRate());
					rgroupOwnerRepository.save(rgroupOwner);
				}
				
			}
			/*4.保存rgroupAreaItem 当前版本只支持单个小区 end*/
			
			return rule.getId();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizValidateException("保存团购内容失败。");
		}
		
	}
	
	@Override
	public void pubRgroup(String rgroupRuleId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 获取团购信息
	 * @param rgroupRuleId
	 */
	@Override
	public RgroupVO queryRgroupByRule(String rgroupRuleId, boolean isOnsale) {
		
		Assert.hasText(rgroupRuleId, "团购id不能为空。");
		RgroupVO vo = new RgroupVO();
		try {
			Long ruleId = Long.valueOf(rgroupRuleId);
			
			List<Integer> statusList = new ArrayList<>();
			if (isOnsale) {
				statusList.add(ModelConstant.RULE_STATUS_ON);
			} else {
				statusList.add(ModelConstant.RULE_STATUS_ON);
				statusList.add(ModelConstant.RULE_STATUS_OFF);
			}
			
			RgroupRule rule = rgroupRuleRepository.findByIdAndStatusIn(ruleId, statusList);
			if (rule == null) {
				if (isOnsale) {
					throw new BizValidateException("团购已下架");
				} else {
					throw new BizValidateException("未查询到团购, id : " + rgroupRuleId);
				}
			}
			vo.setRuleId(String.valueOf(rule.getId()));
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			String descMoreStr = rule.getDescriptionMore();
			TypeReference<DescriptionMore[]> typeReference = new TypeReference<RgroupVO.DescriptionMore[]>() {};
			DescriptionMore[]descriptionMore = objectMapper.readValue(descMoreStr, typeReference);
			vo.setDescription(rule.getDescription());
			vo.setDescriptionMore(descriptionMore);
			vo.setStatus(rule.getStatus());
			
			List<String> descMoreImages = new ArrayList<>();
			for (DescriptionMore descM : descriptionMore) {
				String imageUrl = descM.getImage();
				if (!StringUtils.isEmpty(imageUrl)) {
					descMoreImages.add(imageUrl);
				}
				Thumbnail[]thumbnails = descM.getThumbnail();
				if (thumbnails!=null && thumbnails.length>0) {
					for (Thumbnail thumbnail : thumbnails) {
						if (!StringUtils.isEmpty(thumbnail.getUrl())) {
							descMoreImages.add(thumbnail.getUrl());
						}
					}
				}
			}
			vo.setDescMoreImages(descMoreImages);
			
			int ruleStatus = rule.getStatus();
			String type = "0";
			if (ModelConstant.RULE_STATUS_ON == ruleStatus) {
				type = "1";
			}
			vo.setType(type);
			Date startDate = rule.getStartDate();
			String dtFormat = "yyyy/MM/dd HH:mm";
			String startDateStr = DateUtil.dtFormat(startDate, dtFormat);
			vo.setStartDate(startDateStr);
			vo.setStartDateMills(startDate.getTime());
			Date endDate = rule.getEndDate();
			String endDateStr = DateUtil.dtFormat(endDate, dtFormat);
			vo.setEndDate(endDateStr);
			vo.setEndDateMills(endDate.getTime());
			if (isOnsale) {	//如果只查询上架的商品，需要判断团购结束的时间
				if (endDate.getTime() - System.currentTimeMillis() <= 0) {
					throw new BizValidateException("团购已结束");
				}
			}
			
			vo.setGroupMinNum(rule.getGroupMinNum());
			long createDate = rule.getCreateDate();
			vo.setCreateDate(DateUtil.getSendTime(createDate));
			
			List<Product> productList = productRepository.findMultiByRuleId(ruleId);
			List<ProductVO> proVoList = new ArrayList<>();

			TypeReference<Tag[]> tagRefer = new TypeReference<Tag[]>() {};
			for (Product product : productList) {
				ProductVO proVo = new ProductVO();
				proVo.setId(String.valueOf(product.getId()));
				proVo.setStatus(product.getStatus());
				proVo.setDescription(product.getOtherDesc());
				String picsStr = product.getPictures();
				List<Thumbnail> thumbnailList = new ArrayList<>();
				String[]imageList = null;
				if (!StringUtils.isEmpty(picsStr)) {
					String[]pics = picsStr.split(",");
					imageList= pics;
					for (String imgUrl : pics) {
						Thumbnail thumbnail = new Thumbnail();
						thumbnail.setUrl(imgUrl);
						thumbnailList.add(thumbnail);
					}
					
				}
				proVo.setImageList(imageList);
				proVo.setImages(thumbnailList.toArray(new Thumbnail[0]));
				proVo.setName(product.getName());
				
				proVo.setSinglePrice(String.valueOf(product.getSinglePrice()));
				String miniPriceStr = "";
				if (product.getMiniPrice() > 0f) {
					miniPriceStr = String.valueOf(product.getMiniPrice());
				}
				proVo.setMiniPrice(miniPriceStr);
				
				String oriPriceStr = "";
				if (product.getOriPrice() > 0f) {
					oriPriceStr = String.valueOf(product.getOriPrice());
				}
				proVo.setOriPrice(oriPriceStr);
				
				String stockStr = String.valueOf(product.getTotalCount());
				if (product.getTotalCount() > 1000000) {
					stockStr = "";
				}
				proVo.setTotalCount(stockStr);
				String userLimitCount = String.valueOf(product.getUserLimitCount());
				if (product.getUserLimitCount() == 9999) {
					userLimitCount = "";
				}
				proVo.setUserLimitCount(userLimitCount);
				String tagStr = product.getTags();
				if (!StringUtils.isEmpty(tagStr)) {
					Tag[]tags = objectMapper.readValue(tagStr, tagRefer);
					proVo.setTags(tags);
				}
				proVoList.add(proVo);
			}
			vo.setProductList(proVoList.toArray(new ProductVO[0]));
			
			List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByRuleId(ruleId);
			if (areaList != null && areaList.size() > 0) {
				RgroupAreaItem areaItem = areaList.get(0);
				Region region = regionRepository.findById(areaItem.getRegionId());
				vo.setRegion(region);
			}
			
			RgroupOwner rgroupOwner = rgroupOwnerRepository.findByUserId(rule.getOwnerId());
			RgroupOwnerVO rgroupOwnerVO = new RgroupOwnerVO();
			rgroupOwnerVO.setAttendees(rgroupOwner.getAttendees());
			rgroupOwnerVO.setFollowers(rgroupOwner.getFollowers());
			rgroupOwnerVO.setMembers(rgroupOwner.getMembers());
			rgroupOwnerVO.setOwnerId(rule.getOwnerId());
			rgroupOwnerVO.setOwnerName(rule.getOwnerName());
			rgroupOwnerVO.setOwnerImg(rule.getOwnerImg());
			rgroupOwnerVO.setOwnerTel(rule.getOwnerTel());
			vo.setRgroupOwner(rgroupOwnerVO);
			
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw new BizValidateException(e.getMessage());
		}
		
		return vo;
	}
	
	/**
	 * 更新团购状态
	 * @param ruleId
	 */
	@Override
	@Transactional
	public void updateRgroupStatus(long ruleId, boolean isPub) {
		
		int ruleStatus = ModelConstant.RULE_STATUS_ON;
		int distributionStatus = ModelConstant.DISTRIBUTION_STATUS_ON;
		if (!isPub) {
			ruleStatus = ModelConstant.RULE_STATUS_OFF;
			distributionStatus = ModelConstant.DISTRIBUTION_STATUS_OFF;
		}
		
		if (ruleId == 0l) {
			throw new BizValidateException("团购id不能为空");
		}
		Optional<RgroupRule> ruleOptional = rgroupRuleRepository.findById(ruleId); 
		if (ruleOptional == null) {
			throw new BizValidateException("未查询到团购, id: " + ruleId);
		}
		RgroupRule rule = ruleOptional.get();
		if (rule == null) {
			throw new BizValidateException("未查询到团购, id: " + ruleId);
		}
		
		rule.setStatus(ruleStatus);
		rgroupRuleRepository.save(rule);
		
		List<RgroupAreaItem> itemList = rgroupAreaItemRepository.findByRuleId(ruleId);
		for (RgroupAreaItem rgroupAreaItem : itemList) {
			rgroupAreaItem.setStatus(distributionStatus);
		}
		rgroupAreaItemRepository.saveAll(itemList);
	}

}
