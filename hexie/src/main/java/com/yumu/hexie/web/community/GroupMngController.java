package com.yumu.hexie.web.community;

import com.yumu.hexie.common.Constants;
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
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.community.GroupMngService;
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
 * @create 2022-05-05 20:59
 */
@RestController
@RequestMapping(value = "/community")
public class GroupMngController extends BaseController {

    @Autowired
    private GroupMngService groupMngService;
    @Autowired
    private WechatCoreService wechatCoreService;

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
        List<GroupInfoVo> list = groupMngService.queryGroupList(user, queryGroupReq);
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
        Boolean flag = groupMngService.updateGroupInfo(user, groupId, operType);
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
        GroupSumResp resp = groupMngService.queryGroupSum(user, groupId);
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
        List<GroupOrderVo> list = groupMngService.queryGroupOrder(user, queryGroupReq);
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
        GroupOrderVo groupOrderVo = groupMngService.queryGroupOrderDetail(user, orderId);
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
        Boolean flag = groupMngService.handleVerifyCode(user, orderId, code);
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
        Boolean flag = groupMngService.cancelOrder(user, orderId);
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
        Boolean flag = groupMngService.refundOrder(user, refundInfoReq);
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
        Boolean flag = groupMngService.noticeReceiving(user, groupId);
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
        List<ProductDepot> list = groupMngService.queryProductDepotList(user, searchValue, currentPage);
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
        Boolean flag = groupMngService.delProductDepot(user, productId);
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
        Boolean flag = groupMngService.operProductDepot(user, productDepotReq);
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
        ProductDepot depot = groupMngService.queryProductDepotDetail(user, productId);
        return BaseResult.successResult(depot);
    }

    /**
     * 查询自定义标签
     * @param user
     * @return
     */
    @RequestMapping(value = "/queryDepotTags", method = RequestMethod.GET)
    public BaseResult<Map<String, List<ProductDepotTags>>> queryDepotTags(@ModelAttribute(Constants.USER) User user) {
        Map<String, List<ProductDepotTags>> map = groupMngService.queryProductDepotTags(user);
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
        Boolean flag = groupMngService.saveDepotTag(user, tagName);
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
        Boolean flag = groupMngService.delDepotTag(user, tagId);
        return BaseResult.successResult(flag);
    }

    /**
     * 获取小程序二维码
     * @param user
     * @param path
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getMiniQrCode", method = {RequestMethod.GET}, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public String getMiniQrCode(@ModelAttribute(Constants.USER) User user, @RequestParam() String path, @RequestParam() String param) throws Exception {
        return wechatCoreService.getUnlimitedQrcode(path, param);
    }

    /**
     * 后台查询商品库
     * @param outSidProductDepotReq
     * @return
     */
    @RequestMapping(value = "/outside/getProductDepotList", method = RequestMethod.POST)
    public CommonResponse<Object> getProduct(@RequestBody OutSidProductDepotReq outSidProductDepotReq) {
        return groupMngService.queryProductDepotListPage(outSidProductDepotReq);
    }

    /**
     * 后台查询商品库商品关联的团购
     * @param map
     * @return
     */
    @RequestMapping(value = "/outside/getRelateGroup", method = RequestMethod.POST)
    public CommonResponse<Object> getRelateGroupa(@RequestBody Map<String, String> map) {
        return groupMngService.queryRelateGroup(map.get("depotId"));
    }

    @RequestMapping(value = "/outside/delDepot", method = RequestMethod.POST)
    public CommonResponse<String> delDepot(@RequestBody Map<String, String> map) {
        String str = groupMngService.delDepotById(map.get("depotId"));
        CommonResponse<String> commonResponse = new CommonResponse<>();
        commonResponse.setResult("00");
        commonResponse.setData(str);
        return commonResponse;
    }

    /**
     * 后台查询团购列表
     * @param outSidProductDepotReq
     * @return
     */
    @RequestMapping(value = "/outside/groupList", method = RequestMethod.POST)
    public CommonResponse<Object> groupList(@RequestBody OutSidProductDepotReq outSidProductDepotReq) {
        return groupMngService.queryGroupListPage(outSidProductDepotReq);
    }

    /**
     * 后台，操作团购
     * @param map
     * @return
     */
    @RequestMapping(value = "/outside/operGroup", method = RequestMethod.POST)
    public CommonResponse<String> operGroup(@RequestBody Map<String, String> map) {
        String str =  groupMngService.operGroupByOutSid(map.get("groupId"), map.get("operType"));
        CommonResponse<String> commonResponse = new CommonResponse<>();
        commonResponse.setResult("00");
        commonResponse.setData(str);
        return commonResponse;
    }
}
