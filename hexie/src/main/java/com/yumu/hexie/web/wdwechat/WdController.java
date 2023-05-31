package com.yumu.hexie.web.wdwechat;

import com.yumu.hexie.service.wdwechat.req.WdCenterReq;
import com.yumu.hexie.service.wdwechat.WdService;
import com.yumu.hexie.service.wdwechat.resp.BaseResp;
import com.yumu.hexie.service.wdwechat.resp.TokenResp;
import com.yumu.hexie.service.wdwechat.resp.UserInfoResp;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 5月 星期二
 * @Desc :
 */
@RestController
@RequestMapping("/gmwy")
public class WdController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(WdController.class);
    @Autowired
    private WdService wdService;
    /**
     * 通过手机号获取用户token
     * @param req
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    @ResponseBody
    public BaseResp<TokenResp> getToken(@RequestBody(required = false) WdCenterReq req) {
        log.info("WdCenterReq : " + req);
        if(req == null || !StringUtils.hasText(req.getPhone())) {
            return BaseResp.fail("获取token失败,参数不能为空");
        }
        TokenResp resp = wdService.getTokenByPhone(req);
        if(resp != null) {
            return BaseResp.success(resp);
        } else {
          return BaseResp.fail("获取token失败");
        }
    }

    /**
     * 通过token获取用户详情信息
     * @param req
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public BaseResp<UserInfoResp> getUserInfo(@RequestBody(required = false) WdCenterReq req, @RequestHeader HttpHeaders headers) {
        String token = headers.getFirst("Authorization");
        log.info("WdCenterReq : " + req);
        log.info("Authorization : " + token);
        if(!StringUtils.hasText(token) || req == null || !StringUtils.hasText(req.getSign())) {
            return BaseResp.fail("参数不能为空");
        }
        UserInfoResp resp = wdService.getUserInfoByToken(req, token);
        if(resp != null) {
            return BaseResp.success(resp);
        } else {
            return BaseResp.fail("获取用户信息失败");
        }
    }


}
