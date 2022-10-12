package com.yumu.hexie.web.community;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.community.req.*;
import com.yumu.hexie.integration.community.resp.*;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.community.AccountService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-08 15:42
 */
@RestController
@RequestMapping(value = "/community")
public class AccountController extends BaseController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/getAccount", method = RequestMethod.POST)
    public BaseResult<AccountInfoVO> getOrderList(@ModelAttribute(Constants.USER) User user) throws Exception {
        AccountInfoVO accountInfoVo = accountService.getAccountInfo(user);
        return BaseResult.successResult(accountInfoVo);
    }

    /**
     * 获取账户可用余额和银行卡信息
     *
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getSurplusMoney", method = RequestMethod.POST)
    public BaseResult<AccountSurplusVO> getSurplusMoney(@ModelAttribute(Constants.USER) User user) throws Exception {
        AccountSurplusVO accountSurplusVO = accountService.getSurplusAndBank(user);
        return BaseResult.successResult(accountSurplusVO);
    }

    /**
     * 提现申请
     *
     * @param user
     * @param surplusVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/applySurplus", method = RequestMethod.POST)
    public BaseResult<Object> applySurplus(@ModelAttribute(Constants.USER) User user, @RequestBody SurplusVO surplusVO) throws Exception {
        boolean flag = accountService.applySurplu(user, surplusVO);
        if (flag) {
            return BaseResult.successResult(null);
        } else {
            return BaseResult.fail("提交提现申请失败，请刷新重试");
        }
    }

    /**
     * 获取账户流水列表
     *
     * @param user
     * @param queryWaterVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getWaterList", method = RequestMethod.POST)
    public BaseResult<QueryWaterListResp> getWaterList(@ModelAttribute(Constants.USER) User user, @RequestBody QueryWaterVO queryWaterVO) throws Exception {
        QueryWaterListResp resp = accountService.queryWaterList(user, queryWaterVO);
        return BaseResult.successResult(resp);
    }

    /**
     * 获取支付流水列表
     *
     * @param user
     * @param queryWaterVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getPayWaterList", method = RequestMethod.POST)
    public BaseResult<QueryWaterListResp> getPayWaterList(@ModelAttribute(Constants.USER) User user, @RequestBody QueryWaterVO queryWaterVO) throws Exception {
        QueryWaterListResp resp = accountService.queryPayWaterList(user, queryWaterVO);
        return BaseResult.successResult(resp);
    }

    /**
     * 查询银行卡信息
     * @param user
     * @return
     */
    @RequestMapping(value = "/getBankList", method = RequestMethod.GET)
    public BaseResult<List<AccountBankResp>> getBankList(@ModelAttribute(Constants.USER) User user) throws Exception {
        List<AccountBankResp> list = accountService.queryBankList(user);
        return BaseResult.successResult(list);
    }
    /**
     * 绑定银行卡
     *
     * @param user
     * @param bankVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveBank", method = RequestMethod.POST)
    public BaseResult<Object> saveBank(@ModelAttribute(Constants.USER) User user, @RequestBody BankVO bankVO) throws Exception {
        boolean flag = accountService.saveBank(user, bankVO);
        if (flag) {
            return BaseResult.successResult(null);
        } else {
            return BaseResult.fail("绑定银行卡失败，请刷新重试");
        }
    }

    /**
     * 删除银行卡信息
     * @param user
     * @param bankNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delBankInfo/{bankNo}", method = RequestMethod.POST)
    public BaseResult<Object> delBankInfo(@ModelAttribute(Constants.USER) User user, @PathVariable String bankNo) throws Exception {
        boolean flag = accountService.delBankInfo(user, bankNo);
        if (flag) {
            return BaseResult.successResult(true);
        } else {
            return BaseResult.fail("删除银行卡失败，请刷新重试");
        }
    }


}
