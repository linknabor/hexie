package com.yumu.hexie.model;

//枚举会好一点，但是需要时间，后续可以优化
public class ModelConstant {
	public static final String SPLIT = ",";
	//消息类型
	public static final int MESSAGE_GONGGAO = 0;
	public static final int MESSAGE_ZIXUN = 1;
	public static final int MESSAGE_STATUS_VALID = 0;
	public static final int MESSAGE_STATUS_INVALID = 1;

	//资讯子类型
	public static final int MESSAGE_SUB_TYPE_HEATH = 1;
	public static final int MESSAGE_SUB_TYPE_FUN = 2;
	public static final int MESSAGE_SUB_TYPE_CITY = 3;
	public static final int MESSAGE_SUB_TYPE_TEATCH = 4;
	public static final int MESSAGE_SUB_TYPE_LIFE = 5;
	public static final int MESSAGE_SUB_TYPE_MATERNAL = 6;
	public static final int MESSAGE_SUB_TYPE_OTHER = 0;

	//区域类型
	public static final int REGION_ALL = 0;
	public static final int REGION_PROVINCE = 1;
	public static final int REGION_CITY = 2;
	public static final int REGION_COUNTY = 3;
	public static final int REGION_XIAOQU = 4;
	
	//订单类型
	//0.团购单 1.单个订单 3.特卖  4.团购 5.到家服务（预约）,6维修单，11自定义服务订单,12核销券
	public static final int ORDER_TYPE_GROUP = 0;
	public static final int ORDER_TYPE_GROUP_SINGLE = 1;
	public static final int ORDER_TYPE_ONSALE = 3;
	public static final int ORDER_TYPE_RGROUP = 4;
    public static final int ORDER_TYPE_YUYUE = 5;
    public static final int ORDER_TYPE_REPAIR = 6;
    public static final int ORDER_TYPE_SERVICE = 11;
    public static final int ORDER_TYPE_EVOUCHER = 12;
    public static final int ORDER_TYPE_PROMOTION = 13;
    public static final int ORDER_TYPE_SAASSALE = 14;
    
    //serviceOperator类型
    public static final int SERVICE_OPER_TYPE_WEIXIU = 1;	//维修工
    public static final int SERVICE_OPER_TYPE_WAITER = 2;	//店小二
    public static final int SERVICE_OPER_TYPE_BAOJIE = 3;	//店小二
    public static final int SERVICE_OPER_TYPE_STAFF = 5;	//物业人员
    public static final int SERVICE_OPER_TYPE_SERVICE = 10;	//自定义服务
    public static final int SERVICE_OPER_TYPE_EVOUCHER = 11;	//优惠券核销人员
    public static final int SERVICE_OPER_TYPE_ONSALE_TAKER = 12;	//特卖接单人员
    public static final int SERVICE_OPER_TYPE_RGROUP_TAKER = 13;	//团购接单人员
    public static final int SERVICE_OPER_TYPE_PROMOTION = 14;	//推广接单提醒
    public static final int SERVICE_OPER_TYPE_SAASSALE = 15;	//saas售卖接单提醒
    public static final int SERVICE_OPER_TYPE_MSG_SENDER = 16;	//物业消息发送人员
    public static final int SERVICE_OPER_TYPE_WUYE_FEE_STAFF = 17;
    public static final int SERVICE_OPER_TYPE_OTHER_FEE_STAFF = 18;
    
    //电子优惠券状态
    public static final int EVOUCHER_STATUS_INIT = 0;	//初始化
    public static final int EVOUCHER_STATUS_NORMAL = 1;	//正常
    public static final int EVOUCHER_STATUS_USED = 2;	//已使用
    public static final int EVOUCHER_STATUS_EXPIRED = 3;	//过期
    public static final int EVOUCHER_STATUS_INVALID = 4;	//不可用,退款后的状态

	//订单操作类型
	public static final int ORDER_OP_CREATE = 1;
	public static final int ORDER_OP_REQPAY = 2;
	public static final int ORDER_OP_UPDATE_PAYSTATUS = 3;
	public static final int ORDER_OP_CANCEL = 4;
	public static final int ORDER_OP_ASYNC = 5;
	public static final int ORDER_OP_SEND = 6;
	public static final int ORDER_OP_SIGN = 7;
	public static final int ORDER_OP_COMMENT = 8;
	public static final int ORDER_OP_REFUND_REQ = 9;
	public static final int ORDER_OP_RETURN = 10;
	public static final int ORDER_OP_REFUND_FINISH = 11;
	public static final int ORDER_OP_CONFIRM = 12;//变为配货中
	//订单状态
	//0. 创建完成 1. 已支付 2. 已用户取消 3. 待退款 4. 退款中  5. 已发货 6.已签收 7. 已后台取消 8. 商户取消 9. 已确认 10.已退货（退货中走线下流程） 11.已退款 12.配货中（商户确认中）
	public static final int ORDER_STATUS_INIT = 0; 
	public static final int ORDER_STATUS_PAYED = 1;
	public static final int ORDER_STATUS_CANCEL = 2; 
	public static final int ORDER_STATUS_APPLYREFUND = 3; //暂时不使用
	public static final int ORDER_STATUS_REFUNDING = 4; 
	public static final int ORDER_STATUS_SENDED = 5; 
	public static final int ORDER_STATUS_RECEIVED = 6; 
	public static final int ORDER_STATUS_CANCEL_BACKEND = 7; 
	public static final int ORDER_STATUS_CANCEL_MERCHANT = 8;
	public static final int ORDER_STATUS_CONFIRM = 9;
	public static final int ORDER_STATUS_RETURNED = 10;
	public static final int ORDER_STATUS_REFUNDED = 11;
	public static final int ORDER_STATUS_ACCEPTED = 15;
	
	public static final int ORDER_ASYNC_STATUS_N = 0;//未同步
	public static final int ORDER_ASYNC_STATUS_Y = 1;//已同步
	
	public static final int ORDER_PINGJIA_TYPE_N = 0;
	public static final int ORDER_PINGJIA_TYPE_Y = 1;
	
	public static final int REFUND_REASON_GROUP_CANCEL = 1;
	public static final int REFUND_REASON_GROUP_BACKEND = 2;
	public static final int REFUND_REASON_GROUP_USER_REFUND = 3;
	public static final int REFUND_REASON_GROUP_OWNER_REFUND = 4;
	
	//0商户派送 1用户自提 2第三方配送 3上门服务 4用户到店
	public static final int LOGISTIC_TYPE_MERCHANT = 0;
	public static final int LOGISTIC_TYPE_USER = 1;
	public static final int LOGISTIC_TYPE_THIRDPART = 2;
	public static final int LOGISTIC_TYPE_HOME_SER = 3;
	public static final int LOGISTIC_TYPE_GO_MERCHANT = 4;
	
	
	//拼单状态 拼单中，已成团，已取消,超时退出，订单取消
	public static final int GROUP_STAUS_GROUPING = 1;
	public static final int GROUP_STAUS_FINISH = 2;
	public static final int GROUP_STAUS_CANCEL = 3;
	public static final int GROUP_STAUS_FULL = 4;//已成团未全支付
	public static final int GROUP_STAUS_INIT = 5;//发起人创建拼单，但未支付
	
	
	//团购状态
	public static final int RGROUP_STAUS_GROUPING = 1;
	public static final int RGROUP_STAUS_FINISH = 2;
	public static final int RGROUP_STAUS_CANCEL = 3;
	public static final int RGROUP_STAUS_DELIVERING = 4;	//发货中
	public static final int RGROUP_STAUS_DELIVERED = 5;	//发货完成
	
	//用户状态
	//0.初始化  1.绑定手机 2.设定小区 3.绑定房产 4.禁止
	public static final int USER_STATUS_INIT = 0;
	public static final int USER_STATUS_BINDED = 1;
	public static final int USER_STATUS_ADDRESSED = 2;
	public static final int USER_STATUS_HOUSED = 3;
	public static final int USER_STATUS_FOBBID = 4;
	
	//删除状态
	public static final int VALIDATE_STATUS_ONLINE = 0;
	public static final int VALIDATE_STATUS_OFFLINE = 1;
	
	
	
	//商品状态  //0.初始化   1. 上架   2.下架  3.删除
	public static final int PRODUCT_INIT = 0;
	public static final int PRODUCT_ONSALE = 1;
	public static final int PRODUCT_OFF = 2;
	public static final int PRODUCT_DELETED = 3;

	//商品类型（7~N用于特卖品牌的分类，添加注意是否冲突）
	public static final int PRODUCT_TYPE_TUTECHAN= 1;
	public static final int PRODUCT_TYPE_MEISHIDIAN= 2;
	public static final int PRODUCT_TYPE_SHUIGUOTAN= 3;
	public static final int PRODUCT_TYPE_SHENGXIANGUAN= 4;
	public static final int PRODCUT_TYPE_LINGSHIPU=5;
	public static final int PRODUCT_TYPE_ACTIVITY = 6;//用于特卖首页中活动
	public static final int PRODUCT_FEATURED= 100;//仅用于查询
	//团购分类
	public static final int RGROUP_PRODUCT_TYPE_BEYOND = 1; //自营团
	public static final int RGROUP_PRODUCT_TYPE_ZAIZAIMA = 2; //仔仔团
	public static final int RGROUP_PRDOUCT_TYPE_XIAOQIANSHANZHEN = 3; //小千山珍
	public static final int RGROUP_PRODUCT_TYPE_ABTUAN = 4; //AB团
	public static final int RGROUP_PRODUCT_TYPE_GANCHANGXIAN = 5; //敢试鲜
	public static final int RGROUP_PRODUCT_TYPE_FEATURED = 100;//精选，仅限于查询
	
	public static final int GOODS_TYPE_PRODUCT = 0;	//商品类售卖
	public static final int GOODS_TYPE_SERVICE = 1;	//服务类
	
	public static final int RULE_TYPE_CITY = 0;
	public static final int RULE_TYPE_NEARBY = 1;

	public static final int RULE_STATUS_OFF = 0;	//未发布
	public static final int RULE_STATUS_ON = 1;		//进行中
	public static final int RULE_STATUS_END = 2;	//已结束。即便仍在有效时间段内，依然是结束，强制结束
	public static final int RULE_STATUS_DEL = 3;	//已删除。不可见，回收站里找，界面上的删除暂时都是软删除
	public static final int RULE_STATUS_PHYS_DEL = 4;	//已删除。物理删除
	
	public static final int DISTRIBUTION_STATUS_ON = 0;
	public static final int DISTRIBUTION_STATUS_OFF = 1;
	
	public static final int POINT_TYPE_ZIMA = 0;
	public static final int POINT_TYPE_LVDOU = 1;
	public static final int POINT_TYPE_JIFEN = 2;
	


	//帖子状态 0.正常 1.撤销
	public static final String THREAD_STATUS_NORMAL = "0";
	public static final String THREAD_STATUS_DELETED = "1";
	
	//发布信息分类
	public static final int THREAD_CATEGORY_OUTDOORS = 1;	//户外活动
	public static final int THREAD_CATEGORY_PETS = 2;	//宠物宝贝
	public static final int THREAD_CATEGORY_CATE = 3;	//吃货天地	
	public static final int THREAD_CATEGORY_STORE = 4;	//二手市场
	public static final int THREAD_CATEGORY_EDUCATION = 5;	//亲子教育
	public static final int THREAD_CATEGORY_SPORTS = 6;	//运动达人
	public static final int THREAD_CATEGORY_CHAT = 7;	//社区杂谈

	public static final int THREAD_CATEGORY_COMPLAINT = 8;	//业主投诉
	public static final int THREAD_CATEGORY_SUGGESTION = 9;	//业主意见
	public static final int THREAD_CATEGORY_HEALTH_REPORT = 10;	//健康上报
	public static final int THREAD_CATEGORY_MASK_RESV = 11;	//口罩预约
	public static final int THREAD_CATEGORY_SERVICE_RESV = 12;	//服务预约

	//帖子回复状态 0.正常 1.撤销
	public static final String COMMENT_STATUS_NORMAL = "0";
	public static final String COMMENT_STATUS_DELETED = "1";

	//预约单状态 0初始化、 1预约成功、2预约失败、3预约超时 、4需要与商户同步、 5已取消
	public static final int ORDER_STAUS_YUYUE_INIT = 0;
	public static final int ORDER_STAUS_YUYUE_SUCCESS = 1;
	public static final int ORDER_STAUS_YUYUE_FAIL = 2;
	public static final int ORDER_STAUS_YUYUE_TIMEOUT = 3;
	public static final int ORDER_STAUS_YUYUE_USED = 4;//需要与商户同步
	public static final int ORDER_STAUS_YUYUE_CANCEL = 5;//已取消

	//服务单支付方式
	public static final int YUYUE_PAYMENT_TYPE_OFFLINE = 0;//线下付款
	public static final int YUYUE_PAYMENT_TYPE_WEIXIN = 1;//微信支付

	//增加预约单的支付状态
	public static final int YUYUE_PAYSTATUS_INIT = 0;
	public static final int YUYUE_PAYSTATUS_PAYED = 1;
	public static final int YUYUE_PAYSTATUS_UNPAYED = 2;
	public static final int YUYUE_PAYSTATUS_REFUNDING = 3;
	public static final int YUYUE_PAYSTATUS_REFUNDED = 4;
	
	//增加预约单类型
	public static final int YUYUE_PRODUCT_TYPE_FASUPER = 1;//尚匠
	public static final int YUYUE_PRODUCT_TYPE_FLOWERPLUS = 2;//花加
	public static final int YUYUE_PRODUCT_TYPE_HUYAORAL = 3;//沪雅口腔
	public static final int YUYUE_PRODUCT_TYPE_DAOJIAMEI = 4;//白富美
	public static final int YUYUE_PRODUCT_TYPE_AYILAILE = 5;//阿姨来了
	public static final int YUYUE_PRODUCT_TYPE_WEIZHUANGWANG=6;//微装网
	public static final int YUYUE_PRODUCT_TYPE_BOVO=7;//邦天乐
	public static final int YUYUE_PRODUCT_TYPE_GAOFEI=8;//高飞
	public static final int YUYUE_PRODUCT_TYPE_HAORENSHENG=9;//好人生
	public static final int YUYUE_PRODUCT_TYPE_JIUYE = 10;//九曳
	public static final int YUYUE_PRODUCT_TYPE_BAOJIE = 11;//保洁服务
	public static final int YUYUE_PRODUCT_TYPE_AIXIANGBAN = 12;//爱相伴服务
	public static final int YUYUE_PRODUCT_TYPE_HAOJIAAN = 13;//好家安
	//增加服务类型
	public static final int YUYUE_SERVICE_TYPE_CAR = 0;//汽车
	public static final int YUYUE_SERVICE_TYPE_FLOWER = 1;//鲜花
	public static final int YUYUE_SERVICE_TYPE_HOUSEKEEPING = 2;//家政
	public static final int YUYUE_SERVICE_TYPE_LIFE = 3;//生活
	public static final int YUYUE_SERVICE_TYPE_DECORATION = 4;//装修
	public static final int YUYUE_SERVICE_TYPE_HEALTH = 5;//健康
	public static final int YUYUE_SERVICE_TYPE_CHESHI = 6;//车饰
	public static final int YUYUE_SERVICE_TYPE_HAORENSHENG = 7;///好人生
	//是否是精选服务
	public static final int YUYUE_SERVICE_UNHANDPICK = 0;//非精选
	public static final int YUYUE_SERVICE_HANDPICK = 1;//精选
	
	//是否是精选服务
	public static final int YUYUE_SERVICE_STATUS_UNUSED = 0;//未服务
	public static final int YUYUE_SERVICE_STATUS_USED = 1;//已服务
	
	//服务类型
	public static final int YUYUE_SERVICE_CYCLE = 0;//周期
	public static final int YUYUE_SERVICE_SINGLE = 1;//单次
	//增加banner类型
	public static final int BANNER_TYPE_SERVICE = 0;//到家
	public static final int BANNER_TYPE_ONSALE = 1;//特卖
	public static final int BANNER_TYPE_RGROUP = 2;//团购
	public static final int BANNER_TYPE_WUYE = 3;//特卖
	public static final int BANNER_TYPE_ACTIVITY = 4;//特卖首页活动
	public static final int BANNER_TYPE_ONSALEBRAND = 5;//特卖品牌
	
	public static final int BANNER_STATUS_VALID = 1;
	public static final int BANNER_STATUS_INVALID = 0;
	
	public static final int EXCEPTION_BIZ_TYPE_ORDER = 1;
	public static final int EXCEPTION_BIZ_TYPE_DAOJIA = 2;
	public static final int EXCEPTION_BIZ_TYPE_LUNTAN = 3;
	public static final int EXCEPTION_BIZ_TYPE_RGROUP = 4;
	public static final int EXCEPTION_BIZ_TYPE_PINDAN = 5;
	public static final int EXCEPTION_BIZ_TYPE_ONSALE = 6;
	public static final int EXCEPTION_BIZ_TYPE_SCHEDULE = 7;
	public static final int EXCEPTION_BIZ_TYPE_COUPON = 8;
	public static final int EXCEPTION_BIZ_TYPE_SYSTEM = 9;
	public static final int EXCEPTION_BIZ_TYPE_TEMPLATEMSG = 10;
	public static final int EXCEPTION_BIZ_TYPE_H5LOGIN = 11;
	
	public static final int EXCEPTION_LEVEL_INFO = 1;
	public static final int EXCEPTION_LEVEL_ERROR = 2;
	
	

	public static final int SCHEDULE_TYPE_PAY_TIMEOUT = 1;
	public static final int SCHEDULE_TYPE_REFUND_STATUS = 2;
	public static final int SCHEDULE_TYPE_PAY_STATUS = 3;
	public static final int SCHEDULE_TYPE_PINDAN_TIMEOUT = 4;
    public static final int SCHEDULE_TYPE_TUANGOU_TIMEOUT = 5;
    public static final int SCHEDULE_TYPE_XIYI_TIMEOUT = 6;
    public static final int SCHEDULE_TYPE_BAOJIE_TIMEOUT = 7;
	
    //资讯类型  0.物业公告, 1.业委会公告, 2.居委会公告, 3.便民信息, 9.系统资讯-全局，10系统资讯-公众号级, 11圈子, 12群发消息推送 13账单通知  14欠费账单通知 15意见回复通知 16工单进度提醒
    public static final int NOTICE_TYPE2_WUYE = 0;	//物业公告
    public static final int NOTICE_TYPE2_YEWEI = 1;	//业委会公告
    public static final int NOTICE_TYPE2_JUWEI = 2;	//居委会公告
    public static final int NOTICE_TYPE2_BIANMIN = 3;
    public static final int NOTICE_TYPE2_ALL = 9;
    public static final int NOTICE_TYPE2_APP = 10;
    public static final int NOTICE_TYPE2_MOMENTS = 11;
    public static final int NOTICE_TYPE2_NOTIFICATIONS = 12;
	public static final int NOTICE_TYPE2_BIll = 13;
	public static final int NOTICE_TYPE2_ARREARS_BILL = 14;
	public static final int NOTICE_TYPE2_THREAD = 15;
	public static final int NOTICE_TYPE2_ORDER = 16;
	public static final int NOTICE_TYPE2_RGROUP = 17;
	
	//通知类型
	public static final int NOTICE_TYPE_ORDER = 1;//订单通知
	public static final int NOTICE_TYPE_COMMENT = 2;//评论消息
	public static final int NOTICE_TYPE_RGROUP = 3;//团购消息
	public static final int NOTICE_TYPE_SYS_PUSH = 4;//推送消息
	public static final int NOTICE_TYPE_YUYUE = 5;//预约消息
	public static final int NOTICE_TYPE_COUPON = 6;//预约消息
	
	public static final int NOTICE_SUB_TYPE_ORDERSUCCESS = 1;
	public static final int NOTICE_SUB_TYPE_ORDERSENDGOODS = 2;
	public static final int NOTICE_SUB_TYPE_ORDERREFUND = 3;
	public static final int NOTICE_SUB_TYPE_GROUPSUCCESS = 1;
	public static final int NOTICE_SUB_TYPE_GROUPFAIL = 2;
	public static final int NOTICE_SUB_TYPE_GROUPNOTIFY = 3;
	public static final int NOTICE_SUB_TYPE_GROUPARRIVAL = 4;
	
	
	/***********现金券************** 1:订单分裂（支付成功通过分享产生红包）|2:用户注册|3:关注红包|4:活动发布|5:订单分裂模板(详见1)|6:会员注册|7.订单分裂2（支付成功直接塞红包） 8.订单分裂模板2（(详见7)）  */
	//种子类型
	public static final int COUPON_SEED_ORDER_BUY = 1;
	public static final int COUPON_SEED_USER_REGIST = 2;
	public static final int COUPON_SEED_USER_SUBSCRIB = 3;
	public static final int COUPON_SEED_ACTIVITY = 4;
	public static final int COUPON_SEED_ORDER_BUY_TEMPLATE = 5;
	public static final int COUPON_SEED_MEMBER = 6;
	public static final int COUPON_SEED_ORDER_BUY2 = 7;
	public static final int COUPON_SEED_ORDER_BUY2_TEMPLATE = 8;
	
	//种子状态
	public static final int COUPON_SEED_STATUS_AVAILABLE = 0;
	public static final int COUPON_SEED_STATUS_INVALID = 1;
	public static final int COUPON_SEED_STATUS_EMPTY = 2;
	public static final int COUPON_SEED_STATUS_TIMEOUT = 3;
	
	//现金券规则
	public static final int COUPON_RULE_STATUS_AVAILABLE = 1;
	public static final int COUPON_RULE_STATUS_INVALID = 2;
	
	public static final int COUPON_STATUS_AVAILABLE = 1;
	public static final int COUPON_STATUS_USED = 2;
	public static final int COUPON_STATUS_TIMEOUT = 3;
	public static final int COUPON_STATUS_LOCKED = 4;

	public static final int COLLOCATION_STATUS_AVAILABLE = 1;
	public static final int COLLOCATION_STATUS_INVAILID = 2;
	
	//评论或者投诉
	public static final int HAOJIAAN_COMMPENT_STATUS_COMMENT = 1;//评论
	public static final int HAOJIAAN_COMMPENT_STATUS_COMPLAIN = 2;//投诉

	public static final String PARA_TYPE_SECT = "1";
	public static final String PARA_TYPE_CSP = "2";
	
	public static final String KEY_TYPE_BOTTOM_ICON = "cfg:page:bottomIcon";	//底部图标缓存key
	public static final String KEY_TYPE_BGIMAGE = "cfg:page:bgImage";	//空白背景图
	public static final String KEY_TYPE_BANNER = "cfg:page:banner";		//页面顶部轮播图
	public static final String KEY_TYPE_QRCODE = "cfg:page:qrcode";		//公众号二维码
	public static final String KEY_TYPE_CSHOTLINE = "cfg:page:csHotline";
	public static final String KEY_TYPE_PAGECONFIG = "cfg:page:pageConfigView";	//页面配置
	public static final String KEY_TYPE_WUYEPAY_TABS = "cfg:page:wuyePayTabs";	//物业缴费选项卡
	public static final String KEY_TYPE_MENU_CSP = "cfg:page:menu:csp";	//公众号菜单
	public static final String KEY_TYPE_MENU_SECT = "cfg:page:menu:sect";	//公众号菜单
	public static final String KEY_TYPE_MENU_DEFAULT = "cfg:page:menu:default";	//公众号菜单
	public static final String KEY_TYPE_MENU_APP_BINDED = "cfg:page:menu:app:default";	//公众号菜单-绑定了房屋的
	public static final String KEY_TYPE_MINI_ROLE_PAGE = "cfg:page:miniRolePage";	//小程序访问控制

	public static final int WECHAT_CARD_TYPE_MEMBER = 1;	//微信会员卡
	
	//微信会员卡领卡渠道
	public static final String CARD_GET_SUBSCRIBE = "subscribe";	//关注领卡
	public static final String CARD_GET_REGISTER = "register";	//注册领卡
	public static final String CARD_GET_MENU = "menu";	//菜单领卡
	
	public static final int CARD_STATUS_NONE = 1;	//未领卡
	public static final int CARD_STATUS_GET = 2;	//已领卡
	public static final int CARD_STATUS_ACTIVATED = 3;	//已激活
	public static final int CARD_STATUS_DELETED = 4;	//已删除
	
	//队列
	public static final String KEY_ADD_POINT_QUEUE = "addPointQueue";
	public static final String KEY_BIND_HOUSE_QUEUE = "bindHouseQueue";
	public static final String KEY_USER_LOGIN = "lock:userLoginSession:";

	public static final String KEY_MOBILE_VERICODE = "mobileVericode_";	//手机短信验证码
	public static final String KEY_VERICODE_FREQUENCY = "vericode:Frequency:";
	public static final String KEY_VERICODE_TOTAL_LIMIT = "vericode:TotalLimit:";
	public static final String KEY_VERICODE_IP_FREQUENCY = "vericode:IpFrequency:";
	public static final String KEY_VERICODE_TRADE_ID = "vericodeInvoiceTrade_";	//发票申请短信验证码
	public static final String KEY_VERICODE_RECEIPT_TRADE_ID = "vericode:receipt:trade:";	//电子申请短信验证码,后面接_sh:trade_water_id或者_guizhou:trade_water_id
	
	/*下面这些的value值不要改，是另一个系统推送过来的，要改两边都要改 start */
	public static final String KEY_WECHAT_CARD_CATAGORY = "wechatCardCatagory";
	public static final String KEY_EVENT_SUBSCRIBE_QUEUE = "queueEventSubscribe";	//关注事件队列
	public static final String KEY_EVENT_GETCARD_QUEUE = "queueEventUserGetCard";	//领卡事件消息队列
	public static final String KEY_EVENT_UPDATECARD_QUEUE = "queueEventUpdateCard";//更新卡事件消息队列
	public static final String KEY_EVENT_SUBSCRIBE_MSG_QUEUE = "queueSubscribeMsg";	//用户在图文等场景内订阅通知的操作 事件队列
	public static final String KEY_EVENT_SUBSCRIBE_UPDATE_QUEUE = "queueEventUpdateSubscribe";	//关注事件消息队列，更新用户信息用
	public static final String KEY_EVENT_UNSUBSCRIBE_QUEUE = "queueEventUnsubscribe";	//取消关注事件消息队列 tpauth -> hexie
	/*下面这些的value值不要改，是另一个系统推送过来的，要改两边都要改 end */
	
	public static final String KEY_UNSUBSCRIBE_NOTIFY_QUEUE = "queue:notify:unsubscribe";	//取消关注通知，hexie -> community
	public static final String KEY_UNSUBSCRIBE_NOTIFY_CHECK = "lock:unsubscribeNotification:";	//防重
	
	public static final String KEY_WUYE_REFUND_ORDER = "wuyeRefundOrder_";	// 物业退款交易ID 
	public static final String KEY_WUYE_REFUND_QUEUE = "queueWuyeRefund";
	
	public static final String KEY_MAINTANANCE_SWITCH = "maintananceSwitch";

	public static final int BANK_CARD_TYPE_DEBIT = 1;	//借记卡
	public static final int BANK_CARD_TYPE_CREDIT = 2;	//贷记卡
	
	public static final String KEY_NOTIFY_PAY_QUEUE = "queue:notify:pay";	//物业支付到账通知（给物业工作人员推送）
	public static final String KEY_NOTIFY_HOUSE_BINDER_QUEUE = "queue:notify:houseBinder";	//物业支付到账通知(给绑定房屋的人推送)
	public static final String KEY_NOTIFY_SERVICE_QUEUE = "queue:notify:service";	//服务消息推送
	public static final String KEY_UPDATE_OPERATOR_QUEUE = "queue:operator:update";	//服务人员更新
	public static final String KEY_UPDATE_SERVICE_CFG_QUEUE = "queue:servicecfg:update";	//服务配置更新
	public static final String KEY_UPDATE_ORDER_STATUS_QUEUE = "queue:orderstatus:update";	//订单状态（服务、特卖、团购）更新
	public static final String KEY_NOTIFY_DELIVERY_QUEUE = "queue:noitfy:delivery";	//特卖、团购快递发货通知
	public static final String KEY_NOTIFY_PARTNER_REFUND_QUEUE = "queue:notify:partnerRefund";	//合伙人退款通知
	public static final String KEY_NOTIFY_ESHOP_REFUND_QUEUE = "queue:notify:eshopRefund";	//合伙人退款通知
	public static final String KEY_NOTIFY_WUYE_COUPON_QUEUE = "queue:notify:wuyeCoupon";	//物业红包消费通知
	
	public static final String KEY_ORDER_ACCEPTED = "lock:serviceOrder:";
	
	public static final String KEY_CUSTOM_SERVICE = "cfg:customservice";
	public static final String KEY_NOITFY_PAY_DUPLICATION_CHECK = "lock:payNotification:";
	public static final String KEY_ASSIGN_CS_ORDER_DUPLICATION_CHECK = "lock:assginCsOrder:";
	public static final String KEY_NOTIFY_WORK_ORDER_DUPLICATION_CHECK = "lock:workOrder:";
	public static final String KEY_NOTIFY_CONVERSION_DUPLICATION_CHECK = "lock:conversion:";
	public static final String KEY_CS_SERVED_SECT = "cfg:customservice:sect:";
	
	public static final String KEY_MSG_TEMPLATE = "cfg:msgtemplate:template";
	public static final String KEY_MSG_TEMPLATE_URL = "cfg:msgtemplate:url";
	public static final String KEY_SUBSCRIBE_MSG_TEMPLATE = "cfg:subscribeMsg:template";	//所有能订阅的消息模板
	
	public static final String KEY_WUYE_PARAM_CFG = "cfg:wuyeParam";
	public static final int SMS_TYPE_REG = 101;	//用户注册短信
	public static final int SMS_TYPE_INVOICE = 102;	//发票验证码获取
	public static final int SMS_TYPE_PROMOTION_PAY = 103;	//发票验证码获取
	public static final int SMS_TYPE_RESET_PASSWORD = 104;	//发票验证码获取
	public static final int SMS_TYPE_RECEIPT = 105;	//电子收据验证码获取
	
	public static final String KEY_PRO_RULE_INFO = "product:rule:";
	public static final String KEY_PRO_STOCK = "product:stock:";
	public static final String KEY_PRO_FREEZE = "product:freeze:";
	
	public static final String KEY_COUPON_RULE = "coupon:rule:";
	public static final String KEY_COUPON_TOTAL = "coupon:total:";	//总数
	public static final String KEY_COUPON_USED = "coupon:used:";	//已使用的
	public static final String KEY_COUPON_SEED = "coupon:seed:";
	public static final String KEY_COUPON_GAIN_QUEUE = "queue:coupon:gain";
	
	public static final String KEY_USER_COUPON_SEED = "user:gaiedCouponSeed:";	//用户已领过的红包种子
	
	public static final String KEY_USER_CACHED = "user:cached";	//微信用户后面接openid,支付宝用户，后面接appid_userid
	public static final String KEY_USER_SERVE_ROLE = "user:servRole";	//用户服务类型
	public static final String KEY_USER_SUBSCRIBE_MSG_TEMPLATE = "user:subscribeMsgTemplate";	//用户订阅过的消息模板
	
	public static final String H5_USER_TYPE_ALIPAY = "1"; //支付宝
	public static final String H5_USER_TYPE_MINNI = "2"; //微信小程序
	public static final String H5_USER_TYPE_WECHAT = "3"; //微信公众号
	
	public static final int EVOUCHER_TYPE_VERIFICATION = 0;	//核销券
	public static final int EVOUCHER_TYPE_PROMOTION = 1;	//推广券码
	
	public static final String KEY_HEXIE_PARTNER = "partner:";	//合伙人
	public static final String KEY_MSG_VIEW_CACHE = "msg:cached";
	
	//模板业务类型, 0.普通用户的模板，1工作人员模板
	public static final int SUBSCRIBE_MSG_TEMPLATE_BIZ_TYPE_NORMAL = 0;
	public static final int SUBSCRIBE_MSG_TEMPLATE_BIZ_TYPE_OPERATOR = 1;	
	
	//消息类型，0模板消息，1订阅消息
	public static final int MSG_TYPE_TEMPLATE = 0;
	public static final int MSG_TYPE_SUBSCRIBE_MSG = 1;

	//微信用户关注状态
	public static final int WECHAT_USER_SUBSCRIBED = 1;
	public static final int WECHAT_USER_UNSUBSCRIBED = 0;

	public static final String KEY_WORKORER_MSG_QUEUE = "queue:workorder:msg";
	public static final String KEY_CONVERSION_MSG_QUEUE = "queue:conversion:msg";

	public static final String KEY_MP_QRCODE_CACHED = "mpQrCode:cached:";
	public static final String KEY_EVENT_SCAN_SUBSCRIBE_QUEUE = "queue:event:scanSubscribe";	//扫码关注事件消息队列(未关注过的用户)
	public static final String KEY_EVENT_SCAN_QUEUE = "queue:event:scan";	//扫码事件消息队列(已关注过的用户)
	public static final String KEY_EVENT_VIEW_MINIPROGRAM = "queue:event:viewMiniprogram";	//用户查看小程序事件
	public static final String KEY_EVENT_TEMPLATE_MSG_RETRY = "event:retry:";	//模板消息重试次数，后面接时间KEY，value是重试次数
	
	public static final String KEY_REGISER_AND_BIND_QUEUE = "queue:registerBind";
	public static final String KEY_INVOICE_NOTIFICATION_LOCK = "lock:invoiceNotification:";	//开票成功通知-锁
	public static final String KEY_RECEIPT_NOTIFICATION_LOCK = "lock:receiptNotification:";	//收据开具成功通知-锁
	public static final String KEY_INVOICE_NOTIFICATION_QUEUE = "queue:invoice:notification";	//开票成功通知
	public static final String KEY_RECEIPT_NOTIFICATION_QUEUE = "queue:receipt:notification";	//收据开具成功通知
	public static final String KEY_INVOICE_APPLICATIONF_FLAG = "invoice:application:";	//发票申请标记，后面接trade_water_id
	public static final String KEY_RECEIPT_APPLICATIONF_FLAG = "receipt:application:";	//电子收据申请标记，后面接_sh:trade_water_id
	
	public static final String KEY_WORKORDER_CFG = "workorder:cfg:";	//工单配置，后面接city_db:sect_id

	public static final String interactReplyNoticeQueue = "queue:interact:replyToHx"; //业主意见物业回复的通知给用户
	public static final String interactGradeNoticeQueue = "queue:interact:grade"; //投诉建议处理结果评价
	//
	public static final String renovationNoticeQueue = "queue:notify:renovation"; //装修登记审核结果通知给业主
	public static final String KEY_DELIVERY_OPERATOR_NOTICE_MSG_QUEUE = "queue:eshop:deliveryReceiver"; //电商接单人推送
	public static final String KEY_SERVICE_OPERATOR_NOTICE_MSG_QUEUE = "queue:eshop:serviceReceiver"; //服务接单人推送
	public static final String KEY_RGROUP_SUCCESS_NOTICE_MSG_QUEUE = "queue:notify:rgroup:groupSuccess"; //成团提醒。发给团长
	public static final String KEY_RGROUP_ARRIVAL_NOTICE_QUEUE = "queue:notify:rgroup:arriavalNotice";	//团购到货提醒。发给客户
	public static final String KEY_PAGE_TIPS_SWITCH_SECT = "tips:switchsect:";	//后面接page

	public static final String KEY_USER_SESSION_KEY = "miniuser:sessionKey:";	//小程序的sessionkey
	public static final String KEY_ALI_USER_AUTH_TOKEN = "miniuser:alipay:authToken:";	//支付宝小程序authToken
	public static final String KEY_TYPE_ORG_MENU = "cfg:page:org:menu";		//机构工作人员菜单
	
	public static final String KEY_RGROUP_OWNER_REGION = "rgroup:owner:region:";	//后面接userId
	public static final String KEY_MINI_ACCESS_TOKEN = "miniprogram:accessToken:";	//后面接小程序appid

	public static final String USER_ROLE_RGROUPOWNER = "03";	//01团长
	public static final String KEY_CREATE_NEW_REGION_LOCK = "lock:createRegion:";	//后面接小区名字
	public static final String KEY_RGROUP_NUM_GENERATOR = "rgroup:groupNum:";	//跟团号生成器，后面接ruleId
	public static final String KEY_RGROUP_OWNER_ACCESSED = "rgroup:accessed:owner:";	//后面接团长id,团长被访问次数
	public static final String KEY_RGROUP_OWNER_ORDERED = "rgroup:ordered:owner:";	//后面接团长id,团长被跟团次数
	public static final String KEY_RGROUP_GROUP_ACCESSED = "rgroup:accessed:group:";	//后面接团id,团被访问次数
	public static final String KEY_RGROUP_GROUP_ORDERED = "rgroup:ordered:group:";	//后面接团id，团被下单次数
	
	public static final Integer ORDERITEM_REFUND_STATUS_PAID = 0;	//已支付
	public static final Integer ORDERITEM_REFUND_STATUS_REFUNDED = 1;	//已退款
	public static final Integer ORDERITEM_REFUND_STATUS_REFUNDING = 2;	//退款中
	public static final Integer ORDERITEM_REFUND_STATUS_APPLYREFUND = 3;	//退款审核中
	
	public static final Integer REFUND_STATUS_CANCEL = 0;	//申请撤销或被驳回
	public static final Integer REFUND_STATUS_INIT = 1;		//申请退款
	public static final Integer REFUND_STATUS_AUDIT_PASSED = 2;	//审核通过，如果审核拒绝，则退回状态0
	public static final Integer REFUND_STATUS_SYS_REFUNDING = 3;	//系统退款中
	public static final Integer REFUND_STATUS_REFUNDED = 4;	//退款成功
	
	public static final Integer REFUND_OPERATION_USER_APPLY = 0;	//用户申请
	public static final Integer REFUND_OPERATION_OWNER_APPLY = 1;	//团长申请
	public static final Integer REFUND_OPERATION_CANCEL = 2;	//撤销申请
	public static final Integer REFUND_OPERATION_MODIFY = 3;	//修改申请
	public static final Integer REFUND_OPERATION_PASS_AUDIT = 4;	//申请审核通过
	public static final Integer REFUND_OPERATION_REJECT_AUDIT = 5;	//申请审核拒绝
	public static final Integer REFUND_OPERATION_WITHDRAW_REFUND = 6;	//取消商品并退款？（快团团有这个）
	public static final Integer REFUND_OPERATION_SYS_REFUNDING = 7;	//发起退款
	public static final Integer REFUND_OPERATION_REFUNDED = 8;	//退款完成
	
	public static final String KEY_RGROUP_SECT_TITLE_SEARCH = "rgroup:search:sect:title:";	//后面接user的miniOpenid
	public static final String KEY_RGROUP_LEADER_TITLE_SEARCH = "rgroup:search:leader:title:";	//后面接user的miniOpenid
	
	public static final String KEY_RGROUP_SUBSCRIBE_GROUP = "rgroup:subscribe:group:";	//后面接团购id
	public static final String KEY_RGROUP_SUBSCRIBE_LEADER = "rgroup:subscribe:leader:";	//后面接团长ownerId
	public static final String KEY_RGROUP_SUBSCRIBE_REGION = "rgroup:subscribe:region:";	//后面接regionId
	
	public static final String KEY_RGROUP_PUB_QUEUE = "queue:rgroup:pub";	//团购发布
	
	public static final String KEY_PAGE_VIEW_COUNT = "count:pageview:";	//页面计数统计，后面接appid:日期
	public static final String BEYONDSOFT_TOKEN_WEST = "beyondsoft:west:token";
	
	public static final String KEY_USER_SYS_SHWY = "_shwy";	//上海物业
	public static final String KEY_USER_SYS_LIFEPAY = "_lifepay";	//支付宝生活缴费
	
	public static final String KEY_TRADE_BIND_HOU = "trade:bindHou:";	//后面接tradeWaterId
}		
