package com.yumu.hexie.service.wdwechat.Impl;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.RSAUtil;
import com.yumu.hexie.integration.wdwechat.WdWechatUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWd;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.wdwechat.WdService;
import com.yumu.hexie.service.wdwechat.req.SyncUserInfoReq;
import com.yumu.hexie.service.wdwechat.req.WdCenterReq;
import com.yumu.hexie.service.wdwechat.resp.BaseResp;
import com.yumu.hexie.service.wdwechat.resp.TokenResp;
import com.yumu.hexie.service.wdwechat.resp.UserInfoResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    @Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WdWechatUtil wdWechatUtil;

    @Override
    public TokenResp getTokenByPhone(WdCenterReq req) {
        //2.解析手机号,手机号用了公钥加密,这里要解出来
        String phone = req.getPhone();
        try {
            URLDecoder.decode(phone, "UTF-8");
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
        } else {
            //TODO 这里是否需要自动注册
            //先放入redis暂存？
            String token = UUID.randomUUID().toString();
            String key = "register:" + ConstantWd.APPID + ":" + token;
            redisTemplate.opsForValue().set(key, phone, 1, TimeUnit.HOURS);
            TokenResp resp = new TokenResp();
            resp.setToken(token);

            //暂时有效期1小时
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            String str = DateUtil.dttmFormat(calendar.getTime());
            resp.setExpire_time(str);
            return resp;
        }
    }

    @Override
    public UserInfoResp getUserInfoByToken(WdCenterReq req, String token) {
        if(StringUtils.hasText(token)) {
            try {
                token = new String(Base64.getDecoder().decode(token));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
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

    @Override
    public String replUserTel(WdCenterReq req) {
        if(!StringUtils.hasText(req.getPhone())) {
            return "手机号为空";
        }
        String phone = req.getPhone();
        try {
            phone = RSAUtil.decrypt(phone, ConstantWd.PRIVATE_KEY);
        } catch (Exception e) {
            return "解析手机号失败";
        }

        //更新用户手机号
        User user = userRepository.findByAppIdAndUniqueCode(ConstantWd.APPID, req.getUniqueCode());
        if(user != null) {
            user.setTel(phone);
            userRepository.save(user);
        }
        return "SUCCESS";
    }

    @Override
    public String syncUserInfo(User user) {
        User userDB = userRepository.findById(user.getId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        SyncUserInfoReq req = new SyncUserInfoReq();
        req.setAppid(ConstantWd.TOKEN_APPID);
        req.setTime(String.valueOf(calendar.getTime().getTime()));
        String sexCn = "未知";
        if(userDB.getSex() == 1) {
            sexCn = "男";
        } else if(userDB.getSex() == 2) {
            sexCn = "女";
        }
        req.setSex(sexCn);
        req.setName(userDB.getName());
        req.setNickname(userDB.getNickname());
        req.setAvatar(userDB.getHeadimgurl());
        String tel = userDB.getTel();
        try {
            tel = RSAUtil.encrypt(tel, ConstantWd.PUBLIC_KEY);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
        req.setPhone(tel);
        try {
            req.setPlatform(JacksonJsonUtil.beanToJson(new ArrayList<>()));
        } catch (Exception ignored) {

        }
        String sign;
        try {
            String str = JacksonJsonUtil.beanToJson(req);
            Map<String, Object> map = JacksonJsonUtil.json2map(str);
            Map<String, Object> m = new TreeMap<>(map);
            str = JacksonJsonUtil.beanToJson(m);

            sign = RSAUtil.signByPrivate(str, ConstantWd.PRIVATE_KEY, "UTF-8");
            m.put("sign", sign);
            BaseResp<WdCenterReq> resp = wdWechatUtil.sycnWdUserInfo(m);
            if("1".equals(resp.getCode())) {
                WdCenterReq r = resp.getData();
                if(StringUtils.hasText(r.getUniqueCode())) {
                    userDB.setUniqueCode(r.getUniqueCode());
                    userRepository.save(userDB);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
        return null;
    }

    @Override
    public void syncUserTel(User user) {
        User userDB = userRepository.findById(user.getId());
        Map<String, Object> map = new TreeMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String tel = userDB.getTel();
        try {
            tel = RSAUtil.encrypt(tel, ConstantWd.PUBLIC_KEY);
            if(StringUtils.hasText(tel)) {
                try {
                    tel = URLEncoder.encode(tel, "UTF-8");
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            log.error("syncUserTel tel error：", e);
            return;
        }
        if(StringUtils.isEmpty(userDB.getUniqueCode())) {
            log.error("user id:" + user.getId() + " syncUserTel UniqueCode is empty");
            return;
        }
        try {
            map.put("appid", ConstantWd.TOKEN_APPID);
            map.put("time", String.valueOf(calendar.getTime().getTime()));
            map.put("newPhone", tel);
            map.put("uniqueCode", userDB.getUniqueCode());
            String str = JacksonJsonUtil.beanToJson(map);
            String sign = RSAUtil.signByPrivate(str, ConstantWd.PRIVATE_KEY, "UTF-8");
            map.put("sign", sign);
            BaseResp<Object> resp = wdWechatUtil.sycnWdUserTel(map);
            if("1".equals(resp.getCode())) {
                Object r = resp.getData();
                if(r != null) {
                    log.info("syncUserTel resp:" + r);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
