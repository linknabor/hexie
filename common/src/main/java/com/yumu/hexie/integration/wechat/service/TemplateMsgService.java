package com.yumu.hexie.integration.wechat.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.ConfigUtil;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wechat.entity.templatemsg.HaoJiaAnCommentVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.HaoJiaAnOrderVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.PaySuccessVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.RegisterSuccessVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.RepairOrderVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.TemplateItem;
import com.yumu.hexie.integration.wechat.entity.templatemsg.TemplateMsg;
import com.yumu.hexie.integration.wechat.entity.templatemsg.WuyePaySuccessVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.WuyeServiceVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.YuyueOrderVO;
import com.yumu.hexie.integration.wechat.util.WeixinUtil;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnComment;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnOrder;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.impl.GotongServiceImpl;

public class TemplateMsgService {
	
	private static final Logger log = LoggerFactory.getLogger(TemplateMsgService.class);

	public static String SUCCESS_URL = ConfigUtil.get("successUrl");
	public static String REG_SUCCESS_URL = ConfigUtil.get("regSuccessUrl");

	public static final String TEMPLATE_TYPE_PAY_SUCCESS = "paySuccessTemplate";
	public static final String TEMPLATE_TYPE_REG_SUCCESS = "registerSuccessTemplate";
	public static final String TEMPLATE_TYPE_WUYEPAY_SUCCESS = "wuyePaySuccessTemplate";
	public static final String TEMPLATE_TYPE_REPAIR_ASSIGN = "reapirAssginTemplate";
	public static final String TEMPLATE_TYPE_YUYUE_ASSGIN = "yuyueNoticeTemplate";
	public static final String TEMPLATE_TYPE_COMPLAIN = "complainTemplate";
	public static final String TEMPLATE_TYPE_SERVICE = "serviceTemplate";
	
	
	/**
	 * 模板消息发送
	 */
	public static String TEMPLATE_MSG = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	private static boolean sendMsg(TemplateMsg< ? > msg, String accessToken) {
        log.error("发送模板消息------");
		WechatResponse jsonObject;
		try {
			jsonObject = WeixinUtil.httpsRequest(TEMPLATE_MSG, "POST", JacksonJsonUtil.beanToJson(msg), accessToken);
			if(jsonObject.getErrcode() == 0) {
				return true;
			}
		} catch (JSONException e) {
			log.error("发送模板消息失败: " +e.getMessage());
		}
		return false;
	}
	
	public static void sendPaySuccessMsg(ServiceOrder order, String accessToken, String appId) {
		log.error("发送模板消息！！！！！！！！！！！！！！！" + order.getOrderNo());
		PaySuccessVO vo = new PaySuccessVO();
		vo.setFirst(new TemplateItem("您的订单：("+order.getOrderNo()+")已支付成功"));

		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		String price = decimalFormat.format(order.getPrice());
		vo.setOrderMoneySum(new TemplateItem(price+"元"));
		vo.setOrderProductName(new TemplateItem(order.getProductName()));
		if(StringUtils.isEmpty(order.getSeedStr())) {
			//vo.setRemark(new TemplateItem("我们已收到您的货款，开始为您打包商品，请耐心等待: )"));
		} else {
			vo.setRemark(new TemplateItem("恭喜您得到超值现金券一枚，查看详情并分享链接即可领取。"));
		}
		TemplateMsg<PaySuccessVO> msg = new TemplateMsg<PaySuccessVO>();
		msg.setData(vo);
		
		msg.setTemplate_id(getTemplateByAppId(appId, TEMPLATE_TYPE_PAY_SUCCESS));
		String url = SUCCESS_URL.replace("ORDER_ID", ""+order.getId()).replace("ORDER_TYPE", ""+order.getOrderType());
		url = AppUtil.addAppOnUrl(url, appId);
		msg.setUrl(url);
		msg.setTouser(order.getOpenId());
		sendMsg(msg, accessToken);
	}
	
	/**
	 * 发送注册成功后的模版消息
	 * @param user
	 */
	public static void sendRegisterSuccessMsg(User user, String accessToken){
		
		log.error("用户注册成功，发送模版消息："+user.getId()+",openid: " + user.getOpenid());
		
		RegisterSuccessVO vo = new RegisterSuccessVO();
		vo.setFirst(new TemplateItem("您好，您已注册成功"));
		vo.setUserName(new TemplateItem(user.getRealName()));
		Date currDate = new Date();
		String registerDateTime = DateUtil.dttmFormat(currDate);
		vo.setRegisterDateTime(new TemplateItem(registerDateTime));
		vo.setRemark(new TemplateItem("点击详情查看。"));
		
		TemplateMsg<RegisterSuccessVO>msg = new TemplateMsg<RegisterSuccessVO>();
		msg.setData(vo);
		msg.setTemplate_id(getTemplateByAppId(user.getAppId(), TEMPLATE_TYPE_REG_SUCCESS));
		
		String url = AppUtil.addAppOnUrl(REG_SUCCESS_URL, user.getAppId());
		msg.setUrl(url);
		msg.setTouser(user.getOpenid());
		sendMsg(msg, accessToken);
	
	}
	
	/**
	 * 发送注册成功后的模版消息
	 * @param user
	 */
	public static void sendWuYePaySuccessMsg(User user, String tradeWaterId, String feePrice, String accessToken){
		
		log.error("用户支付物业费成功，发送模版消息："+user.getId()+",openid: " + user.getOpenid());
		
		WuyePaySuccessVO vo = new WuyePaySuccessVO();
		vo.setFirst(new TemplateItem("物业费缴费成功，缴费信息如下:"));
		vo.setTrade_water_id(new TemplateItem(tradeWaterId));
		vo.setReal_name(new TemplateItem(user.getRealName()));
		vo.setFee_price(new TemplateItem(new BigDecimal(feePrice).setScale(2).toString()));
		vo.setFee_type(new TemplateItem("物业费"));
		
		Date currDate = new Date();
		String payDateTime = DateUtil.dttmFormat(currDate);
		vo.setPay_time((new TemplateItem(payDateTime)));
		vo.setRemark(new TemplateItem("点击详情查看"));
		
		TemplateMsg<WuyePaySuccessVO>msg = new TemplateMsg<WuyePaySuccessVO>();
		msg.setData(vo);
		msg.setTemplate_id(getTemplateByAppId(user.getAppId(), TEMPLATE_TYPE_WUYEPAY_SUCCESS));
		String url = AppUtil.addAppOnUrl(REG_SUCCESS_URL, user.getAppId());
		msg.setUrl(url);
		msg.setTouser(user.getOpenid());
		sendMsg(msg, accessToken);
	
	}

	/**
	 * 发送维修单信息给维修工
	 * @param seed
	 * @param ro
	 */
    public static void sendRepairAssignMsg(RepairOrder ro, ServiceOperator op, String accessToken, String appId) {
    	
    	log.info("发送维修单分配模版消息#########" + ", order id: " + ro.getId() + "operator id : " + op.getId());

    	//更改为使用模版消息发送
    	RepairOrderVO vo = new RepairOrderVO();
    	vo.setTitle(new TemplateItem(op.getName()+"，您有新的维修单！"));
    	vo.setOrderNum(new TemplateItem(ro.getOrderNo()));
    	vo.setCustName(new TemplateItem(ro.getReceiverName()));
    	vo.setCustMobile(new TemplateItem(ro.getTel()));
    	vo.setCustAddr(new TemplateItem(ro.getAddress()));
    	vo.setRemark(new TemplateItem("有新的维修单"+ro.getXiaoquName()+"快来抢单吧"));
  
    	TemplateMsg<RepairOrderVO>msg = new TemplateMsg<RepairOrderVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(appId, TEMPLATE_TYPE_REPAIR_ASSIGN));
    	String url = GotongServiceImpl.WEIXIU_NOTICE + ro.getId();
    	msg.setUrl(AppUtil.addAppOnUrl(url, appId));
    	msg.setTouser(op.getOpenId());
    	TemplateMsgService.sendMsg(msg, accessToken);
    	
    }
    
    public static void sendYuyueBillMsg(String openId,String title,String billName, 
    			String requireTime, String url, String accessToken, String appId) {

        //更改为使用模版消息发送
        YuyueOrderVO vo = new YuyueOrderVO();
        vo.setTitle(new TemplateItem(title));
        vo.setProjectName(new TemplateItem(billName));
        vo.setRequireTime(new TemplateItem(requireTime));
        vo.setRemark(new TemplateItem("请尽快处理！"));
  
        TemplateMsg<YuyueOrderVO>msg = new TemplateMsg<YuyueOrderVO>();
        msg.setData(vo);
        msg.setTemplate_id(getTemplateByAppId(appId, TEMPLATE_TYPE_YUYUE_ASSGIN));
        url = AppUtil.addAppOnUrl(url, appId);
        msg.setUrl(url);
        msg.setTouser(openId);
        TemplateMsgService.sendMsg(msg, accessToken);
        
    }
    
   
    public static void sendHaoJiaAnAssignMsg(HaoJiaAnOrder hOrder, User user, String accessToken,String openId) {
    	HaoJiaAnOrderVO vo = new HaoJiaAnOrderVO();
    	vo.setTitle(new TemplateItem("有新的预约服务"));
    	vo.setAppointmentDate(new TemplateItem(hOrder.getExpectedTime()));
    	vo.setAppointmentContent(new TemplateItem(hOrder.getServiceTypeName()));
    	vo.setAddress(new TemplateItem("预约地址：" + hOrder.getStrWorkAddr()+" "+hOrder.getStrName()+" "+(hOrder.getStrMobile()==null?"":hOrder.getStrMobile()+"\r\n"
    			+"备注:"+(hOrder.getMemo()==null?"":hOrder.getMemo()))));
    	log.error("预约服务的userId="+user.getId()+"");
    	log.error("预约服务的user="+user+""); 	
    	
    	TemplateMsg<HaoJiaAnOrderVO> msg = new TemplateMsg<HaoJiaAnOrderVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(user.getAppId(), TEMPLATE_TYPE_YUYUE_ASSGIN));
    	String url = GotongServiceImpl.YUYUE_NOTICE + hOrder.getyOrderId();
    	url = AppUtil.addAppOnUrl(url, user.getAppId());
    	msg.setUrl(url);
    	msg.setTouser(openId);
    	TemplateMsgService.sendMsg(msg, accessToken);
    }
    
    //投诉模板，发送给商家
    public static void sendHaoJiaAnCommentMsg(HaoJiaAnComment comment, User user, String accessToken,String openId) {
    	log.error("sendHaoJiaAnCommentMsg的用户电话="+comment.getCommentUserTel());
    	HaoJiaAnCommentVO vo = new HaoJiaAnCommentVO();
    	vo.setTitle(new TemplateItem("用户投诉"));//标题
    	vo.setUserName(new TemplateItem(comment.getCommentUserName()));//用户姓名
    	vo.setUserTel(new TemplateItem(comment.getCommentUserTel()));//用户电话
    	vo.setReason(new TemplateItem(comment.getCommentContent()));//投诉事由
    	vo.setOrderNo(new TemplateItem(comment.getYuyueOrderNo()));;//订单编号
    	vo.setMemo(new TemplateItem("用户对您的服务有投诉，请尽快联系用户处理。"));//备注（固定内容）
    	log.error("投诉的userId="+user.getId()+"");
    	log.error("投诉的user="+user+""); 
    	TemplateMsg<HaoJiaAnCommentVO> msg = new TemplateMsg<HaoJiaAnCommentVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(user.getAppId(), TEMPLATE_TYPE_COMPLAIN));
    	String url = GotongServiceImpl.COMPLAIN_DETAIL + comment.getId();
    	url = AppUtil.addAppOnUrl(url, user.getAppId());
    	msg.setUrl(url);
    	msg.setTouser(openId);
    	TemplateMsgService.sendMsg(msg, accessToken);
    }
    
    /**
     * 不同公众号用不同模板消息
     */
    public static String getTemplateByAppId(String appId, String templateType) {
    	
    	Assert.hasText(templateType, "模板消息类型不能为空。");
    	
    	if (StringUtils.isEmpty(appId)) {
			appId = ConfigUtil.get("appId");
		}
    	
    	String key = templateType + "_" + appId;
    	
    	String templateId = ConfigUtil.get(key);
    	return templateId;

    	
    }
    
    /**
     * 测试模板
     * @param openid
     * @param accessToken
     * @param appId
     */
    public static void testSend(String openid, String accessToken, String appId) {
	
    	WuyeServiceVO vo = new WuyeServiceVO();
	  	vo.setTitle(new TemplateItem("已接收您的快递包裹！"));
	  	vo.setOrderNum(new TemplateItem(String.valueOf(System.currentTimeMillis())));
	  	String recvDate = DateUtil.dtFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
	  	vo.setRecvDate(new TemplateItem(recvDate));
	  	vo.setRemark(new TemplateItem("请及时到物业领取。"));
	  	
	  	TemplateMsg<WuyeServiceVO>msg = new TemplateMsg<WuyeServiceVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(appId, TEMPLATE_TYPE_SERVICE));
    	String url = GotongServiceImpl.SERVICE_URL + "10086";
    	msg.setUrl(AppUtil.addAppOnUrl(url, appId));
    	msg.setTouser(openid);
    	TemplateMsgService.sendMsg(msg, accessToken);
  	
	}
    
    public static void main(String[] args) {
		
    	System.out.println(DateUtil.dtFormat(new Date(), "yyyy-MM-dd HH:mm:ss"));
    	
	}
    
    
    /**
     * 快递外卖
     * @param openid
     * @param accessToken
     * @param appId
     */
    public static void sendExpressDelivery(String openid, String accessToken, String appId,long userId,String type) {
    	WuyeServiceVO vo = new WuyeServiceVO();
    	if("0".equals(type)) {
    		vo.setTitle(new TemplateItem("您的快递已送达！"));
    	  	vo.setOrderNum(new TemplateItem(String.valueOf(System.currentTimeMillis())));
    	  	String recvDate = DateUtil.dtFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
    	  	vo.setRecvDate(new TemplateItem(recvDate));
    	  	vo.setRemark(new TemplateItem("请及时到物业领取。"));
    	}else {
    		vo.setTitle(new TemplateItem("您的外卖已送达！"));
    	  	vo.setOrderNum(new TemplateItem(String.valueOf(System.currentTimeMillis())));
    	  	String recvDate = DateUtil.dtFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
    	  	vo.setRecvDate(new TemplateItem(recvDate));
    	  	vo.setRemark(new TemplateItem("请及时到物业领取。"));
    	}
	  	
	  	TemplateMsg<WuyeServiceVO>msg = new TemplateMsg<WuyeServiceVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(appId, TEMPLATE_TYPE_SERVICE));
    	String url = GotongServiceImpl.EXPRESS_URL + userId;
    	msg.setUrl(AppUtil.addAppOnUrl(url, appId));
    	msg.setTouser(openid);
    	TemplateMsgService.sendMsg(msg, accessToken);
  	
	}

}
