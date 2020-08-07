package com.yumu.hexie.integration.wechat.service;

import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wechat.entity.menu.Menu;
import com.yumu.hexie.integration.wechat.util.WeixinUtil;

/**
 * 菜单创建
 */
public class MenuService {

	/**
	 * 菜单创建（POST） 限100（次/天）
	 */
	public static String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

	/**
	 * 菜单查询
	 */
	public static String MENU_GET = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	
	/**
	 * 菜单删除
	 */
	public static String MENU_DELETE = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

	/**
	 * 创建菜单
	 * 
	 * @param jsonMenu
	 *            json格式
	 * @return 状态 0 表示成功、其他表示失败
	 */
	public static Integer createMenu(String jsonMenu, String accessToken) {
		WechatResponse jsonObject = WeixinUtil.httpsRequest(MENU_CREATE, "POST", jsonMenu, accessToken);
		if(null != jsonObject)
			return jsonObject.getErrcode();
		return 1;
	}

	/**
	 * 创建菜单
	 * 
	 * @param menu
	 *            菜单实例
	 * @return 0表示成功，其他值表示失败
	 */
	public static Integer createMenu(Menu menu, String accessToken) {
		try {
			return createMenu(JacksonJsonUtil.beanToJson(menu), accessToken);//JSONObject.valueToString(menu));
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}


	/**
	 * 查询菜单
	 * 
	 * @return 菜单结构json字符串
	 */
	public static WechatResponse getMenuJson(String accessToken) {
		return WeixinUtil.httpsRequest(MENU_GET, "GET", null, accessToken);
	}

	/**
	 * 删除菜单
	 * 
	 * @return 菜单结构json字符串
	 */
	public static WechatResponse deleteMenuJson(String accessToken) {
		return WeixinUtil.httpsRequest(MENU_DELETE, "GET", null, accessToken);
	}
	
	/**
	 * 查询菜单
	 * @return Menu 菜单对象
	 */
	public static Menu getMenu(String accessToken) {
		Menu menu =  getMenuJson(accessToken).getMenu();
		return menu;
	}

	

}
