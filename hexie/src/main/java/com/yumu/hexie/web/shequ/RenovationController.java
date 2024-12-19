package com.yumu.hexie.web.shequ;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.renovation.req.SaveRenovationReq;
import com.yumu.hexie.integration.renovation.resp.RenovationInfoResp;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.RenovationService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Package : 西部装修登记
 * @Author :
 * @Date : 2024 12月 星期四
 * @Desc :
 */
@RestController
@RequestMapping("/renovation")
public class RenovationController extends BaseController {

    @Autowired
    private RenovationService renovationService;

    /**
     * 查询用户登记信息
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public BaseResult<List<RenovationInfoResp>> getRenovationList(@ModelAttribute(Constants.USER) User user, @RequestParam() String sectId) throws Exception {
        List<RenovationInfoResp> list = renovationService.getRenovationList(user, sectId);
        return BaseResult.successResult(list);
    }

    /**
     * 根据ID查询登记信息
     * @param user
     * @param registerId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public BaseResult<RenovationInfoResp> getRenovationInfoById(@ModelAttribute(Constants.USER) User user, @RequestParam() String registerId) throws Exception {
        RenovationInfoResp resp = renovationService.getRenovationInfoById(user, registerId);
        return BaseResult.successResult(resp);
    }

    /**
     * 保存用户登记信息
     * @param user
     * @param req
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public BaseResult<Boolean> saveRenovation(@ModelAttribute(Constants.USER) User user, @RequestBody SaveRenovationReq req) throws Exception {
        Boolean flag = renovationService.saveRenovation(user, req);
        return BaseResult.successResult(flag);
    }

    /**
     * 登记作废
     * @param user
     * @param registerId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public BaseResult<Boolean> getIndexCar(@ModelAttribute(Constants.USER) User user, @RequestParam() String registerId) throws Exception {
        Boolean flag = renovationService.cancelRenovation(user, registerId);
        return BaseResult.successResult(flag);
    }
}
