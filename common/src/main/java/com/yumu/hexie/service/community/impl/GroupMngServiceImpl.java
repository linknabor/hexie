package com.yumu.hexie.service.community.impl;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.community.req.OutSidProductDepotReq;
import com.yumu.hexie.integration.community.req.OutsideSaveProDepotReq;
import com.yumu.hexie.integration.community.req.ProductDepotReq;
import com.yumu.hexie.integration.community.req.QueryGroupOwnerReq;
import com.yumu.hexie.integration.community.req.QueryGroupReq;
import com.yumu.hexie.integration.community.resp.GroupInfoVo;
import com.yumu.hexie.integration.community.resp.GroupOrderVo;
import com.yumu.hexie.integration.community.resp.GroupOwnerVO;
import com.yumu.hexie.integration.community.resp.GroupProductSumVo;
import com.yumu.hexie.integration.community.resp.GroupSumResp;
import com.yumu.hexie.integration.community.resp.OutSidDepotResp;
import com.yumu.hexie.integration.community.resp.OutSidRelateGroupResp;
import com.yumu.hexie.integration.community.resp.QueryDepotDTO;
import com.yumu.hexie.integration.community.resp.QueryProDepotResp;
import com.yumu.hexie.integration.eshop.mapper.SaleAreaMapper;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductDepot;
import com.yumu.hexie.model.commonsupport.info.ProductDepotArea;
import com.yumu.hexie.model.commonsupport.info.ProductDepotAreaRepository;
import com.yumu.hexie.model.commonsupport.info.ProductDepotRepository;
import com.yumu.hexie.model.commonsupport.info.ProductDepotTags;
import com.yumu.hexie.model.commonsupport.info.ProductDepotTagsRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.RefundRecord;
import com.yumu.hexie.model.market.RefundRecordRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.user.RgroupOwner;
import com.yumu.hexie.model.user.RgroupOwnerRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.community.GroupMngService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.UserNoticeService;
import com.yumu.hexie.vo.RgroupVO;
import com.yumu.hexie.vo.RgroupVO.RegionVo;
import com.yumu.hexie.vo.RgroupVO.RgroupOwnerVO;
import com.yumu.hexie.vo.RgroupVO.Thumbnail;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-05-05 21:01
 */
@Service
public class GroupMngServiceImpl implements GroupMngService {

	private static Logger logger = LoggerFactory.getLogger(GroupMngServiceImpl.class);
	
	private static final String DEFAULT_AGENT_NO = "000000000000"; // 全平台机构号。如果是全平台的，不用加筛选条件，全不能看。

	@Autowired
	private RgroupRuleRepository rgroupRuleRepository;

	@Autowired
	private ServiceOrderRepository serviceOrderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserNoticeService userNoticeService;

	@Autowired
	private ProductDepotRepository productDepotRepository;

	@Autowired
	private ProductDepotTagsRepository productDepotTagsRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RefundRecordRepository refundRecordRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private RgroupOwnerRepository rgroupOwnerRepository;

	@Autowired
	private RgroupAreaItemRepository rgroupAreaItemRepository;

	@Autowired
	private AgentRepository agentRepository;
	
	@Autowired
	private ProductDepotAreaRepository productDepotAreaRepository;

	@Override
	public List<GroupInfoVo> queryGroupList(User user, QueryGroupReq queryGroupReq) {
		try {
			List<Sort.Order> sortList = new ArrayList<>();
			Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
			sortList.add(order);
			Sort sort = Sort.by(sortList);
			Pageable pageable = PageRequest.of(queryGroupReq.getCurrentPage(), 10, sort);

			Page<Object[]> page = rgroupRuleRepository.findRgroupList(user.getId(), queryGroupReq.getQueryName(),
					queryGroupReq.getGroupStatus(), pageable);
			List<GroupInfoVo> list = ObjectToBeanUtils.objectToBean(page.getContent(), GroupInfoVo.class);

			if (list == null) {
				list = new ArrayList<>();
			}

			for (GroupInfoVo info : list) {
				info.setGroupDate(DateUtil.getSendTime(info.getCreateDate().longValue()));
				// 获取团购的图片
				List<RgroupVO.DescriptionMore> listDesc = JSONArray.parseArray(info.getDescriptionMore(),
						RgroupVO.DescriptionMore.class);
				// 是否有图片
				List<String> textList = new ArrayList<>();
				List<String> imagesList = new ArrayList<>();
				if (listDesc != null) {
					for (RgroupVO.DescriptionMore desc : listDesc) {
						if (!StringUtils.isEmpty(desc.getText())) {
							textList.add(desc.getText());
						}
						if ("2".equals(desc.getType())) {
							if (!StringUtils.isEmpty(desc.getImage())) {
								imagesList.add(desc.getImage());
							}
						}
						if ("1".equals(desc.getType())) {
							Thumbnail[] thumbs = desc.getThumbnail();
							if (thumbs != null && thumbs.length > 0) {
								for (Thumbnail thumb : thumbs) {
									if (thumb != null && !StringUtils.isEmpty(thumb.getUrl())) {
										imagesList.add(thumb.getUrl());
									}
								}
							}
						}
					}
				}

				info.setDesc(textList.size() == 0 ? "" : textList.get(0));
				info.setProductImg(imagesList.size() == 0 ? "" : imagesList.get(0));
				info.setImages(imagesList);

				if (imagesList.size() == 0) {
					List<Product> productList = productRepository.findMultiByRuleId(info.getId().longValue());
					for (Product product : productList) {
						String[] pics = product.getPictureList();
						if (pics != null && pics.length > 0) {
							for (String pic : pics) {
								if (!StringUtils.isEmpty(pic)) {
									imagesList.add(pic);
								}

							}
						}
					}
					info.setImages(imagesList);
				}

				// 统计支付的，退款的，取消的，预览的
				float realityAmt = 0;
				float refundAmt = 0;
				List<ServiceOrder> serviceOrderList = serviceOrderRepository
						.findByGroupRuleId(info.getId().longValue());
				for (ServiceOrder serviceOrder : serviceOrderList) {
					if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_PAYED
							|| serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_SENDED
							|| serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_RECEIVED
							|| serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_CONFIRM) {

						realityAmt += serviceOrder.getPrice();

					}
					if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_REFUNDED) {
						Float refundedAmt = serviceOrder.getRefundAmt() == null ? 0F : serviceOrder.getRefundAmt();
						refundAmt += refundedAmt;
					}
				}

				info.setRealityAmt(realityAmt);
				info.setRefundAmt(refundAmt);

				int totalOrdered = 0; // 被下单总次数
				String totalOrderedStr = stringRedisTemplate.opsForValue()
						.get(ModelConstant.KEY_RGROUP_GROUP_ORDERED + info.getId());
				if (!StringUtils.isEmpty(totalOrderedStr)) {
					totalOrdered = Integer.parseInt(totalOrderedStr);
				}

				int totalAccessed = 0; // 被下单总次数
				String totalAccessedStr = stringRedisTemplate.opsForValue()
						.get(ModelConstant.KEY_RGROUP_GROUP_ACCESSED + info.getId());
				if (!StringUtils.isEmpty(totalAccessedStr)) {
					totalAccessed = Integer.parseInt(totalAccessedStr);
				}

				info.setFollowNum(totalOrdered);
				info.setCancelNum(totalOrdered - info.getCurrentNum());
				info.setQueryNum(totalAccessed);

			}
			return list;
		} catch (Exception e) {
			throw new BizValidateException(e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public Boolean updateGroupInfo(User user, String groupId, String operType) throws Exception {

		if (!"1".equals(operType) && !"3".equals(operType) && !"4".equals(operType) && !"5".equals(operType)) {
			throw new BizValidateException("不合法的操作数据类型");
		}
		if (StringUtils.isEmpty(groupId)) {
			throw new BizValidateException("团购编号为空，请刷新重试");
		}

		RgroupRule rgroupRule = rgroupRuleRepository.findById(Long.parseLong(groupId));
		if (rgroupRule != null) {
			if ("1".equals(operType)) { // 结束操作
				// 修改团购结束日期
				rgroupRule.setStatus(ModelConstant.RULE_STATUS_END);
			} else if ("3".equals(operType)) { // 删除团购
				rgroupRule.setStatus(ModelConstant.RULE_STATUS_DEL);
			} else if ("4".equals(operType)) { // 开启团购
				Date date = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DATE, 2);
				date = calendar.getTime();
				rgroupRule.setEndDate(date);
				rgroupRule.setStatus(ModelConstant.RULE_STATUS_ON);

				List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByRuleId(rgroupRule.getId());
				for (RgroupAreaItem rgroupAreaItem : areaList) {
					rgroupAreaItemRepository.updateStatus(ModelConstant.DISTRIBUTION_STATUS_ON, rgroupAreaItem.getId());
					rgroupAreaItemRepository.save(rgroupAreaItem);
				}

				/* 通知订阅 */
				if (ModelConstant.RULE_STATUS_ON == rgroupRule.getStatus()) {
					Map<String, String> map = new HashMap<>();
					map.put("ruleId", String.valueOf(rgroupRule.getId()));
					String queue = JacksonJsonUtil.getMapperInstance(false).writeValueAsString(map);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_RGROUP_PUB_QUEUE, queue);
				}

			} else if ("5".equals(operType)) {
				rgroupRule.setHidden(true);
			}
		} else {
			throw new BizValidateException("未查到团购信息，请刷新重试");
		}
		rgroupRuleRepository.save(rgroupRule);
		return true;
	}

	@Override
	public CommonResponse<Object> queryProductDepotListPage(OutSidProductDepotReq outSidProductDepotReq) {
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Sort.Order> orderList = new ArrayList<>();
			Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
			orderList.add(order);
			Sort sort = Sort.by(orderList);

			List<Long> agentIds = null;
			String defaultAgentNo = "000000000000"; // 全平台机构号。如果是全平台的，不用加筛选条件，全不能看。
			if (!StringUtils.isEmpty(outSidProductDepotReq.getAgentNo())
					&& !defaultAgentNo.equals(outSidProductDepotReq.getAgentNo())) {
				Agent agent = agentRepository.findByAgentNo(outSidProductDepotReq.getAgentNo());
				if (agent != null) {
					agentIds = new ArrayList<>();
					agentIds.add(agent.getId());
				}
			}
			Pageable pageable = PageRequest.of(outSidProductDepotReq.getCurrentPage(),
					outSidProductDepotReq.getPageSize(), sort);
			Page<Object[]> page = productDepotRepository.getDepotListPage(outSidProductDepotReq.getProductName(),
					outSidProductDepotReq.getOwnerName(), agentIds, pageable);
			List<OutSidDepotResp> list = ObjectToBeanUtils.objectToBean(page.getContent(), OutSidDepotResp.class);

			QueryListDTO<List<OutSidDepotResp>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			responsePage.setContent(list);

			commonResponse.setData(responsePage);
			commonResponse.setResult("00");

		} catch (Exception e) {

			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99"); // TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}

	@Override
	public CommonResponse<Object> queryRelateGroup(String depotId) {
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {

			List<Object[]> rgroupRules = rgroupRuleRepository.queryGroupByDepotId(depotId);
			List<OutSidRelateGroupResp> list = ObjectToBeanUtils.objectToBean(rgroupRules, OutSidRelateGroupResp.class);

			for (OutSidRelateGroupResp resp : list) {
				String groupStatusCn = "";
				Date date = new Date();
				if (resp.getStatus() == ModelConstant.RULE_STATUS_ON) {
					if (resp.getStartDate().getTime() <= date.getTime()
							&& resp.getEndDate().getTime() >= date.getTime()) {
						groupStatusCn = "跟团中";
					}
					if (resp.getStartDate().getTime() > date.getTime()) {
						groupStatusCn = "未开始";
					}
				} else if (resp.getStatus() == ModelConstant.RULE_STATUS_OFF) {
					if (resp.getStartDate().getTime() <= date.getTime()
							&& resp.getEndDate().getTime() >= date.getTime()) {
						groupStatusCn = "预览中";
					}
				}
				if (resp.getEndDate().getTime() < date.getTime()) {
					groupStatusCn = "已结束";
				}
				if (resp.getStatus() == ModelConstant.RULE_STATUS_END) {
					groupStatusCn = "已结束";
				}
				if (resp.getStatus() == ModelConstant.RULE_STATUS_DEL) {
					groupStatusCn = "已删除";
				}
				resp.setStatus_cn(groupStatusCn);
			}
			QueryListDTO<List<OutSidRelateGroupResp>> responsePage = new QueryListDTO<>();
			responsePage.setContent(list);
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");

		} catch (Exception e) {

			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99"); // TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}

	@Override
	public String delDepotById(String depotId) {
		productDepotRepository.deleteById(Long.parseLong(depotId));
		return "SUCCESS";
	}

	@Override
	public CommonResponse<Object> queryGroupListPage(OutSidProductDepotReq outSidProductDepotReq) {
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Sort.Order> orderList = new ArrayList<>();
			Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
			orderList.add(order);
			Sort sort = Sort.by(orderList);

			Pageable pageable = PageRequest.of(outSidProductDepotReq.getCurrentPage(),
					outSidProductDepotReq.getPageSize(), sort);
			Page<Object[]> page = rgroupRuleRepository.queryGroupByOutSid(outSidProductDepotReq.getProductName(),
					outSidProductDepotReq.getOwnerName(), outSidProductDepotReq.getOwnerId(), pageable);
			List<OutSidRelateGroupResp> list = ObjectToBeanUtils.objectToBean(page.getContent(),
					OutSidRelateGroupResp.class);

			for (OutSidRelateGroupResp resp : list) {
				String groupStatusCn = "";
				Date date = new Date();
				if (resp.getStatus() == ModelConstant.RULE_STATUS_ON) {
					if (resp.getStartDate().getTime() <= date.getTime()
							&& resp.getEndDate().getTime() >= date.getTime()) {
						groupStatusCn = "跟团中";
					}
					if (resp.getStartDate().getTime() > date.getTime()) {
						groupStatusCn = "未开始";
					}
				} else if (resp.getStatus() == ModelConstant.RULE_STATUS_OFF) {
					if (resp.getStartDate().getTime() <= date.getTime()
							&& resp.getEndDate().getTime() >= date.getTime()) {
						groupStatusCn = "预览中";
					}
				}
				if (resp.getEndDate().getTime() < date.getTime()) {
					groupStatusCn = "已结束";
				}
				if (resp.getStatus() == ModelConstant.RULE_STATUS_END) {
					groupStatusCn = "已结束";
				}
				if (resp.getStatus() == ModelConstant.RULE_STATUS_DEL) {
					groupStatusCn = "已删除";
				}
				resp.setStatus_cn(groupStatusCn);
			}
			QueryListDTO<List<OutSidRelateGroupResp>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			responsePage.setContent(list);
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");

		} catch (Exception e) {

			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99"); // TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}

	@Override
	@Transactional
	public String operGroupByOutSid(String groupId, String operType) {
		if (StringUtils.isEmpty(groupId)) {
			throw new BizValidateException("团购编号为空，请刷新重试");
		}
		if (StringUtils.isEmpty(operType)) {
			throw new BizValidateException("操作类型为空，请刷新重试");
		}

		RgroupRule rgroupRule = rgroupRuleRepository.findById(Long.parseLong(groupId));
		if (rgroupRule == null) {
			throw new BizValidateException("未查询到团购信息。团购id: " + groupId);
		}
		if (!"0".equals(operType) && !"1".equals(operType)) {
			throw new BizValidateException("不支持的操作类型。");
		}
		if ("0".equals(operType)) { // 下架
			rgroupRule.setStatus(ModelConstant.RULE_STATUS_END);
		} else if ("1".equals(operType)) { // 删除
			rgroupRule.setStatus(ModelConstant.RULE_STATUS_DEL);
		}
		rgroupRuleRepository.save(rgroupRule);
		return Constants.SERVICE_SUCCESS;
	}

	/**
	 * 查询团长信息
	 * 
	 * @param queryGroupOwnerReq
	 * @return
	 */
	@Override
	public CommonResponse<Object> getGroupOwners(QueryGroupOwnerReq queryGroupOwnerReq) {

		logger.info("getGroupOwners, queryGroupOwnerReq : " + queryGroupOwnerReq);

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		QueryListDTO<List<GroupOwnerVO>> queryListDTO = null;
		try {
			List<Sort.Order> sortList = new ArrayList<>();
			Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
			sortList.add(order);
			Sort sort = Sort.by(sortList);
			Pageable pageable = PageRequest.of(queryGroupOwnerReq.getCurrentPage(), queryGroupOwnerReq.getPageSize(),
					sort);

			String agentNo = queryGroupOwnerReq.getAgentNo();
			if (DEFAULT_AGENT_NO.equals(agentNo)) {
				agentNo = "";
			}
			Page<Object[]> page = rgroupOwnerRepository.findByUserIdAndTelLikeAndNameAndAgentNo(queryGroupOwnerReq.getId(),
					queryGroupOwnerReq.getTel(), queryGroupOwnerReq.getName(), agentNo, pageable);

			List<GroupOwnerVO> list = ObjectToBeanUtils.objectToBean(page.getContent(), GroupOwnerVO.class);

			queryListDTO = new QueryListDTO<>();
			queryListDTO.setContent(list);
			queryListDTO.setTotalPages(page.getTotalPages());
			queryListDTO.setTotalSize(page.getTotalElements());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			commonResponse.setResult("99");
			commonResponse.setErrMsg(e.getMessage());
		}
		commonResponse.setData(queryListDTO);
		commonResponse.setResult("00");
		return commonResponse;

	}

	/**
	 * 更新团长费率
	 * 
	 * @param queryGroupOwnerReq
	 * @return
	 */
	@Override
	public CommonResponse<Object> updateOwnerFeeRate(RgroupOwnerVO rgroupOwnerVO) {

		logger.info("updateOwnerFeeRate, rgroupOwnerVO : " + rgroupOwnerVO);
		Assert.isTrue(rgroupOwnerVO.getOwnerId() > 0, "团长id不能为空。");
		Assert.hasText(rgroupOwnerVO.getConsultRate(), "费率不能为空。");

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			RgroupOwner rgroupOwner = rgroupOwnerRepository.findByUserId(rgroupOwnerVO.getOwnerId());
			if (rgroupOwner == null) {
				throw new BizValidateException("未查询到团长信息。 userId : " + rgroupOwnerVO.getOwnerId());
			}
			rgroupOwner.setFeeRate(rgroupOwnerVO.getConsultRate());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			commonResponse.setResult("99");
			commonResponse.setErrMsg(e.getMessage());
		}
		commonResponse.setData(Constants.SERVICE_SUCCESS);
		commonResponse.setResult("00");
		return commonResponse;

	}

	@Override
	public GroupSumResp queryGroupSum(User user, String groupId) throws Exception {

		// 汇总当前团购的有效订单，总金额和退款金额
		GroupSumResp resp = new GroupSumResp();
		List<GroupSumResp.SearchVo> searchVoList = new ArrayList<>();

		List<ServiceOrder> serviceOrderList = null;
		long ruleId = 0l;
		if (!StringUtils.isEmpty(groupId)) {
			ruleId = Long.valueOf(groupId);
		}
		if (ruleId != 0l) {
			serviceOrderList = serviceOrderRepository.findByGroupRuleId(ruleId);
		} else {
			serviceOrderList = serviceOrderRepository.findByGroupLeaderId(user.getId());
		}
		int validNum = serviceOrderList.size(); // 有效订单数
		Float totalAmt = 0F; // 总金额
		Float refundAmt = 0F; // 退款金额
		for (ServiceOrder serviceOrder : serviceOrderList) {
			if (ModelConstant.ORDER_STATUS_CANCEL == serviceOrder.getStatus()
					|| ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {
				validNum = validNum - 1;
//                refundAmt += serviceOrder.getPrice();
			} else {
				totalAmt += serviceOrder.getPrice();
			}
			if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_REFUNDED) {
				Float refunded = serviceOrder.getRefundAmt() == null ? 0F : serviceOrder.getRefundAmt();
				refundAmt += refunded;
			}
		}
		// 有效订单 全部订单-取消的订单
		GroupSumResp.SearchVo searchVo = new GroupSumResp.SearchVo();
		searchVo.setName("有效订单");
		searchVo.setNum(String.valueOf(validNum));
		searchVo.setMessage("有效订单：全部订单-已取消订单");
		searchVoList.add(searchVo);

		// 总金额 全部订单的金额，包括取消的订单
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String totalAmtStr = decimalFormat.format(totalAmt);
		searchVo = new GroupSumResp.SearchVo();
		searchVo.setName("订单总金额");
		searchVo.setNum("¥" + totalAmtStr);
		searchVo.setMessage("订单总金额：所有订单金额的加总");
		searchVoList.add(searchVo);

		// 退款金额 全部订单的退款金额
		String refundAmtStr = decimalFormat.format(refundAmt);
		searchVo = new GroupSumResp.SearchVo();
		searchVo.setName("退款金额");
		searchVo.setNum("¥" + refundAmtStr);
		searchVo.setMessage("退款金额：所有订单的退款金额");
		searchVoList.add(searchVo);
		resp.setSearchVoList(searchVoList);

		// 查询团下单的商品列表
		GroupSumResp.ProductVo productVo = new GroupSumResp.ProductVo();

		List<Integer> status = new ArrayList<>();
		status.add(ModelConstant.ORDER_STATUS_PAYED);
		status.add(ModelConstant.ORDER_STATUS_CANCEL);
		status.add(ModelConstant.ORDER_STATUS_SENDED);
		status.add(ModelConstant.ORDER_STATUS_RECEIVED);
		status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
		status.add(ModelConstant.ORDER_STATUS_CONFIRM);
		status.add(ModelConstant.ORDER_STATUS_REFUNDING);
		status.add(ModelConstant.ORDER_STATUS_REFUNDED);
		List<Object[]> groupProductSumVos = serviceOrderRepository.findProductSum(ruleId, user.getId(), status);
		int totalNum = 0;
		int totalVerify = 0;
		List<GroupProductSumVo> vos = new ArrayList<>();
		if (groupProductSumVos != null && groupProductSumVos.size() > 0) {
			vos = ObjectToBeanUtils.objectToBean(groupProductSumVos, GroupProductSumVo.class);
			for (GroupProductSumVo vo : vos) {
				totalNum += vo.getCount().intValue();
				totalVerify += vo.getVerifyNum().intValue();
			}
		}
		productVo.setProducts(vos);
		productVo.setTotalNum(totalNum);
		productVo.setVerifyNum(totalVerify);

		resp.setProductVo(productVo);
		return resp;
	}

	@Override
	public List<GroupOrderVo> queryGroupOrder(User user, QueryGroupReq queryGroupReq) throws Exception {
		List<Integer> status = new ArrayList<>();
		String verifyStatus = ""; // 是否核销
		List<Integer> itemStatus = null; // 子项退款状态,默认0，未退款
		if ("0".equals(queryGroupReq.getOrderStatus())) { // 查全部
			status.add(ModelConstant.ORDER_STATUS_PAYED);
//            status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
			status.add(ModelConstant.ORDER_STATUS_RECEIVED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
			status.add(ModelConstant.ORDER_STATUS_REFUNDING);
			status.add(ModelConstant.ORDER_STATUS_REFUNDED);

		} else if ("1".equals(queryGroupReq.getOrderStatus())) { // 查待核销的
			status.add(ModelConstant.ORDER_STATUS_PAYED);
			verifyStatus = "0";

			itemStatus = new ArrayList<>();
			itemStatus.add(ModelConstant.ORDERITEM_REFUND_STATUS_PAID);
		} else if ("2".equals(queryGroupReq.getOrderStatus())) { // 查退款申请
//            status.add(ModelConstant.ORDER_STATUS_REFUNDING);
//            status.add(ModelConstant.ORDER_STATUS_REFUNDED);

			status.add(ModelConstant.ORDER_STATUS_PAYED);
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
			status.add(ModelConstant.ORDER_STATUS_RECEIVED);

			itemStatus = new ArrayList<>();
			itemStatus.add(ModelConstant.ORDERITEM_REFUND_STATUS_APPLYREFUND);
			itemStatus.add(ModelConstant.ORDERITEM_REFUND_STATUS_REFUNDING);
			itemStatus.add(ModelConstant.ORDERITEM_REFUND_STATUS_REFUNDED);
		} else {
			throw new BizValidateException("不合法的参数类型");
		}

		List<Sort.Order> sortList = new ArrayList<>();
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
		sortList.add(order);
		Sort sort = Sort.by(sortList);
		Pageable pageable = PageRequest.of(queryGroupReq.getCurrentPage(), 10, sort);

		long ruleId = 0l;
		if (!StringUtils.isEmpty(queryGroupReq.getGroupId())) {
			ruleId = Long.parseLong(queryGroupReq.getGroupId());
		}
		long regionId = 0l;
		if (!StringUtils.isEmpty(queryGroupReq.getRegionId())) {
			regionId = Long.parseLong(queryGroupReq.getRegionId());
		}

		Page<Object[]> page = serviceOrderRepository.findByGroupRuleIdPage(user.getId(), ruleId, regionId, status,
				verifyStatus, itemStatus, queryGroupReq.getQueryName(), pageable);
		List<GroupOrderVo> list = ObjectToBeanUtils.objectToBean(page.getContent(), GroupOrderVo.class);
		if (list != null) {
			Sort refundSort = Sort.by(Direction.DESC, "id");
			for (GroupOrderVo vo : list) {
				vo.setStatusCn(ServiceOrder.getStatusStr(vo.getStatus()));

				String orderDate = "";
				if (vo.getPayDate() != null) {
					orderDate = DateUtil.dttmFormat(vo.getPayDate());
				} else {
					orderDate = DateUtil.dtFormat(vo.getCreateDate().longValue(), DateUtil.dttmSimple);
				}
				vo.setOrderDate(orderDate);

				// 0商户派送 1用户自提 2第三方配送
				String logisticType = "商户派送";
				if (vo.getLogisticType() == 1) {
					logisticType = "用户自提";
				} else if (vo.getLogisticType() == 2) {
					logisticType = "第三方配送";
				}
				vo.setLogisticTypeCn(logisticType);

				// 获取用户昵称和头像
				User userInfo = userRepository.findById(vo.getUserId().longValue());
				if (userInfo != null) {
					vo.setUserName(userInfo.getNickname());
					vo.setUserHead(userInfo.getHeadimgurl());
				}

//                List<BuyGoodsVo> buyVos = new ArrayList<>();
				// 查询订单下的商品
				ServiceOrder serviceOrder = new ServiceOrder();
				serviceOrder.setId(vo.getId().longValue());
				List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
				vo.setOrderItems(items);

				List<RefundRecord> refundRecords = refundRecordRepository.findByOrderId(serviceOrder.getId(),
						refundSort);
				vo.setRefundRecords(refundRecords);
				if (refundRecords != null && refundRecords.size() > 0) {
					vo.setLatestRefund(refundRecords.get(0));
				}
			}
		}

		return list;
	}

	@Override
	public GroupOrderVo queryGroupOrderDetail(User user, String orderId) {
		if (StringUtils.isEmpty(orderId)) {
			throw new BizValidateException("订单号为空，请刷新重试");
		}
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.parseLong(orderId));
		if (serviceOrder == null) {
			throw new BizValidateException("未查到订单，请刷新重试");
		}
		GroupOrderVo groupOrder = new GroupOrderVo();
		BeanUtils.copyProperties(serviceOrder, groupOrder);
		groupOrder.setStatusCn(ServiceOrder.getStatusStr(groupOrder.getStatus()));
		groupOrder.setOrderDate(DateUtil.dttmFormat(serviceOrder.getPayDate()));
		groupOrder.setUserId(BigInteger.valueOf(serviceOrder.getUserId()));
		groupOrder.setId(BigInteger.valueOf(serviceOrder.getId()));

		// 0商户派送 1用户自提 2第三方配送
		String logisticType = "商户派送";
		if (groupOrder.getLogisticType() == 1) {
			logisticType = "用户自提";
		} else if (groupOrder.getLogisticType() == 2) {
			logisticType = "第三方配送";
		}
		groupOrder.setLogisticTypeCn(logisticType);

		// 获取用户昵称和头像
		User userInfo = userRepository.findById(serviceOrder.getUserId());
		if (userInfo != null) {
			groupOrder.setUserName(userInfo.getNickname());
			groupOrder.setUserHead(userInfo.getHeadimgurl());
		}

		// 查询订单下的商品
		List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
		groupOrder.setOrderItems(items);

		// 查询商品退款记录
		Sort sort = Sort.by(Direction.DESC, "id");
		List<RefundRecord> records = refundRecordRepository.findByOrderId(Long.valueOf(orderId), sort);
		if (records != null && records.size() > 0) {
			groupOrder.setLatestRefund(records.get(0));
		}
		return groupOrder;
	}

	@Override
	@Transactional
	public Boolean handleVerifyCode(User user, String orderId, String code) {
		if (StringUtils.isEmpty(code)) {
			throw new BizValidateException("核销码为空，请重新输入");
		}

		ServiceOrder serviceOrder = serviceOrderRepository.findByIdAndGroupLeaderId(Long.parseLong(orderId),
				user.getId());
		if (serviceOrder == null) {
			throw new BizValidateException("未查到订单，请刷新重试");
		}
		List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
		boolean flag = false;
		for (OrderItem item : items) {
			if (item.getVerifyStatus() == 0 && item.getCode().equals(code)) {
				item.setVerifyStatus(1);
				orderItemRepository.save(item);
				// 修改订单状态为已签收
				serviceOrder.setStatus(ModelConstant.ORDER_STATUS_RECEIVED);
				serviceOrderRepository.save(serviceOrder);
				flag = true;
			}
		}
		if (!flag) {
			throw new BizValidateException("核销码不正确或已经核销，请确认后重试");
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean cancelOrder(User user, String orderId) throws Exception {
		ServiceOrder serviceOrder = serviceOrderRepository.findByIdAndGroupLeaderId(Long.parseLong(orderId),
				user.getId());
		if (serviceOrder == null) {
			throw new BizValidateException("未查到订单，请刷新重试");
		}
		// 取消订单并退款
//        reFunding(serviceOrder);
//        serviceOrder.setStatus(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
//        serviceOrderRepository.save(serviceOrder);
		return true;
	}

	@Override
	public Boolean noticeReceiving(User user, String groupId) {
		if (StringUtils.isEmpty(groupId)) {
			throw new BizValidateException("团购编号未知，请刷新重试");
		}
		List<ServiceOrder> list = new ArrayList<>();
		// 查询出未提货的订单，前提必须是已经成团
		RgroupRule rgroupRule = rgroupRuleRepository.findById(Long.parseLong(groupId));
		if (rgroupRule != null) {
			if (rgroupRule.getGroupStatus() == ModelConstant.RGROUP_STAUS_FINISH) {
				List<ServiceOrder> serviceOrderList = serviceOrderRepository.findByGroupRuleId(Long.parseLong(groupId));
				for (ServiceOrder order : serviceOrderList) {
					if (order.getStatus() == ModelConstant.ORDER_STATUS_PAYED || order.getStatus() == ModelConstant.ORDER_STATUS_CONFIRM) {
						list.add(order);
					}
				}
			}
		}

		if (list.size() == 0) {
			throw new BizValidateException("没有可推送的提货用户");
		}

		for (ServiceOrder order : list) {
			userNoticeService.groupArriaval(order);
		}
		return true;
	}

	@Override
	public List<ProductDepot> queryProductDepotList(User user, String searchValue, int currentPage) {
//        User userInfo = userRepository.findById(user.getId());
//        if(userInfo == null) {
//            throw new BizValidateException("用户不存在");
//        }
//        if(!"03".equals(user.getRoleId())) {
//            throw new BizValidateException("当前用户不是团长，无法操作");
//        }

		List<Sort.Order> sortList = new ArrayList<>();
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
		sortList.add(order);
		Sort sort = Sort.by(sortList);
		Pageable pageable = PageRequest.of(currentPage, 10, sort);

		return productDepotRepository.findByOwnerIdAndNameContaining(user.getId(), searchValue, pageable);
	}

	@Override
	public Boolean delProductDepot(User user, String productId) {
		if (StringUtils.isEmpty(productId)) {
			throw new BizValidateException("商品编号不能为空");
		}
//        User userInfo = userRepository.findById(user.getId());
//        if(userInfo == null) {
//            throw new BizValidateException("用户不存在");
//        }
//        if(!"03".equals(user.getRoleId())) {
//            throw new BizValidateException("当前用户不是团长，无法操作");
//        }
		productDepotRepository.deleteById(Long.parseLong(productId));
		return true;
	}

	@Override
	public ProductDepot operProductDepot(User user, ProductDepotReq productDepotReq) {
		ProductDepot depot = new ProductDepot();
		if (!StringUtils.isEmpty(productDepotReq.getProductId())) { // 编辑
			depot = productDepotRepository.findById(Long.parseLong(productDepotReq.getProductId())).get();
		}

		BeanUtils.copyProperties(productDepotReq, depot);
		if (!StringUtils.isEmpty(productDepotReq.getPictures())) {
			String[] strs = productDepotReq.getPictures().split(",");
			depot.setMainPicture(strs[0]);
			depot.setSmallPicture(strs[0]);
		}

		if (StringUtils.isEmpty(productDepotReq.getTotalCount())) {
			depot.setTotalCount(Integer.MAX_VALUE);
		}
		if (!StringUtils.isEmpty(productDepotReq.getTags())) {
			JSONArray jsonArray = new JSONArray();
			String[] strs = productDepotReq.getTags().split(",");
			for (String key : strs) {
				if (!StringUtils.isEmpty(key)) {
					ProductDepotTags tag = productDepotTagsRepository.findById(Long.parseLong(key)).get();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", tag.getId());
					jsonObject.put("name", tag.getName());
					jsonObject.put("color", "#FF9333");
					jsonArray.add(jsonObject);
				}
			}
			if (jsonArray.size() > 0) {
				depot.setTags(jsonArray.toJSONString());
			}
		}
		depot.setOwnerId(user.getId());
		depot.setOwnerName(user.getName());
		productDepotRepository.save(depot);
		return depot;
	}

	/**
	 * 后台上架商品库商品
	 * @param outsideSaveProDepotReq
	 * @return
	 */
	@Override
	@Transactional
	public CommonResponse<Object> saveProductDepot(OutsideSaveProDepotReq outsideSaveProDepotReq) {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		ProductDepot depot = new ProductDepot();
		depot.setOwnerId(0l);
		depot.setOwnerName(outsideSaveProDepotReq.getAgentName());
		if (!StringUtils.isEmpty(outsideSaveProDepotReq.getId())) { // 编辑
			depot = productDepotRepository.findById(Long.parseLong(outsideSaveProDepotReq.getId())).get();
		} else {
			if (!StringUtils.isEmpty(outsideSaveProDepotReq.getAgentNo())
					&& !DEFAULT_AGENT_NO.equals(outsideSaveProDepotReq.getAgentNo())) {
				Agent agent = agentRepository.findByAgentNo(outsideSaveProDepotReq.getAgentNo());
				if (agent != null) {
					depot.setAgentId(agent.getId());
				}
			}
		}
		BeanUtils.copyProperties(outsideSaveProDepotReq, depot);
		if (!StringUtils.isEmpty(outsideSaveProDepotReq.getPictures())) {
			String[] strs = outsideSaveProDepotReq.getPictures().split(",");
			depot.setMainPicture(strs[0]);
			depot.setSmallPicture(strs[0]);
		}

		if (StringUtils.isEmpty(outsideSaveProDepotReq.getTotalCount())) {
			depot.setTotalCount(Integer.MAX_VALUE);
		}
		if (!StringUtils.isEmpty(outsideSaveProDepotReq.getProTags())) {
			JSONArray jsonArray = new JSONArray();
			String[] strs = outsideSaveProDepotReq.getProTags().split(",");
			for (String name : strs) {
				if (!StringUtils.isEmpty(name)) {
					ProductDepotTags tag = productDepotTagsRepository.findByName(name);
					if (tag == null) {
						tag = new ProductDepotTags();
						tag.setName(name);
						tag.setOwnerId(-1l);
						productDepotTagsRepository.save(tag);
					}
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", tag.getId());
					jsonObject.put("name", tag.getName());
					jsonObject.put("color", "#FF9333");
					jsonArray.add(jsonObject);
				}
			}
			if (jsonArray.size() > 0) {
				depot.setTags(jsonArray.toJSONString());
			}
		}
		int areaLimit = 0;
		if (outsideSaveProDepotReq.getSaleAreas()!=null && outsideSaveProDepotReq.getSaleAreas().size()>0) {
			areaLimit = 1;
		}
		depot.setAreaLimit(areaLimit);
		productDepotRepository.save(depot);
		
		if (areaLimit == 1) {
			List<ProductDepotArea> areas = productDepotAreaRepository.findByDepotId(depot.getId());
			if (areas!=null && areas.size()>0) {
				productDepotAreaRepository.deleteAll(areas);
			}
			for (Region saleArea : outsideSaveProDepotReq.getSaleAreas()) {
				Region region = getRegion(saleArea);
				ProductDepotArea productDepotArea = new ProductDepotArea();
				productDepotArea.setDepotId(depot.getId());
				productDepotArea.setRegionId(region.getId());
				productDepotAreaRepository.save(productDepotArea);
			}
		}
		commonResponse.setData(Constants.SERVICE_SUCCESS);
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	/**
	 * 获取平台小区对应的region
	 * @param saleArea
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
					for (Region currRegion : regionList) {
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
			region = regionList.get(0);
		}
		return region;
	}

	@Override
	public ProductDepot queryProductDepotDetail(User user, String productId) {
		return productDepotRepository.findById(Long.parseLong(productId)).get();
	}
	
	@Override
	public CommonResponse<Object> queryProductDepotById(String depotIdStr) {
		
		Assert.hasText(depotIdStr, "库存id不能为空。");
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			long depotId = Long.valueOf(depotIdStr);
			QueryDepotDTO dto = new QueryDepotDTO();
			Optional<ProductDepot> optional = productDepotRepository.findById(depotId);
			ProductDepot productDepot = null;
			if (optional.isPresent()) {
				productDepot = optional.get();
			}
			if (productDepot!=null) {
				QueryProDepotResp queryProDepotResp = new QueryProDepotResp();
				queryProDepotResp.setId(productDepot.getId());
				queryProDepotResp.setName(productDepot.getName());
				queryProDepotResp.setOriPrice(productDepot.getOriPrice());
				queryProDepotResp.setMiniPrice(productDepot.getMiniPrice());
				queryProDepotResp.setSinglePrice(productDepot.getSinglePrice());
				queryProDepotResp.setPictures(productDepot.getPictures());
				queryProDepotResp.setOtherDesc(productDepot.getOtherDesc());
				queryProDepotResp.setAreaLimit(productDepot.getAreaLimit());
				queryProDepotResp.setSpecs(productDepot.getSpecs());
				queryProDepotResp.setTotalCount(productDepot.getTotalCount());
				if (!StringUtils.isEmpty(productDepot.getTags())) {
					queryProDepotResp.setTags(productDepot.getTags());
				}
				dto.setContent(queryProDepotResp);
				
				List<Object[]> regions = productDepotAreaRepository.findRegionsByDepotId(depotId);
				List<SaleAreaMapper> areaList = ObjectToBeanUtils.objectToBean(regions, SaleAreaMapper.class);
				if (areaList!=null) {
					dto.setSaleArea(areaList);
				}
			}
			commonResponse.setData(dto);
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99"); // TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}

	@Override
	public Map<String, List<ProductDepotTags>> queryProductDepotTags(User user) {
		List<ProductDepotTags> ownerList = productDepotTagsRepository.findByOwnerId(user.getId());
		List<ProductDepotTags> pubList = productDepotTagsRepository.findByOwnerId(0);
		Map<String, List<ProductDepotTags>> map = new HashMap<>();
		map.put("owner", ownerList);
		map.put("public", pubList);
		return map;
	}

	@Override
	public Boolean saveDepotTag(User user, String tagName) {
		ProductDepotTags tags = new ProductDepotTags();
		tags.setName(tagName);
		tags.setOwnerId(user.getId());
		productDepotTagsRepository.save(tags);
		return true;
	}

	@Override
	public Boolean delDepotTag(User user, String tagId) {
		ProductDepotTags tag = productDepotTagsRepository.findById(Long.parseLong(tagId)).get();
		if (user.getId() != tag.getOwnerId()) {
			throw new BizValidateException("只能操作当前用户的标签");
		}
		productDepotTagsRepository.delete(tag);
		return true;
	}

	@Override
	@Transactional
	public List<ProductDepot> saveDepotFromSales(User user, String productIds) {

		Assert.hasText(productIds, "请选择要导入的商品");

		String[] productArr = productIds.split(",");
		if (productArr == null) {
			throw new BizValidateException("请选择要导入的商品");
		}
		List<Long> products = new ArrayList<>();
		for (String productId : productArr) {
			if (StringUtils.isEmpty(productId)) {
				continue;
			}
			products.add(Long.valueOf(productId));
		}
		List<Product> productList = productRepository.findAllById(products);
		List<ProductDepot> depotList = new ArrayList<>();
		for (Product product : productList) {
			ProductDepot depot = new ProductDepot();
			BeanUtils.copyProperties(product, depot, "id", "createDate", "startDate", "endDate");
			int totalCount = product.getTotalCount();
			if (totalCount == Integer.MAX_VALUE) {
				depot.setTotalCount(9999999);
			}
			depot.setAgentId(0l);
			depot.setOwnerId(user.getId());
			depot.setOwnerName(user.getName());
			productDepotRepository.save(depot);

			product.setDepotId(depot.getId());
			productRepository.save(product);

			depotList.add(depot);
		}
		return depotList;
	}

	/**
	 * 获取退款申请记录
	 * 
	 * @param user
	 * @param productIds
	 * @return
	 */
	@Override
	public List<RefundRecord> getRefundApply(User user, String oid) {

		Assert.hasText(oid, "请选择要退款的商品");
		long orderId = Long.valueOf(oid);
		Sort sort = Sort.by(Direction.DESC, "id");
		List<RefundRecord> records = refundRecordRepository.findByOrderId(orderId, sort);
		return records;

	}

	@Override
	public List<RgroupVO> queryGroupsByOwner(User user) {

		List<Sort.Order> sortList = new ArrayList<>();
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
		sortList.add(order);
		Sort sort = Sort.by(sortList);
		Pageable pageable = PageRequest.of(0, 1000, sort); // TODO 这里分页显示数为1000，没有分页
		Page<RgroupRule> rgroupPages = rgroupRuleRepository.findByOwnerIdAndDescriptionLike(user.getId(), "", pageable);
		List<RgroupRule> groups = rgroupPages.getContent();

		List<RgroupVO> groupVo = new ArrayList<>();
		for (RgroupRule rgroupRule : groups) {
			RgroupVO vo = new RgroupVO();
			String createDate = DateUtil.dtFormat(rgroupRule.getCreateDate(), DateUtil.dttmSimple);
			vo.setCreateDate(createDate);
			vo.setDescription(rgroupRule.getDescription());
			vo.setRuleId(String.valueOf(rgroupRule.getId()));
			groupVo.add(vo);
		}

		return groupVo;
	}

	@Override
	public List<RegionVo> queryGroupRegionsByOwner(User user, String ruleIdStr) {

		long ruleId = 0l;
		if (!StringUtils.isEmpty(ruleIdStr)) {
			ruleId = Long.valueOf(ruleIdStr);
		}
		List<Sort.Order> sortList = new ArrayList<>();
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
		sortList.add(order);
		Sort sort = Sort.by(sortList);
		Pageable pageable = PageRequest.of(0, 1000, sort); // TODO 这里分页显示数为1000，没有分页
		Page<Region> regionPages = regionRepository.findByRgroupOwner(user.getId(), ruleId, pageable);
		List<Region> regions = regionPages.getContent();

		List<RegionVo> regionList = new ArrayList<>();
		for (Region region : regions) {
			RegionVo vo = new RegionVo();
			vo.setId(region.getId());
			vo.setName(region.getName());
			vo.setXiaoquAddress(region.getXiaoquAddress());
			regionList.add(vo);
		}
		return regionList;
	}

}
