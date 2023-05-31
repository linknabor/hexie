package com.yumu.hexie.service.wdwechat.Impl;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.RSAUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWd;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.wdwechat.WdService;
import com.yumu.hexie.service.wdwechat.req.WdCenterReq;
import com.yumu.hexie.service.wdwechat.resp.TokenResp;
import com.yumu.hexie.service.wdwechat.resp.UserInfoResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 5月 星期二
 * @Desc :
 */
@Service("wdService")
public class WdServiceImpl implements WdService {

    private static final Logger log = LoggerFactory.getLogger(WdServiceImpl.class);

    @Inject
    private UserRepository userRepository;

    @Override
    public TokenResp getTokenByPhone(WdCenterReq req) {
        //2.解析手机号,手机号用了公钥加密,这里要解出来
        String phone = req.getPhone();
        try {
            phone = RSAUtil.decrypt(phone, ConstantWd.PRIVATE_KEY);
        } catch (Exception e) {
           return null;
        }

        //3.根据手机号查询用户信息
        List<User> list = userRepository.findByTelAndAppId(phone, ConstantWd.APPID);
        //4.判断用户是否存在
        if(list != null && list.size() > 0) {
            User user = list.get(0);
            //TODO 这里应该有一个动态的token
            String str = user.getWuyeId();
            String token = Base64.getEncoder().encodeToString(str.getBytes());
            TokenResp resp = new TokenResp();
            resp.setToken(token);
            resp.setExpire_time("2099-12-31 23:59:59");
            return resp;
        }
        return null;
    }

    @Override
    public UserInfoResp getUserInfoByToken(WdCenterReq req, String token) {
        if(StringUtils.hasText(token)) {
            token = new String(Base64.getDecoder().decode(token));
        }
        List<User> list = userRepository.findByWuyeIdAndAppId(token, ConstantWd.APPID);
        if(list != null && list.size() > 0) {
            User user = list.get(0);

            UserInfoResp resp = new UserInfoResp();
            String sexCn = "未知";
            if(user.getSex() == 1) {
                sexCn = "男";
            } else if(user.getSex() == 2) {
                sexCn = "女";
            }
            resp.setSex(sexCn);
            resp.setName(user.getName());
            resp.setNickname(user.getNickname());
            resp.setAvatar(user.getHeadimgurl());
            if(user.getRegisterDate() != 0) {
                String registerDate = DateUtil.dttmFormat(new Date(user.getRegisterDate()));
                resp.setCreated_time(registerDate);
            }
            String tel = user.getTel();
            try {
                tel = RSAUtil.encrypt(tel, ConstantWd.PUBLIC_KEY);
            } catch (Exception e) {
                return null;
            }
            resp.setPhone(tel); //TODO 这里要用公钥加密
            return resp;
        }
        return null;
    }
}
