package com.yumu.hexie.web.wdwechat;

import com.alibaba.fastjson.JSONObject;
import com.yumu.hexie.common.util.RSAUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWd;
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
import org.springframework.http.MediaType;
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
     * @param time
     * @param phone
     * @param sign
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    @ResponseBody
    public BaseResp<TokenResp> getToken(@RequestParam("time") String time,
                                        @RequestParam("phone") String phone,
                                        @RequestParam("sign") String sign) {

        WdCenterReq req = new WdCenterReq();
        req.setTime(time);
        req.setPhone(phone);
        req.setSign(sign);
        log.info("WdCenterReq : " + req);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", time);
        jsonObject.put("phone", phone);
        boolean flag;
        try {
            flag = RSAUtil.verify(jsonObject.toString(), RSAUtil.getPublicKey(ConstantWd.PRIVATE_KEY), sign);
        } catch (Exception e) {
            return BaseResp.fail("验签失败");
        }

        if(!StringUtils.hasText(sign) || !flag) {
            return BaseResp.fail("验签失败");
        }
        if(!StringUtils.hasText(req.getPhone())) {
            return BaseResp.fail("获取token失败,参数不能为空");
        }
        TokenResp resp = wdService.getTokenByPhone(req);
        log.info("TokenResp : " + resp);
        if(resp != null) {
            return BaseResp.success(resp);
        } else {
          return BaseResp.fail("获取token失败");
        }
    }

    /**
     * 通过token获取用户详情信息
     * @param time
     * @param sign
     * @param headers
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public BaseResp<UserInfoResp> getUserInfo(@RequestParam("time") String time,
                                              @RequestParam("sign") String sign,
                                              @RequestHeader HttpHeaders headers) {
        String token = headers.getFirst("Authorization");
        WdCenterReq req = new WdCenterReq();
        req.setTime(time);
        req.setSign(sign);
        log.info("WdCenterReq : " + req);
        log.info("Authorization : " + token);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", time);
        boolean flag;
        try {
            flag = RSAUtil.verify(jsonObject.toString(), RSAUtil.getPublicKey(ConstantWd.PRIVATE_KEY), sign);
        } catch (Exception e) {
            return BaseResp.fail("验签失败");
        }

        if(!StringUtils.hasText(sign) || !flag) {
            return BaseResp.fail("验签失败");
        }

        if(!StringUtils.hasText(token) || !StringUtils.hasText(req.getSign())) {
            return BaseResp.fail("参数不能为空");
        }
        UserInfoResp resp = wdService.getUserInfoByToken(req, token);
        log.info("UserInfoResp :" + resp);
        if(resp != null) {
            return BaseResp.success(resp);
        } else {
            return BaseResp.fail("获取用户信息失败");
        }
    }


}
