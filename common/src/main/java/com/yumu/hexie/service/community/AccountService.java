package com.yumu.hexie.service.community;

import com.yumu.hexie.integration.community.req.BankVO;
import com.yumu.hexie.integration.community.req.EditOrderReq;
import com.yumu.hexie.integration.community.req.QueryWaterVO;
import com.yumu.hexie.integration.community.req.SurplusVO;
import com.yumu.hexie.integration.community.resp.*;
import com.yumu.hexie.model.user.User;

import java.util.List;

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
    List<GroupInfoListResp> queryGroupList(User user, String queryName, String groupStatus);

    //更新团购状态
    Boolean updateGroupInfo(User user, String groupId, String operType);

    //查询团购汇总信息
    GroupSumResp queryGroupTotal(User user, String groupId);

    //查询团购订单列表
    List<GroupOrderResp> queryGroupOrder(User user, String groupId, String orderStatus, String searchValue, String type);

    //修改订单信息
    Boolean editOrder(User user, EditOrderReq editOrderReq);
}
