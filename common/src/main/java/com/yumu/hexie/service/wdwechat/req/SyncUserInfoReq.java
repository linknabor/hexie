package com.yumu.hexie.service.wdwechat.req;

import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 6月 星期五
 * @Desc :
 */
public class SyncUserInfoReq {
    private String time = "";
    private String appid = "";
    private String sex = "";
    private String name = "";
    private String nickname = "";
    private String avatar = "";
    private String birthday = "";
    private String last_login_time = "";
    private String created_time = "";
    private String register_ip = "";
    private String last_login_ip = "";
    private String entry = "";
    private String phone = "";
    private String id_card = "";
    private String platform = "";

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        if(StringUtils.hasText(sex)) {
            try {
                sex = URLEncoder.encode(sex, "UTF-8");
            } catch (Exception ignored) {
            }
        }
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(StringUtils.hasText(name)) {
            try {
                name = URLEncoder.encode(name, "UTF-8");
            } catch (Exception ignored) {
            }
        }
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if(StringUtils.hasText(nickname)) {
            try {
                nickname = URLEncoder.encode(nickname, "UTF-8");
            } catch (Exception ignored) {
            }
        }
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if(StringUtils.hasText(avatar)) {
            try {
                avatar = URLEncoder.encode(avatar, "UTF-8");
            } catch (Exception ignored) {
            }
        }
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        if(StringUtils.hasText(birthday)) {
            try {
                birthday = URLEncoder.encode(birthday, "UTF-8");
            } catch (Exception ignored) {
            }
        }
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
        if(StringUtils.hasText(phone)) {
            try {
                phone = URLEncoder.encode(phone, "UTF-8");
            } catch (Exception ignored) {
            }
        }
        this.phone = phone;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "SyncUserInfoReq{" +
                "time='" + time + '\'' +
                ", appid='" + appid + '\'' +
                ", sex='" + sex + '\'' +
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
                ", platform='" + platform + '\'' +
                '}';
    }
}
