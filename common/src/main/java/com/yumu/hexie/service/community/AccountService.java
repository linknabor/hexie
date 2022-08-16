package com.yumu.hexie.service.community;

import com.yumu.hexie.integration.community.req.*;
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

    //获取账户支付流水列表
    QueryWaterListResp queryPayWaterList(User user, QueryWaterVO queryWaterVO) throws Exception;

    //查询银行卡信息
    List<AccountBankResp> queryBankList(User user) throws Exception;

    //绑定银行卡
    boolean saveBank(User user, BankVO bankVO) throws Exception;

    //删除银行卡信息
    boolean delBankInfo(User user, String bankNo)  throws Exception;
}
