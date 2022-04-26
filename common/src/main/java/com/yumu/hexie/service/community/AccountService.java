package com.yumu.hexie.service.community;

import com.yumu.hexie.integration.community.req.*;
import com.yumu.hexie.integration.community.resp.*;
import com.yumu.hexie.model.commonsupport.info.ProductDepot;
import com.yumu.hexie.model.commonsupport.info.ProductDepotTags;
import com.yumu.hexie.model.user.User;

import java.util.List;
import java.util.Map;

public interface AccountService {

    //获取账户余额
    AccountInfoVO getAccountInfo(User user) throws Exception;

    //获取账户余额和银行卡信息
    AccountSurplusVO getSurplusAndBank(User user) throws Exception;

    //申请提现
    boolean applySurplu(User user, SurplusVO surplusVO) throws Exception;

    //获取账户流水列表
    QueryWaterListResp queryWaterList(User user, QueryWaterVO queryWaterVO) throws Exception;

    //绑定银行卡
    boolean saveBank(User user, BankVO bankVO) throws Exception;

    //查询团购列表
    List<GroupInfoVo> queryGroupList(User user, QueryGroupReq queryGroupReq);

    //更新团购状态
    Boolean updateGroupInfo(User user, String groupId, String operType);

    //查询团购汇总信息
    GroupSumResp queryGroupSum(User user, String groupId) throws Exception;

    //查询团购订单列表
    List<GroupOrderVo> queryGroupOrder(User user, QueryGroupReq queryGroupReq) throws Exception;

    //根据订单ID查询订单详情
    GroupOrderVo queryGroupOrderDetail(User user, String orderId);

    //订单核销
    Boolean handleVerifyCode(User user, String orderId, String code);

    //取消订单
    Boolean cancelOrder(User user, String orderId) throws Exception;

    //订单退款
    Boolean refundOrder(User user, RefundInfoReq refundInfoReq) throws Exception;

    //未提货通知
    Boolean noticeReceiving(User user, String groupId);

    //查询商品列表
    List<ProductDepot> queryProductDepotList(User user, String searchValue, int currentPage);

    //删除商品
    Boolean delProductDepot(User user, String productId);

    //新增编辑商品
    Boolean operProductDepot(User user, ProductDepotReq productDepotReq);

    //根据商品ID查询商品库
    ProductDepot queryProductDepotDetail(User user, String productId);

    Map<String, List<ProductDepotTags>> queryProductDepotTags(User user);

    //添加自定义标签
    Boolean saveDepotTag(User user, String tagName);

    //删除自定义标签
    Boolean delDepotTag(User user, String tagId);
}
