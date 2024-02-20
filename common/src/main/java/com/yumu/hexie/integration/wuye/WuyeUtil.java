package com.yumu.hexie.integration.wuye;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.MyHttpClient;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.resp.BillStartDate;
import com.yumu.hexie.integration.wuye.resp.CellListVO;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.resp.PayWaterListVO;
import com.yumu.hexie.integration.wuye.vo.HexieConfig;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieHouses;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceInfo;
import com.yumu.hexie.integration.wuye.vo.PayResult;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.impl.LocationServiceImpl;

public class WuyeUtil {
	private static final Logger log = LoggerFactory.getLogger(WuyeUtil.class);

	// 接口地址
	private static final String HOUSE_DETAIL_URL = "getHoseInfoSDO.do?user_id=%s&sect_id=%s"; // 房屋详情地址
	private static final String ADD_HOUSE_URL = "addHouseSDO.do?user_id=%s&stmt_id=%s&mng_cell_id=%s&openid=%s&appid=%s&mobile=%s"; // 添加房子
	private static final String SYS_ADD_HOUSE_URL = "billSaveHoseSDO.do?user_id=%s&stmt_id=%s&house_id=%s"; // 扫一扫（添加房子）
	private static final String DEL_HOUSE_URL = "delHouseSDO.do?user_id=%s&mng_cell_id=%s&openid=%s&appid=%s&mobile=%s"; // 删除房子
	private static final String BILL_LIST_URL = "getBillListMSDO.do?user_id=%s&pay_status=%s&startDate=%s&endDate=%s&curr_page=%s&total_count=%s&house_id=%s&sect_id=%s"; // 获取账单列表
	private static final String PAY_RECORD_URL = "payMentRecordSDO.do?user_id=%s&startDate=%s&endDate=%s"; // 获取支付记录列表
	private static final String PAY_INFO_URL = "payMentRecordInfoSDO.do?user_id=%s&trade_water_id=%s"; // 获取支付记录详情
	private static final String WXLOGIN_URL = "weixinLoginSDO.do?weixin_id=%s&appid=%s&unionid=%s"; // 登录验证（微信登录）
	private static final String MEMBER_WX_PAY_URL = "member/memberPayRequestSDO.do?bill_id=%s&openid=%s&totalPrice=%s&notifyUrl=%s"; // 微信支付请求
	private static final String MEMBER_WX_Query_URL = "member/memberQueryOrderSDO.do?bill_id=%s"; // 微信支付查询请求
	private static final String WX_PAY_NOTICE = "wechatPayQuerySDO.do?user_id=%s&trade_water_id=%s"; // 微信支付返回
	private static final String COUPON_USE_QUERY_URL = "conponUseQuerySDO.do?user_id=%s";
	private static final String APPLY_INVOICE_URL = "applyInvoiceSDO.do?mobile=%s&invoice_title=%s&invoice_title_type=%s&credit_code=%s&trade_water_id=%s&openid=%s";
	private static final String INVOICE_INFO_TO_TRADE = "getInvoiceInfoSDO.do?trade_water_id=%s";
	private static final String MNG_HEXIE_LIST_URL = "queryHeXieMngByIdSDO.do"+ "?sect_id=%s&build_id=%s&unit_id=%s&data_type=%s";//合协社区物业缴费的小区级联
	
	private static final String SYNC_SERVICE_CFG_URL = "param/getParamSDO.do?info_id=%s&type=%s&para_name=%s";
	private static final String BILL_LIST_DATE = "getBillStartDateSDO.do?user_id=%s&mng_cell_id=%s";//获取无账单日期
	private static final String PAY_WATER_URL = "getMngCellByTradeIdSDO.do?user_id=%s&trade_water_id=%s"; // 获取支付记录涉及的房屋
	private static final String BIND_BY_TRADE_URL = "bindHouseByTradeIdSDO.do?user_id=%s&trade_water_id=%s&openid=%s&appid=%s&bind_type=%s&mobile=%s";
	private static final String GET_HOUSE_VERNO_URL = "queryHouByVouNoSDO.do?user_id=%s&ver_no=%s"; //根据户号查询房屋
	
	private static final Logger Log = LoggerFactory.getLogger(WuyeUtil.class);
	
	/**
	 * 查询房屋列表
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HouseListVO> queryHouse(User user, String sectId){
		if (StringUtils.isEmpty(sectId)) {
			sectId = "";
		}
		String url = getRequestUri(user) + String.format(HOUSE_DETAIL_URL, user.getWuyeId(), sectId);
		return (BaseResult<HouseListVO>)httpGet(url,HouseListVO.class);
	}
	
	/**
	 * 绑定房产
	 * @param user
	 * @param stmtId
	 * @param houseId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HexieUser> bindHouse(User user, String stmtId,String houseId) {
		String url = getRequestUri(user) + String.format(ADD_HOUSE_URL, user.getWuyeId(), stmtId,houseId, 
				user.getOpenid(), user.getAppId(), user.getTel());

		log.info("【绑定房产url】="+url);
		return (BaseResult<HexieUser>)httpGet(url,HexieUser.class);
	}
	
	/**
	 * 根据订单查询房产信息
	 * @param user
	 * @param stmtId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HexieHouse> getHouse(User user, String stmtId) {
		String url = getRequestUri(user) + String.format(SYS_ADD_HOUSE_URL, user.getWuyeId(), stmtId, "");
		return (BaseResult<HexieHouse>)httpGet(url,HexieHouse.class);
	}
	
	/**
	 * 删除房产
	 * @param user
	 * @param houseId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HouseListVO> deleteHouse(User user, String houseId) {
		String url = getRequestUri(user) + String.format(DEL_HOUSE_URL, user.getWuyeId(), houseId, user.getOpenid(), user.getAppId(), user.getTel());
		return (BaseResult<HouseListVO>)httpGet(url,HouseListVO.class);
	}
	
	/**
	 * 根据订单查询房产信息
	 * @param user
	 * @param stmtId
	 * @param house_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HexieHouse> getHouse(User user, String stmtId, String house_id) {
		String url = getRequestUri(user) + String.format(SYS_ADD_HOUSE_URL, user.getWuyeId(), stmtId, house_id);
		return (BaseResult<HexieHouse>)httpGet(url,HexieHouse.class);
	}

	/**
	 * 用户登录
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HexieUser> userLogin(User user) {
		String openid = user.getOpenid();
		String appid = user.getAppId();
		String unionid = "";
		if (StringUtils.isEmpty(unionid)) {
			unionid = user.getUnionid();
		}
		if (StringUtils.isEmpty(unionid)) {
			unionid = "";
		}
		if (StringUtils.isEmpty(openid) || "0".equals(openid)) {
			openid = user.getMiniopenid();
			appid = user.getMiniAppId();
		}
		if (StringUtils.isEmpty(openid) || "0".equals(openid)) {
			openid = user.getAliuserid();
			appid = user.getAliappid();
		}
		String url = getRequestUri(user) + String.format(WXLOGIN_URL, openid, appid, unionid);
		return (BaseResult<HexieUser>)httpGet(url,HexieUser.class);
	}
	
	/**
	 * 缴费记录查询
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<PayWaterListVO> queryPaymentList(User user, String startDate, String endDate){
		//total_count 和curr_page没有填
		if(startDate == null){
			startDate = "";
		}
		if(endDate == null){
			endDate = "";
		}
		String url = getRequestUri(user) + String.format(PAY_RECORD_URL, user.getWuyeId(), startDate, endDate);
		return (BaseResult<PayWaterListVO>)httpGet(url,PayWaterListVO.class);
	}
	
	/**
	 * 缴费详情
	 * @param userId
	 * @param waterId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<PaymentInfo> queryPaymentDetail(User user, String waterId){
		String url = getRequestUri(user) + String.format(PAY_INFO_URL, user.getWuyeId(), waterId);
		return (BaseResult<PaymentInfo>)httpGet(url,PaymentInfo.class);
	}
	
	/**
	 * 账单记录
	 * @param user
	 * @param payStatus
	 * @param startDate
	 * @param endDate
	 * @param currentPage
	 * @param totalCount
	 * @param house_id
	 * @param sect_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<BillListVO> queryBillList(User user,String payStatus,String startDate,String endDate, String currentPage, String totalCount, String house_id,String sect_id, String regionurl){
		
		if (StringUtils.isEmpty(regionurl)) {
			regionurl = getRequestUri(user);
		}
		String url = regionurl + String.format(BILL_LIST_URL,user.getWuyeId(),payStatus,startDate,endDate,currentPage,totalCount,house_id,sect_id);
		return (BaseResult<BillListVO>)httpGet(url,BillListVO.class);
	}
	
	/**

	 * 通知已支付
	 * @param user
	 * @param tradeWaterId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<PayResult> noticePayed(User user, String tradeWaterId) {
		String url = getRequestUri(user) + String.format(WX_PAY_NOTICE, user.getWuyeId(), tradeWaterId);
		return (BaseResult<PayResult>)httpGet(url,PayResult.class);
	}
	
	/**
	 * 红包使用情况查询
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<String> couponUseQuery(User user){
		
		String url = getRequestUri(user) + String.format(COUPON_USE_QUERY_URL, user.getWuyeId());
		return (BaseResult<String>)httpGet(url,String.class);
		
	}
	
	/**
	 * 更新电子发票抬头
	 * @param user
	 * @param invoice_title
	 * @param invoice_title_type
	 * @param credit_code
	 * @param trade_water_id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BaseResult<String> updateInvoice(String mobile, String invoice_title, String invoice_title_type, String credit_code, String trade_water_id, String openid) {

		try {
			User user = new User();
			invoice_title = URLEncoder.encode(invoice_title,"GBK");
			String url = getRequestUri(user) + String.format(APPLY_INVOICE_URL, mobile, invoice_title, invoice_title_type, credit_code, trade_water_id, openid);
			return (BaseResult<String>)httpGet(url,String.class);
		} catch (UnsupportedEncodingException e) {
			BaseResult r= new BaseResult();
			r.setResult("99");
			return r;
		}
	}
	
	/**
	 * 根据交易ID查询对应房屋的发票信息
	 * @param user
	 * @param trade_water_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<InvoiceInfo> getInvoiceInfo(String trade_water_id) {
		User user = new User();
		String url = getRequestUri(user) + String.format(INVOICE_INFO_TO_TRADE, trade_water_id);
		return (BaseResult<InvoiceInfo>)httpGet(url,InvoiceInfo.class);
	}
	
	/**
	 * 根据ID查询指定类型的合协社区物业信息
	 * @param user
	 * @param sect_id
	 * @param build_id
	 * @param unit_id
	 * @param data_type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<CellListVO> getMngHeXieList(User user, String sect_id, String build_id, String unit_id, String data_type, String regionurl) throws Exception{

		if (StringUtils.isEmpty(regionurl)) {
			regionurl = getRequestUri(user);
		}
		String url = regionurl + String.format(MNG_HEXIE_LIST_URL, sect_id,build_id,unit_id,data_type);
		return (BaseResult<CellListVO>)httpGet(url,CellListVO.class);
	}
	
	/**
	 * 根据交易ID查询涉及到的房屋
	 * @param user
	 * @param trade_water_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<String> getPayWaterToCell(User user, String trade_water_id) {
		String url = getRequestUri(user) + String.format(PAY_WATER_URL, user.getWuyeId(), trade_water_id);
		return (BaseResult<String>)httpGet(url, String.class);
	}
	
    /**
     * 会员支付
     * @param billId
     * @param totalPrice
     * @param openId
     * @param notifyUrl
     * @return
     * @throws Exception
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BaseResult<WechatPayInfo> getMemberPrePayInfo(User user, String billId, float totalPrice, String notifyUrl) throws Exception {

		String url = getRequestUri(user) + String.format(MEMBER_WX_PAY_URL, billId, user.getOpenid(), totalPrice, notifyUrl);
		BaseResult baseResult = httpGet(url,WechatPayInfo.class);
		return (BaseResult<WechatPayInfo>)baseResult;
	}
	
    /**
     * 查询会员支付订单信息
     * @param paymentNo
     * @return
     * @throws Exception
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BaseResult<WechatPayInfo> queryOrderInfo(User user, String paymentNo) throws Exception {

		String url = getRequestUri(user) + String.format(MEMBER_WX_Query_URL, paymentNo);
		BaseResult baseResult = httpGet(url,WechatPayInfo.class);
		return (BaseResult<WechatPayInfo>)baseResult;
	}
	
	/**
	 * 查询参数配置
	 * @param reqUrl
	 * @param c
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BaseResult<HexieConfig> queryServiceCfg(User user, String infoId, String type, String paraName) throws Exception {

		String url = getRequestUri(user) + String.format(SYNC_SERVICE_CFG_URL, infoId, type, paraName);
		BaseResult baseResult = httpGet(url, HexieConfig.class);
		return (BaseResult<HexieConfig>)baseResult;
	}
	
	/**
	 * 无账单获取缴费日期
	 * @param userid
	 * @param house_id
	 * @param regionurl
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<BillStartDate> getBillStartDateSDO(User user,String house_id,String regionurl) throws Exception{

		if (StringUtils.isEmpty(regionurl)) {
			regionurl = getRequestUri(user);
		}
		String url = regionurl + String.format(BILL_LIST_DATE, user.getWuyeId(), house_id);
		return (BaseResult<BillStartDate>)httpGet(url,BillStartDate.class);
	}
	
	/**
	 * 根据交易绑定房屋
	 * @param user
	 * @param tradeWaterId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HexieHouses> bindByTrade(User user, String tradeWaterId, String bindType) {
		
		String url = getRequestUri(user) + String.format(BIND_BY_TRADE_URL, user.getWuyeId(), tradeWaterId, user.getOpenid(), user.getAppId(), bindType, user.getTel());
		BaseResult<HexieHouses> baseResult = httpGet(url,HexieHouses.class);
		return baseResult;
	}
	
	/**
	 * 根据户号查询房屋信息
	 * @param userId
	 * @param stmtId
	 * @param house_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HexieHouse> getHouseByVerNo(User user, String verNo) {
		String url = getRequestUri(user) + String.format(GET_HOUSE_VERNO_URL, user.getWuyeId(), verNo);
		BaseResult<HexieHouse> baseResult = httpGet(url,HexieHouse.class);
		return baseResult;
	}
	
	/**
	 * get请求
	 * TODO 使用restTemplate替代
	 * @param reqUrl
	 * @param c
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static BaseResult httpGet(String reqUrl, Class c){
		
		HttpGet get = new HttpGet(reqUrl);
		get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
		String resp;
		
		String err_code = null;
		String err_msg = null;
		
		try {
			Log.error("REQ:" + reqUrl);
			resp = MyHttpClient.getStringFromResponse(MyHttpClient.execute(get),"GBK");

			if(reqUrl.indexOf("wechatPayRequestSDO.do")>=0) {
				resp = resp.replace("package", "packageValue");
				Map respMap = JacksonJsonUtil.json2map(resp);
				String result = (String)respMap.get("result");
				if (!"00".equals(result)) {
					err_msg = (String)respMap.get("err_msg");
					err_code = result;
					throw new BizValidateException(err_code+", " +err_msg);
				}
			}
			
			if(reqUrl.indexOf("otherWechatPayRequestSDO.do")>=0) {
				resp = resp.replace("package", "packageValue");
				Map respMap = JacksonJsonUtil.json2map(resp);
				String result = (String)respMap.get("result");
				if (!"00".equals(result)) {
					err_msg = (String)respMap.get("err_msg");
					err_code = result;
					throw new BizValidateException(err_code+", " +err_msg);
				}
			}
			
			if (reqUrl.indexOf("wechatPayQuerySDO.do")>=0) {
				Map respMap = JacksonJsonUtil.json2map(resp);
				String result = (String)respMap.get("result");
				if (!"00".equals(result)) {
					err_msg = (String)respMap.get("err_msg");
					err_code = result;
					throw new BizValidateException(err_code+", " +err_msg);
				}
			}
			if (reqUrl.indexOf("getInvoiceInfoSDO.do")>=0) {
				Map respMap = JacksonJsonUtil.json2map(resp);
				String result = (String)respMap.get("result");
				if (!"00".equals(result)) {
					err_msg = (String)respMap.get("err_msg");
					err_code = result;
					throw new BizValidateException(err_code+", " +err_msg);
				}
			}
			
			Log.error("RESP:" + resp);
			BaseResult v =jsonToBeanResult(resp, c);
			return v;
		} catch (Exception e) {
			Log.error("err msg :" + e.getMessage());
		}
		BaseResult r= new BaseResult();
		r.setResult("99");
		r.setData(err_msg);
		return r;
	}
	
	private static String getRequestUri(User user) {
		
		String userSysCode = SystemConfigServiceImpl.getSysMap().get(user.getAppId());
		RegionUrl regionUrl = LocationServiceImpl.getCodeUrlMap().get(userSysCode);
		String requestUri = SystemConfigServiceImpl.getREQUEST_URL();
		if (regionUrl!=null) {
			String urlLink = regionUrl.getRegionUrl();
			if (!StringUtils.isEmpty(urlLink)) {
				requestUri = urlLink;
			}
		}
		return requestUri;
	}
	
	/**
	 * json 转  bean
	 * @param jsonStr
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static BaseResult jsonToBeanResult(String jsonStr,Class type) throws Exception{  
		JavaType javaType = JacksonJsonUtil.getCollectionType(BaseResult.class, type);
		return JacksonJsonUtil.getMapperInstance(false).readValue(jsonStr, javaType);
	}

}
