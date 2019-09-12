package com.yumu.hexie.common.util;

import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;

public class AppUtil {

	 /**
     * 是否为主公众号的appId
     * @param appId
     * @return
     */
    public static boolean isMainApp(String appId) {
    	
    	boolean flag = false;
    	String mainAppId = ConstantWeChat.APPID;
    	if (mainAppId.equals(appId)) {
			flag = true;
		}
    	return flag;
    }
}
