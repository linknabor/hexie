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
	
	
	@Override
	@Transactional
	public void saveRgroup(RgroupVO rgroupVo) {
	
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
			
			RgroupOwner rgroupOwner = rgroupOwnerRepository.findByUserId(owner.getOwnerId());
			if (rgroupOwner == null) {
				rgroupOwner = new RgroupOwner();
				User user = userService.getById(owner.getOwnerId());
				rgroupOwner.setUserId(owner.getOwnerId());
				rgroupOwner.setMiniopenid(user.getMiniopenid());
				rgroupOwnerRepository.save(rgroupOwner);
			}
			/*1.保存团长信息 end */

			
			/*2.保存rGroupRule start */
			RgroupRule rule = new RgroupRule();
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
			rule.setGroupStatus(ModelConstant.GROUP_STAUS_INIT);
			
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
				com.yumu.hexie.model.commonsupport.info.Product product = new com.yumu.hexie.model.commonsupport.info.Product();
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
				StringBuffer bf = new StringBuffer();
				for (Thumbnail image : images) {
					bf.append(image.getUrl()).append(",");
				}
				bf.deleteCharAt(bf.length()-1);
				product.setPictures(bf.toString());
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
//				product.setAgentId(agent.getId());	//写到rgroupRule上，这里都填0
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
				
				ProductRule productRule = new ProductRule();
				productRule.setProductId(product.getId());
				productRule.setRuleId(rule.getId());
				productRuleRepository.save(productRule);
				
				//缓存库存和商品规则
				ProductRuleCache productRuleCache = new ProductRuleCache(product, rule);
				String key = ModelConstant.KEY_PRO_RULE_INFO + productRuleCache.getId()+ "_" + product.getId();
				redisRepository.setProdcutRule(key, productRuleCache);
				stringRedisTemplate.opsForValue().set(ModelConstant.KEY_PRO_STOCK + product.getId(), String.valueOf(product.getTotalCount()));
			
			}
			/*3.保存product end */
			
			/*4.保存rgroupAreaItem 当前版本只支持单个小区 start*/
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
			/*4.保存rgroupAreaItem 当前版本只支持单个小区 end*/
			
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
	public RgroupVO queryRgroupByRule(String rgroupRuleId) {
		
		Assert.hasText(rgroupRuleId, "团购id不能为空。");
		RgroupVO vo = new RgroupVO();
		try {
			Long ruleId = Long.valueOf(rgroupRuleId);
			RgroupRule rule = null;
			Optional<RgroupRule> optional = rgroupRuleRepository.findById(ruleId);
			if (optional != null) {
				rule = optional.get();
			}

			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			String descMoreStr = rule.getDescriptionMore();
			TypeReference<DescriptionMore[]> typeReference = new TypeReference<RgroupVO.DescriptionMore[]>() {};
			DescriptionMore[]descriptionMore = objectMapper.readValue(descMoreStr, typeReference);
			vo.setDescription(rule.getDescription());
			vo.setDescriptionMore(descriptionMore);
			int ruleStatus = rule.getStatus();
			String type = "0";
			if (ModelConstant.RULE_STATUS_ON == ruleStatus) {
				type = "1";
			}
			vo.setType(type);
			Date startDate = rule.getStartDate();
			String startDateStr = DateUtil.dtFormat(startDate, DateUtil.dttmSimple);
			vo.setStartDate(startDateStr);
			Date endDate = rule.getEndDate();
			String endDateStr = DateUtil.dtFormat(endDate, DateUtil.dttmSimple);
			vo.setEndDate(endDateStr);
			vo.setGroupMinNum(rule.getGroupMinNum());
			long createDate = rule.getCreateDate();
			vo.setCreateDate(DateUtil.getSendTime(createDate));
			
			List<Product> productList = productRepository.findMultiByRuleId(ruleId);
			List<ProductVO> proVoList = new ArrayList<>();

			TypeReference<Tag[]> tagRefer = new TypeReference<Tag[]>() {};
			for (Product product : productList) {
				ProductVO proVo = new ProductVO();
				proVo.setDescription(product.getOtherDesc());
				String picsStr = product.getPictures();
				List<Thumbnail> thumbnailList = new ArrayList<>();
				if (!StringUtils.isEmpty(picsStr)) {
					String[]pics = picsStr.split(",");
					for (String imgUrl : pics) {
						Thumbnail thumbnail = new Thumbnail();
						thumbnail.setUrl(imgUrl);
						thumbnailList.add(thumbnail);
					}
					
				}
				proVo.setImages(thumbnailList.toArray(new Thumbnail[0]));
				proVo.setMiniPrice(String.valueOf(product.getMiniPrice()));
				proVo.setName(product.getName());
				proVo.setOriPrice(String.valueOf(product.getOriPrice()));
				proVo.setSinglePrice(String.valueOf(product.getSinglePrice()));
				proVo.setTotalCount(String.valueOf(product.getTotalCount()));
				proVo.setUserLimitCount(String.valueOf(product.getUserLimitCount()));
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

}
