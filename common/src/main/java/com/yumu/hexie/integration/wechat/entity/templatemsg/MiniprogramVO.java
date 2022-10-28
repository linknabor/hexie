package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.util.StringJoiner;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-06-08 19:46
 */
public class MiniprogramVO {
    private String appid;
    private String pagepath;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPagepath() {
        return pagepath;
    }

    public void setPagepath(String pagepath) {
        this.pagepath = pagepath;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MiniprogramVO.class.getSimpleName() + "[", "]")
                .add("appid='" + appid + "'")
                .add("pagepath='" + pagepath + "'")
                .toString();
    }
}
