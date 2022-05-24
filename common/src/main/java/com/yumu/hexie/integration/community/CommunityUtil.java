package com.yumu.hexie.integration.community;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.community.req.BankVO;
import com.yumu.hexie.integration.community.req.QueryWaterVO;
import com.yumu.hexie.integration.community.req.SurplusVO;
import com.yumu.hexie.integration.community.resp.AccountBankResp;
import com.yumu.hexie.integration.community.resp.AccountInfoVO;
import com.yumu.hexie.integration.community.resp.AccountSurplusVO;
import com.yumu.hexie.integration.community.resp.QueryWaterListResp;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-08 16:02
 */
@Service
public class CommunityUtil {
    @Autowired
    private RestUtil restUtil;
    @Autowired
    private RequestUtil requestUtil;

    private static final String QUERY_ACCOUNT_INFO_URL = "group/getAccountInfoSDO.do";    //获取账户信息
    private static final String QUERY_ACCOUNT_SURPLUS_URL = "group/getAccountSurplusSDO.do";    //获取账户余额
    private static final String APPLY_SURPLUS_URL = "group/applySurplusSDO.do";    //提现申请
    private static final String QUERY_WATER_LIST_URL = "group/getAccountWaterListSDO.do";    //获取账户流水列表
    private static final String QUERY_PAY_WATER_LIST_URL = "group/getGroupPayWaterListSDO.do";    //获取支付流水列表
    private static final String QUERY_BANK_LIST_URL = "group/queryBankListSDO.do";    //获取银行卡列表
    private static final String SAVE_BANK_URL = "group/saveBankSDO.do";    //绑定银行卡
    private static final String DEL_BANK_URL = "group/delBankSDO.do";    //删除银行卡

    /**
     * 获取账户信息
     *
     * @param user
     * @param orgOperator
     * @return
     * @throws Exception
     */
    public CommonResponse<AccountInfoVO> queryAccountInfo(User user, OrgOperator orgOperator) throws Exception {

        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += QUERY_ACCOUNT_INFO_URL;

        Map<String, String> map = new HashMap<>();
        map.put("orgId", orgOperator.getOrgId());
        map.put("roleId", user.getRoleId());
        map.put("orgOperId", orgOperator.getOrgOperId());

        TypeReference<CommonResponse<AccountInfoVO>> typeReference = new TypeReference<CommonResponse<AccountInfoVO>>() {
        };
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);

    }

    /**
     * 获取账户信息
     *
     * @param user
     * @param orgOperator
     * @return
     * @throws Exception
     */
    public CommonResponse<AccountSurplusVO> querySurplus(User user, OrgOperator orgOperator) throws Exception {

        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += QUERY_ACCOUNT_SURPLUS_URL;

        Map<String, String> map = new HashMap<>();
        map.put("orgId", orgOperator.getOrgId());
        map.put("roleId", user.getRoleId());
        map.put("orgOperId", orgOperator.getOrgOperId());

        TypeReference<CommonResponse<AccountSurplusVO>> typeReference = new TypeReference<CommonResponse<AccountSurplusVO>>() {
        };
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);

    }

    /**
     * 提现申请
     *
     * @param user
     * @param orgOperator
     * @param surplusVO
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> applySurplus(User user, OrgOperator orgOperator, SurplusVO surplusVO) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += APPLY_SURPLUS_URL;
        surplusVO.setOrgId(orgOperator.getOrgId());
        surplusVO.setRoleId(user.getRoleId());
        surplusVO.setOrgOperId(orgOperator.getOrgOperId());

        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>() {
        };
        return restUtil.exchangeOnUri(requestUrl, surplusVO, typeReference);

    }

    /**
     * 获取账户流水列表
     *
     * @param user
     * @param orgOperator
     * @param queryWaterVO
     * @return
     * @throws Exception
     */
    public CommonResponse<QueryWaterListResp> queryWaterList(User user, OrgOperator orgOperator, QueryWaterVO queryWaterVO) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += QUERY_WATER_LIST_URL;
        queryWaterVO.setOrgId(orgOperator.getOrgId());
        queryWaterVO.setRoleId(user.getRoleId());
        queryWaterVO.setOrgOperId(orgOperator.getOrgOperId());

        if (!StringUtils.isEmpty(queryWaterVO.getQueryDate())) {
            String beginDate = DateUtil.dtFormat(Long.parseLong(queryWaterVO.getQueryDate()), DateUtil.dSimple);
            queryWaterVO.setQueryDate(beginDate);
        }

        TypeReference<CommonResponse<QueryWaterListResp>> typeReference = new TypeReference<CommonResponse<QueryWaterListResp>>() {
        };
        return restUtil.exchangeOnUri(requestUrl, queryWaterVO, typeReference);
    }

    /**
     * 获取账户流水列表
     *
     * @param user
     * @param orgOperator
     * @param queryWaterVO
     * @return
     * @throws Exception
     */
    public CommonResponse<QueryWaterListResp> queryPayWaterList(User user, OrgOperator orgOperator, QueryWaterVO queryWaterVO) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += QUERY_PAY_WATER_LIST_URL;
        queryWaterVO.setOrgId(orgOperator.getOrgId());
        queryWaterVO.setRoleId(user.getRoleId());
        queryWaterVO.setOrgOperId(orgOperator.getOrgOperId());

        if (!StringUtils.isEmpty(queryWaterVO.getQueryDate())) {
            String beginDate = DateUtil.dtFormat(Long.parseLong(queryWaterVO.getQueryDate()), DateUtil.dSimple);
            queryWaterVO.setQueryDate(beginDate);
        }

        TypeReference<CommonResponse<QueryWaterListResp>> typeReference = new TypeReference<CommonResponse<QueryWaterListResp>>() {
        };
        return restUtil.exchangeOnUri(requestUrl, queryWaterVO, typeReference);
    }

    /**
     * 查询银行卡列表
     * @param user
     * @param orgOperator
     * @return
     * @throws Exception
     */
    public CommonResponse<List<AccountBankResp>> getBankList(User user, OrgOperator orgOperator) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += QUERY_BANK_LIST_URL;
        Map<String, String> map = new HashMap<>();
        map.put("orgOperId", orgOperator.getOrgOperId());

        TypeReference<CommonResponse<List<AccountBankResp>>> typeReference = new TypeReference<CommonResponse<List<AccountBankResp>>>() {
        };
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 绑定银行卡
     *
     * @param user
     * @param orgOperator
     * @param bankVO
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> saveBank(User user, OrgOperator orgOperator, BankVO bankVO) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += SAVE_BANK_URL;
        bankVO.setOrgId(orgOperator.getOrgId());
        bankVO.setRoleId(user.getRoleId());
        bankVO.setOrgOperId(orgOperator.getOrgOperId());

        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>() {
        };
        return restUtil.exchangeOnUri(requestUrl, bankVO, typeReference);

    }

    /**
     * 删除银行卡信息
     * @param user
     * @param orgOperator
     * @param bankNo
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> delBank(User user, OrgOperator orgOperator, String bankNo) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += DEL_BANK_URL;
        Map<String, String> map = new HashMap<>();
        map.put("orgOperId", orgOperator.getOrgOperId());
        map.put("bankNo", bankNo);
        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>() {
        };
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);

    }

}
