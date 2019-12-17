package com.yumu.hexie.integration.wuye;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.ValidationException;

import org.apache.http.client.methods.HttpGet;
import org.hibernate.bytecode.buildtime.spi.ExecutionException;
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
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;

public class WuyeUtil {
	private static final Logger log = LoggerFactory.getLogger(WuyeUtil.class);

	private static String REQUEST_ADDRESS;
	private static String REQUEST_ADDRESS_GZ;
	private static String SYSTEM_NAME;
	private static Properties props = new Properties();
	
	static {
		try {
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("wechat.properties"));
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		REQUEST_ADDRESS = props.getProperty("requestUrl");
		REQUEST_ADDRESS_GZ = props.getProperty("requestUrlGz");
		SYSTEM_NAME = props.getProperty("sysName");
	}

	// 接口地址
	private static final String HOUSE_DETAIL_URL = "getHoseInfoSDO.do?user_id=%s"; // 房屋详情地址
	private static final String ADD_HOUSE_URL = "addHouseSDO.do?user_id=%s&stmt_id=%s&mng_cell_id=%s"; // 添加房子
	private static final String ADD_HOUSENOSTMT_URL = "addHouseNoStmtSDO.do?user_id=%s&mng_cell_id=%s&area=%s"; // 无账单添加房子
	private static final String SYS_ADD_HOUSE_URL = "billSaveHoseSDO.do?user_id=%s&stmt_id=%s&house_id=%s"; // 扫一扫（添加房子）
	private static final String DEL_HOUSE_URL = "delHouseSDO.do?user_id=%s&mng_cell_id=%s"; // 删除房子
	private static final String BILL_LIST_URL = "getBillListMSDO.do?user_id=%s&pay_status=%s&startDate=%s&endDate=%s&curr_page=%s&total_count=%s&house_id=%s&sect_id=%s"; // 获取账单列表
	private static final String BILL_LIST_STD_URL = "getPayListStdSDO.do?user_id=%s&start_date=%s&end_date=%s&mng_cell_id=%s&sect_id=%s"; // 获取账单列表
	private static final String BILL_DETAIL_URL = "getBillInfoMSDO.do?user_id=%s&stmt_id=%s&bill_id=%s"; // 获取账单详情
	private static final String PAY_RECORD_URL = "payMentRecordSDO.do?user_id=%s&startDate=%s&endDate=%s"; // 获取支付记录列表
	private static final String PAY_INFO_URL = "payMentRecordInfoSDO.do?user_id=%s&trade_water_id=%s"; // 获取支付记录详情
	private static final String QUICK_PAY_URL = "quickPaySDO.do?stmt_id=%s&curr_page=%s&total_count=%s"; // 快捷支付
	private static final String WXLOGIN_URL = "weixinLoginSDO.do?weixin_id=%s"; // 登录验证（微信登录）
	private static final String WX_PAY_URL = "wechatPayRequestSDO.do?user_id=%s&bill_id=%s&stmt_id=%s&openid=%s&coupon_unit=%s&coupon_num=%s"
			+ "&coupon_id=%s&from_sys=%s&mianBill=%s&mianAmt=%s&reduceAmt=%s&fee_mianBill=%s&fee_mianAmt=%s&invoice_title_type=%s&credit_code=%s&mobile=%s&invoice_title=%s"; // 微信支付请求
	private static final String OTHER_WX_PAY_URL = "otherWechatPayRequestSDO.do?user_id=%s&mng_cell_id=%s&start_date=%s&end_date=%s&openid=%s&coupon_unit=%s&coupon_num=%s"
			+ "&coupon_id=%s&from_sys=%s&mianBill=%s&mianAmt=%s&reduceAmt=%s&invoice_title_type=%s&credit_code=%s&mobile=%s&invoice_title=%s"; // 微信支付请求
	private static final String MEMBER_WX_PAY_URL = "member/memberPayRequestSDO.do?bill_id=%s&openid=%s&totalPrice=%s&notifyUrl=%s"; // 微信支付请求
	private static final String MEMBER_WX_Query_URL = "member/memberQueryOrderSDO.do?bill_id=%s"; // 微信支付查询请求
	private static final String WX_PAY_NOTICE = "wechatPayQuerySDO.do?user_id=%s&trade_water_id=%s"; // 微信支付返回
	//private static final String GET_LOCATION_URL = "getGeographicalPositionSDO.do"; // 用户地理位置
	private static final String COUPON_USE_QUERY_URL = "conponUseQuerySDO.do?user_id=%s";
	private static final String APPLY_INVOICE_URL = "applyInvoiceSDO.do?mobile=%s&invoice_title=%s&invoice_title_type=%s&credit_code=%s&trade_water_id=%s";
	private static final String INVOICE_INFO_TO_TRADE = "getInvoiceInfoSDO.do?trade_water_id=%s";
	private static final String MNG_HEXIE_LIST_URL = "queryHeXieMngByIdSDO.do"+ "?sect_id=%s&build_id=%s&unit_id=%s&data_type=%s";//合协社区物业缴费的小区级联
	private static final String SECT_VAGUE_LIST_URL = "queryVagueSectByNameSDO.do"+ "?sect_name=%s";//合协社区物业缴费的小区级联 模糊查询小区
	private static final String SYNC_SERVICE_CFG_URL = "param/getParamSDO.do?info_id=%s&type=%s&para_name=%s";
	private static final String BILL_LIST_DATE = "getBillStartDateSDO.do?user_id=%s&mng_cell_id=%s";//获取无账单日期
	private static final String PAY_WATER_URL = "getMngCellByTradeIdSDO.do?user_id=%s&trade_water_id=%s"; // 获取支付记录涉及的房屋
	private static final String BIND_BY_TRADE_URL = "bindHouseByTradeIdSDO.do?user_id=%s&trade_water_id=%s";
	
	private static final Logger Log = LoggerFactory.getLogger(WuyeUtil.class);
	
	/**
	 * 账单快捷缴费
	 * @param user
	 * @param stmtId
	 * @param currPage
	 * @param totalCount
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<BillListVO> quickPayInfo(User user, String stmtId, String currPage, String totalCount) {
		String url = getRequestUri(user) + String.format(QUICK_PAY_URL, stmtId, currPage, totalCount);
		return (BaseResult<BillListVO>)httpGet(url,BillListVO.class);
	}
	
	/**
	 * 查询房屋列表
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HouseListVO> queryHouse(User user){
		String url = getRequestUri(user) + String.format(HOUSE_DETAIL_URL, user.getWuyeId());
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
		String url = getRequestUri(user) + String.format(ADD_HOUSE_URL,user.getWuyeId(),stmtId,houseId);
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
	 * 无账单绑定房产
	 * @param user
	 * @param houseId
	 * @param area
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<HexieUser> bindHouseNoStmt(User user, String houseId, String area) {
		String url = getRequestUri(user) + String.format(ADD_HOUSENOSTMT_URL, user.getWuyeId(), houseId, area);
		log.error("【绑定房产url】="+url);
		return (BaseResult<HexieUser>)httpGet(url,HexieUser.class);
	}
	
	/**
	 * 删除房产
	 * @param user
	 * @param houseId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<String> deleteHouse(User user, String houseId) {
		String url = getRequestUri(user) + String.format(DEL_HOUSE_URL, user.getWuyeId(), houseId);
		return (BaseResult<String>)httpGet(url,String.class);
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
		String url = getRequestUri(user) + String.format(WXLOGIN_URL, user.getOpenid());
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
	 * 无账单查询账单
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param house_id
	 * @param sect_id
	 * @param regionurl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<BillListVO> queryBillList(User user, String startDate, String endDate, String house_id,String sect_id,String regionurl){
		log.info("startDate:"+startDate+", endDate"+endDate);
		if (StringUtils.isEmpty(regionurl)) {
			regionurl = getRequestUri(user);
		}
		String url = regionurl + String.format(BILL_LIST_STD_URL, user.getWuyeId(),startDate,endDate,house_id,sect_id);
		return (BaseResult<BillListVO>)httpGet(url,BillListVO.class);
	}
	
	/**
	 * 账单详情 anotherbillIds(逗号分隔) 汇总了去支付,来自BillInfo的bill_id
	 * @param user
	 * @param stmtId
	 * @param anotherbillIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<PaymentInfo> getBillDetail(User user,String stmtId,String anotherbillIds, String regionurl){
		
		if (StringUtils.isEmpty(regionurl)) {
			regionurl = getRequestUri(user);
		}
		String url = getRequestUri(user) + String.format(BILL_DETAIL_URL,user.getWuyeId(),stmtId,anotherbillIds);
		return (BaseResult<PaymentInfo>)httpGet(url,PaymentInfo.class);
	}
	
	/**
	 * 缴费
	 * @param user
	 * @param billId
	 * @param stmtId
	 * @param couponUnit
	 * @param couponNum
	 * @param couponId
	 * @param mianBill
	 * @param mianAmt
	 * @param reduceAmt
	 * @param invoice_title_type
	 * @param credit_code
	 * @param invoice_title
	 * @param regionurl
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BaseResult<WechatPayInfo> getPrePayInfo(User user,String billId,String stmtId,
		String couponUnit, String couponNum, String couponId,String mianBill,String mianAmt, String reduceAmt,String fee_mianBill,String fee_mianAmt,
		String invoice_title_type, String credit_code, String invoice_title,String regionurl) throws Exception {
		
		String appid = user.getAppId();
		String fromSys = SYSTEM_NAME;
		if (StringUtils.isEmpty(appid)) {
			//do nothing
		}else {
			fromSys = SystemConfigServiceImpl.getSysMap().get(appid);
		}
		
		if (StringUtils.isEmpty(regionurl)) {
			regionurl = getRequestUri(user);
		}
		
		invoice_title = URLEncoder.encode(invoice_title,"GBK");
		String url = regionurl + String.format(WX_PAY_URL, user.getWuyeId(),billId,stmtId,user.getOpenid(),
					couponUnit,couponNum,couponId,fromSys,mianBill, mianAmt, reduceAmt,fee_mianBill,fee_mianAmt, invoice_title_type, credit_code, user.getTel(), invoice_title);
	
		BaseResult baseResult = httpGet(url,WechatPayInfo.class);
		if (!baseResult.isSuccess()) {
			throw new ValidationException(baseResult.getData().toString());
		}
		return (BaseResult<WechatPayInfo>)baseResult;
	}
	
	/**
	 * 无账单缴费
	 * @param user
	 * @param houseId
	 * @param start_date
	 * @param end_date
	 * @param couponUnit
	 * @param couponNum
	 * @param couponId
	 * @param mianBill
	 * @param mianAmt
	 * @param reduceAmt
	 * @param invoice_title_type
	 * @param credit_code
	 * @param invoice_title
	 * @param regionurl
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BaseResult<WechatPayInfo> getOtherPrePayInfo(User user,String houseId,String start_date,String end_date,
		String couponUnit, String couponNum, String couponId,String mianBill,String mianAmt, String reduceAmt,
		String invoice_title_type, String credit_code, String invoice_title,String regionurl) throws Exception {
		invoice_title = URLEncoder.encode(invoice_title,"GBK");
		
		String appid = user.getAppId();
		String fromSys = SYSTEM_NAME;
		if (StringUtils.isEmpty(appid)) {
			//do nothing
		}else {
			fromSys = SystemConfigServiceImpl.getSysMap().get(appid);
		}
		
		if (StringUtils.isEmpty(regionurl)) {
			regionurl = getRequestUri(user);
		}
		
		String url = regionurl + String.format(OTHER_WX_PAY_URL, user.getWuyeId(),houseId,start_date,end_date,user.getOpenid(),
					couponUnit,couponNum,couponId,fromSys,mianBill, mianAmt, reduceAmt, invoice_title_type, credit_code, user.getTel(), invoice_title);
	
		BaseResult baseResult = httpGet(url,WechatPayInfo.class);
		if (!baseResult.isSuccess()) {
			throw new ValidationException(baseResult.getData().toString());
		}
		return (BaseResult<WechatPayInfo>)baseResult;
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
	public static BaseResult<String> updateInvoice(String mobile, String invoice_title, String invoice_title_type, String credit_code, String trade_water_id) {

		try {
			User user = new User();
			invoice_title = URLEncoder.encode(invoice_title,"GBK");
			String url = getRequestUri(user) + String.format(APPLY_INVOICE_URL, mobile, invoice_title, invoice_title_type, credit_code, trade_water_id);
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
	 * 根据名称模糊查询合协社区小区列表
	 * @param sect_name
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static BaseResult<CellListVO> getVagueSectByName(User user, String sect_name, String regionurl) throws Exception{
		
		if (StringUtils.isEmpty(regionurl)) {
			regionurl = getRequestUri(user);
		}
		sect_name = URLEncoder.encode(sect_name,"GBK");
		String url = regionurl + String.format(SECT_VAGUE_LIST_URL, sect_name);
		log.info("【url】:"+url);
		return (BaseResult<CellListVO>)httpGet(url,CellListVO.class);
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
	public static BaseResult<HexieHouses> bindByTrade(User user, String tradeWaterId) {
		
		String url = getRequestUri(user) + String.format(BIND_BY_TRADE_URL, user.getWuyeId(), tradeWaterId);
		BaseResult<HexieHouses> baseResult = httpGet(url,HexieHouses.class);
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
					throw new ExecutionException(err_code+", " +err_msg);
				}
			}
			
			if(reqUrl.indexOf("otherWechatPayRequestSDO.do")>=0) {
				resp = resp.replace("package", "packageValue");
				Map respMap = JacksonJsonUtil.json2map(resp);
				String result = (String)respMap.get("result");
				if (!"00".equals(result)) {
					err_msg = (String)respMap.get("err_msg");
					err_code = result;
					throw new ExecutionException(err_code+", " +err_msg);
				}
			}
			
			if (reqUrl.indexOf("wechatPayQuerySDO.do")>=0) {
				Map respMap = JacksonJsonUtil.json2map(resp);
				String result = (String)respMap.get("result");
				if (!"00".equals(result)) {
					err_msg = (String)respMap.get("err_msg");
					err_code = result;
					throw new ExecutionException(err_code+", " +err_msg);
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
		
		String appId = user.getAppId();
		String requestUri = REQUEST_ADDRESS;
		if ("_guizhou".equals(SystemConfigServiceImpl.getSysMap().get(appId))) {
			requestUri = REQUEST_ADDRESS_GZ;
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
