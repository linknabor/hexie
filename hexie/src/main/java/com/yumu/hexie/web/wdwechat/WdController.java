package com.yumu.hexie.web.wdwechat;

import com.alibaba.fastjson.JSONObject;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.RSAUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWd;
import com.yumu.hexie.service.wdwechat.req.WdCenterReq;
import com.yumu.hexie.service.wdwechat.WdService;
import com.yumu.hexie.service.wdwechat.resp.BaseResp;
import com.yumu.hexie.service.wdwechat.resp.TokenResp;
import com.yumu.hexie.service.wdwechat.resp.UserInfoResp;
import com.yumu.hexie.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.TreeMap;

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
            flag = RSAUtil.verify(jsonObject.toString(), RSAUtil.getPublicKey(ConstantWd.PUBLIC_KEY), sign);
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
        log.info("Authorization : " + token);

        WdCenterReq req = new WdCenterReq();
        req.setTime(time);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", time);
        boolean flag;
        try {
            flag = RSAUtil.verify(jsonObject.toString(), RSAUtil.getPublicKey(ConstantWd.PUBLIC_KEY), sign);
        } catch (Exception e) {
            return BaseResp.fail("验签失败");
        }

        req.setSign(sign);
        log.info("WdCenterReq : " + req);

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

    /**
     * 接收用户新手机号通知
     * @param time
     * @param sign
     * @param uniqueCode
     * @param newPhone
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/notifyUserTel", method = RequestMethod.POST)
    @ResponseBody
    public BaseResp<Object> notifyUserTel(@RequestParam("time") String time,
                              @RequestParam("sign") String sign,
                              @RequestParam("uniqueCode") String uniqueCode,
                              @RequestParam("newPhone") String newPhone,
                              @RequestParam("phone") String phone) {
        WdCenterReq req = new WdCenterReq();
        req.setTime(time);
        req.setPhone(newPhone);
        req.setUniqueCode(uniqueCode);
        boolean flag;
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("time", time);
            map.put("newPhone", newPhone);
            map.put("uniqueCode", uniqueCode);
            map.put("phone", phone);
            String str = JacksonJsonUtil.beanToJson(map);
            log.info("notifyUserTel :" + str);

            flag = RSAUtil.verify(str, RSAUtil.getPublicKey(ConstantWd.PUBLIC_KEY), sign);
        } catch (Exception e) {
            return BaseResp.fail("验签失败");
        }
        req.setSign(sign);
        log.info("notifyUserTel body :" + req);

        if(!StringUtils.hasText(sign) || !flag) {
            return BaseResp.fail("验签失败");
        }
        String resp = wdService.replUserTel(req);
        if("SUCCESS".equals(resp)) {
            return BaseResp.success(resp);
        } else {
            return BaseResp.fail(resp);
        }
    }
}
