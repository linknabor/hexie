package com.yumu.hexie.service.shequ.impl;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.renovation.RenovationUtil;
import com.yumu.hexie.integration.renovation.req.SaveRenovationReq;
import com.yumu.hexie.integration.renovation.resp.RenovationInfoResp;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.RenovationService;
import com.yumu.hexie.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** 装修登记
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 12月 星期五
 * @Desc :
 */
@Service("renovationService")
public class RenovationServiceImpl implements RenovationService {
    @Autowired
    private RenovationUtil renovationUtil;
    @Autowired
    private UserService userService;

    @Override
    public List<RenovationInfoResp> getRenovationList(User user, String sectId) throws Exception {
        CommonResponse<List<RenovationInfoResp>> commonResponse = renovationUtil.getRenovationList(user, sectId);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public RenovationInfoResp getRenovationInfoById(User user, String registerId) throws Exception {
        CommonResponse<RenovationInfoResp> commonResponse = renovationUtil.getRenovationInfo(user, registerId);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public Boolean saveRenovation(User user, SaveRenovationReq req) throws Exception {
        User userDB = userService.getById(user.getId());
        if(userDB == null) {
            throw new BizValidateException("用户信息不存在，保存失败");
        }
        CommonResponse<Boolean> commonResponse = renovationUtil.saveRenovation(userDB, req);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public Boolean cancelRenovation(User user, String registerId) throws Exception {
        CommonResponse<Boolean> commonResponse = renovationUtil.cancelRenovation(user, registerId);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }
}
