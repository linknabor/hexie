package com.yumu.hexie.service.common.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.cache.ProductRuleCache;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRule;
import com.yumu.hexie.model.commonsupport.info.ProductRuleRepository;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.OrgOperatorRepository;
import com.yumu.hexie.service.common.PublishService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.vo.CreateRgroupReq;
import com.yumu.hexie.vo.CreateRgroupReq.DescriptionMore;
import com.yumu.hexie.vo.CreateRgroupReq.Product;
import com.yumu.hexie.vo.CreateRgroupReq.RgroupOwner;
import com.yumu.hexie.vo.CreateRgroupReq.Thumbnail;

public class PublishServiceImpl implements PublishService {
	
	private static Logger logger = LoggerFactory.getLogger(PublishServiceImpl.class);
	
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
	

	@Override
	@Transactional
	public void saveRgroup(CreateRgroupReq createRgroupReq) {
	
		logger.info("saveRgroup : " + createRgroupReq);
		Assert.hasText(createRgroupReq.getRgroupOwner().getOwnerTel(), "团长联系方式不能为空");
		Assert.hasText(createRgroupReq.getType(), "保存类型不能为空");
		Assert.hasText(createRgroupReq.getDescription(), "团购标题不能为空");
		Assert.noNullElements(createRgroupReq.getDescriptionMore(), "团购活动内容不能为空");
		Assert.noNullElements(createRgroupReq.getProductList(), "团购商品不能为空");
		Assert.hasText(createRgroupReq.getStartDate(), "团购起始日期不能为空");
		Assert.hasText(createRgroupReq.getEndDate(), "团购结束日期不能为空");
		Assert.notNull(createRgroupReq.getRegion(), "团购区域不能为空");
		
		try {
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			
			/*1.保存rGroupRule start */
			RgroupRule rule = new RgroupRule();
			rule.setDescription(createRgroupReq.getDescription());
			DescriptionMore[]descMore = createRgroupReq.getDescriptionMore();
			String descStr = objectMapper.writeValueAsString(descMore);
			rule.setDescriptionMore(descStr);
			
			String startDateStr = createRgroupReq.getStartDate();
			startDateStr += ":00";
			Date startDate = DateUtil.parse(startDateStr, "yyyy/MM/dd HH:mm:ss");
			rule.setStartDate(startDate);
			
			String endDateStr = createRgroupReq.getEndDate();
			endDateStr += ":59";
			Date endDate = DateUtil.parse(endDateStr, "yyyy/MM/dd HH:mm:ss");
			rule.setEndDate(endDate);
			rule.setFreeShippingNum(1);
			rule.setLimitNumOnce(99);
			
			Product[]productList = createRgroupReq.getProductList();
			Product productVo = productList[0];
			String ruleName = productVo.getName() + "等" + productList.length + "件商品";
			rule.setName(ruleName);
			rule.setProductName(ruleName);
			rule.setProductType(1002);	//团购都1002
			
			int ruleStatus = ModelConstant.RULE_STATUS_OFF;
			if ("1".equals(createRgroupReq.getType())) {
				ruleStatus = ModelConstant.RULE_STATUS_ON;
			}
			rule.setStatus(ruleStatus);
			rule.setSupportRegionType(ModelConstant.REGION_ALL);
			rule.setTimeoutForPay(30*60*1000l);	//半小时支付超市
			rule.setGroupMaxNum(2000);
			rule.setGroupMinNum(createRgroupReq.getGroupMinNum());
			rule.setGroupStatus(ModelConstant.GROUP_STAUS_INIT);
			
			Region region = createRgroupReq.getRegion();
			RgroupOwner owner = createRgroupReq.getRgroupOwner();
			rule.setOwnerId(owner.getOwnerId());
			rule.setOwnerName(owner.getOwnerName());
			rule.setOwnerAddr(region.getName() + region.getXiaoquAddress());
			rule.setOwnerTel(owner.getOwnerTel());
			rgroupRuleRepository.save(rule);
			/*1.保存rGroupRule end */
			
			/*2.保存团长信息 start */
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
			
			/*2.保存团长信息 end */
			
			/*3.保存product start */
			for (Product productView : productList) {
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
			rgroupAreaItem.setRuleCloseTime(ruleCloseTime);	//取规则的结束时间,转成毫秒
			rgroupAreaItem.setSortNo(10);	
			rgroupAreaItem.setRuleName(rule.getName());
			/*4.保存rgroupAreaItem 当前版本只支持单个小区 end*/
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizValidateException("保存团购内容失败。");
		}
		
	}
	
	public static void main(String[] args) {
		
		String date = "2022/04/19 19:13";
		Date d = DateUtil.parse(date, "yyyy/MM/dd HH:mm");
		System.out.println(d);
	}

	@Override
	public void pubRgroup(long rgroupRuleId) {
		// TODO Auto-generated method stub

	}

}
