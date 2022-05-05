package com.yumu.hexie.service.community.impl;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.community.CommunityUtil;
import com.yumu.hexie.integration.community.req.*;
import com.yumu.hexie.integration.community.resp.*;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.community.AccountService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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




}
