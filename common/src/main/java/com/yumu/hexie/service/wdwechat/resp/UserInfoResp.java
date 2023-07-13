package com.yumu.hexie.service.wdwechat.resp;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 5月 星期二
 * @Desc :
 */
public class UserInfoResp {
    private String sex = ""; //性别
    private String name = ""; //真实姓名
    private String nickname = ""; //昵称
    private String avatar = ""; //头像
    private String birthday = ""; //生日
    private String last_login_time = ""; //最后登录时间
    private String created_time = ""; //注册时间
    private String register_ip = ""; //注册ip
    private String last_login_ip = ""; //最后登录ip
    private String entry = ""; //注册入口
    private String phone = ""; //手机号-公钥加密
    private String id_card = ""; //身份证号-公钥加密
    private List<Platform> platform = new ArrayList<>(); //

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getRegister_ip() {
        return register_ip;
    }

    public void setRegister_ip(String register_ip) {
        this.register_ip = register_ip;
    }

    public String getLast_login_ip() {
        return last_login_ip;
    }

    public void setLast_login_ip(String last_login_ip) {
        this.last_login_ip = last_login_ip;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public List<Platform> getPlatform() {
        return platform;
    }

    public void setPlatform(List<Platform> platform) {
        this.platform = platform;
    }

    public static class Platform {
        private String type; //平台类型 wechat weibo douyin
        private String platform; //对应的开放平台
        private String socialite_code; //对应的小程序或者公众号服务号的编码，可以是名称拼音
        private String socialite_name; //对应的小程序或者公众号服务号的名称
        private String socialite_type; //对应的小程序或者公众号服务号的类型，mini service
        private String openid; //openid  公钥加密
        private String unionid; //union 公钥加密

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getSocialite_code() {
            return socialite_code;
        }

        public void setSocialite_code(String socialite_code) {
            this.socialite_code = socialite_code;
        }

        public String getSocialite_name() {
            return socialite_name;
        }

        public void setSocialite_name(String socialite_name) {
            this.socialite_name = socialite_name;
        }

        public String getSocialite_type() {
            return socialite_type;
        }

        public void setSocialite_type(String socialite_type) {
            this.socialite_type = socialite_type;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }

    @Override
    public String toString() {
        return "UserInfoResp{" +
                "sex='" + sex + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", birthday='" + birthday + '\'' +
                ", last_login_time='" + last_login_time + '\'' +
                ", created_time='" + created_time + '\'' +
                ", register_ip='" + register_ip + '\'' +
                ", last_login_ip='" + last_login_ip + '\'' +
                ", entry='" + entry + '\'' +
                ", phone='" + phone + '\'' +
                ", id_card='" + id_card + '\'' +
                ", platform=" + platform +
                '}';
    }
}
