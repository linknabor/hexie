package com.yumu.hexie.integration.wuye;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.ValidationException;

import org.apache.http.client.methods.HttpGet;
import org.hibernate.bytecode.buildtime.spi.ExecutionException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.yumu.hexie.common.util.HttpUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.MyHttpClient;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.resp.CellListVO;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.resp.PayWaterListVO;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceInfo;
import com.yumu.hexie.integration.wuye.vo.PayResult;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;

public class WuyeUtil {
	private static final Logger log = LoggerFactory.getLogger(WuyeUtil.class);

	private static String REQUEST_ADDRESS = "http://www.e-shequ.com/mobileInterface/mobile/";
	private static String SYSTEM_NAME;
	private static Properties props = new Properties();
	
	static {
		try {
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("wechat.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		REQUEST_ADDRESS = props.getProperty("requestUrl");
		SYSTEM_NAME = props.getProperty("sysName");
	}

	// 接口地址
	private static final String HOUSE_DETAIL_URL = "getHoseInfoSDO.do?user_id=%s"; // 房屋详情地址
	private static final String ADD_HOUSE_URL = "addHouseSDO.do?user_id=%s&stmt_id=%s&mng_cell_id=%s"; // 添加房子
	private static final String ADD_HOUSENOSTMT_URL = "addHouseNoStmtSDO.do?user_id=%s&mng_cell_id=%s&area=%s"; // 无账单添加房子
	private static final String SYS_ADD_HOUSE_URL = "billSaveHoseSDO.do?user_id=%s&stmt_id=%s"; // 扫一扫（添加房子）
	private static final String DEL_HOUSE_URL = "delHouseSDO.do?user_id=%s&mng_cell_id=%s"; // 删除房子
	private static final String BILL_LIST_URL = "getBillListMSDO.do?user_id=%s&pay_status=%s&startDate=%s&endDate=%s&curr_page=%s&total_count=%s&house_id=%s"; // 获取账单列表
	private static final String BILL_DETAIL_URL = "getBillInfoMSDO.do?user_id=%s&stmt_id=%s&bill_id=%s"; // 获取账单详情
	private static final String PAY_RECORD_URL = "payMentRecordSDO.do?user_id=%s&startDate=%s&endDate=%s"; // 获取支付记录列表
	private static final String PAY_INFO_URL = "payMentRecordInfoSDO.do?user_id=%s&trade_water_id=%s"; // 获取支付记录详情
	private static final String QUICK_PAY_URL = "quickPaySDO.do?stmt_id=%s&curr_page=%s&total_count=%s"; // 快捷支付
	private static final String WXLOGIN_URL = "weixinLoginSDO.do?weixin_id=%s"; // 登录验证（微信登录）
	private static final String WX_PAY_URL = "wechatPayRequestSDO.do?user_id=%s&bill_id=%s&stmt_id=%s&openid=%s&coupon_unit=%s&coupon_num=%s"
			+ "&coupon_id=%s&from_sys=%s&mianBill=%s&mianAmt=%s&reduceAmt=%s&invoice_title_type=%s&credit_code=%s&mobile=%s&invoice_title=%s"; // 微信支付请求
	private static final String WX_PAY_NOTICE = "wechatPayQuerySDO.do?user_id=%s&bill_id=%s&stmt_id=%s&trade_water_id=%s&package=%s"; // 微信支付返回
	//private static final String GET_LOCATION_URL = "getGeographicalPositionSDO.do"; // 用户地理位置
	private static final String COUPON_USE_QUERY_URL = "conponUseQuerySDO.do?user_id=%s";
	private static final String APPLY_INVOICE_URL = "applyInvoiceSDO.do?mobile=%s&invoice_title=%s&invoice_title_type=%s&credit_code=%s&trade_water_id=%s";
	private static final String INVOICE_INFO_TO_TRADE = "getInvoiceInfoSDO.do?trade_water_id=%s";
	private static final String MNG_HEXIE_LIST_URL = "queryHeXieMngByIdSDO.do"+ "?sect_id=%s&build_id=%s&unit_id=%s&data_type=%s";//合协社区物业缴费的小区级联
	private static final String SECT_VAGUE_LIST_URL = "queryVagueSectByNameSDO.do"+ "?sect_name=%s";//合协社区物业缴费的小区级联 模糊查询小区
	
	public static BaseResult<BillListVO> quickPayInfo(String stmtId, String currPage, String totalCount) {
		String url = REQUEST_ADDRESS + String.format(QUICK_PAY_URL, stmtId, currPage, totalCount);
		return (BaseResult<BillListVO>)httpGet(url,BillListVO.class);
	}
	
	// 1.房产列表
	public static BaseResult<HouseListVO> queryHouse(String userId){
		String url = REQUEST_ADDRESS + String.format(HOUSE_DETAIL_URL, userId);
		return (BaseResult<HouseListVO>)httpGet(url,HouseListVO.class);
	}
	
	// 2.绑定房产
	public static BaseResult<HexieUser> bindHouse(String userId,String stmtId,String houseId) {
		String url = REQUEST_ADDRESS + String.format(ADD_HOUSE_URL, userId,stmtId,houseId);
		log.error("【绑定房产url】="+url);
		return (BaseResult<HexieUser>)httpGet(url,HexieUser.class);
	}
	//无账单绑定房产
	public static BaseResult<HexieUser> bindHouseNoStmt(String userId,String houseId, String area) {
		String url = REQUEST_ADDRESS + String.format(ADD_HOUSENOSTMT_URL, userId,houseId,area);
		log.error("【绑定房产url】="+url);
		return (BaseResult<HexieUser>)httpGet(url,HexieUser.class);
	}
	// 3.删除房产
	public static BaseResult<String> deleteHouse(String userId,String houseId) {
		String url = REQUEST_ADDRESS + String.format(DEL_HOUSE_URL, userId,houseId);
		return (BaseResult<String>)httpGet(url,String.class);
	}
	
	// 4.根据订单查询房产信息
	public static BaseResult<HexieHouse> getHouse(String userId,String stmtId) {
		String url = REQUEST_ADDRESS + String.format(SYS_ADD_HOUSE_URL, userId,stmtId);
		return (BaseResult<HexieHouse>)httpGet(url,HexieHouse.class);
	}

	// 5.用户登录
	public static BaseResult<HexieUser> userLogin(String openId) {
		String url = REQUEST_ADDRESS + String.format(WXLOGIN_URL, openId);
		return (BaseResult<HexieUser>)httpGet(url,HexieUser.class);
	}
	// 6.缴费记录查询
	public static BaseResult<PayWaterListVO> queryPaymentList(String userId,String startDate,String endDate){
		//total_count 和curr_page没有填
		if(startDate == null){
			startDate = "";
		}
		if(endDate == null){
			endDate = "";
		}
		String url = REQUEST_ADDRESS + String.format(PAY_RECORD_URL, userId,startDate,endDate);
		return (BaseResult<PayWaterListVO>)httpGet(url,PayWaterListVO.class);
	}
	// 7.缴费详情
	public static BaseResult<PaymentInfo> queryPaymentDetail(String userId,String waterId){
		String url = REQUEST_ADDRESS + String.format(PAY_INFO_URL, userId,waterId);
		return (BaseResult<PaymentInfo>)httpGet(url,PaymentInfo.class);
	}
	
	//status 00,01,02? startDate 2015-02
	// 8.账单记录
	public static BaseResult<BillListVO> queryBillList(String userId,String payStatus,String startDate,String endDate, String currentPage, String totalCount, String house_id){
		//total_count 和curr_page没有填
		String url = REQUEST_ADDRESS + String.format(BILL_LIST_URL, userId,payStatus,startDate,endDate,currentPage,totalCount,house_id);
		return (BaseResult<BillListVO>)httpGet(url,BillListVO.class);
	}
	// 9.账单详情 anotherbillIds(逗号分隔) 汇总了去支付,来自BillInfo的bill_id
	public static BaseResult<PaymentInfo> getBillDetail(String userId,String stmtId,String anotherbillIds){
		String url = REQUEST_ADDRESS + String.format(BILL_DETAIL_URL, userId,stmtId,anotherbillIds);
		return (BaseResult<PaymentInfo>)httpGet(url,PaymentInfo.class);
	}
	// 10.缴费
	public static BaseResult<WechatPayInfo> getPrePayInfo(String userId,String billId,String stmtId,String openId,
		String couponUnit, String couponNum, String couponId,String mianBill,String mianAmt, String reduceAmt,
		String invoice_title_type, String credit_code, String mobile, String invoice_title) throws Exception {
		invoice_title = URLEncoder.encode(invoice_title,"GBK");
		String url = REQUEST_ADDRESS + String.format(WX_PAY_URL, userId,billId,stmtId,openId,
					couponUnit,couponNum,couponId,SYSTEM_NAME,mianBill, mianAmt, reduceAmt, invoice_title_type, credit_code, mobile, invoice_title);
	
		BaseResult baseResult = httpGet(url,WechatPayInfo.class);
		if (!baseResult.isSuccess()) {
			throw new ValidationException(baseResult.getData().toString());
		}
		return (BaseResult<WechatPayInfo>)baseResult;
	}
	
	// 11.通知已支付
	public static BaseResult<PayResult> noticePayed(String userId,String billId,String stmtId, String tradeWaterId, String packageId) {
		String url = REQUEST_ADDRESS + String.format(WX_PAY_NOTICE, userId,billId,stmtId, tradeWaterId, packageId);
		return (BaseResult<PayResult>)httpGet(url,PayResult.class);
	}
	
	// 12.红包使用情况查询
	public static BaseResult<String> couponUseQuery(String userId){
		
		String url = REQUEST_ADDRESS + String.format(COUPON_USE_QUERY_URL, userId);
		return (BaseResult<String>)httpGet(url,String.class);
		
	}
	
	// 13.更新电子发票抬头
	public static BaseResult<String> updateInvoice(String mobile, String invoice_title, String invoice_title_type, String credit_code, String trade_water_id) {
		try {
			invoice_title = URLEncoder.encode(invoice_title,"GBK");
			String url = REQUEST_ADDRESS + String.format(APPLY_INVOICE_URL, mobile, invoice_title, invoice_title_type, credit_code, trade_water_id);
			return (BaseResult<String>)httpGet(url,String.class);
		} catch (UnsupportedEncodingException e) {
			BaseResult r= new BaseResult();
			r.setResult("99");
			return r;
		}
	}
	
	// 14.根据交易ID查询对应房屋的发票信息
	public static BaseResult<InvoiceInfo> getInvoiceInfo(String trade_water_id)
	{
		String url = REQUEST_ADDRESS + String.format(INVOICE_INFO_TO_TRADE, trade_water_id);
		return (BaseResult<InvoiceInfo>)httpGet(url,InvoiceInfo.class);
	}
	
	//15.根据ID查询指定类型的合协社区物业信息
	public static BaseResult<CellListVO> getMngHeXieList(String sect_id, String build_id, String unit_id, String data_type) throws Exception{
//		//中文打码
//		sect_name = URLEncoder.encode(sect_name, "gbk");
//		Map<String, String>map = new HashMap<String, String>();
//		map.put("sect_name", sect_name);
//		map.put("build_id", build_id);
//		map.put("unit_id", unit_id);
//		map.put("data_type", data_type);
//		String response = "";
//		//请求
//		response = HttpUtil.doPostMap(REQUEST_ADDRESS+MNG_HEXIE_LIST_URL, map, "gbk");
//		BaseResult v = new BaseResult<CellListVO>();
//		try {
//			v =jsonToBeanResult(response, CellListVO.class);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return v;

		String url = REQUEST_ADDRESS + String.format(MNG_HEXIE_LIST_URL, sect_id,build_id,unit_id,data_type);
		log.error("【url】:"+url);
		return (BaseResult<CellListVO>)httpGet(url,CellListVO.class);
	}
	
	//20.根据名称模糊查询合协社区小区列表
	public static BaseResult<CellListVO> getVagueSectByName(String sect_name) throws Exception{
//		log.error("ceshi2");
//		log.error("123:"+REQUEST_ADDRESS+SECT_VAGUE_LIST_URL);
//		//中文打码
//		String nname = sect_name;
		//sect_name = URLEncoder.encode(sect_name, "gbk");

//		Map<String, String>map = new HashMap<String, String>();
//		map.put("sect_name", sect_name);
//		String response = HttpUtil.doPostMap(REQUEST_ADDRESS+SECT_VAGUE_LIST_URL+"?sect_name="+nname, null, "gbk");
//		log.error("【response】:"+response);
//		return jsonToBeanResult(response, CellListVO.class);
		
		String url = REQUEST_ADDRESS + String.format(SECT_VAGUE_LIST_URL, sect_name);
		log.error("【url】:"+url);
		return (BaseResult<CellListVO>)httpGet(url,CellListVO.class);
	}
	
	
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
			e.printStackTrace();
			Log.error("err msg :" + e.getMessage());
		}
		BaseResult r= new BaseResult();
		r.setResult("99");
		r.setData(err_msg);
		return r;
	}
	
	public static BaseResult jsonToBeanResult(String jsonStr,Class type) throws Exception{  
		JavaType javaType = JacksonJsonUtil.getCollectionType(BaseResult.class, type);
		return JacksonJsonUtil.getMapperInstance(false).readValue(jsonStr, javaType);
	}


	private static final Logger Log = LoggerFactory.getLogger(WuyeUtil.class);
	
	public static void main(String args[]) throws JSONException {
		String resp = "{\"result\":\"00\",\"data\":{\"trade_water_id\":\"20160112175644955015\",\"merger_status\":\"02\",\"package\":\"wx20160112175645c1930803540408946371\"}}";

		try {
			BaseResult v =jsonToBeanResult(resp, PayResult.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
