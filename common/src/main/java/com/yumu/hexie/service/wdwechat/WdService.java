package com.yumu.hexie.service.wdwechat;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.wdwechat.req.WdCenterReq;
import com.yumu.hexie.service.wdwechat.resp.TokenResp;
import com.yumu.hexie.service.wdwechat.resp.UserInfoResp;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 5月 星期二
 * @Desc :
 */
public interface WdService {

    //根据手机号获取token
    TokenResp getTokenByPhone(WdCenterReq req);

    //根据token获取用户详情
    UserInfoResp getUserInfoByToken(WdCenterReq req, String token);

    String replUserTel(WdCenterReq req);

    String syncUserInfo(User user);

    void syncUserTel(User user);
}
