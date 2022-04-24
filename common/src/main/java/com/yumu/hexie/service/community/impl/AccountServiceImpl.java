package com.yumu.hexie.service.community.impl;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.community.CommunityUtil;
import com.yumu.hexie.integration.community.req.*;
import com.yumu.hexie.integration.community.resp.*;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.community.AccountService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.user.UserNoticeService;
import com.yumu.hexie.service.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-08 15:57
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserService userService;

    @Autowired
    private CommunityUtil communityUtil;

    @Autowired
    private RgroupRuleRepository rgroupRuleRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Inject
    private BaseOrderService baseOrderService;

    @Autowired
    private UserNoticeService userNoticeService;

    @Override
    public AccountInfoVO getAccountInfo(User user) throws Exception {
        OrgOperator orgOperator = userService.getOrgOperator(user);
        CommonResponse<AccountInfoVO> commonResponse = communityUtil.queryAccountInfo(user, orgOperator);
        if (!"00".equals(commonResponse.getResult())) {
            throw new BizValidateException("加载账户信息失败, errMsg : " + commonResponse.getErrMsg());
        }
        return commonResponse.getData();
    }

    @Override
    public AccountSurplusVO getSurplusAndBank(User user) throws Exception {
        OrgOperator orgOperator = userService.getOrgOperator(user);
        CommonResponse<AccountSurplusVO> commonResponse = communityUtil.querySurplus(user, orgOperator);
        if (!"00".equals(commonResponse.getResult())) {
            throw new BizValidateException("获取账户余额失败, errMsg : " + commonResponse.getErrMsg());
        }
        return commonResponse.getData();
    }

    @Override
    public boolean applySurplu(User user, SurplusVO surplusVO) throws Exception {
        OrgOperator orgOperator = userService.getOrgOperator(user);
        CommonResponse<Boolean> commonResponse = communityUtil.applySurplus(user, orgOperator, surplusVO);
        if (!"00".equals(commonResponse.getResult())) {
            throw new BizValidateException("提现申请失败, errMsg : " + commonResponse.getErrMsg());
        }
        return commonResponse.getData();
    }

    @Override
    public QueryWaterListResp queryWaterList(User user, QueryWaterVO queryWaterVO) throws Exception {
        OrgOperator orgOperator = userService.getOrgOperator(user);
        CommonResponse<QueryWaterListResp> commonResponse = communityUtil.queryWaterList(user, orgOperator, queryWaterVO);
        if (!"00".equals(commonResponse.getResult())) {
            throw new BizValidateException("获取账户明细失败, errMsg : " + commonResponse.getErrMsg());
        }
        return commonResponse.getData();
    }

    @Override
    public boolean saveBank(User user, BankVO bankVO) throws Exception {
        OrgOperator orgOperator = userService.getOrgOperator(user);
        CommonResponse<Boolean> commonResponse = communityUtil.saveBank(user, orgOperator, bankVO);
        if (!"00".equals(commonResponse.getResult())) {
            throw new BizValidateException("绑定银行卡失败, errMsg : " + commonResponse.getErrMsg());
        }
        return commonResponse.getData();
    }

    @Override
    public List<GroupInfoVo> queryGroupList(User user, QueryGroupReq queryGroupReq) {
        try {
            List<Sort.Order> sortList = new ArrayList<>();
            Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
            sortList.add(order);
            Sort sort = Sort.by(sortList);
            Pageable pageable = PageRequest.of(queryGroupReq.getCurrentPage(), 10, sort);

            Page<Object[]> page = rgroupRuleRepository.findRgroupList(user.getId(), queryGroupReq.getQueryName(), queryGroupReq.getGroupStatus(), pageable);
            List<GroupInfoVo> list = ObjectToBeanUtils.objectToBean(page.getContent(), GroupInfoVo.class);

            if (list == null) {
                list = new ArrayList<>();
            }

            for (GroupInfoVo info : list) {
                info.setGroupDate(DateUtil.getSendTime(info.getCreateDate().longValue()));
                if (info.getStatus() == 1) {
                    Date date = new Date();
                    if (info.getStartDate().getTime() <= date.getTime()
                            && info.getEndDate().getTime() <= date.getTime()) { //跟团中
                        info.setGroupStatusCn("正在跟团中");
                    } else if (info.getEndDate().getTime() > date.getTime()) {
                        info.setGroupStatusCn("已结束");
                    } else if (info.getEndDate().getTime() < date.getTime()) {
                        info.setGroupStatusCn("未开始");
                    }
                } else {
                    info.setGroupStatusCn("预览中");
                }

                //统计支付的，退款的，取消的，预览的
                float realityAmt = 0;
                float refundAmt = 0;
                List<ServiceOrder> serviceOrderList = serviceOrderRepository.findByGroupRuleId(info.getId().longValue());
                for (ServiceOrder serviceOrder : serviceOrderList) {
                    if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_PAYED
                            || serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_SENDED
                            || serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_RECEIVED
                            || serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_CONFIRM) {
                        realityAmt += serviceOrder.getPrice();
                    } else if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_REFUNDED) {
                        refundAmt += serviceOrder.getPrice();
                    }
                }

                info.setRealityAmt(realityAmt);
                info.setRefundAmt(refundAmt);

                //TODO 后面从缓存里取
                info.setFollowNum(1);
                info.setCancelNum(1);
                info.setQueryNum(10);
            }
            return list;
        } catch (Exception e) {
            throw new BizValidateException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Boolean updateGroupInfo(User user, String groupId, String operType) {

        if (!"1".equals(operType) && !"3".equals(operType)) {
            throw new BizValidateException("不合法的操作数据类型");
        }
        if (ObjectUtils.isEmpty(groupId)) {
            throw new BizValidateException("团购编号为空，请刷新重试");
        }

        Optional<RgroupRule> optional = rgroupRuleRepository.findById(Long.parseLong(groupId));
        if (optional.isPresent()) {
            RgroupRule rgroupRule = optional.get();
            if ("1".equals(operType)) {
                //修改团购结束日期
                rgroupRule.setEndDate(new Date());
            } else {
                //下架团购
                rgroupRule.setStatus(0);
            }
            throw new BizValidateException("为查到团购信息，请刷新重试");
        }
        return true;
    }

    @Override
    public GroupSumResp queryGroupSum(User user, String groupId) throws Exception {
        if (ObjectUtils.isEmpty(groupId)) {
            throw new BizValidateException("团购ID不能为空，请刷新重试");
        }

        //汇总当前团购的有效订单，总金额和退款金额
        GroupSumResp resp = new GroupSumResp();
        List<GroupSumResp.SearchVo> searchVoList = new ArrayList<>();

        List<ServiceOrder> serviceOrderList = serviceOrderRepository.findByGroupRuleId(Long.parseLong(groupId));
        int validNum = serviceOrderList.size(); //有效订单数
        float totalAmt = 0; //总金额
        float refundAmt = 0; //退款金额
        for (ServiceOrder serviceOrder : serviceOrderList) {
            if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_CANCEL) {
                validNum = validNum - 1;
                refundAmt += serviceOrder.getPrice();
            }
            totalAmt += serviceOrder.getPrice();
            if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_REFUNDED) {
                refundAmt += serviceOrder.getPrice();
            }
        }
        //有效订单 全部订单-取消的订单
        GroupSumResp.SearchVo searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("有效订单");
        searchVo.setNum(String.valueOf(validNum));
        searchVo.setMessage("有效订单：全部订单-已取消订单");
        searchVoList.add(searchVo);

        //总金额 全部订单的金额，包括取消的订单
        searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("订单总金额");
        searchVo.setNum("¥" + totalAmt);
        searchVo.setMessage("订单总金额：所有订单金额的加总（包含已取消订单）");
        searchVoList.add(searchVo);

        //退款金额 全部订单的退款金额
        searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("退款金额");
        searchVo.setNum("¥" + refundAmt);
        searchVo.setMessage("退款金额：所有订单的退款金额（包含已取消订单）");
        searchVoList.add(searchVo);
        resp.setSearchVoList(searchVoList);

        //查询团下单的商品列表
        GroupSumResp.ProductVo productVo = new GroupSumResp.ProductVo();

        List<Integer> status = new ArrayList<>();
        status.add(ModelConstant.ORDER_STATUS_PAYED);
        status.add(ModelConstant.ORDER_STATUS_SENDED);
        status.add(ModelConstant.ORDER_STATUS_RECEIVED);

        List<Object[]> groupProductSumVos = serviceOrderRepository.findProductSum(Long.parseLong(groupId), status);
        List<GroupProductSumVo> vos = ObjectToBeanUtils.objectToBean(groupProductSumVos, GroupProductSumVo.class);
        int totalNum = 0;
        int totalVerify = 0;
        for (GroupProductSumVo vo : vos) {

            totalNum += vo.getCount().intValue();
            totalVerify += vo.getVerifyNum().intValue();
        }
        productVo.setProducts(vos);
        productVo.setTotalNum(totalNum);
        productVo.setVerifyNum(totalVerify);

        resp.setProductVo(productVo);
        return resp;
    }

    @Override
    public List<GroupOrderVo> queryGroupOrder(User user, QueryGroupReq queryGroupReq) throws Exception {
        List<Sort.Order> sortList = new ArrayList<>();
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
        sortList.add(order);
        Sort sort = Sort.by(sortList);
        Pageable pageable = PageRequest.of(queryGroupReq.getCurrentPage(), 10, sort);

        Page<Object[]> page = serviceOrderRepository.findByGroupRuleIdPage(Long.parseLong(queryGroupReq.getGroupId()), pageable);
        List<GroupOrderVo> list = ObjectToBeanUtils.objectToBean(page.getContent(), GroupOrderVo.class);

        for (GroupOrderVo vo : list) {
            vo.setStatusCn(ServiceOrder.getStatusStr(vo.getStatus()));
            vo.setOrderDate(DateUtil.dttmFormat(vo.getPayDate()));

            //0商户派送 1用户自提 2第三方配送
            String logisticType = "商户派送";
            if (vo.getLogisticType() == 1) {
                logisticType = "用户自提";
            } else if (vo.getLogisticType() == 2) {
                logisticType = "第三方配送";
            }
            vo.setLogisticTypeCn(logisticType);

            //获取用户昵称和头像
            User userInfo = userRepository.findById(vo.getUserId().longValue());
            if (userInfo != null) {
                vo.setUserName(userInfo.getNickname());
                vo.setUserHead(userInfo.getHeadimgurl());
            }

            List<BuyGoodsVo> buyVos = new ArrayList<>();
            //查询订单下的商品
            ServiceOrder serviceOrder = new ServiceOrder();
            serviceOrder.setId(vo.getId().longValue());
            List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
            for (OrderItem item : items) {
                //订单里购买的商品
                BuyGoodsVo buyVo = new BuyGoodsVo();
                buyVo.setGoodsName(item.getProductName());
                buyVo.setGoodsNum(item.getCount());
                buyVo.setGoodsAmt(item.getPrice());
                buyVos.add(buyVo);
            }
            vo.setBuyGoodsVoList(buyVos);
        }
        return list;
    }

    @Override
    public GroupOrderVo queryGroupOrderDetail(User user, String orderId) {
        if (ObjectUtils.isEmpty(orderId)) {
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

        //0商户派送 1用户自提 2第三方配送
        String logisticType = "商户派送";
        if (groupOrder.getLogisticType() == 1) {
            logisticType = "用户自提";
        } else if (groupOrder.getLogisticType() == 2) {
            logisticType = "第三方配送";
        }
        groupOrder.setLogisticTypeCn(logisticType);

        //获取用户昵称和头像
        User userInfo = userRepository.findById(serviceOrder.getUserId());
        if (userInfo != null) {
            groupOrder.setUserName(userInfo.getNickname());
            groupOrder.setUserHead(userInfo.getHeadimgurl());
        }

        List<BuyGoodsVo> buyVos = new ArrayList<>();
        //查询订单下的商品
        List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
        for (OrderItem item : items) {
            //订单里购买的商品
            BuyGoodsVo buyVo = new BuyGoodsVo();
            buyVo.setGoodsId(item.getProductId());
            buyVo.setGoodsName(item.getProductName());
            buyVo.setGoodsNum(item.getCount());
            buyVo.setGoodsAmt(item.getPrice());
            buyVo.setGoodsImage(item.getProductPic());
            buyVos.add(buyVo);
        }
        groupOrder.setBuyGoodsVoList(buyVos);
        return groupOrder;
    }

    @Override
    @Transactional
    public Boolean handleVerifyCode(User user, String orderId, String code) {
        if (ObjectUtils.isEmpty(code)) {
            throw new BizValidateException("核销码为空，请重新输入");
        }

        ServiceOrder serviceOrder = serviceOrderRepository.findByIdAndGroupLeaderId(Long.parseLong(orderId), user.getId());
        if (serviceOrder == null) {
            throw new BizValidateException("未查到订单，请刷新重试");
        }
        List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
        boolean flag = false;
        for (OrderItem item : items) {
            if (item.getVerifyStatus() == '0' && item.getCode().equals(code)) {
                item.setVerifyStatus(1);
                orderItemRepository.save(item);
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
        ServiceOrder serviceOrder = serviceOrderRepository.findByIdAndGroupLeaderId(Long.parseLong(orderId), user.getId());
        if (serviceOrder == null) {
            throw new BizValidateException("未查到订单，请刷新重试");
        }
        //TODO 取消订单 在快团团上是分为2步，取消订单和退款，可以只先取消订单，也可以取消订单并退款
        reFunding(serviceOrder);
//        serviceOrder.setStatus(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
//        serviceOrderRepository.save(serviceOrder);
        return true;
    }

    @Override
    @Transactional
    public Boolean refundOrder(User user, RefundInfoReq refundInfoReq) throws Exception {
        ServiceOrder serviceOrder = serviceOrderRepository.findByIdAndGroupLeaderId(Long.parseLong(refundInfoReq.getOrderId()), user.getId());
        if (serviceOrder == null) {
            throw new BizValidateException("未查到订单，请刷新重试");
        }
        reFunding(serviceOrder);
        return true;
    }

    private void reFunding(ServiceOrder o) throws Exception {
        o.setGroupStatus(ModelConstant.GROUP_STAUS_CANCEL);
        if (ModelConstant.ORDER_STATUS_PAYED == o.getStatus()) {
            baseOrderService.refund(o);
        } else {
            baseOrderService.cancelOrder(o);
        }
    }

    @Override
    public Boolean noticeReceiving(User user, String groupId) {
        if (ObjectUtils.isEmpty(groupId)) {
            throw new BizValidateException("团购编号未知，请刷新重试");
        }
        List<ServiceOrder> list = new ArrayList<>();
        //查询出未提货的订单，前提必须是已经成团
        Optional<RgroupRule> optional = rgroupRuleRepository.findById(Long.parseLong(groupId));
        if (optional.isPresent()) {
            RgroupRule rgroupRule = optional.get();
            if (rgroupRule.getGroupStatus() == ModelConstant.RGROUP_STAUS_FINISH) {
                List<ServiceOrder> serviceOrderList = serviceOrderRepository.findByGroupRuleId(Long.parseLong(groupId));
                for (ServiceOrder order : serviceOrderList) {
                    if (order.getStatus() == ModelConstant.ORDER_STATUS_PAYED) {
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
}
