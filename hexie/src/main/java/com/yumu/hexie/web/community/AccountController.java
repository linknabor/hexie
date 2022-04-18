package com.yumu.hexie.web.community;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.community.req.BankVO;
import com.yumu.hexie.integration.community.req.EditOrderReq;
import com.yumu.hexie.integration.community.req.QueryWaterVO;
import com.yumu.hexie.integration.community.req.SurplusVO;
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
     * @param user
     * @param queryWaterVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getWaterList", method = RequestMethod.POST)
    public BaseResult<QueryWaterListResp> getWaterList(@ModelAttribute(Constants.USER) User user, @RequestBody QueryWaterVO queryWaterVO) throws Exception{
        QueryWaterListResp resp = accountService.queryWaterList(user, queryWaterVO);
        return BaseResult.successResult(resp);
    }

    /**
     * 绑定银行卡
     * @param user
     * @param bankVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveBank", method = RequestMethod.POST)
    public BaseResult<Object> saveBank(@ModelAttribute(Constants.USER) User user, @RequestBody BankVO bankVO) throws Exception{
        boolean flag = accountService.saveBank(user, bankVO);
        if (flag) {
            return BaseResult.successResult(null);
        } else {
            return BaseResult.fail("绑定银行卡失败，请刷新重试");
        }
    }

    /**
     * 查询团购列表
     * @param user
     * @param queryName
     * @param groupStatus
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getGroupList", method = RequestMethod.GET)
    public BaseResult<List<GroupInfoListResp>> getGroupList(@ModelAttribute(Constants.USER) User user, @RequestParam String  queryName, @RequestParam String groupStatus) throws Exception{
        List<GroupInfoListResp> list = accountService.queryGroupList(user, queryName, groupStatus);
        return BaseResult.successResult(list);
    }

    /**
     * 更新团购状态
     * @param user
     * @param groupId
     * @param operType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateGroupInfo", method = RequestMethod.GET)
    public BaseResult<Boolean> updateGroupInfo(@ModelAttribute(Constants.USER) User user, @RequestParam String groupId, @RequestParam String operType) throws Exception{
        Boolean flag = accountService.updateGroupInfo(user, groupId, operType);
        return BaseResult.successResult(flag);
    }

    /**
     * 查询团购汇总信息
     * @param user
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/getGroupOrderList", method = RequestMethod.GET)
    public BaseResult<GroupSumResp> getGroupOrderList(@ModelAttribute(Constants.USER) User user, @RequestParam String groupId) {
        GroupSumResp resp = accountService.queryGroupTotal(user, groupId);
        return BaseResult.successResult(resp);
    }

    /**
     * 查询团购订单列表
     * @param user
     * @param groupId
     * @param orderStatus
     * @param searchValue
     * @param type
     * @return
     */
    @RequestMapping(value = "/queryGroupOrder", method = RequestMethod.GET)
    public BaseResult<List<GroupOrderResp>> queryGroupOrder(@ModelAttribute(Constants.USER) User user, @RequestParam String groupId,
                                                      @RequestParam String orderStatus, @RequestParam String searchValue, @RequestParam String type) {
        List<GroupOrderResp> list = accountService.queryGroupOrder(user, groupId, orderStatus, searchValue, type);
        return BaseResult.successResult(list);
    }

    /**
     * 修改团购订单信息
     * @param user
     * @param editOrderReq
     * @return
     */
    @RequestMapping(value = "/editOrder", method = RequestMethod.POST)
    public BaseResult<Boolean> editOrder(@ModelAttribute(Constants.USER) User user, @RequestBody EditOrderReq editOrderReq) {
        Boolean flag = accountService.editOrder(user, editOrderReq);
        return BaseResult.successResult(flag);
    }
}
