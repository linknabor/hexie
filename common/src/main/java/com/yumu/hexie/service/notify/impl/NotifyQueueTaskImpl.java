package com.yumu.hexie.service.notify.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.RedisLock;
import com.yumu.hexie.integration.wechat.service.MsgCfg;
import com.yumu.hexie.integration.wuye.req.CommunityRequest;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.sales.req.NoticeRgroupSuccess;
import com.yumu.hexie.service.sales.req.NoticeServiceOperator;
import com.yumu.hexie.service.shequ.NoticeService;
import com.yumu.hexie.service.shequ.vo.InteractCommentNotice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO.ServiceCfg;
import com.yumu.hexie.integration.eshop.vo.QueryRgroupsVO;
import com.yumu.hexie.integration.notify.ConversionNotification;
import com.yumu.hexie.integration.notify.InvoiceNotification;

import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.ReceiptNotification;
import com.yumu.hexie.integration.notify.WorkOrderNotification;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.Notice;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.system.BizError;
import com.yumu.hexie.model.system.BizErrorRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;
import com.yumu.hexie.service.eshop.PartnerService;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.notify.NotifyQueueTask;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.UserNoticeService;

@Service
public class NotifyQueueTaskImpl implements NotifyQueueTask {

    private static final Logger logger = LoggerFactory.getLogger(NotifyQueueTaskImpl.class);

    @Autowired
    @Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    @Qualifier(value = "staffclientStringRedisTemplate")
    private RedisTemplate<String, String> staffclientStringRedisTemplate;
    @Autowired
    private MaintenanceService maintenanceService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GotongService gotongService;
    @Autowired
    private ServiceOperatorRepository serviceOperatorRepository;
    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private BaseOrderService baseOrderService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private WechatMsgService wechatMsgService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private BizErrorRepository bizErrorRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private UserNoticeService userNoticeService;
    
    /**
     * 异步发送到账模板消息
     */
    @Override
    @Async("taskExecutor")
    public void sendWuyeNotificationAysc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_PAY_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(json)) {
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                AccountNotification queue = objectMapper.readValue(json, new TypeReference<AccountNotification>() {
                });

                logger.info("start to consume wuyeNotificatione queue : " + queue);

                boolean isSuccess = false;

                /*推广订单 特殊处理 start*/
                String tradeWaterId = queue.getOrderId();
                ServiceOrder order = serviceOrderRepository.findByOrderNo(tradeWaterId);
                if (order != null) {
                    int orderType = order.getOrderType();
                    if (ModelConstant.ORDER_TYPE_PROMOTION == orderType || ModelConstant.ORDER_TYPE_SAASSALE == orderType) {
                        List<Map<String, String>> openidList = new ArrayList<>();
                        int operType;
                        if (ModelConstant.ORDER_TYPE_PROMOTION == orderType) {
                            operType = ModelConstant.SERVICE_OPER_TYPE_PROMOTION;
                        } else {
                            operType = ModelConstant.SERVICE_OPER_TYPE_SAASSALE;
                        }
                        List<ServiceOperator> opList = serviceOperatorRepository.findByType(operType);
                        for (ServiceOperator serviceOperator : opList) {
                            Map<String, String> openids = new HashMap<>();
                            openids.put("openid", serviceOperator.getOpenId());
                            openidList.add(openids);
                        }
                        queue.setOpenids(openidList);

                        if (ModelConstant.ORDER_TYPE_PROMOTION == orderType) {

                            String address = order.getAddress();    //逗号分隔，需要split
                            String[] addrArr = address.split(",");

                            String remark = "";
                            if (addrArr.length != 4) {
                                logger.error("当前地址: " + address + "，不能分成 省市区");
                            } else {
                                String province = addrArr[0];
                                String city = addrArr[1];
                                String county = addrArr[2];
                                String sect = addrArr[3];

                                if (province.contains("上海")
                                        || province.contains("北京")
                                        || province.contains("重庆")
                                        || province.contains("天津")) {
                                    province = "";
                                }

                                remark = province + city + county + sect;
                                remark = order.getReceiverName() + "-" + remark;
                                logger.info("remark : " + remark);
                            }
                            queue.setRemark(remark);
                        }

                    }
                }
                /*推广订单 特殊处理 end*/

                List<Map<String, String>> openidList = queue.getOpenids();
                if (openidList == null || openidList.isEmpty()) {
                    continue;
                }
                List<Map<String, String>> resendList = new ArrayList<>();
                for (Map<String, String> openidMap : openidList) {

                    User user = null;
                    String openid = openidMap.get("openid");
                    if (StringUtils.isEmpty(openid) || "0".equals(openid)) {
                        logger.warn("openid is empty, will skip. ");
                        continue;
                    }
                    List<User> userList = userRepository.findByOpenid(openid);
                    if (userList != null && !userList.isEmpty()) {
                        user = userList.get(0);
                    } else {
                        logger.warn("can not find user, openid : " + openid);
                    }
                    if (user != null) {
                        try {
                            queue.setUser(user);
                            gotongService.sendPayNotification(queue);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);    //发送失败的，需要重发
                            resendList.add(openidMap);

                        }
                    }

                }
                if (resendList.isEmpty()) {
                    isSuccess = true;
                }

                if (!isSuccess) {
                    queue.setOpenids(resendList);
                    String value = objectMapper.writeValueAsString(queue);
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_PAY_QUEUE, value);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }


    }

    /**
     * 异步发送自定义服务模板消息
     */
    @Override
    @Async("taskExecutor")
    public void sendCustomServiceNotificationAysc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String orderId = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_SERVICE_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(orderId)) {
                    logger.info("order id is null, will skip !");
                    continue;
                }
                logger.info("start to consume customServiceNotification queue : " + orderId);

                boolean isSuccess = false;
                try {
                    ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(orderId);
                    if (serviceOrder == null || serviceOrder.getId() == 0) {
                        logger.info("can not find order : " + orderId);
                        continue;
                    }

                    Region region = regionRepository.findById(serviceOrder.getXiaoquId());

                    //给操作员发送发货模板消息
                    NoticeServiceOperator noticeServiceOperator = new NoticeServiceOperator();
                    noticeServiceOperator.setSectId(region.getSectId());
                    noticeServiceOperator.setAddress(serviceOrder.getAddress());
                    noticeServiceOperator.setCreateDate(serviceOrder.getCreateDate());
                    noticeServiceOperator.setReceiverName(serviceOrder.getReceiverName());
                    noticeServiceOperator.setId(serviceOrder.getId());
                    noticeServiceOperator.setProductName(serviceOrder.getProductName());
                    noticeServiceOperator.setOrderType(serviceOrder.getOrderType());
                    noticeServiceOperator.setTel(serviceOrder.getTel());
                    noticeServiceOperator.setSubType(serviceOrder.getSubType());
                    noticeServiceOperator.setSubTypeName(serviceOrder.getSubTypeName());
                    noticeServiceOperator.setOrderNo(serviceOrder.getOrderNo());

                    int operType = ModelConstant.SERVICE_OPER_TYPE_SERVICE;
                    long agentId = serviceOrder.getAgentId();
                    logger.info("agentId is : " + agentId);
                    List<ServiceOperator> opList;
                    if (agentId > 1) {    //1是默认奈博的，所以跳过
                        opList = serviceOperatorRepository.findByTypeAndAgentId(operType, agentId);
                    } else {
                        opList = serviceOperatorRepository.findByType(operType);
                    }
                    logger.info("oper list size : " + opList.size());
                    List<Long> list = new ArrayList<>();
                    for (ServiceOperator serviceOperator : opList) {
                        list.add(serviceOperator.getUserId());
                    }
                    noticeServiceOperator.setOpers(list);

                    try {
                        //放入合协管家的redis中
                        String key = ModelConstant.KEY_ASSIGN_CS_ORDER_DUPLICATION_CHECK + orderId;
                        Long result = RedisLock.lock(key, staffclientStringRedisTemplate, 3600l);
                        logger.info("result : " + result);
                        if (0 == result) {
                            logger.info("trade : " + orderId + ", already in the send queue, will skip .");
                            return;
                        }

                        ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                        String value = objectMapper.writeValueAsString(noticeServiceOperator);
                        logger.info("redis service order push :" + value);
                        staffclientStringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_SERVICE_OPERATOR_NOTICE_MSG_QUEUE, value);
                    } catch (Exception e) {
                        logger.error("custom push redis error", e);
                    }
                    isSuccess = true;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                if (!isSuccess) {
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_SERVICE_QUEUE, orderId);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 异步更新服务配置信息
     */
    @Override
    @Async("taskExecutor")
    @CacheEvict(cacheNames = ModelConstant.KEY_USER_SERVE_ROLE, allEntries = true)
    public void updateServiceCfgAysc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_UPDATE_SERVICE_CFG_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(json)) {
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                ServiceCfgDTO dto = objectMapper.readValue(json, new TypeReference<ServiceCfgDTO>() {
                });
                logger.info("start to consume service cfg queue : " + dto);
                ServiceCfg cfg = dto.getServiceCfg();
                String serviceId = cfg.getServiceId();
                if ("0".equals(serviceId)) {
                    continue;
                }

                boolean isSuccess = false;
                try {
                    //不要循环操作redisTemplate，有TCP成本
                    String operType = cfg.getOperType();
                    if ("add".equals(operType) || "edit".equals(operType)) {
                        redisTemplate.opsForHash().put(ModelConstant.KEY_CUSTOM_SERVICE, serviceId, cfg.getServiceName());

                        String sectIds = cfg.getSectId();
                        if (!StringUtils.isEmpty(sectIds)) {
                            String[] sectArr = sectIds.split(",");
                            for (String sectId : sectArr) {
                                Map<Object, Object> csMap = redisTemplate.opsForHash().entries(ModelConstant.KEY_CS_SERVED_SECT + sectId);
                                csMap.put(sectId, cfg.getServiceId());
                                redisTemplate.opsForHash().putAll(ModelConstant.KEY_CS_SERVED_SECT + sectId, csMap);
                            }
                        }


                    } else if ("delete".equals(operType)) {
                        redisTemplate.opsForHash().delete(ModelConstant.KEY_CUSTOM_SERVICE, serviceId);

                        String sectIds = cfg.getSectId();
                        if (!StringUtils.isEmpty(sectIds)) {
                            String[] sectArr = sectIds.split(",");
                            for (String sectId : sectArr) {
                                Map<Object, Object> csMap = redisTemplate.opsForHash().entries(ModelConstant.KEY_CS_SERVED_SECT + sectId);
                                csMap.remove(sectId);
                                redisTemplate.opsForHash().putAll(ModelConstant.KEY_CS_SERVED_SECT + sectId, csMap);
                            }
                        }
                    }

                    if ("delete".equals(operType)) {
                        List<ServiceOperator> opList = serviceOperatorRepository.findByType(ModelConstant.SERVICE_OPER_TYPE_SERVICE);
                        opList.forEach(oper -> {
                            String subTypes = oper.getSubType();
                            if (StringUtils.isEmpty(subTypes)) {
                                return;
                            }
                            String[] subTypeArr = subTypes.split(",");
                            List<String> opSubList = Arrays.asList(subTypeArr);    //返回的list不是java.util.list，是一个内部类，不能使用remove等操作。所以外面套一层
                            List<String> tepmList = new ArrayList<>(opSubList);
                            tepmList.remove(serviceId);
                            StringBuilder bf = new StringBuilder();
                            for (String subType : tepmList) {
                                bf.append(subType).append(",");
                            }
                            String subs = bf.substring(0, bf.length() - 1);
                            oper.setSubType(subs);
                            serviceOperatorRepository.save(oper);

                        });
                    }

                    isSuccess = true;

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    String value = objectMapper.writeValueAsString(json);
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_UPDATE_SERVICE_CFG_QUEUE, value);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }


    }

    @Override
    @Async("taskExecutor")
    public void updateOrderStatusAysc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String tradeWaterId = redisTemplate.opsForList().leftPop(ModelConstant.KEY_UPDATE_ORDER_STATUS_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(tradeWaterId)) {
                    continue;
                }
                logger.info("start to consume orderStatus update queue : " + tradeWaterId);

                boolean isSuccess = false;
                try {
                    baseOrderService.finishOrder(tradeWaterId);
                    isSuccess = true;

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_UPDATE_ORDER_STATUS_QUEUE, tradeWaterId);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 给操作员发送发货提醒
     */
    @Override
    @Async("taskExecutor")
    public void sendDeliveryNotificationAsyc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String tradeWaterId = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_DELIVERY_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(tradeWaterId)) {
                    continue;
                }
                logger.info("start to consume notify delivery queue : " + tradeWaterId);

                boolean isSuccess = false;
                try {
                    logger.info("notify delivery, tradeWaterId : " + tradeWaterId);

                    ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(tradeWaterId);
                    if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
                        continue;
                    }
                    logger.info("notify delivery, orderNo : " + serviceOrder.getOrderNo());
                    logger.info("notify delivery, orderType : " + serviceOrder.getOrderType());

                    if (ModelConstant.ORDER_TYPE_ONSALE == serviceOrder.getOrderType()
                            || ModelConstant.ORDER_TYPE_RGROUP == serviceOrder.getOrderType()) {

                        Long groupOrderId = serviceOrder.getGroupOrderId();
                        logger.info("notify delivery, groupOrderId : " + groupOrderId);

                        List<ServiceOrder> orderList = new ArrayList<>();
                        if (groupOrderId != null) {
                            orderList = serviceOrderRepository.findByGroupOrderId(groupOrderId);
                        } else {
                            orderList.add(serviceOrder);
                        }

                        for (ServiceOrder o : orderList) {

                            Region region = regionRepository.findById(o.getXiaoquId());
                            //给操作员发送发货模板消息
                            NoticeServiceOperator noticeServiceOperator = new NoticeServiceOperator();
                            noticeServiceOperator.setSectId(region.getSectId());
                            noticeServiceOperator.setAddress(o.getAddress());
                            noticeServiceOperator.setCreateDate(o.getCreateDate());
                            noticeServiceOperator.setReceiverName(o.getReceiverName());
                            noticeServiceOperator.setId(o.getId());
                            noticeServiceOperator.setProductName(o.getProductName());
                            noticeServiceOperator.setOrderType(serviceOrder.getOrderType());
                            noticeServiceOperator.setTel(serviceOrder.getTel());
                            noticeServiceOperator.setSubType(serviceOrder.getSubType());
                            noticeServiceOperator.setSubTypeName(serviceOrder.getSubTypeName());
                            noticeServiceOperator.setOrderNo(serviceOrder.getOrderNo());

                            int operType;
                            switch (serviceOrder.getOrderType()) {
                                case ModelConstant.ORDER_TYPE_ONSALE: //特卖
                                    operType = ModelConstant.SERVICE_OPER_TYPE_ONSALE_TAKER;
                                    break;
                                case ModelConstant.ORDER_TYPE_RGROUP: //团购
                                    operType = ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER;
                                    break;
                                default:
                                    operType = 0;
                            }

                            long agentId = o.getAgentId();
                            logger.info("agentId is : " + agentId);
                            List<ServiceOperator> opList = serviceOperatorRepository.findByTypeAndAgentId(operType, agentId);
//                            if (agentId > 1) {    //1是默认奈博的，所以跳过
//                                opList = serviceOperatorRepository.findByTypeAndProductIdAndAgentId(operType, serviceOrder.getProductId(), agentId);
//                            } else {
//                                opList = serviceOperatorRepository.findByTypeAndProductId(operType, serviceOrder.getProductId());
//                            }
                            logger.info("oper list size : " + opList.size());
                            List<Long> list = new ArrayList<>();
                            for (ServiceOperator serviceOperator : opList) {
                                list.add(serviceOperator.getUserId());
                            }
                            noticeServiceOperator.setOpers(list);

                            try {
                                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                                String value = objectMapper.writeValueAsString(noticeServiceOperator);
                                logger.info("redis service order push :" + value);
                                //放入合协管家的redis中
                                staffclientStringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_DELIVERY_OPERATOR_NOTICE_MSG_QUEUE, value);
                            } catch (Exception e) {
                                logger.error("custom push redis error", e);
                            }
                        }
                    }
                    isSuccess = true;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_DELIVERY_QUEUE, tradeWaterId);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 合伙人退款更新有效期
     */
    @Override
    @Async("taskExecutor")
    public void updatePartnerAsync() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String queue = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_PARTNER_REFUND_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(queue)) {
                    continue;
                }
                boolean isSuccess = false;
                try {
                    ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                    List<PartnerNotification> list = objectMapper.readValue(queue, new TypeReference<List<PartnerNotification>>() {
                    });

                    logger.info("start to consume notify update partner refund queue : " + queue);

                    if (list == null || list.isEmpty()) {
                        continue;
                    }
                    for (PartnerNotification partnerNotification : list) {
                        logger.info("partnerNotification : " + partnerNotification);
                        partnerService.invalidate(partnerNotification);
                    }
                    isSuccess = true;

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_PARTNER_REFUND_QUEUE, queue);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    /**
     * 商品订单退款，包括：特卖、团购、核销券、合伙人、saas套件的售卖、自定义服务订单
     */
    @Override
    @Async("taskExecutor")
    public void eshopRefundAsync() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String str = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_ESHOP_REFUND_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(str)) {
                    continue;
                }

                JSONObject json = JSONObject.parseObject(str);
                String orderNo = json.getOrDefault("trade_water_id", "").toString();
                String productIds = json.getOrDefault("product_id", "").toString();

                boolean isSuccess = false;
                try {
                    logger.info("start to consume eshop refund queue, orderNo : " + orderNo);
                    if (StringUtils.isEmpty(orderNo)) {
                        continue;
                    }

                    ServiceOrder order = serviceOrderRepository.findByOrderNo(orderNo);
                    if (order != null) {
                        List<ServiceOrder> orderList = new ArrayList<>();
                        if (ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType()) {
                            orderList = serviceOrderRepository.findByGroupOrderId(order.getGroupOrderId());
                        } else {
                            orderList.add(order);
                        }
                        for (ServiceOrder o : orderList) {
                            baseOrderService.finishRefund(o, productIds);
                        }

                    }
                    isSuccess = true;

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_ESHOP_REFUND_QUEUE, orderNo);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    /**
     * 商品订单退款，包括：特卖、团购、核销券、合伙人、saas套件的售卖、自定义服务订单
     */
    @Override
    @Async("taskExecutor")
    public void consumeWuyeCouponAsync() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String value = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_WUYE_COUPON_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                boolean isSuccess = false;
                try {
                    ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                    TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {
                    };
                    Map<String, String> map = objectMapper.readValue(value, typeReference);
                    String couponId = map.get("couponId");
                    String orderId = map.get("orderId");
                    logger.info("start to consume wuye conpon queue, couponId : " + couponId + ", orderId : " + orderId);
                    couponService.consume(orderId, couponId);
                    isSuccess = true;

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_WUYE_COUPON_QUEUE, value);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    /**
     * 异步发送到账模板消息(给房屋绑定者推送)
     */
    @Override
    @Async("taskExecutor")
    public void sendWuyeNotification4HouseBinderAysc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_HOUSE_BINDER_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(json)) {
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                AccountNotification queue = objectMapper.readValue(json, new TypeReference<AccountNotification>() {
                });

                logger.info("start to consume wuyeNotificatione4HouseBinde queue : " + queue);

                boolean isSuccess = false;

                List<Map<String, String>> wuyeIdList = queue.getWuyeIds();
                if (wuyeIdList == null || wuyeIdList.isEmpty()) {
                    continue;
                }
                List<Map<String, String>> resendList = new ArrayList<>();
                for (Map<String, String> wuyeIdMap : wuyeIdList) {

                    User user = null;
                    String wuyeId = wuyeIdMap.get("wuyeid");
                    if (StringUtils.isEmpty(wuyeId)) {
                        logger.warn("wuyeId is empty, will skip. ");
                        continue;
                    }
                    List<User> userList = userRepository.findByWuyeId(wuyeId);
                    if (userList != null && !userList.isEmpty()) {
                        user = userList.get(0);
                    } else {
                        logger.warn("can not find user, wuyeId : " + wuyeId);
                    }
                    if (user != null) {
                        try {
                            queue.setUser(user);
                            gotongService.sendPayNotification4HouseBinder(queue);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);    //发送失败的，需要重发
                            resendList.add(wuyeIdMap);

                        }
                    }

                }
                if (resendList.isEmpty()) {
                    isSuccess = true;
                }

                if (!isSuccess) {
                    queue.setOpenids(resendList);
                    String value = objectMapper.writeValueAsString(queue);
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_HOUSE_BINDER_QUEUE, value);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }


    }

    /**
     * 给移动端的物业员工推送工单消息
     */
    @Override
    @Async("taskExecutor")
    public void sendWorkOrderMsgNotificationAsyc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String orderStr = redisTemplate.opsForList().leftPop(ModelConstant.KEY_WORKORER_MSG_QUEUE, 10, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(orderStr)) {
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                WorkOrderNotification won = objectMapper.readValue(orderStr, new TypeReference<WorkOrderNotification>() {
                });
                logger.info("start to consume workorder queue : " + won);

                //添加消息到消息中心
                saveNotice(won);

                boolean isSuccess = false;
                try {
                    logger.info("send workorder msg async, workorder : " + won);
                    isSuccess = gotongService.sendWorkOrderNotification(won);

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_WORKORER_MSG_QUEUE, orderStr);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void saveNotice(WorkOrderNotification won) {
        String title = "";
        String operName = "";
        if ("05".equals(won.getOperation())) {
            title = "您的" + won.getOrderType() + "工单已被受理";
            operName = won.getAcceptor();
        } else if ("02".equals(won.getOperation())) {
            title = "您的" + won.getOrderType() + "工单已被驳回";
            operName = won.getRejector();
        } else if ("07".equals(won.getOperation())) {
            title = "您的" + won.getOrderType() + "工单已完工";
            operName = won.getFinisher();
        }

        String content = won.getContent();
        if (!StringUtils.isEmpty(content)) {
            if (content.length() > 120) {
                content = content.substring(0, 110);
                content += "...";
            }
        }

        if (!StringUtils.isEmpty(title)) {
            StringBuilder sb = new StringBuilder();
            sb.append(title).append("|")
                    .append("工单编号：").append(won.getOrderId()).append("|")
                    .append("工单内容：").append(content).append("|")
                    .append("工单状态：").append(won.getOrderStatus()).append("|")
                    .append("工单处理人：").append(operName);

            List<com.yumu.hexie.integration.notify.Operator> operList = won.getOperatorList();
            if (operList != null && !operList.isEmpty()) {
                com.yumu.hexie.integration.notify.Operator operator = operList.get(0);
                if (!StringUtils.isEmpty(operator.getOpenid())) {
                    CommunityRequest request = new CommunityRequest();
                    request.setTitle(sb.toString());
                    request.setContent(sb.toString());
                    request.setSummary(sb.toString());
                    request.setAppid(operator.getAppid());
                    request.setOpenid(operator.getOpenid());
                    request.setNoticeType(ModelConstant.NOTICE_TYPE2_ORDER);

                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
                    request.setPublishDate(df1.format(new Date()));
                    request.setOutsideKey(Long.parseLong(won.getOrderId()));
                    String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_WORK_ORDER_DETAIL) + won.getOrderId();
                    String url = AppUtil.addAppOnUrl(msgUrl, operator.getAppid());
                    request.setUrl(url);
                    noticeService.addOutSidNotice(request);
                }
            }
        }
    }


    /**
     * 给移动端的物业员工推送工单消息
     */
    @Override
    @Async("taskExecutor")
    public void handleConversionAsyc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String orderStr = redisTemplate.opsForList().leftPop(ModelConstant.KEY_CONVERSION_MSG_QUEUE, 10, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(orderStr)) {
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                ConversionNotification cn = objectMapper.readValue(orderStr, new TypeReference<ConversionNotification>() {
                });
                logger.info("start to consume conversion queue : " + cn);

                String threadId = cn.getSourceId();
                if (StringUtils.isEmpty(threadId)) {
                    logger.warn("conversion source id is empty, will skip . orderId : " + cn.getOrderId());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * 发票开具模板消息通知
     */
    @Override
    @Async("taskExecutor")
    public void sendInvoiceMsgAsyc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String queue = redisTemplate.opsForList().leftPop(ModelConstant.KEY_INVOICE_NOTIFICATION_QUEUE, 10, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(queue)) {
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                InvoiceNotification in = objectMapper.readValue(queue, new TypeReference<InvoiceNotification>() {
                });

                //查看用户有没有在移动端申请，如果没有，不推送模板消息
                String orderId = in.getOrderId();
                String pageApplied = redisTemplate.opsForValue().get(ModelConstant.KEY_INVOICE_APPLICATIONF_FLAG + orderId);    //页面申请
                String applied = in.getApplied();    //公众号是1交易无须申请.其他交易0
                if (!"1".equals(pageApplied) && !"1".equals(applied)) {    //表示用户没有在移动端申请。扔回队列继续轮，直到用户在移动端申请位置

                    if (System.currentTimeMillis() - Long.parseLong(in.getTimestamp()) > 3600l * 24 * 10 * 1000) {    //超过10天没申请，出队
                        logger.info("user does not apply 4 invoice .. more than 10 days. will remove from the queue! orderId : " + orderId);
                    } else {
                        redisTemplate.opsForList().rightPush(ModelConstant.KEY_INVOICE_NOTIFICATION_QUEUE, queue);
//							logger.info("user does not apply 4 invoice .. will loop again. orderId: " + orderId);
                    }
                    continue;
                }
                logger.info("start to consume invoice msg queue : " + in);
                String openid = in.getOpenid();
                if (StringUtils.isEmpty(openid) || "null".equalsIgnoreCase(openid) || "0".equals(openid)) {
                    logger.warn("openid is null, will skip.");
                    continue;
                }

                User user = null;
                List<User> userList = userRepository.findByOpenid(openid);
                if (userList != null && !userList.isEmpty()) {
                    user = userList.get(0);
                } else {
                    logger.warn("can not find user, openid : " + openid);
                }
                boolean isSuccess = false;
                WechatResponse wechatResponse;
                if (user != null) {
                    try {
                        in.setUser(user);
                        logger.info("start send Msg4FinishInvoice,  invoiceNotification : " + in);
                        wechatResponse = gotongService.sendMsg4FinishInvoice(in);
                        logger.info("wechatResponse : " + wechatResponse);
                        if (wechatResponse.getErrcode() == 0) {
                            isSuccess = true;
                        } else {
                            if (wechatResponse.getErrcode() == 43004) {    //未关注的，不要重复发了。直接出队，并记录下来。
                                isSuccess = true;
                            } else if (wechatResponse.getErrcode() == 45009) {    //reach max api daily quota limit
                                isSuccess = true;
                            } else if (wechatResponse.getErrcode() == 40036) {		//invalid template_id size，没有配模板
                            	isSuccess = true;
                            } else if (wechatResponse.getErrcode() == 43101) {	//user refuse to accept the msg
                            	isSuccess = true;
							} else if (wechatResponse.getErrcode() == 40003) {
								isSuccess = true;	//invalid openid
							} else if (wechatResponse.getErrcode() == 48001) {
								isSuccess = true;
							} else if (wechatResponse.getErrcode() == 99999) {	//user refuse to accept the msg
                            	isSuccess = true;	//未配置模板消息
							}
                            if (isSuccess) {
                                try {
                                    BizError bizError = new BizError();
                                    bizError.setBizId(Long.parseLong(in.getOrderId()));
                                    bizError.setBizType(ModelConstant.EXCEPTION_BIZ_TYPE_TEMPLATEMSG);
                                    bizError.setLevel(ModelConstant.EXCEPTION_LEVEL_INFO);
                                    bizError.setMessage(wechatResponse.getErrmsg());
                                    bizError.setRemark(queue);
                                    bizErrorRepository.save(bizError);
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);    //发送失败的，需要重发

                    }
                    if (!isSuccess) {
                        redisTemplate.opsForList().rightPush(ModelConstant.KEY_INVOICE_NOTIFICATION_QUEUE, queue);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 电子收据开具成功模板消息通知
     */
    @Override
    @Async("taskExecutor")
    public void sendReceiptMsgAsyc() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String queue = redisTemplate.opsForList().leftPop(ModelConstant.KEY_RECEIPT_NOTIFICATION_QUEUE, 10, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(queue)) {
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                ReceiptNotification in = objectMapper.readValue(queue, new TypeReference<ReceiptNotification>() {});
                logger.info("receiptNotification is : " + in);
                String orderId = in.getTradeWaterId();	//交易流水号
                String appid = in.getAppid();	//判断贵州还是上海
                
                String userSysCode = SystemConfigServiceImpl.getSysMap().get(appid);	//是否_guizhou
        		String sysSource = "_sh";
        		if ("_guizhou".equals(userSysCode)) {
        			sysSource = "_guizhou";
        		}
                
                //查看用户有没有在移动端申请，如果没有，不推送模板消息
                String key = ModelConstant.KEY_RECEIPT_APPLICATIONF_FLAG + sysSource + ":" +orderId;
                String pageApplied = redisTemplate.opsForValue().get(key);    //页面申请
                String applied = in.getApplied();    //公众号是1交易无须申请.其他交易0
                if (!"1".equals(pageApplied) && !"1".equals(applied)) {    //表示用户没有在移动端申请。扔回队列继续轮，直到用户在移动端申请位置

                    if (System.currentTimeMillis() - Long.parseLong(in.getTimestamp()) > 3600l * 24 * 10 * 1000) {    //超过10天没申请，出队;电子收据理论上不会有这种情况
                        logger.info("user does not apply 4 invoice .. more than 10 days. will remove from the queue! orderId : " + orderId);
                    } else {
                        redisTemplate.opsForList().rightPush(ModelConstant.KEY_RECEIPT_NOTIFICATION_QUEUE, queue);
//							logger.info("user does not apply 4 invoice .. will loop again. orderId: " + orderId);
                    }
                    continue;
                }

                logger.info("start to consume receipt msg queue : " + in);
                String openid = in.getOpenid();
                if (StringUtils.isEmpty(openid) || "null".equalsIgnoreCase(openid) || "0".equals(openid)) {
                    logger.warn("openid is null, will skip.");
                    continue;
                }

                boolean isSuccess = false;
                try {
                    logger.info("start send Msg4FinishReceipt,  receiptNotification : " + in);
                    WechatResponse wechatResponse = gotongService.sendMsg4FinishReceipt(in);
                    logger.info("wechatResponse : " + wechatResponse);
                    if (wechatResponse.getErrcode() == 0) {
                        isSuccess = true;
                    } else {
                        if (wechatResponse.getErrcode() == 43004) {    //未关注的，不要重复发了。直接出队，并记录下来。
                            isSuccess = true;
                        } else if (wechatResponse.getErrcode() == 45009) {    //reach max api daily quota limit
                            isSuccess = true;
                        } else if (wechatResponse.getErrcode() == 40036) {		//invalid template_id size，没有配模板
                        	isSuccess = true;
                        } else if (wechatResponse.getErrcode() == 43101) {	//user refuse to accept the msg
                        	isSuccess = true;
                        } else if (wechatResponse.getErrcode() == 40003) {
							isSuccess = true;	//invalid openid
						} else if (wechatResponse.getErrcode() == 48001) {
							isSuccess = true;
						} else if (wechatResponse.getErrcode() == 99999) {	//user refuse to accept the msg
                        	isSuccess = true;	//未配置模板消息
						}
                        if (isSuccess) {
                            try {
                                BizError bizError = new BizError();
                                bizError.setBizId(Long.parseLong(in.getReceiptId()));
                                bizError.setBizType(ModelConstant.EXCEPTION_BIZ_TYPE_TEMPLATEMSG);
                                bizError.setLevel(ModelConstant.EXCEPTION_LEVEL_INFO);
                                bizError.setMessage(wechatResponse.getErrmsg());
                                bizError.setRemark(queue);
                                bizErrorRepository.save(bizError);
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);    //发送失败的，需要重发

                }
                if (!isSuccess) {
                    String retryKey = ModelConstant.KEY_EVENT_TEMPLATE_MSG_RETRY + sysSource + ":" +orderId;
					Long retry = redisTemplate.opsForValue().increment(retryKey);
					if (retry <= 5) {
						logger.info(",sg4FinishReceipt queue consume failed !, repush into the queue. json : " + queue);
						redisTemplate.opsForList().rightPush(ModelConstant.KEY_RECEIPT_NOTIFICATION_QUEUE, queue);
					} else {
						logger.info("retry times reached max, will discard the msg, orderId : " + orderId);
						redisTemplate.delete(retryKey);
					}
                    
                }
            
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 团购到货通知
     */
    @Override
    @Async("taskExecutor")
    public void noticeRgroupArrial() {

        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String str = redisTemplate.opsForList().leftPop(ModelConstant.KEY_RGROUP_ARRIVAL_NOTICE_QUEUE, 30, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(str)) {
                    logger.info("queue str is empty, will skip !");
                    continue;
                }
                
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                QueryRgroupsVO queryRgroupsVO = objectMapper.readValue(str, new TypeReference<QueryRgroupsVO>() {});
                String ruleId = queryRgroupsVO.getRuleId();		//团购规则id
                String leaderId = queryRgroupsVO.getUserid();	//团长id
                
                logger.info("start to consume noticeRgroupArrial queue : " + queryRgroupsVO);
                if (StringUtils.isEmpty(ruleId)) {
                    logger.info("ruleId is null, will skip !");
                    continue;
                }

                try {
            		List<Integer> groupStatus = new ArrayList<>();
            		groupStatus.add(ModelConstant.RGROUP_STAUS_FINISH);
            		
            		List<ServiceOrder> orderList = serviceOrderRepository.findByRGroupAndGroupStatusAndLeaderId(ruleId, leaderId, groupStatus);
            		if (orderList == null || orderList.isEmpty()) {
						logger.info("can't find orders, will skip . ruleId : " + ruleId);
					}
            		for (ServiceOrder serviceOrder : orderList) {
//            			if (ModelConstant.ORDER_STATUS_SENDED == serviceOrder.getStatus()) {
//            				logger.info("order sended, will skip !");
//            				continue;
//						}
            			if (ModelConstant.ORDER_STATUS_RECEIVED == serviceOrder.getStatus()) {
            				logger.info("order received, will skip !");
            				continue;
						}
            			userNoticeService.groupArriaval(serviceOrder);
            			
            		}

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 通知成团
     */
    @Async("taskExecutor")
    @Override
    public void notifyGroupSuccess() {
        while (true) {
            try {

                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String str = redisTemplate.opsForList().leftPop(ModelConstant.KEY_RGROUP_SUCCESS_NOTICE_MSG_QUEUE,
                        10, TimeUnit.SECONDS);

                if (str == null) {
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                NoticeRgroupSuccess noticeRgroupSuccess = objectMapper.readValue(str, NoticeRgroupSuccess.class);
                logger.info("strat to noticeRgroupSuccess queue : " + noticeRgroupSuccess);
                
                Date pubDate = new Date();
                for (long userId : noticeRgroupSuccess.getOpers()) {
                    User sendUser = userRepository.findById(userId);
                    
                    Notice notice = new Notice();
                    notice.setNoticeType(ModelConstant.NOTICE_TYPE2_RGROUP);
                    notice.setAppid(sendUser.getAppId());
                    notice.setContent(noticeRgroupSuccess.getProductName());
                    notice.setOpenid(sendUser.getOpenid());
                    notice.setOutsideKey(noticeRgroupSuccess.getRuleId());
                    notice.setTitle(noticeRgroupSuccess.getSectName() + " 拼团成功");
                    notice.setStatus(0);
                    notice.setPublishDate(DateUtil.dtFormat(pubDate, DateUtil.dttmSimple));
                    notice.setSummary(noticeRgroupSuccess.getProductName());
                }

                boolean isSuccess = false;
                try {
                    gotongService.sendGroupSuccessNotification(noticeRgroupSuccess);
                    isSuccess = true;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
                }

                if (!isSuccess) {
                    logger.info("notifyGroupSuccess failed !, repush into the queue. : " + str);
                    redisTemplate.opsForList().rightPush(ModelConstant.KEY_RGROUP_SUCCESS_NOTICE_MSG_QUEUE, str);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    @Async("taskExecutor")
    public void notifyInteractComment() {
        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String str = redisTemplate.opsForList().leftPop(ModelConstant.interactReplyNoticeQueue, 10, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(str)) {
                    logger.info("queue str is empty, will skip !");
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                InteractCommentNotice commentNotice = objectMapper.readValue(str, InteractCommentNotice.class);
                logger.info("start to InteractCommentNotice queue : " + commentNotice);
                
                if (StringUtils.isEmpty(commentNotice.getAppid())) {
					logger.warn("user appid is null, will skip noticing !");
					continue;
				}
                
                List<User> userList = userRepository.findByOpenid(commentNotice.getOpenid());
                User user = null;
                if (userList != null && !userList.isEmpty()) {
                    user = userList.get(0);
                } else {
                	user = userRepository.findByMiniopenid(commentNotice.getOpenid());
                }
                if(user == null) {
                	logger.warn("can't find user : " + commentNotice.getOpenid() + ", will skip noticing !");
					continue;
                }
                commentNotice.setOpenid(user.getOpenid());
                commentNotice.setAppid(user.getAppId());
                commentNotice.setMiniOpenid(user.getMiniopenid());
                commentNotice.setMiniAppid(user.getMiniAppId());

                //保存到通知表
                //添加到消息中心
                CommunityRequest request = new CommunityRequest();
                StringBuilder sb = new StringBuilder();
                sb.append("意见标题：").append(commentNotice.getContent()).append("|");
                sb.append("回复内容：").append(commentNotice.getCommentContent()).append("|");
                sb.append("回复人：").append(commentNotice.getCommentName());
                request.setTitle(sb.toString());
                request.setContent(sb.toString());
                request.setSummary(sb.toString());
                request.setAppid(commentNotice.getAppid());
                request.setOpenid(commentNotice.getOpenid());
                request.setNoticeType(ModelConstant.NOTICE_TYPE2_THREAD);
                String url = wechatMsgService.getMsgUrl(MsgCfg.URL_OPINION_NOTICE);
                url = AppUtil.addAppOnUrl(url, commentNotice.getAppid());
                url = url.replaceAll("INTERACT_ID", commentNotice.getInteractId()+"");
                request.setUrl(url);
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
                request.setPublishDate(df1.format(new Date()));
                noticeService.addOutSidNotice(request);

                boolean isSuccess = false;
                try {
                    WechatResponse wechatResponse = gotongService.sendInteractNotification(commentNotice);
                    logger.info("wechatResponse : " + wechatResponse);
					
					if (wechatResponse.getErrcode() == 0) {
						isSuccess = true;
					}
					if (wechatResponse.getErrcode() == 40037) {
						logger.error("invalid template_id, 请联系系统管理员！");
						isSuccess = true;
					}
					if (wechatResponse.getErrcode() == 45009) {
						logger.error("reach max api daily quota limit, 请联系系统管理员！");
						isSuccess = true;
					}
					if (wechatResponse.getErrcode() == 43004) {
						logger.error("require subscribe, 请联系系统管理员！");
						isSuccess = true;
					}
					if (wechatResponse.getErrcode() == 43101) {	//user refuse to accept the msg
                    	isSuccess = true;
					}
					if (wechatResponse.getErrcode() == 48001) {
						logger.error("api unauthorized, 请联系系统管理员！");
						isSuccess = true;
					}
					if (wechatResponse.getErrcode() == 99998) {
                    	isSuccess = true;	//appid为空
					}
					if (wechatResponse.getErrcode() == 99999) {
                    	isSuccess = true;	//未配置模板消息
					}
					
					logger.info("wechatResponse : " + wechatResponse);
                    
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    logger.info("InteractCommentNotice failed !, repush into the queue. : " + str);
                    redisTemplate.opsForList().rightPush(ModelConstant.interactReplyNoticeQueue, str);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    //
    @Override
    @Async("taskExecutor")
    public void notifyInteractGrade() {
        while (true) {
            try {
                if (!maintenanceService.isQueueSwitchOn()) {
                    logger.info("queue switch off ! ");
                    Thread.sleep(60000);
                    continue;
                }
                String str = redisTemplate.opsForList().leftPop(ModelConstant.interactGradeNoticeQueue, 10, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(str)) {
                    logger.info("queue str is empty, will skip !");
                    continue;
                }
                ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
                InteractCommentNotice notice = objectMapper.readValue(str, InteractCommentNotice.class);
                logger.info("start to notifyInteractGrade queue : " + notice);
                
                List<User> userList = userRepository.findByOpenid(notice.getOpenid());
                User user = null;
                if (userList != null && !userList.isEmpty()) {
                    user = userList.get(0);
                } else {
                	user = userRepository.findByMiniopenid(notice.getOpenid());
                }
                if(user == null) {
                	logger.warn("can't find user : " + notice.getOpenid() + ", will skip notifyInteractGrade !");
					continue;
                }
                notice.setOpenid(user.getOpenid());
                notice.setAppid(user.getAppId());

                boolean isSuccess = false;
                try {
                    gotongService.sendInteractGradeNotification(notice);
                    isSuccess = true;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (!isSuccess) {
                    logger.info("notifyInteractGrade failed !, repush into the queue. : " + str);
                    redisTemplate.opsForList().rightPush(ModelConstant.interactGradeNoticeQueue, str);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
