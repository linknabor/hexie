package com.yumu.hexie.service.shequ;

import com.yumu.hexie.integration.renovation.req.SaveRenovationReq;
import com.yumu.hexie.integration.renovation.resp.RenovationInfoResp;
import com.yumu.hexie.model.user.User;

import java.util.List;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 12月 星期五
 * @Desc :
 */
public interface RenovationService {

    //查询用户登记列表
    List<RenovationInfoResp> getRenovationList(User user, String sectId) throws Exception;

    //根据ID查询登记详情
    RenovationInfoResp getRenovationInfoById(User user, String registerId) throws Exception;

    //保存登记信息
    Boolean saveRenovation(User user, SaveRenovationReq req) throws Exception;

    //作废登记信息
    Boolean cancelRenovation(User user, String registerId) throws Exception;
}
