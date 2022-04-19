package com.yumu.hexie.service.community.impl;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.community.CommunityUtil;
import com.yumu.hexie.integration.community.req.BankVO;
import com.yumu.hexie.integration.community.req.QueryWaterVO;
import com.yumu.hexie.integration.community.req.SurplusVO;
import com.yumu.hexie.integration.community.resp.*;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.community.AccountService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private ProductRepository productRepository;

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
    public List<GroupInfoVo> queryGroupList(User user, String queryName, String groupStatus) {
        List<GroupInfoVo> list = new ArrayList<>();
        //TODO
        GroupInfoVo resp = new GroupInfoVo();
        resp.setGroupId("1");
        resp.setGroupName("水果套餐A");
        resp.setGroupPrice("108.00");
        resp.setGroupDate("10分钟前");
        resp.setGroupStatus("1");
        resp.setGroupStatusCn("正在跟团中");
        resp.setRealityAmt("216.00");
        resp.setRefundAmt("108.00");
        resp.setFollowNum("2");
        resp.setCancelNum("1");
        resp.setQueryNum("10");
        list.add(resp);

        resp = new GroupInfoVo();
        resp.setGroupId("2");
        resp.setGroupName("水果套餐B");
        resp.setGroupPrice("199.00");
        resp.setGroupDate("10分钟前");
        resp.setGroupStatus("9");
        resp.setGroupStatusCn("已结束");
        resp.setRealityAmt("398.00");
        resp.setRefundAmt("398.00");
        resp.setFollowNum("2");
        resp.setCancelNum("2");
        resp.setQueryNum("10000");
        list.add(resp);

        resp = new GroupInfoVo();
        resp.setGroupId("3");
        resp.setGroupName("水果套餐C");
        resp.setGroupPrice("199.00");
        resp.setGroupDate("10分钟前");
        resp.setGroupStatus("9");
        resp.setGroupStatusCn("已结束");
        resp.setRealityAmt("398.00");
        resp.setRefundAmt("398.00");
        resp.setFollowNum("2");
        resp.setCancelNum("2");
        resp.setQueryNum("10000");
        list.add(resp);

        resp = new GroupInfoVo();
        resp.setGroupId("4");
        resp.setGroupName("水果套餐D");
        resp.setGroupPrice("199.00");
        resp.setGroupDate("10分钟前");
        resp.setGroupStatus("9");
        resp.setGroupStatusCn("已结束");
        resp.setRealityAmt("398.00");
        resp.setRefundAmt("398.00");
        resp.setFollowNum("2");
        resp.setCancelNum("2");
        resp.setQueryNum("10000");
        list.add(resp);

        if ("1".equals(groupStatus)) {
            list.remove(1);
        } else if ("2".equals(groupStatus)) {
            list = new ArrayList<>();
        } else if ("3".equals(groupStatus)) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public Boolean updateGroupInfo(User user, String groupId, String operType) {
        if ("1".equals(operType)) { //结束团

        } else if ("3".equals(operType)) { //删除团

        } else if ("4".equals(operType)) { //隐藏团

        } else {
            throw new BizValidateException("不合法的操作数据类型");
        }
        return true;
    }

    @Override
    public GroupSumResp queryGroupSum(User user, String groupId) {
        Assert.hasText(groupId, "团购ID不能为空");

        //汇总当前团购的有效订单，总金额和退款金额
        GroupSumResp resp = new GroupSumResp();
        List<GroupSumResp.SearchVo> searchVoList = new ArrayList<>();
        //有效订单 全部订单-取消的订单
        GroupSumResp.SearchVo searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("有效订单");
        searchVo.setNum("12");
        searchVo.setMessage("有效订单：全部订单-已取消订单");
        searchVoList.add(searchVo);

        //总金额 全部订单的金额，包括取消的订单
        searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("订单总金额");
        searchVo.setNum("1200.2");
        searchVo.setMessage("订单总金额：所有订单金额的加总（包含已取消订单）");
        searchVoList.add(searchVo);

        //退款金额 全部订单的退款金额
        searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("退款金额");
        searchVo.setNum("12.00");
        searchVo.setMessage("退款金额：所有订单的退款金额（包含已取消订单）");
        searchVoList.add(searchVo);
        resp.setSearchVoList(searchVoList);

        //查询团购商品列表
        GroupSumResp.ProductVo productVo = new GroupSumResp.ProductVo();
        List<GroupSumResp.Product> products = new ArrayList<>();
        int totalNum = 0;
        int totalVerify = 0;
        for(int i=0; i<4; i++) {
            GroupSumResp.Product product = new GroupSumResp.Product();
            product.setId(i+"");
            product.setName("套餐"+i);
            product.setNum(20);
            product.setVerify(10);
            products.add(product);

            totalNum+= 20;
            totalVerify+= 10;
        }
        productVo.setProducts(products);
        productVo.setTotalNum(totalNum);
        productVo.setVerifyNum(totalVerify);
        resp.setProductVo(productVo);
        return resp;
    }

    @Override
    public List<GroupOrderVo> queryGroupOrder(User user, String groupId, String orderStatus, String searchValue) {
        List<GroupOrderVo> groupOrders = new ArrayList<>();
        for(int i=0; i<2; i++) {
            GroupOrderVo groupOrder = new GroupOrderVo();
            groupOrder.setGroupNum(i+1 +"");
            groupOrder.setOrderId("123");
            groupOrder.setOrderStatus("已支付");
            groupOrder.setUserName("张三"+i);
            groupOrder.setUserHead("https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK2dBo1ovGPzBEW8N5IibJTbgsy6ic317Cj8VhzlPBstaI1fWJ6vVg4nudNK2IxZ4mXPjxwMoUQQOug/132");
            groupOrder.setOrderDate("2022-12-25 12:25:45");
            groupOrder.setOrderNum(i+1);
            groupOrder.setTotalAmt(new BigDecimal("54152.24"));
            groupOrder.setReceiverName("刘仲杰");
            groupOrder.setReceiverTel("17349778859");
            groupOrder.setReceiverAddr("上海市闵行区浦涛路510弄15号1201");
            if(i== 0) {
                groupOrder.setLogistics("客户自提");
                groupOrder.setGroupDesc("这里是备注");
            }else {
                groupOrder.setLogistics("同城配送");
                groupOrder.setGroupDesc("");
            }

            List<BuyGoodsVo> buyVos = new ArrayList<>();
            for(int k=0; k<2; k++) {
                //订单里购买的商品
                BuyGoodsVo buyVo = new BuyGoodsVo();
                buyVo.setGoodsName("水果"+k);
                buyVo.setGoodsNum(1);
                buyVo.setGoodsAmt(new BigDecimal(10));
                buyVos.add(buyVo);
            }
            groupOrder.setBuyGoodsVoList(buyVos);
            groupOrders.add(groupOrder);
        }

        return groupOrders;
    }

    @Override
    public GroupOrderVo queryGroupOrderDetail(User user, String orderId) {
        GroupOrderVo groupOrder = new GroupOrderVo();
        groupOrder.setGroupNum(1 +"");
        groupOrder.setOrderId("123");
        groupOrder.setOrderStatus("已支付");
        groupOrder.setUserName("张三");
        groupOrder.setUserHead("https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK2dBo1ovGPzBEW8N5IibJTbgsy6ic317Cj8VhzlPBstaI1fWJ6vVg4nudNK2IxZ4mXPjxwMoUQQOug/132");
        groupOrder.setOrderDate("2022-12-25 12:25:45");
        groupOrder.setOrderNum(1);
        groupOrder.setTotalAmt(new BigDecimal("54152.24"));
        groupOrder.setReceiverName("刘仲杰");
        groupOrder.setReceiverTel("17349778859");
        groupOrder.setReceiverAddr("上海市闵行区浦涛路510弄15号1201");
        groupOrder.setLogistics("客户自提");
        groupOrder.setGroupDesc("这里是备注");

        List<BuyGoodsVo> buyVos = new ArrayList<>();
        for(int k=0; k<2; k++) {
            //订单里购买的商品
            BuyGoodsVo buyVo = new BuyGoodsVo();
            buyVo.setGoodsName("水果"+k);
            buyVo.setGoodsNum(1);
            buyVo.setGoodsAmt(new BigDecimal(10));
            buyVo.setGoodsImage("");
            buyVos.add(buyVo);
        }
        groupOrder.setBuyGoodsVoList(buyVos);

        return groupOrder;
    }
}
