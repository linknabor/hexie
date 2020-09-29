package com.yumu.hexie.integration.wechat.constant;

/**
 * 微信常量
 */
public class ConstantWeChat {
	
	public static String APPID;
	
	public static String APPSECRET;
	
	public static String MERCHANT_ID;
	
	public static String MERCHANT_KEY;
	
	public static String COMPONENT_APPID;
	
	public static String KEYSTORE;
	
	public static String UNIFIEDURL;
	
	public static String NOTIFYURL;
	
	/**
	 * 
	 */
	public static final String TRADETYPE = "JSAPI";
	
	/**
	 * 返回消息类型：文本
	 */
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 返回消息类型：图片
	 */
	public static final String RESP_MESSAGE_TYPE_IMAGE = "image";
	
	/**
	 * 返回消息类型：语音
	 */
	public static final String RESP_MESSAGE_TYPE_VOICE = "voice";
	
	/**
	 * 返回消息类型：视频
	 */
	public static final String RESP_MESSAGE_TYPE_VIDEO = "video";
	
	/**
	 * 返回消息类型：音乐
	 */
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

	/**
	 * 返回消息类型：图文
	 */
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";
	
	/**
	 * 请求消息类型：文本
	 */
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 请求消息类型：图片
	 */
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

	/**
	 * 请求消息类型：链接
	 */
	public static final String REQ_MESSAGE_TYPE_LINK = "link";

	/**
	 * 请求消息类型：地理位置
	 */
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

	/**
	 * 请求消息类型：音频
	 */
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

	/**
	 * 请求消息类型：事件
	 */
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";

	/**
	 * 事件类型：subscribe(关注)
	 */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	/**
	 * 事件类型：unsubscribe(取消关注)
	 */
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

	/**
	 * 事件类型：CLICK(自定义菜单点击事件)
	 */
	public static final String EVENT_TYPE_CLICK = "CLICK";
	/**
	 * 事件类型：VIEW(进入页面)
	 */
	public static final String EVENT_TYPE_VIEW = "VIEW";
	/**
	 * 事件类型：LOCATION(位置信息)
	 */
	public static final String EVENT_TYPE_LOCATION = "LOCATION";
	
	/**
	 * 事件类型：SCAN(二维码扫描事件)
	 */
	public static final String EVENT_TYPE_SCAN = "scancode_waitmsg";	
	
	/**
	 * OAUTH scope
	 */
	public static final String SCOPE_SNSAPI_BASE = "snsapi_base";
	public static final String SCOPE_SNSAPI_USERINFO = "snsapi_userinfo";
	
	
	public static final String KEY_COMPONENT_ACESS_TOKEN = "oaAuth_componentAccessToken";
	public static final String KEY_AUTHORIZER_ACCESS_TOKEN = "oaAuth_authorizerAccessToken_";	//后面要拼接appId 
	public static final String KEY_AUTHORIZER_JS_TICKET = "oaAuth_authorizerJsTicket_";
	
}
