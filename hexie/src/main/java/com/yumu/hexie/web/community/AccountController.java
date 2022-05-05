package com.yumu.hexie.web.community;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.community.req.*;
import com.yumu.hexie.integration.community.resp.*;
import com.yumu.hexie.model.commonsupport.info.ProductDepot;
import com.yumu.hexie.model.commonsupport.info.ProductDepotTags;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.community.AccountService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private WechatCoreService wechatCoreService;

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
     * 查询团购列表
     *
     * @param user
     * @param queryGroupReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getGroupList", method = RequestMethod.GET)
    public BaseResult<List<GroupInfoVo>> getGroupList(@ModelAttribute(Constants.USER) User user, QueryGroupReq queryGroupReq) throws Exception {
        List<GroupInfoVo> list = accountService.queryGroupList(user, queryGroupReq);
        return BaseResult.successResult(list);
    }

    /**
     * 更新团购状态
     *
     * @param user
     * @param groupId
     * @param operType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateGroupInfo", method = RequestMethod.GET)
    public BaseResult<Boolean> updateGroupInfo(@ModelAttribute(Constants.USER) User user, @RequestParam String groupId, @RequestParam String operType) throws Exception {
        Boolean flag = accountService.updateGroupInfo(user, groupId, operType);
        return BaseResult.successResult(flag);
    }

    /**
     * 查询团购汇总信息
     *
     * @param user
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/queryGroupSum", method = RequestMethod.GET)
    public BaseResult<GroupSumResp> queryGroupSum(@ModelAttribute(Constants.USER) User user, @RequestParam String groupId) throws Exception {
        GroupSumResp resp = accountService.queryGroupSum(user, groupId);
        return BaseResult.successResult(resp);
    }

    /**
     * 查询团购订单列表
     *
     * @param user
     * @param queryGroupReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryGroupOrder", method = RequestMethod.GET)
    public BaseResult<List<GroupOrderVo>> queryGroupOrder(@ModelAttribute(Constants.USER) User user, QueryGroupReq queryGroupReq) throws Exception {
        List<GroupOrderVo> list = accountService.queryGroupOrder(user, queryGroupReq);
        return BaseResult.successResult(list);
    }

    /**
     * 根据订单ID查询订单详情
     *
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/queryGroupOrderDetail/{orderId}", method = RequestMethod.GET)
    public BaseResult<GroupOrderVo> queryGroupOrderDetail(@ModelAttribute(Constants.USER) User user, @PathVariable String orderId) {
        GroupOrderVo groupOrderVo = accountService.queryGroupOrderDetail(user, orderId);
        return BaseResult.successResult(groupOrderVo);
    }

    /**
     * 订单核销
     *
     * @param user
     * @param code
     * @return
     */
    @RequestMapping(value = "/verifyCode/{orderId}/{code}", method = RequestMethod.GET)
    public BaseResult<Boolean> verifyCode(@ModelAttribute(Constants.USER) User user, @PathVariable String orderId, @PathVariable String code) {
        Boolean flag = accountService.handleVerifyCode(user, orderId, code);
        return BaseResult.successResult(flag);
    }

    /**
     * 取消订单
     *
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/cancelOrder/{orderId}", method = RequestMethod.GET)
    public BaseResult<Boolean> cancelOrder(@ModelAttribute(Constants.USER) User user, @PathVariable String orderId) throws Exception {
        Boolean flag = accountService.cancelOrder(user, orderId);
        return BaseResult.successResult(flag);
    }

    /**
     * 订单退款
     *
     * @param user
     * @param refundInfoReq
     * @return
     */
    @RequestMapping(value = "/refundOrder", method = RequestMethod.POST)
    public BaseResult<Boolean> refundOrder(@ModelAttribute(Constants.USER) User user, @RequestBody RefundInfoReq refundInfoReq) throws Exception {
        Boolean flag = accountService.refundOrder(user, refundInfoReq);
        return BaseResult.successResult(flag);
    }

    /**
     * 未提货通知
     *
     * @param user
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/noticeReceiving/{groupId}", method = RequestMethod.GET)
    public BaseResult<Boolean> noticeReceiving(@ModelAttribute(Constants.USER) User user, @PathVariable String groupId) {
        Boolean flag = accountService.noticeReceiving(user, groupId);
        return BaseResult.successResult(flag);
    }

    /**
     * 查询团长的商品列表
     * @param user
     * @param searchValue
     * @return
     */
    @RequestMapping(value = "/queryProductDepotList", method = RequestMethod.GET)
    public BaseResult<List<ProductDepot>> queryProductList(@ModelAttribute(Constants.USER) User user, @RequestParam String searchValue, @RequestParam int currentPage) {
        List<ProductDepot> list = accountService.queryProductDepotList(user, searchValue, currentPage);
        return BaseResult.successResult(list);
    }

    /**
     * 删除商品
     * @param user
     * @param productId
     * @return
     */
    @RequestMapping(value = "/delProductDepot/{productId}", method = RequestMethod.GET)
    public BaseResult<Boolean> delProduct(@ModelAttribute(Constants.USER) User user, @PathVariable String productId) {
        Boolean flag = accountService.delProductDepot(user, productId);
        return BaseResult.successResult(flag);
    }

    /**
     * 新增，编辑商品库
     * @param user
     * @param productDepotReq
     * @return
     */
    @RequestMapping(value = "/operProductDepot", method = RequestMethod.POST)
    public BaseResult<Boolean> operProductDepot(@ModelAttribute(Constants.USER) User user, @RequestBody ProductDepotReq productDepotReq) {
        Boolean flag = accountService.operProductDepot(user, productDepotReq);
        return BaseResult.successResult(flag);
    }

    /**
     * 查询商品库详情
     * @param user
     * @param productId
     * @return
     */
    @RequestMapping(value = "/queryProductDepotDetail/{productId}", method = RequestMethod.GET)
    public BaseResult<ProductDepot> queryProductDepotDetail(@ModelAttribute(Constants.USER) User user, @PathVariable String productId) {
        ProductDepot depot = accountService.queryProductDepotDetail(user, productId);
        return BaseResult.successResult(depot);
    }

    /**
     * 查询自定义标签
     * @param user
     * @return
     */
    @RequestMapping(value = "/queryDepotTags", method = RequestMethod.GET)
    public BaseResult<Map<String, List<ProductDepotTags>>> queryDepotTags(@ModelAttribute(Constants.USER) User user) {
        Map<String, List<ProductDepotTags>> map = accountService.queryProductDepotTags(user);
        return BaseResult.successResult(map);
    }

    /**
     * 添加自定义标签
     * @param user
     * @param tagName
     * @return
     */
    @RequestMapping(value = "/saveDepotTag", method = RequestMethod.GET)
    public BaseResult<Boolean> saveDepotTag(@ModelAttribute(Constants.USER) User user, @RequestParam String tagName) {
        Boolean flag = accountService.saveDepotTag(user, tagName);
        return BaseResult.successResult(flag);
    }

    /**
     * 删除自定义标签
     * @param user
     * @param tagId
     * @return
     */
    @RequestMapping(value = "/delDepotTag/{tagId}", method = RequestMethod.GET)
    public BaseResult<Boolean> delDepotTag(@ModelAttribute(Constants.USER) User user, @PathVariable String tagId) {
        Boolean flag = accountService.delDepotTag(user, tagId);
        return BaseResult.successResult(flag);
    }

    @RequestMapping(value = "/getMiniQrCode", method = {RequestMethod.GET}, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public String getMiniQrCode(@ModelAttribute(Constants.USER) User user, @RequestParam() String path, @RequestParam() String param) throws Exception {
        return wechatCoreService.getUnlimitedQrcode(path, param);
    }
}
