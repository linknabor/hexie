package com.yumu.hexie.service.common.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.eshop.mapper.QueryRgroupSectsMapper;
import com.yumu.hexie.integration.eshop.req.CreateRgroupRequest;
import com.yumu.hexie.integration.eshop.req.InviteLeaderRequest;
import com.yumu.hexie.integration.eshop.req.CreateRgroupRequest.GroupOwnerInfo;
import com.yumu.hexie.integration.eshop.req.CreateRgroupRequest.Sect;
import com.yumu.hexie.integration.eshop.resp.InviteLeaderResp;
import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
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
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.rgroup.RgroupUser;
import com.yumu.hexie.model.market.rgroup.RgroupUserRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.OrgOperatorRepository;
import com.yumu.hexie.model.user.RgroupOwner;
import com.yumu.hexie.model.user.RgroupOwnerRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.RgroupV3Service;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.RgroupAreaRegionMapper;
import com.yumu.hexie.vo.RgroupOrderRecordVO;
import com.yumu.hexie.vo.RgroupRecordsVO;
import com.yumu.hexie.vo.RgroupSubscribeVO;
import com.yumu.hexie.vo.RgroupVO;
import com.yumu.hexie.vo.RgroupVO.DescriptionMore;
import com.yumu.hexie.vo.RgroupVO.ProductVO;
import com.yumu.hexie.vo.RgroupVO.RegionVo;
import com.yumu.hexie.vo.RgroupVO.RgroupOwnerVO;
import com.yumu.hexie.vo.RgroupVO.Tag;
import com.yumu.hexie.vo.RgroupVO.Thumbnail;

@Service
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
	private RgroupUserRepository rgroupUserRepository;
	@Autowired 
	private OrderItemRepository orderItemRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private EshopUtil eshopUtil;
	@Autowired
	private TemplateMsgService templateMsgService;
	@Autowired
	private SystemConfigService systemConfigService;
	
	
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
		
		RegionVo[]regions = rgroupVo.getRegions();
		if (regions == null) {	//兼容旧版本
			Region formRegion = rgroupVo.getRegion();
			if (formRegion == null) {
				Assert.notNull(formRegion, "团购区域不能为空");
			} else {
				regions = new RegionVo[1];
				RegionVo vo = new RegionVo();
				vo.setId(formRegion.getId());
				vo.setName(formRegion.getName());
				vo.setSectId(formRegion.getSectId());
				vo.setXiaoquAddress(formRegion.getXiaoquAddress());
				vo.setLatitude(formRegion.getLatitude());
				vo.setLongitude(formRegion.getLongitude());
				regions[0] = vo;
				rgroupVo.setRegions(regions);
			}
		} else {
			Assert.noNullElements(rgroupVo.getRegions(), "团购区域不能为空");
		}
		
		try {
			
			boolean isCopy = false;	//是否拷贝开团
			if ("copy".equals(rgroupVo.getAction())) {
				isCopy = true;
			}
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			CreateRgroupRequest createRgroupRequest = new CreateRgroupRequest();
			
			/*1.保存团长信息 start */
			RgroupOwnerVO owner = rgroupVo.getRgroupOwner();
			User ownerUser = null;
			List<User> userList = userService.getByTel(owner.getOwnerTel());
			if (userList == null || userList.isEmpty()) {
				throw new BizValidateException("未查询到用户，手机：" + owner.getOwnerTel());
			}
			if (userList.size() > 1) {
				for (User user : userList) {
					if (!StringUtils.isEmpty(user.getMiniopenid())) {
						ownerUser = user;
						break;
					}
				}
			} else {
				ownerUser = userList.get(0);
			}
			
			OrgOperator orgOperator = orgOperatorRepository.findByUserIdAndRoleId(ownerUser.getId(), ModelConstant.USER_ROLE_RGROUPOWNER);
			Agent agent = null;
			
			if (orgOperator == null) {
				agent = agentRepository.findByAgentNo("000000000000");
				orgOperator = new OrgOperator();
				orgOperator.setOrgId(agent.getAgentNo());
				orgOperator.setOrgName(agent.getName());
				orgOperator.setOrgOperName(owner.getOwnerName());
				orgOperator.setRoleId(ModelConstant.USER_ROLE_RGROUPOWNER);
				orgOperator.setUserId(ownerUser.getId());
				orgOperator.setOrgOperId("");		//TODO 联动backmng后回调
				orgOperator.setOrgType("");	//联动后回调
				orgOperatorRepository.save(orgOperator);
				
			} else {
				agent = agentRepository.findByAgentNo(orgOperator.getOrgId());
			}
			
			RgroupOwner rgroupOwner = rgroupOwnerRepository.findByUserId(ownerUser.getId());
			if (rgroupOwner == null) {
				rgroupOwner = new RgroupOwner();
				
				rgroupOwner.setUserId(ownerUser.getId());
				rgroupOwner.setMiniopenid(ownerUser.getMiniopenid());
				rgroupOwner.setMiniappid(ownerUser.getMiniAppId());
				rgroupOwner.setName(ownerUser.getName());
				rgroupOwner.setHeadImgUrl(ownerUser.getHeadimgurl());
				rgroupOwner.setTel(ownerUser.getTel());
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
			if (isCopy) {
				ruleIdStr = "";
			}
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
			} else if ("2".equals(rgroupVo.getType())) {
				ruleStatus = ModelConstant.RULE_STATUS_ON;
			}
			rule.setStatus(ruleStatus);
			rule.setSupportRegionType(ModelConstant.REGION_ALL);
			rule.setTimeoutForPay(30*60*1000l);	//半小时支付超市
			rule.setGroupMaxNum(2000);
			rule.setGroupMinNum(rgroupVo.getGroupMinNum());
			
			if (ModelConstant.RULE_STATUS_ON == ruleStatus) {
				rule.setGroupStatus(ModelConstant.GROUP_STAUS_GROUPING);
			} else {
				rule.setGroupStatus(ModelConstant.GROUP_STAUS_INIT);
			}
			RegionVo region = rgroupVo.getRegions()[0];
			rule.setOwnerId(ownerUser.getId());
			rule.setOwnerName(owner.getOwnerName());
			rule.setOwnerImg(owner.getOwnerImg());
			rule.setOwnerSect(region.getName());
			rule.setSectCount(rgroupVo.getRegions().length);
//			rule.setOwnerAddr(region.getXiaoquAddress());
			rule.setOwnerTel(owner.getOwnerTel());
			rule.setAgentId(agent.getId());
			rule.setUpdateDate(new Date());
			rgroupRuleRepository.save(rule);
			/*2.保存rGroupRule end */
			
			/*3.保存product start */
			for (ProductVO productView : productList) {
				String productIdStr = productView.getId();
				if (isCopy) {
					productIdStr = "";
				}
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
					product.setOriPrice(Float.valueOf(productView.getOriPrice()));
				}
				product.setSinglePrice(Float.valueOf(productView.getSinglePrice()));
				String desc = "";
				if (!StringUtils.isEmpty(productView.getDescription())) {
					desc = productView.getDescription();
				}
				product.setOtherDesc(desc);
				
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
				product.setDepotId(0l);
				if (!StringUtils.isEmpty(productView.getDepotId())) {
					product.setDepotId(Long.valueOf(productView.getDepotId()));
				}
				product.setSpecs(productView.getSpecs());
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
			
			/*4.保存rgroupAreaItem 20220926支持多小区 start*/
			List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByOriRuleId(rule.getId());
			Map<Long, RgroupAreaItem> areaMap =new HashMap<>();
			if (areaList != null && areaList.size() > 0) {
				for (RgroupAreaItem areaItem : areaList) {
					areaMap.put(areaItem.getRegionId(), areaItem);
					areaItem.setRuleId(0l);
					rgroupAreaItemRepository.save(areaItem);
				}
			}
			List<Sect> sectList = new ArrayList<>();
			Map<Long, Region> dbSects = new HashMap<>();
			for (int i = 0; i < rgroupVo.getRegions().length; i++) {
				
				RegionVo regionVo = rgroupVo.getRegions()[i];
				RgroupAreaItem rgroupAreaItem = areaMap.get(regionVo.getId());
				if (rgroupAreaItem == null) {
					rgroupAreaItem = new RgroupAreaItem();
					rgroupAreaItem.setRegionId(regionVo.getId());
					rgroupAreaItem.setRegionType(ModelConstant.REGION_XIAOQU);
				}
				
				if (ModelConstant.RULE_STATUS_ON == rule.getStatus()) {
					rgroupAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_ON);
				}else {
					rgroupAreaItem.setStatus(ModelConstant.DISTRIBUTION_STATUS_OFF);
				}
				long ruleCloseTime = rule.getEndDate().getTime();
				rgroupAreaItem.setRuleId(rule.getId());
				rgroupAreaItem.setOriRuleId(rule.getId());
				rgroupAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
				rgroupAreaItem.setSortNo(10);	
				rgroupAreaItem.setRuleName(rule.getName());
				Integer miniNum = 1;
				if (!StringUtils.isEmpty(regionVo.getMiniNum())) {
					miniNum = Integer.valueOf(regionVo.getMiniNum());
				}
				rgroupAreaItem.setGroupMinNum(miniNum);
				rgroupAreaItem.setGroupStatus(ModelConstant.GROUP_STAUS_GROUPING);
				rgroupAreaItem.setRemark(regionVo.getRemark());
				rgroupAreaItemRepository.save(rgroupAreaItem);
				
				Region dbSect = regionRepository.findById(regionVo.getId());
				if (StringUtils.isEmpty(dbSect.getSectId())) {
					List<Region> distList = regionRepository.findByNameAndRegionType(dbSect.getParentName(), ModelConstant.REGION_COUNTY);
					Region dist = distList.get(0);
					List<Region> cityList = regionRepository.findByNameAndRegionType(dist.getParentName(), ModelConstant.REGION_CITY);
					Region city = cityList.get(0);
					createRgroupRequest.setCreateSect(Boolean.TRUE);
					
					Sect sect = new Sect();
					sect.setRegionId(dbSect.getId());
					sect.setProvince(city.getParentName());
					sect.setCity(dist.getParentName());
					sect.setDistrict(dbSect.getParentName());
					sect.setSectName(dbSect.getName());
					sect.setSectAddr(dbSect.getXiaoquAddress());
					sectList.add(sect);
				}
				dbSects.put(dbSect.getId(), dbSect);
				
			}
			createRgroupRequest.setSects(sectList);
			
			if (createRgroupRequest.getCreateOwner() || createRgroupRequest.getCreateSect()) {
				CreateRgroupRequest response = eshopUtil.createRgroup("", createRgroupRequest);
				List<Sect> respSectList = response.getSects();
				if (createRgroupRequest.getCreateSect() && respSectList != null && !respSectList.isEmpty()) {
					for (Sect sect : respSectList) {
						Region dbSect = dbSects.get(sect.getRegionId());
						dbSect.setSectId(sect.getSectId());
						regionRepository.save(dbSect);
					}
					
					if (StringUtils.isEmpty(ownerUser.getSectId())) {
						ownerUser.setSectId(respSectList.get(0).getSectId());
						ownerUser.setCspId(sectList.get(0).getCspId());
						ownerUser.setXiaoquName(respSectList.get(0).getSectName());
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
			
			/*通知订阅*/
			if (ModelConstant.RULE_STATUS_ON == ruleStatus) {
				Map<String, String> map = new HashMap<>();
				map.put("ruleId", String.valueOf(rule.getId()));
				String queue = objectMapper.writeValueAsString(map);
				stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_RGROUP_PUB_QUEUE, queue);
			}
			return rule.getId();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizValidateException("保存团购内容失败。");
		}
		
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
				statusList.add(ModelConstant.RULE_STATUS_END);
			}
			
			RgroupRule rule = rgroupRuleRepository.findByIdAndStatusIn(ruleId, statusList);
			if (rule == null) {
				throw new BizValidateException("未查询到团购, id : " + rgroupRuleId);
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
//			if (isOnsale) {	//如果只查询上架的商品，需要判断团购结束的时间
//				if (endDate.getTime() - System.currentTimeMillis() <= 0) {
//					throw new BizValidateException("团购已结束");
//				}
//			}
			
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
				proVo.setSpecs(product.getSpecs());
				proVoList.add(proVo);
			}
			vo.setProductList(proVoList.toArray(new ProductVO[0]));
			
			List<Object[]> areaList = rgroupAreaItemRepository.findWithRegionByRuleId(ruleId);
			List<RgroupAreaRegionMapper> regionList = ObjectToBeanUtils.objectToBean(areaList, RgroupAreaRegionMapper.class);
			if (regionList == null) {
				regionList = new ArrayList<>();
			}
			RegionVo[] regionVos = new RegionVo[regionList.size()];
			for (int j = 0; j < regionList.size(); j++) {
				RgroupAreaRegionMapper areaRegionMapper = regionList.get(j);
				RegionVo regionVo = new RegionVo();
				regionVo.setId(areaRegionMapper.getId().longValue());
				regionVo.setName(areaRegionMapper.getName());
				regionVo.setXiaoquAddress(areaRegionMapper.getXiaoquAddress());
				regionVo.setMiniNum(String.valueOf(areaRegionMapper.getGroupMinNum()));
				regionVo.setCurrentNum(String.valueOf(areaRegionMapper.getCurrentNum()));
				regionVo.setRemark(areaRegionMapper.getRemark());
				regionVo.setLatitude(areaRegionMapper.getLatitude());
				regionVo.setLongitude(areaRegionMapper.getLongitude());
				regionVo.setSectId(areaRegionMapper.getSectId());
				regionVos[j] = regionVo;
			}
			vo.setRegions(regionVos);
			
			Region region = regionRepository.findById(regionVos[0].getId());	//TODO 兼容旧版本
			vo.setRegion(region);	//TODO 兼容旧版本
			
			RgroupOwnerVO rgroupOwnerVO = new RgroupOwnerVO();
			int attendees = 0;
			String attendeesStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ORDERED + rule.getOwnerId());
			if (!StringUtils.isEmpty(attendeesStr)) {
				attendees = Integer.parseInt(attendeesStr);
			}
			rgroupOwnerVO.setAttendees(attendees);
			int members = 0;
			String membersStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ACCESSED + rule.getOwnerId());
			if (!StringUtils.isEmpty(membersStr)) {
				members = Integer.parseInt(membersStr);
			}
			rgroupOwnerVO.setMembers(members);
			rgroupOwnerVO.setOwnerId(rule.getOwnerId());
			rgroupOwnerVO.setOwnerName(rule.getOwnerName());
			rgroupOwnerVO.setOwnerImg(rule.getOwnerImg());
			rgroupOwnerVO.setOwnerTel(rule.getOwnerTel());
			vo.setRgroupOwner(rgroupOwnerVO);
			
			int groupAccessed = 0;
			String groupAccessedStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_GROUP_ACCESSED + rule.getId());
			if (!StringUtils.isEmpty(groupAccessedStr)) {
				groupAccessed = Integer.parseInt(groupAccessedStr);
			}
			vo.setAccessed(groupAccessed);
			int groupOrdered = 0;
			String groupOrderedStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_GROUP_ORDERED + rule.getId());
			if (!StringUtils.isEmpty(groupOrderedStr)) {
				groupOrdered = Integer.parseInt(groupOrderedStr);
			}
			vo.setOrdered(groupOrdered);
			
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw new BizValidateException(e.getMessage());
		}
		
		return vo;
	}
	
	/**
	 * 更新团购状态
	 * @param ruleId
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void updateRgroupStatus(long ruleId, boolean isPub) throws Exception {
		
		int ruleStatus = ModelConstant.RULE_STATUS_ON;
		int distributionStatus = ModelConstant.DISTRIBUTION_STATUS_ON;
		int groupStatus = ModelConstant.GROUP_STAUS_GROUPING; 
		if (!isPub) {
			ruleStatus = ModelConstant.RULE_STATUS_OFF;
			distributionStatus = ModelConstant.DISTRIBUTION_STATUS_OFF;
			groupStatus = ModelConstant.GROUP_STAUS_INIT;
		}
		if (ruleId == 0l) {
			throw new BizValidateException("团购id不能为空");
		}
		RgroupRule rule = rgroupRuleRepository.findById(ruleId); 
		if (rule == null) {
			throw new BizValidateException("未查询到团购, id: " + ruleId);
		}
		
		rule.setStatus(ruleStatus);
		rule.setGroupStatus(groupStatus);
		rgroupRuleRepository.save(rule);
		
		List<RgroupAreaItem> itemList = rgroupAreaItemRepository.findByRuleId(ruleId);
		for (RgroupAreaItem rgroupAreaItem : itemList) {
			rgroupAreaItem.setStatus(distributionStatus);
		}
		rgroupAreaItemRepository.saveAll(itemList);
		
		if (isPub) {
			/*通知订阅*/
			if (ModelConstant.RULE_STATUS_ON == ruleStatus) {
				Map<String, String> map = new HashMap<>();
				map.put("ruleId", String.valueOf(rule.getId()));
				String queue = JacksonJsonUtil.getMapperInstance(false).writeValueAsString(map);
				stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_RGROUP_PUB_QUEUE, queue);
			}
		}
	}
	
	/**
	 * 获取团长下面的所有团购里里列表
	 * @param rgroupRuleId
	 */
	@Override
	public List<RgroupVO> queryOwnerRgroups(User user, String title, int currentPage) {
		
		List<RgroupVO> voList = new ArrayList<>();
		try {
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "updateDate");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			Pageable pageable = PageRequest.of(currentPage, 10, sort);
			Page<RgroupRule> pages = rgroupRuleRepository.findByOwnerIdAndDescriptionLike(user.getId(), title, pageable);
			List<RgroupRule> ruleList = pages.getContent();
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			long currentMills = System.currentTimeMillis();
			for (RgroupRule rgroupRule : ruleList) {
				RgroupVO vo = new RgroupVO();
				String descMoreStr = rgroupRule.getDescriptionMore();
				DescriptionMore[]descriptionMore = new DescriptionMore[0];
				if (!StringUtils.isEmpty(descMoreStr)) {
					TypeReference<DescriptionMore[]> typeReference = new TypeReference<RgroupVO.DescriptionMore[]>() {};
					descriptionMore = objectMapper.readValue(descMoreStr, typeReference);
				}
				
				List<String> descMoreImages = new ArrayList<>();
				for (DescriptionMore descMore : descriptionMore) {
					if ("0".equals(descMore.getType())) {
						continue;
					} else if ("1".equals(descMore.getType())) {
						Thumbnail[]thumbnails = descMore.getThumbnail();
						if (thumbnails == null || thumbnails.length == 0) {
							continue;
						}
						for (Thumbnail thumbnail : thumbnails) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(thumbnail.getUrl());
							}
						}
					} else if ("2".equals(descMore.getType())) {
						if (!StringUtils.isEmpty(descMore.getImage())) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(descMore.getImage());
							}
						}
					}
				}
				
				List<Product> productList = productRepository.findMultiByRuleId(rgroupRule.getId());
				float miniPrice = 0f;
				float maxPrice = 0f;
				for (int i = 0; i < productList.size(); i++) {
					Product product = productList.get(i);
					if (descMoreImages.size() < 3) {
						String[]pics = product.getPictureList();
						for (String pic : pics) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(pic);
							}
						}
					}
					float singlePrice = product.getSinglePrice();
					if (i == 0) {
						miniPrice = singlePrice;
					} else {
						if (singlePrice < miniPrice) {
							miniPrice = singlePrice;
						}
					}
					if (singlePrice > maxPrice) {
						maxPrice = singlePrice;
					}
				}
				String pricePeriod = miniPrice + "~" + maxPrice;
				if (miniPrice == 0f || miniPrice == maxPrice) {
					pricePeriod = String.valueOf(maxPrice);
				}
				vo.setPricePeriod(pricePeriod);
				vo.setRuleId(String.valueOf(rgroupRule.getId()));
				vo.setDescMoreImages(descMoreImages);
				vo.setDescription(rgroupRule.getDescription());
				Date updateDate = rgroupRule.getUpdateDate();
				String updateDateStr = "";
				if (updateDate != null) {
					if ((currentMills - updateDate.getTime()) > (3600 * 24 * 1000l)) {
						updateDateStr = DateUtil.dtFormat(updateDate, "MM-dd");
						if (!StringUtils.isEmpty(updateDateStr)) {
							String[]dateArr = updateDateStr.split("-");
							updateDateStr = dateArr[0] + "月" + dateArr[1] + "日";
						}
					} else {
						updateDateStr = DateUtil.getSendTime(updateDate.getTime());
						
					}
					vo.setUpdateDate(updateDateStr);
				}
				Date startDate = rgroupRule.getStartDate();
				Date endDate = rgroupRule.getEndDate();
				vo.setStartDateMills(startDate.getTime());
				vo.setEndDateMills(endDate.getTime());
				vo.setStatus(rgroupRule.getStatus());
				voList.add(vo);
			}
			
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw new BizValidateException(e.getMessage());
		}
		
		return voList;
	}
	
	/**
	 * 团购记录
	 */
	@Override
	public RgroupRecordsVO queryOrderRecords(String ruleIdStr, int currentPage) {
		
		RgroupRecordsVO vo = new RgroupRecordsVO();
		List<RgroupOrderRecordVO> orderRecords = new ArrayList<>();
		if (StringUtils.isEmpty(ruleIdStr)) {
			return vo;
		}
		Long ruleId = Long.valueOf(ruleIdStr);
		
		List<Order> orderList = new ArrayList<>();
    	Order order = new Order(Direction.DESC, "createDate");
    	orderList.add(order);
    	Sort sort = Sort.by(orderList);
		Pageable pageable = PageRequest.of(currentPage, 10, sort);
		Page<RgroupUser> rgroupUsers = rgroupUserRepository.findAllByRuleId(ruleId, pageable);
		
		for (RgroupUser rgroupUser : rgroupUsers) {
			RgroupOrderRecordVO record = new RgroupOrderRecordVO();
			String recordDate = DateUtil.getSendTime(rgroupUser.getCreateDate());
			record.setRecordDate(recordDate);
			record.setUserId(rgroupUser.getUserId());
			record.setUserName(rgroupUser.getUserName());
			record.setHeadUrl(rgroupUser.getHeadUrl());
			record.setRuleId(ruleId);
			record.setTel(rgroupUser.getTel());
			List<OrderItem> items = orderItemRepository.findByRuleIdAndUserIdAndOrderId(ruleId, rgroupUser.getUserId(), rgroupUser.getOrderId());
			record.setItems(items);
			orderRecords.add(record);
		}
		vo.setRecords(orderRecords);
		vo.setTotalSize(rgroupUsers.getTotalElements());
		return vo;
	}
	
	@Override
	public List<Map<String, String>> getRefundReason () {
		
		List<Map<String, String>> reasonList = new ArrayList<>();
		Map<String, String> reason = new HashMap<>();
		reason.put("name", "多拍、错拍、不想要");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "没时间去拿");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "大小尺寸与商品描述不符");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "收到商品少见件、破损或污渍");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "团长未发货");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "团长缺货");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "不喜欢、效果不好");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "材质、面料与商品描述不符合");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "质量问题");
		reasonList.add(reason);
		
		reason = new HashMap<>();
		reason.put("name", "其他");
		reasonList.add(reason);
		
		return reasonList;
	}
	
	/**
	 * 团购记录
	 */
	@Override
	public List<Product> getProductFromSales(User user, String productName, List<String>excludeDepotIds, int currentPage) {
		
		List<Order> orderList = new ArrayList<>();
    	Order order = new Order(Direction.DESC, "createDate");
    	orderList.add(order);
    	Sort sort = Sort.by(orderList);
		Pageable pageable = PageRequest.of(currentPage, 10, sort);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, - 6);
		Date createDate = c.getTime();
		Page<Product> products = productRepository.findProductFromSalesByOwner(user.getId(), createDate, productName, excludeDepotIds, pageable);
		return products.getContent();
	}
	
	/**
	 * 获取正在进行团购的小区列表
	 * @throws Exception 
	 */
	@Override
	public List<QueryRgroupSectsMapper> getGroupSects(User user, String sectName, int currentPage) throws Exception {
		
		List<Order> orderList = new ArrayList<>();
    	Order order = new Order(Direction.DESC, "createDate");
    	orderList.add(order);
    	order = new Order(Direction.ASC, "status");
    	orderList.add(order);
    	Sort sort = Sort.by(orderList);
		Pageable pageable = PageRequest.of(currentPage, 10, sort);
		List<Integer> statusList = new ArrayList<>();
		statusList.add(ModelConstant.RULE_STATUS_ON);
		statusList.add(ModelConstant.RULE_STATUS_END);
		Page<Object[]> page = rgroupRuleRepository.findGroupSects(statusList, sectName, pageable);
		List<QueryRgroupSectsMapper> list = null;
		if (page != null) {
			list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryRgroupSectsMapper.class);
		}
		if (list == null) {
			list = new ArrayList<>();
		}
		
		for (QueryRgroupSectsMapper queryRgroupSectsMapper : list) {
			List<RgroupRule> rules = rgroupRuleRepository.findByAreaItem(statusList, 0l, queryRgroupSectsMapper.getId().longValue());
			for (RgroupRule rule : rules) {
				RgroupVO vo = new RgroupVO();
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String descMoreStr = rule.getDescriptionMore();
				DescriptionMore[]descriptionMore = new DescriptionMore[0];
				if (!StringUtils.isEmpty(descMoreStr)) {
					TypeReference<DescriptionMore[]> typeReference = new TypeReference<RgroupVO.DescriptionMore[]>() {};
					descriptionMore = objectMapper.readValue(descMoreStr, typeReference);
					vo.setDescription(rule.getDescription());
					vo.setDescriptionMore(descriptionMore);
					vo.setStatus(rule.getStatus());
				}
				
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
				
				List<String> images = queryRgroupSectsMapper.getGroupImages();
				if (images == null) {
					images = new ArrayList<>();
				}
				if (vo.getDescMoreImages()!= null) {
					images.addAll(vo.getDescMoreImages());
				}
				queryRgroupSectsMapper.setGroupImages(images);
			}
			
		}
		
		return list;
	}
	
	/**
	 * 获取某一小区小的团购列表
	 * @throws Exception 
	 */
	@Override
	public List<RgroupVO> getSectGroups(User user, String regionId, String title, int currentPage) throws Exception {
		
		Assert.hasLength(regionId, "小区id不能为空。");
		
		List<RgroupVO> voList = new ArrayList<>();
		try {
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "updateDate");
	    	orderList.add(order);
	    	order = new Order(Direction.ASC, "status");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			Pageable pageable = PageRequest.of(currentPage, 10, sort);
			
			if (StringUtils.isEmpty(title)) {
				title = "";
			}
			List<Integer> statusList = new ArrayList<>();
			statusList.add(ModelConstant.RULE_STATUS_ON);
			statusList.add(ModelConstant.RULE_STATUS_END);
			Page<RgroupRule> pages = rgroupRuleRepository.findByRegionId(statusList, Long.valueOf(regionId), title, pageable);
			List<RgroupRule> ruleList = pages.getContent();
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			long currentMills = System.currentTimeMillis();
			for (RgroupRule rgroupRule : ruleList) {
				RgroupVO vo = new RgroupVO();
				String descMoreStr = rgroupRule.getDescriptionMore();
				TypeReference<DescriptionMore[]> typeReference = new TypeReference<RgroupVO.DescriptionMore[]>() {};
				DescriptionMore[]descriptionMore = null;
				if (!StringUtils.isEmpty(descMoreStr)) {
					descriptionMore = objectMapper.readValue(descMoreStr, typeReference);
				} else {
					descriptionMore = new DescriptionMore[0];
				}
				RgroupOwnerVO rgroupOwnerVO = new RgroupOwnerVO();
				int attendees = 0;
				String attendeesStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ORDERED + rgroupRule.getOwnerId());
				if (!StringUtils.isEmpty(attendeesStr)) {
					attendees = Integer.parseInt(attendeesStr);
				}
				rgroupOwnerVO.setAttendees(attendees);
				int members = 0;
				String membersStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ACCESSED + rgroupRule.getOwnerId());
				if (!StringUtils.isEmpty(membersStr)) {
					members = Integer.parseInt(membersStr);
				}
				rgroupOwnerVO.setMembers(members);
				rgroupOwnerVO.setOwnerId(rgroupRule.getOwnerId());
				rgroupOwnerVO.setOwnerName(rgroupRule.getOwnerName());
				rgroupOwnerVO.setOwnerImg(rgroupRule.getOwnerImg());
				rgroupOwnerVO.setOwnerTel(rgroupRule.getOwnerTel());
				vo.setRgroupOwner(rgroupOwnerVO);
				
				int groupAttendees = 0;
				String groupAttendeesStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_GROUP_ORDERED + rgroupRule.getId());
				if (!StringUtils.isEmpty(groupAttendeesStr)) {
					groupAttendees = Integer.parseInt(groupAttendeesStr);
				}
				vo.setOrdered(groupAttendees);
				
				int groupMembers = 0;
				String groupMembersStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_GROUP_ACCESSED + rgroupRule.getId());
				if (!StringUtils.isEmpty(groupMembersStr)) {
					groupMembers = Integer.parseInt(groupMembersStr);
				}
				vo.setAccessed(groupMembers);
				
				List<String> descMoreImages = new ArrayList<>();
				for (DescriptionMore descMore : descriptionMore) {
					if ("0".equals(descMore.getType())) {
						continue;
					} else if ("1".equals(descMore.getType())) {
						Thumbnail[]thumbnails = descMore.getThumbnail();
						if (thumbnails == null || thumbnails.length == 0) {
							continue;
						}
						for (Thumbnail thumbnail : thumbnails) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(thumbnail.getUrl());
							}
						}
					} else if ("2".equals(descMore.getType())) {
						if (!StringUtils.isEmpty(descMore.getImage())) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(descMore.getImage());
							}
						}
					}
				}
				
				List<Product> productList = productRepository.findMultiByRuleId(rgroupRule.getId());
				float miniPrice = 0f;
				float maxPrice = 0f;
				for (int i = 0; i < productList.size(); i++) {
					Product product = productList.get(i);
					if (descMoreImages.size() < 3) {
						String[]pics = product.getPictureList();
						for (String pic : pics) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(pic);
							}
						}
					}
					float singlePrice = product.getSinglePrice();
					if (i == 0) {
						miniPrice = singlePrice;
					} else {
						if (singlePrice < miniPrice) {
							miniPrice = singlePrice;
						}
					}
					if (singlePrice > maxPrice) {
						maxPrice = singlePrice;
					}
				}
				String pricePeriod = miniPrice + "~" + maxPrice;
				if (miniPrice == 0f || miniPrice == maxPrice) {
					pricePeriod = String.valueOf(maxPrice);
				}
				vo.setPricePeriod(pricePeriod);
				vo.setRuleId(String.valueOf(rgroupRule.getId()));
				vo.setDescMoreImages(descMoreImages);
				vo.setDescription(rgroupRule.getDescription());
				Date startDate = rgroupRule.getStartDate();
				String startDateStr = "";
				if (startDate != null) {
					if ((currentMills - startDate.getTime()) < (3600 * 24 * 1000l)) {
						startDateStr = DateUtil.dtFormat(startDate, "MM-dd");
						if (!StringUtils.isEmpty(startDateStr)) {
							String[]dateArr = startDateStr.split("-");
							startDateStr = dateArr[0] + "月" + dateArr[1] + "日";
						}
					} else {
						startDateStr = DateUtil.getSendTime(startDate.getTime());
						
					}
					vo.setStartDate(startDateStr);
				}
				Date endDate = rgroupRule.getEndDate();
				vo.setStartDateMills(startDate.getTime());
				vo.setEndDateMills(endDate.getTime());
				vo.setStatus(rgroupRule.getStatus());
				
				RgroupRecordsVO rgroupRecordsVO = queryOrderRecords(vo.getRuleId(), 0);
				vo.setRgroupRecords(rgroupRecordsVO);
				voList.add(vo);
			}
			
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw new BizValidateException(e.getMessage());
		}
		
		return voList;
	}
	
	/**
	 * 获取团购信息
	 * @param rgroupRuleId
	 */
	@Override
	public RgroupOwnerVO getLeaderInfo(String groupLeaderId) {
		
		Assert.hasText(groupLeaderId, "团长id不能为空。");
		try {
			long leaderId = Long.valueOf(groupLeaderId);
			RgroupOwner rgroupOwner = rgroupOwnerRepository.findByUserId(leaderId);
			if(rgroupOwner == null) {
				throw new BizValidateException("未查询到团长信息");
			}
			RgroupOwnerVO rgroupOwnerVO = new RgroupOwnerVO();
			int attendees = 0;
			String attendeesStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ORDERED + rgroupOwner.getUserId());
			if (!StringUtils.isEmpty(attendeesStr)) {
				attendees = Integer.parseInt(attendeesStr);
			}
			rgroupOwnerVO.setAttendees(attendees);
			int members = 0;
			String membersStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ACCESSED + rgroupOwner.getUserId());
			if (!StringUtils.isEmpty(membersStr)) {
				members = Integer.parseInt(membersStr);
			}
			rgroupOwnerVO.setMembers(members);
			rgroupOwnerVO.setOwnerId(leaderId);
			rgroupOwnerVO.setOwnerName(rgroupOwner.getName());
			rgroupOwnerVO.setOwnerImg(rgroupOwner.getHeadImgUrl());
			rgroupOwnerVO.setOwnerTel(rgroupOwner.getTel());
			return rgroupOwnerVO;
			
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw new BizValidateException(e.getMessage());
		}
		
	}
	
	/**
	 * 获取某一小区小的团购列表
	 * @throws Exception 
	 */
	@Override
	public List<RgroupVO> getLeadGroups(String groupLeaderId, String title, int currentPage) throws Exception {
		
		Assert.hasText(groupLeaderId, "团长id不能为空。");
		
		List<RgroupVO> voList = new ArrayList<>();
		try {
			long leaderId = Long.valueOf(groupLeaderId);
			RgroupOwner rgroupOwner = rgroupOwnerRepository.findByUserId(leaderId);
			if(rgroupOwner == null) {
				throw new BizValidateException("未查询到团长信息");
			}
			
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "updateDate");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			Pageable pageable = PageRequest.of(currentPage, 10, sort);
			
			if (StringUtils.isEmpty(title)) {
				title = "";
			}
			
			Page<RgroupRule> pages = rgroupRuleRepository.findByOwnerIdAndDescriptionLike(rgroupOwner.getUserId(), title, pageable);
			List<RgroupRule> ruleList = pages.getContent();
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			long currentMills = System.currentTimeMillis();
			for (RgroupRule rgroupRule : ruleList) {
				RgroupVO vo = new RgroupVO();
				String descMoreStr = rgroupRule.getDescriptionMore();
				TypeReference<DescriptionMore[]> typeReference = new TypeReference<RgroupVO.DescriptionMore[]>() {};
				DescriptionMore[]descriptionMore = null;
				if (!StringUtils.isEmpty(descMoreStr)) {
					descriptionMore = objectMapper.readValue(descMoreStr, typeReference);
				} else {
					descriptionMore = new DescriptionMore[0];
				}
				
				RgroupOwnerVO rgroupOwnerVO = new RgroupOwnerVO();
				int attendees = 0;
				String attendeesStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ORDERED + rgroupRule.getOwnerId());
				if (!StringUtils.isEmpty(attendeesStr)) {
					attendees = Integer.parseInt(attendeesStr);
				}
				rgroupOwnerVO.setAttendees(attendees);
				int members = 0;
				String membersStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ACCESSED + rgroupRule.getOwnerId());
				if (!StringUtils.isEmpty(membersStr)) {
					members = Integer.parseInt(membersStr);
				}
				rgroupOwnerVO.setMembers(members);
				rgroupOwnerVO.setOwnerId(rgroupRule.getOwnerId());
				rgroupOwnerVO.setOwnerName(rgroupRule.getOwnerName());
				rgroupOwnerVO.setOwnerImg(rgroupRule.getOwnerImg());
				rgroupOwnerVO.setOwnerTel(rgroupRule.getOwnerTel());
				vo.setRgroupOwner(rgroupOwnerVO);
				
				int groupAttendees = 0;
				String groupAttendeesStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_GROUP_ORDERED + rgroupRule.getId());
				if (!StringUtils.isEmpty(groupAttendeesStr)) {
					groupAttendees = Integer.parseInt(groupAttendeesStr);
				}
				vo.setOrdered(groupAttendees);
				
				int groupMembers = 0;
				String groupMembersStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_GROUP_ACCESSED + rgroupRule.getId());
				if (!StringUtils.isEmpty(groupMembersStr)) {
					groupMembers = Integer.parseInt(groupMembersStr);
				}
				vo.setAccessed(groupMembers);
				
				List<String> descMoreImages = new ArrayList<>();
				for (DescriptionMore descMore : descriptionMore) {
					if ("0".equals(descMore.getType())) {
						continue;
					} else if ("1".equals(descMore.getType())) {
						Thumbnail[]thumbnails = descMore.getThumbnail();
						if (thumbnails == null || thumbnails.length == 0) {
							continue;
						}
						for (Thumbnail thumbnail : thumbnails) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(thumbnail.getUrl());
							}
						}
					} else if ("2".equals(descMore.getType())) {
						if (!StringUtils.isEmpty(descMore.getImage())) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(descMore.getImage());
							}
						}
					}
				}
				
				List<Product> productList = productRepository.findMultiByRuleId(rgroupRule.getId());
				float miniPrice = 0f;
				float maxPrice = 0f;
				for (int i = 0; i < productList.size(); i++) {
					Product product = productList.get(i);
					if (descMoreImages.size() < 3) {
						String[]pics = product.getPictureList();
						for (String pic : pics) {
							if (descMoreImages.size() < 3) {
								descMoreImages.add(pic);
							}
						}
					}
					float singlePrice = product.getSinglePrice();
					if (i == 0) {
						miniPrice = singlePrice;
					} else {
						if (singlePrice < miniPrice) {
							miniPrice = singlePrice;
						}
					}
					if (singlePrice > maxPrice) {
						maxPrice = singlePrice;
					}
				}
				String pricePeriod = miniPrice + "~" + maxPrice;
				if (miniPrice == 0f || miniPrice == maxPrice) {
					pricePeriod = String.valueOf(maxPrice);
				}
				vo.setPricePeriod(pricePeriod);
				vo.setRuleId(String.valueOf(rgroupRule.getId()));
				vo.setDescMoreImages(descMoreImages);
				vo.setDescription(rgroupRule.getDescription());
				Date startDate = rgroupRule.getStartDate();
				String startDateStr = "";
				if (startDate != null) {
					if ((currentMills - startDate.getTime()) < (3600 * 24 * 1000l)) {
						startDateStr = DateUtil.dtFormat(startDate, "MM-dd");
						if (!StringUtils.isEmpty(startDateStr)) {
							String[]dateArr = startDateStr.split("-");
							startDateStr = dateArr[0] + "月" + dateArr[1] + "日";
						}
					} else {
						startDateStr = DateUtil.getSendTime(startDate.getTime());
						
					}
					vo.setStartDate(startDateStr);
				}
				Date endDate = rgroupRule.getEndDate();
				vo.setStartDateMills(startDate.getTime());
				vo.setEndDateMills(endDate.getTime());
				vo.setStatus(rgroupRule.getStatus());
				
				RgroupRecordsVO rgroupRecordsVO = queryOrderRecords(vo.getRuleId(), 0);
				vo.setRgroupRecords(rgroupRecordsVO);
				voList.add(vo);
			}
			
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw new BizValidateException(e.getMessage());
		}
		
		return voList;
	}
	
	/**
	 * 团购商品访问统计
	 * @param user
	 * @param ruleId
	 * @param ownerId
	 */
	@Override
	public void visitView(User user, String ruleIdStr, String ownerIdStr) {
		
		long ruleId = 0l;
		if (!StringUtils.isEmpty(ruleIdStr)) {
			ruleId = Long.parseLong(ruleIdStr);
		}
		long ownerId = 0l;
		if (!StringUtils.isEmpty(ownerIdStr)) {
			ownerId = Long.parseLong(ownerIdStr);
		}
		
		if (ruleId > 0) {
			stringRedisTemplate.opsForValue().increment(ModelConstant.KEY_RGROUP_GROUP_ACCESSED + ruleId);
		}
		if (ownerId > 0) {
			stringRedisTemplate.opsForValue().increment(ModelConstant.KEY_RGROUP_OWNER_ACCESSED + ownerId);
		}
		
	}
	
	@Override
	public void subscribe(User user, RgroupSubscribeVO rgroupSubscribeVO) {
		
		String key = "";
		switch (rgroupSubscribeVO.getType()) {
		case 0:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_GROUP;
			break;
		case 1:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_LEADER;
			break;
		case 2:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_REGION;
			break;
		default:
			throw new BizValidateException("unknow subscribe type : " + rgroupSubscribeVO.getType());
		}
		key += rgroupSubscribeVO.getInfoId();
		stringRedisTemplate.opsForHash().put(key, user.getMiniopenid(), String.valueOf(System.currentTimeMillis()));
		
	}
	
	@Override
	public void unsubscribe(User user, RgroupSubscribeVO rgroupSubscribeVO) {
		
		String key = "";
		switch (rgroupSubscribeVO.getType()) {
		case 0:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_GROUP;
			break;
		case 1:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_LEADER;
			break;
		case 2:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_REGION;
			break;
		default:
			throw new BizValidateException("unknow subscribe type : " + rgroupSubscribeVO.getType());
		}
		key += rgroupSubscribeVO.getInfoId();
		stringRedisTemplate.opsForHash().delete(key, user.getMiniopenid());
		
	}
	
	@Override
	public boolean getUserSubscribe(User user, RgroupSubscribeVO rgroupSubscribeVO) {
		
		String key = "";
		switch (rgroupSubscribeVO.getType()) {
		case 0:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_GROUP;
			break;
		case 1:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_LEADER;
			break;
		case 2:
			key = ModelConstant.KEY_RGROUP_SUBSCRIBE_REGION;
			break;
		default:
			throw new BizValidateException("unknow subscribe type : " + rgroupSubscribeVO.getType());
		}
		key += rgroupSubscribeVO.getInfoId();
		Object object = stringRedisTemplate.opsForHash().get(key, user.getMiniopenid());
		if (object == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 团购发布后的订阅推送
	 * @param ruleIdStr
	 */
	@Override
	public void sendPubMsg(String ruleId) {
		
		if (StringUtils.isEmpty(ruleId)) {
			logger.info("ruleId is empty, will return .");
			return;
		}
		RgroupVO rgroupVO = queryRgroupByRule(ruleId, true);
		RgroupOwnerVO rgroupOwnerVO = rgroupVO.getRgroupOwner();
		
		String leaderKeyPattern = ModelConstant.KEY_RGROUP_SUBSCRIBE_LEADER + rgroupOwnerVO.getOwnerId();
		Map<Object, Object> leaderSuscribeMap = stringRedisTemplate.opsForHash().entries(leaderKeyPattern);
		logger.info("leaderSuscribeMap : " + leaderSuscribeMap);
		List<String> sendUserList = new ArrayList<>();
		
		if (leaderSuscribeMap != null && !leaderSuscribeMap.isEmpty()) {
			Iterator<Map.Entry<Object, Object>> it = leaderSuscribeMap.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Object, Object> entry = it.next();
				String miniOpenid = (String) entry.getKey();
				sendUserList.add(miniOpenid);
				User sendUser = userService.getByMiniopenid(miniOpenid);
				String accessToken = "";
				if (!StringUtils.isEmpty(sendUser.getAppId())) {
					accessToken = systemConfigService.queryWXAToken(sendUser.getAppId());
				}
				if (StringUtils.isEmpty(accessToken)) {
					continue;
				}
				try {
					templateMsgService.sendGroupLeaderSubscribe(sendUser, rgroupVO, accessToken);
				} catch (Exception e) {
					logger.info(e.getMessage(), e);
				}
			}
		}
		
		
		RegionVo[]regions= rgroupVO.getRegions();
		for (RegionVo regionVo : regions) {
			String keyPattern = ModelConstant.KEY_RGROUP_SUBSCRIBE_REGION + regionVo.getId();
			Map<Object, Object> regionSubscribeMap = stringRedisTemplate.opsForHash().entries(keyPattern);
			logger.info("regionSubscribeMap : " + regionSubscribeMap);
			if (regionSubscribeMap != null && !regionSubscribeMap.isEmpty()) {
				Iterator<Map.Entry<Object, Object>> it = regionSubscribeMap.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry<Object, Object> entry = it.next();
					String miniOpenid = (String) entry.getKey();
					if (sendUserList.contains(miniOpenid)) {
						continue;
					}
					User sendUser = userService.getByMiniopenid(miniOpenid);
					String accessToken = systemConfigService.queryWXAToken(sendUser.getAppId());
					if (!StringUtils.isEmpty(sendUser.getAppId())) {
						accessToken = systemConfigService.queryWXAToken(sendUser.getAppId());
					}
					if (StringUtils.isEmpty(accessToken)) {
						continue;
					}
					Region region = new Region();
					BeanUtils.copyProperties(regionVo, region);
					rgroupVO.setRegion(region);
					if (StringUtils.isEmpty(accessToken)) {
						continue;
					}
					try {
						templateMsgService.sendGroupRegionSubscribe(sendUser, rgroupVO, accessToken);
					} catch (Exception e) {
						logger.info(e.getMessage(), e);
					}
				}
				
			}
			
		}
		
	}
	
	/**
	 *二维码邀请团长
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public boolean inviteGroupLeader(User user, String code) throws Exception {
		
		logger.info("user : " + user.getTel() + ", invite code : " + code);
		
		User ownerUser = null;
		List<User> userList = userService.getByTel(user.getTel());
		if (userList == null || userList.isEmpty()) {
			throw new BizValidateException("未查询到用户，手机：" + user.getTel());
		}
		if (userList.size() > 1) {
			for (User currUser : userList) {
				if (!StringUtils.isEmpty(currUser.getMiniopenid())) {
					ownerUser = currUser;
					break;
				}
			}
		} else {
			ownerUser = userList.get(0);
		}
		
		InviteLeaderRequest inviteLeaderRequest = new InviteLeaderRequest();
    	inviteLeaderRequest.setCode(code);
    	inviteLeaderRequest.setLeaderId(String.valueOf(ownerUser.getId()));
    	inviteLeaderRequest.setMiniopenid(ownerUser.getMiniopenid());
    	inviteLeaderRequest.setName(ownerUser.getName());
    	inviteLeaderRequest.setOpenid(ownerUser.getOpenid());
    	inviteLeaderRequest.setTel(ownerUser.getTel());
    	InviteLeaderResp inviteLeaderResp = eshopUtil.inviteLeader("", inviteLeaderRequest);
		
		Agent agent = agentRepository.findByAgentNo(inviteLeaderResp.getOrgId());
		if (agent == null) {
			agent = new Agent();
			agent.setAgentNo(inviteLeaderResp.getOrgId());
			agent.setName(inviteLeaderResp.getOrgName());
			agent.setStatus(1);
			agentRepository.save(agent);
		}
		
		OrgOperator orgOperator = orgOperatorRepository.findByUserIdAndRoleId(ownerUser.getId(), ModelConstant.USER_ROLE_RGROUPOWNER);
		if (orgOperator == null) {
			orgOperator = new OrgOperator();
			orgOperator.setOrgOperId(inviteLeaderResp.getOrgOperId());
			orgOperator.setOrgOperName(ownerUser.getName());
			orgOperator.setRoleId("03");
			orgOperator.setUserId(ownerUser.getId());
		}
		orgOperator.setOrgId(inviteLeaderResp.getOrgId());
		orgOperator.setOrgName(inviteLeaderResp.getOrgName());
		orgOperator.setOrgType(inviteLeaderResp.getOrgType());
		orgOperatorRepository.save(orgOperator);
		
		RgroupOwner rgroupOwner = rgroupOwnerRepository.findByUserId(ownerUser.getId());
		if (rgroupOwner == null) {
			rgroupOwner = new RgroupOwner();
			rgroupOwner.setUserId(ownerUser.getId());
			rgroupOwner.setMiniopenid(ownerUser.getMiniopenid());
			rgroupOwner.setMiniappid(ownerUser.getMiniAppId());
			rgroupOwner.setName(ownerUser.getName());
			rgroupOwner.setHeadImgUrl(ownerUser.getHeadimgurl());
			rgroupOwner.setTel(ownerUser.getTel());
			
			ownerUser.setRoleId(ModelConstant.USER_ROLE_RGROUPOWNER);
			userService.save(ownerUser);
		}
		rgroupOwner.setFeeRate(inviteLeaderResp.getFeeRate());
		rgroupOwnerRepository.save(rgroupOwner);
		return true;
	}
	
}
