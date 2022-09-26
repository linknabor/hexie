package com.yumu.hexie.service.sales.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.eshop.vo.QueryRgroupsVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.RefundRecord;
import com.yumu.hexie.model.market.RefundRecordRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.rgroup.RgroupUser;
import com.yumu.hexie.model.market.rgroup.RgroupUserRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.sales.CacheableService;
import com.yumu.hexie.service.sales.RgroupService;
import com.yumu.hexie.service.sales.req.NoticeRgroupSuccess;
import com.yumu.hexie.service.user.UserNoticeService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.RgroupOrder;
import com.yumu.hexie.vo.RgroupOrdersVO;

@Service("rgroupService")
public class RgroupServiceImpl implements RgroupService {
	private static final Logger log = LoggerFactory.getLogger(BaseOrderServiceImpl.class);

	@Inject
	private CacheableService cacheableService;
    @Inject
    private ServiceOrderRepository serviceOrderRepository;
	@Inject
	private BaseOrderService baseOrderService;
	@Inject
	private UserService userService;
	@Inject
	private UserNoticeService userNoticeService;
    @Autowired
	@Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private RgroupAreaItemRepository rgroupAreaItemRepository;
    @Autowired
    @Qualifier(value = "staffclientStringRedisTemplate")
    private RedisTemplate<String, String> staffclientStringRedisTemplate;
    @Autowired
    private RgroupUserRepository rgroupUserRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private RefundRecordRepository refundRecordRepository;

	/**
	 * 前端显示进度和限制库存
	 * @param result
	 * @return
	 */
	public List<RgroupAreaItem> addProcessStatus(List<RgroupAreaItem> result) {
        for(RgroupAreaItem item : result){
            String stock = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + item.getProductId());
    		String freeze = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_FREEZE + item.getProductId());
    		int canSale = Integer.parseInt(stock) - Integer.parseInt(freeze);
			item.setTotalCount(canSale);
        }
        return result;
    }
	
	//FIXME
	public RgroupRule findSalePlan(long ruleId) {
		return cacheableService.findRgroupRule(ruleId);
	}

	@Override
	public void refreshGroupStatus(RgroupRule rule) {
		if(System.currentTimeMillis()>rule.getEndDate().getTime()) {
			if(rule.getCurrentNum() < rule.getGroupMinNum()) {
				cancelGroup(rule);
			} else {
				finishGroup(rule);
			}
		} else {
			log.error("该团购未到结束时间！" + rule.getId());
		}
	}
	
	@Override
	@Transactional
	public void refreshGroupDeliveryStatus(RgroupRule rule) {
		List<ServiceOrder> orderList = serviceOrderRepository.findByRGroup(rule.getId());
		if (orderList == null || orderList.isEmpty()) {
			log.warn("can not find serviceOrder by rule, ruleId : " + rule.getId());
			return;
		}
		int delivered = 0;
		int undelivered = 0;
		for (ServiceOrder serviceOrder : orderList) {
			int orderStatus = serviceOrder.getStatus();
			int count = serviceOrder.getCount();
			if (ModelConstant.ORDER_STATUS_SENDED == orderStatus || ModelConstant.ORDER_STATUS_RECEIVED == orderStatus) {
				delivered += count;
			} else {
				undelivered += count;
			}
		}
		if (undelivered > 0) {
			log.info("delivered : " + delivered);
			log.info("still " + undelivered + " not be delivered . ruleId : " + rule.getId());
			return;
		}
		rule.setGroupStatus(ModelConstant.RGROUP_STAUS_DELIVERED);
		cacheableService.save(rule);
		
	}

	private void cancelValidate(RgroupRule rule) {
		if(rule.getGroupStatus() == ModelConstant.RGROUP_STAUS_FINISH){
			throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_RGROUP,rule.getId(),"该团购已完成！").setError();
		}
	}

	private void cancelGroup(RgroupRule rule) {
		log.error("cancelGroup:"+rule.getId());
		cancelValidate(rule);
		List<ServiceOrder> orders = serviceOrderRepository.findByRGroup(rule.getId());
		for(ServiceOrder o : orders){
			try{
				o.setGroupStatus(ModelConstant.GROUP_STAUS_CANCEL);
				if(ModelConstant.ORDER_STATUS_PAYED == o.getStatus()) {
					baseOrderService.refund(o);
				} else {
					baseOrderService.cancelOrder(o);
				}
				User u = userService.getById(o.getUserId());

				userNoticeService.groupFail(u, u.getTel(), o.getGroupRuleId(), rule.getProductName(), rule.getGroupMinNum(), rule.getName());
			}catch(Exception e) {
				log.error("cancelGroupError",e);
			}
		}

		rule.setGroupStatus(ModelConstant.RGROUP_STAUS_CANCEL);
		cacheableService.save(rule);
	}

	private void finishValidate(RgroupRule rule) {
		if(rule.getGroupStatus() == ModelConstant.RGROUP_STAUS_CANCEL || rule.getCurrentNum() < rule.getGroupMinNum()){
			throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_RGROUP,rule.getId(),"该团购已取消或人数不足！").setError();
		}
	}

	private void finishGroup(RgroupRule rule) {
		log.error("finishGroup:"+rule.getId());
		finishValidate(rule);
		Map<Long, Integer> regionMap = new HashMap<>();
		List<ServiceOrder> orders = serviceOrderRepository.findByRGroup(rule.getId());
		for(ServiceOrder o : orders){
			try{
				o.setGroupStatus(ModelConstant.GROUP_STAUS_FINISH);
				if(ModelConstant.ORDER_STATUS_INIT == o.getStatus()) {
					baseOrderService.cancelOrder(o);
				} else if(ModelConstant.ORDER_STATUS_PAYED == o.getStatus()) {
					baseOrderService.confirmOrder(o);
					if (!regionMap.containsKey(o.getXiaoquId())) {
						regionMap.put(o.getXiaoquId(), 1);
					} else {
						Integer count = regionMap.get(o.getXiaoquId());
						regionMap.put(o.getXiaoquId(), ++count);
					}
				} else {
					log.error("finishGroup:"+rule.getId());
				}
			}catch(Exception e) {
				log.error("finishGroup:"+rule.getId(),e);
			}
		}
		rule.setGroupStatus(ModelConstant.RGROUP_STAUS_FINISH);
		rule.setGroupFinishDate(System.currentTimeMillis());
		cacheableService.save(rule);
		
		long currTime = System.currentTimeMillis();
		Iterator<Map.Entry<Long, Integer>> it = regionMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Long, Integer> entry = it.next();
			long regionId = entry.getKey();
			Integer currSectGroupNum = entry.getValue();	//当前小区成团份数
			//给团长发送成团消息
			Region region = regionRepository.findById(regionId);
			NoticeRgroupSuccess noticeRgroupSuccess = new NoticeRgroupSuccess();
			noticeRgroupSuccess.setCreateDate(currTime);
			noticeRgroupSuccess.setGroupNum(currSectGroupNum);
			noticeRgroupSuccess.setOrderType(ModelConstant.ORDER_TYPE_RGROUP);
			noticeRgroupSuccess.setProductName(rule.getProductName());
			noticeRgroupSuccess.setSectId(region.getSectId());
			String price = new BigDecimal(rule.getPrice()).setScale(2, RoundingMode.HALF_UP).toString();
			noticeRgroupSuccess.setPrice(price);
			noticeRgroupSuccess.setRuleId(rule.getId());
			
			List<Long> list = new ArrayList<>();
			List<RgroupAreaItem> areaItemList = rgroupAreaItemRepository.findByProductIdAndRegionId(rule.getProductId(), regionId);
			for (RgroupAreaItem rgroupAreaItem : areaItemList) {
				list.add(rgroupAreaItem.getAreaLeaderId());
			}
			noticeRgroupSuccess.setOpers(list);

			try {
	            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
	            String value = objectMapper.writeValueAsString(noticeRgroupSuccess);
	            //放入合协管家的redis中
	            staffclientStringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_RGROUP_SUCCESS_NOTICE_MSG_QUEUE, value);
	        } catch (Exception e) {
	        	log.error("custom push redis error", e);
	        }
		}
		
	}

	@Override
	public List<RgroupOrder> queryMyRgroupOrders(long userId,List<Integer> status) {
		List<ServiceOrder> orders = serviceOrderRepository.findByUserAndStatusAndType(userId,status, ModelConstant.ORDER_TYPE_RGROUP);
		List<RgroupOrder> result = new ArrayList<>();
		for(ServiceOrder so : orders) {
			result.add(new RgroupOrder(cacheableService.findRgroupRule(so.getGroupRuleId()), so));
		}
		return result;
	}
	
	@Override
	public List<RgroupOrdersVO> queryMyRgroupOrdersV3(long userId, List<Integer> status, String productName, String ruleId, List<Integer> itemStatus, int currentPage) {
		
		Pageable pageable = PageRequest.of(currentPage, 10);
		List<ServiceOrder> orders = serviceOrderRepository.findByUserAndStatusAndTypeV3(userId, status, 
				ModelConstant.ORDER_TYPE_RGROUP, productName, ruleId, itemStatus, pageable);
		List<RgroupOrdersVO> result = new ArrayList<>();
		Sort sort = Sort.by(Direction.DESC, "id");
		for(ServiceOrder so : orders) {
			RgroupRule rule = cacheableService.findRgroupRule(so.getGroupRuleId());
			List<RgroupUser> userList = rgroupUserRepository.findAllByUserIdAndRuleId(userId, rule.getId());
			so.setOrderItems(orderItemRepository.findByServiceOrder(so));
			RgroupOrdersVO vo = new RgroupOrdersVO();
			vo.setOrder(so);
			vo.setRule(rule);
			if (userList != null && userList.size() > 0) {
				vo.setRgroupUser(userList.get(0));
			}
			List<RefundRecord> records = refundRecordRepository.findByOrderId(so.getId(), sort);
			vo.setRefundRecords(records);
			if (records != null && records.size() > 0) {
				vo.setLatestRefund(records.get(0));
			}
			result.add(vo);
		}
		return result;
	}
	
	@Override
	public RgroupOrdersVO queryRgroupOrderDetailV3(long userId, String orderIdStr) {
		
		Assert.hasLength(orderIdStr, "订单id不能为空");
		
		long orderId = Long.valueOf(orderIdStr);
		ServiceOrder so = serviceOrderRepository.findById(orderId);
		Sort sort = Sort.by(Direction.DESC, "id");
		RgroupRule rule = cacheableService.findRgroupRule(so.getGroupRuleId());
		List<RgroupUser> userList = rgroupUserRepository.findAllByUserIdAndRuleId(userId, rule.getId());
		so.setOrderItems(orderItemRepository.findByServiceOrder(so));
		RgroupOrdersVO vo = new RgroupOrdersVO();
		vo.setOrder(so);
		vo.setRule(rule);
		if (userList != null && userList.size() > 0) {
			vo.setRgroupUser(userList.get(0));
		}
		List<RefundRecord> records = refundRecordRepository.findByOrderId(so.getId(), sort);
		vo.setRefundRecords(records);
		if (records != null && records.size() > 0) {
			vo.setLatestRefund(records.get(0));
		}
		return vo;
	}
	
	@Override
	@Transactional
	public void noticeArrival(QueryRgroupsVO queryRgroupsVO) {
	
		Assert.hasText(queryRgroupsVO.getRuleId(), "团购id不能为空。");
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		RgroupRule rule = cacheableService.findRgroupRule(Long.valueOf(queryRgroupsVO.getRuleId()));
		if (ModelConstant.RGROUP_STAUS_FINISH == rule.getGroupStatus()) {
			rule.setGroupStatus(ModelConstant.RGROUP_STAUS_DELIVERING);	//将状态改为发货中
			cacheableService.save(rule);
		}
		
		while(!isSuccess && retryTimes < 3) {
			try {
				
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(queryRgroupsVO);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_RGROUP_ARRIVAL_NOTICE_QUEUE, value);

				isSuccess = true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				retryTimes++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					log.error(e.getMessage(), e);
				}
			}
		}
		if (!isSuccess) {
			throw new BizValidateException("发送团购到货通知失败，请稍后再试。");
		}
		
	}
}


