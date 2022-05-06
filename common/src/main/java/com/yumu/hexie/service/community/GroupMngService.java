package com.yumu.hexie.service.community;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.community.req.OutSidProductDepotReq;
import com.yumu.hexie.integration.community.req.ProductDepotReq;
import com.yumu.hexie.integration.community.req.QueryGroupReq;
import com.yumu.hexie.integration.community.req.RefundInfoReq;
import com.yumu.hexie.integration.community.resp.GroupInfoVo;
import com.yumu.hexie.integration.community.resp.GroupOrderVo;
import com.yumu.hexie.integration.community.resp.GroupSumResp;
import com.yumu.hexie.model.commonsupport.info.ProductDepot;
import com.yumu.hexie.model.commonsupport.info.ProductDepotTags;
import com.yumu.hexie.model.user.User;

import java.util.List;
import java.util.Map;

public interface GroupMngService {
    //查询团购列表
    List<GroupInfoVo> queryGroupList(User user, QueryGroupReq queryGroupReq);

    //查询团购汇总信息
    GroupSumResp queryGroupSum(User user, String groupId) throws Exception;

    //查询团购订单列表
    List<GroupOrderVo> queryGroupOrder(User user, QueryGroupReq queryGroupReq) throws Exception;

    //订单核销
    Boolean handleVerifyCode(User user, String orderId, String code);

    //取消订单
    Boolean cancelOrder(User user, String orderId) throws Exception;

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

    //根据订单ID查询订单详情
    GroupOrderVo queryGroupOrderDetail(User user, String orderId);

    //订单退款
    Boolean refundOrder(User user, RefundInfoReq refundInfoReq) throws Exception;

    //未提货通知
    Boolean noticeReceiving(User user, String groupId);

    //更新团购状态
    Boolean updateGroupInfo(User user, String groupId, String operType);

    //后台访问，查询商品库列表
    CommonResponse<Object> queryProductDepotListPage(OutSidProductDepotReq outSidProductDepotReq);

    //关联商品库商品关联的团购
    CommonResponse<Object> queryRelateGroup(String depotId);

    //根据商品ID删除商品库商品
    String delDepotById(String depotId);

    //后台查询团购列表
    CommonResponse<Object> queryGroupListPage(OutSidProductDepotReq outSidProductDepotReq);

    //后台操作团
    String operGroupByOutSid(String groupId, String operType);
}
