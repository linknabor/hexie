package com.yumu.hexie.integration.wuye;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.wuye.dto.DiscountViewRequestDTO;
import com.yumu.hexie.integration.wuye.dto.GetCellDTO;
import com.yumu.hexie.integration.wuye.dto.OtherPayDTO;
import com.yumu.hexie.integration.wuye.dto.PrepayRequestDTO;
import com.yumu.hexie.integration.wuye.dto.SignInOutDTO;
import com.yumu.hexie.integration.wuye.req.BillDetailRequest;
import com.yumu.hexie.integration.wuye.req.BillStdRequest;
import com.yumu.hexie.integration.wuye.req.DiscountViewRequest;
import com.yumu.hexie.integration.wuye.req.GetCellRequest;
import com.yumu.hexie.integration.wuye.req.MessageRequest;
import com.yumu.hexie.integration.wuye.req.OtherPayRequest;
import com.yumu.hexie.integration.wuye.req.PaySmsCodeRequest;
import com.yumu.hexie.integration.wuye.req.PrepayRequest;
import com.yumu.hexie.integration.wuye.req.QrCodePayServiceRequest;
import com.yumu.hexie.integration.wuye.req.QrCodeRequest;
import com.yumu.hexie.integration.wuye.req.QueryAlipayConsultRequest;
import com.yumu.hexie.integration.wuye.req.QueryCellRequest;
import com.yumu.hexie.integration.wuye.req.QueryEReceiptRequest;
import com.yumu.hexie.integration.wuye.req.QueryOrderRequest;
import com.yumu.hexie.integration.wuye.req.QuerySectRequet;
import com.yumu.hexie.integration.wuye.req.QuickPayRequest;
import com.yumu.hexie.integration.wuye.req.SignInOutRequest;
import com.yumu.hexie.integration.wuye.req.WuyeParamRequest;
import com.yumu.hexie.integration.wuye.resp.AlipayMarketingConsult;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.resp.CellListVO;
import com.yumu.hexie.integration.wuye.resp.MpQrCodeParam;
import com.yumu.hexie.integration.wuye.resp.RadiusSect;
import com.yumu.hexie.integration.wuye.resp.UserAccessSpotResp;
import com.yumu.hexie.integration.wuye.vo.Discounts;
import com.yumu.hexie.integration.wuye.vo.EReceipt;
import com.yumu.hexie.integration.wuye.vo.HexieConfig;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceDetail;
import com.yumu.hexie.integration.wuye.vo.Message;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.QrCodePayService;
import com.yumu.hexie.integration.wuye.vo.ReceiptInfo;
import com.yumu.hexie.integration.wuye.vo.ReceiptInfo.Receipt;
import com.yumu.hexie.integration.wuye.vo.SectInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.BankCard;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;
import com.yumu.hexie.service.common.pojo.dto.ActiveApp;
import com.yumu.hexie.service.shequ.req.RadiusSectReq;
import com.yumu.hexie.service.shequ.req.ReceiptApplicationReq;
import com.yumu.hexie.service.shequ.req.UserAccessRecordReq;
import com.yumu.hexie.service.user.dto.H5UserDTO;
import com.yumu.hexie.vo.req.MessageReq;
import com.yumu.hexie.vo.req.QueryFeeSmsBillReq;

/**
 * 新的WuyeUtil
 * http的请求都改用restTemplate，旧的WuyeUtil中的方法，会慢慢代替掉,最后全部去掉旧版本的httpclient
 * @author david
 *
 */
@Service
public class WuyeUtil2 {
	
	@Value("${sysName}")
	private String sysName;
	@Autowired
	private RestUtil restUtil;
	@Autowired
	private RequestUtil requestUtil;
	@Autowired
	private SystemConfigService systemConfigService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	private static final String QUICK_PAY_URL = "quickPaySDO.do"; // 快捷支付
	private static final String WX_PAY_URL = "wechatPayRequestSDO.do"; // 微信支付请求
	private static final String SMS_PAY_URL = "smsPayRequestSDO.do"; // 微信支付请求
	private static final String DISCOUNT_URL = "getBillPayDetailSDO.do";	//获取优惠明细
	private static final String CARD_PAY_SMS_URL = "getCardPaySmsCodeSDO.do";	//获取优惠明细
	private static final String QUERY_ORDER_URL = "queryOrderSDO.do";	//订单查询
	private static final String BILL_DETAIL_URL = "getBillInfoMSDO.do"; // 获取账单详情
	private static final String BILL_LIST_STD_URL = "getPayListStdSDO.do"; // 获取账单列表
	private static final String OTHER_PAY_URL = "otherPaySDO.do";	//其他收入支付
	private static final String QRCODE_PAY_SERVICE_URL = "getServiceSDO.do";	//二维码支付服务信息
	private static final String QRCODE_URL = "getQRCodeSDO.do";	//二维码支付服务信息
	private static final String SIGN_IN_OUT_URL = "signInSDO.do";	//二维码支付服务信息
	private static final String MNG_HEXIE_LIST_URL = "queryHeXieMngByIdSDO.do"; //合协社区物业缴费的小区级联
	private static final String SYNC_SERVICE_CFG_URL = "param/getParamSDO.do";	//物业参数
	private static final String E_RECEIPT_URL = "getEReceiptSDO.do";	//电子凭证
	private static final String MESSAGE_URL = "msg/sendMessageSDO.do";
	private static final String QUERY_MESSAGE_URL = "msg/getMessageSDO.do";
	private static final String QUERY_MESSAGE_HISTORY_URL = "msg/sendHistorySDO.do";
	private static final String QUERY_CELL_ADDR_URL = "queryCellAddrSDO.do";
	private static final String SECT_VAGUE_LIST_URL = "queryVagueSectByNameSDO.do";//合协社区物业缴费的小区级联 模糊查询小区
	private static final String QUERY_MPQRCODE_PARAM_URL = "queryMpQrCodeParamSDO.do";//获取生成公众号动态二维码的必要参数
	private static final String QUERY_USER_INVOICE_URL = "queryInvoiceByUserSDO.do";	//获取用户申请过的发票 
	private static final String QUERY_FEE_SMS_BILL_URL = "getFeeSmsBillSDO.do";	//获取催缴短信中的欠费账单
	private static final String FEE_SMS_PAY_QRCODE = "getSmsPayQrCodeSDO.do";	//获取催缴短信二维码
	private static final String APPLY_RECEIPT_URL = "receipt/allpyReceiptSDO.do";
	private static final String QUERY_RECEIPT_URL = "receipt/getReceiptSDO.do";
	private static final String QUERY_RECEIPT_LIST_URL = "receipt/getReceiptByUserSDO.do";
	private static final String PUSH_USER_REGISTER_URL = "pushUserRegisterSDO.do";
	private static final String H5_USER_LOGIN_URL = "alipayH5LoginSDO.do";	//h5用户登陆注册
	private static final String QUERY_TRADE_INVOICE_URL = "queryInvoiceByTradeSDO.do";	//获取用户申请过的发票
	private static final String NEWLION_USER_BIND_URL = "bindHouse4NewLionUserSDO.do";	//新郎恩存量用户绑定
	private static final String ADD_HOUSENOSTMT_URL = "addHouseNoStmtSDO.do"; // 无账单添加房子
	private static final String QUERY_HOUSE_BY_ID_URL = "getHouseByIdSDO.do";	//根据房屋ID查询业主已经绑定的房屋信息
	private static final String QUERY_SECT_BY_ID_URL = "getSectByIdSDO.do";	//获取小区信息
	private static final String QUERY_SECT_NEARBY = "getSectNearbySDO.do";	//获取附近的小区
	private static final String QUERY_MARKETING_CONSULT = "alipay/getMarketingConsultSDO.do";	//获取支付宝优惠资讯
	private static final String QUERY_ACCESS_SPOT = "useraccess/getSpotSDO.do";	//获取门禁点信息
	private static final String SAVE_USER_ACCESS_RECORD = "useraccess/saveRecordSDO.do";	//保存用户门禁登记记录
	private static final String CHUNCHUAN_USER_BIND_URL = "bindHouse4ChunchuanUserSDO.do";	//春川存量用户绑定


	private static final String QUERY_PAY_MAPPING = "getPayMappingSDO.do";	//获取扫二维码缴管理费时，码上带的参数对照

	/**
	 * 标准版查询账单
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @param houseId
	 * @param regionName
	 * @return
	 * @throws Exception
	 */
	public BaseResult<BillListVO> queryBillList(User user, String startDate, String endDate, String houseId, String regionName) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, regionName);
		requestUrl += BILL_LIST_STD_URL;
		
		BillStdRequest billStdRequest = new BillStdRequest();
		billStdRequest.setWuyeId(user.getWuyeId());
		billStdRequest.setHouseId(houseId);
		billStdRequest.setStartDate(startDate);
		billStdRequest.setEndDate(endDate);
		
		TypeReference<CommonResponse<BillListVO>> typeReference = new TypeReference<CommonResponse<BillListVO>>(){};
		CommonResponse<BillListVO> hexieResponse = restUtil.exchangeOnUri(requestUrl, billStdRequest, typeReference);
		BaseResult<BillListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	
	
	/**
	 * 账单详情 anotherbillIds(逗号分隔) 汇总了去支付,来自BillInfo的bill_id
	 * @param user
	 * @param stmtId
	 * @param anotherbillIds
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public BaseResult<PaymentInfo> getBillDetail(User user, String stmtId,String anotherbillIds, String regionName) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, regionName);
		requestUrl += BILL_DETAIL_URL;
		BillDetailRequest billDetailRequest = new BillDetailRequest();
		billDetailRequest.setWuyeId(user.getWuyeId());
		billDetailRequest.setStmtId(stmtId);
		billDetailRequest.setBillId(anotherbillIds);
		billDetailRequest.setOpenid(user.getOpenid());
		billDetailRequest.setAppid(user.getAppId());
		
		TypeReference<CommonResponse<PaymentInfo>> typeReference = new TypeReference<CommonResponse<PaymentInfo>>(){};
		CommonResponse<PaymentInfo> hexieResponse = restUtil.exchangeOnUri(requestUrl, billDetailRequest, typeReference);
		BaseResult<PaymentInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}
	
	/**
	 * 账单快捷缴费
	 * @param user
	 * @param stmtId
	 * @param currPage
	 * @param totalCount
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public BaseResult<BillListVO> quickPayInfo(User user, String stmtId, String currPage, String totalCount) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUICK_PAY_URL;
		
		QuickPayRequest quickPayRequest = new QuickPayRequest();
		quickPayRequest.setStmtId(stmtId);
		quickPayRequest.setCurrPage(currPage);
		quickPayRequest.setTotalCount(totalCount);
		
		TypeReference<CommonResponse<BillListVO>> typeReference = new TypeReference<CommonResponse<BillListVO>>(){};
		CommonResponse<BillListVO> hexieResponse = restUtil.exchangeOnUri(requestUrl, quickPayRequest, typeReference);
		BaseResult<BillListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}
	

	/**
	 * 专业版缴费
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<WechatPayInfo> getPrePayInfo(PrepayRequestDTO prepayRequestDTO) throws Exception {
		User user = prepayRequestDTO.getUser();
		String appid = user.getAppId();
		String fromSys = sysName;
		if (!StringUtils.isEmpty(appid)) {
			//TODO 下面静态引用以后改注入
			fromSys = SystemConfigServiceImpl.getSysMap().get(appid);
		}
		if ("2".equals(prepayRequestDTO.getPayType())) {
			fromSys = SystemConfigServiceImpl.getSysMap().get(user.getMiniAppId());
		}
		if ("_shwy".equals(user.getOriSys())) {
			fromSys = user.getOriSys();
		}
		if (StringUtils.isEmpty(fromSys)) {
			fromSys = sysName;
		}
		String requestUrl = requestUtil.getRequestUrl(user, prepayRequestDTO.getRegionName());
		requestUrl += WX_PAY_URL;
		
		PrepayRequest prepayRequest = new PrepayRequest(prepayRequestDTO);
		prepayRequest.setFromSys(fromSys);
		prepayRequest.setAppid(user.getAppId());
		prepayRequest.setPayee_openid(prepayRequestDTO.getPayee_openid());
		if ("0".equals(prepayRequestDTO.getPayType())) {
			prepayRequest.setOpenid(user.getOpenid());
			prepayRequest.setAppid(user.getAppId());
		} else if ("2".equals(prepayRequestDTO.getPayType())) {	//微信小程序支付
			prepayRequest.setOpenid(user.getMiniopenid());
			prepayRequest.setAppid(user.getMiniAppId());
		} else if ("3".equals(prepayRequestDTO.getPayType())) {	//支付宝小程序支付
			prepayRequest.setOpenid(user.getAliuserid());
			prepayRequest.setAppid(user.getAliappid());
		} else if ("4".equals(prepayRequestDTO.getPayType())) {	//支付宝吱口令
//			prepayRequest.setOpenid(user.getAliuserid());
			prepayRequest.setAppid("2021001161682727");
		}

		TypeReference<CommonResponse<WechatPayInfo>> typeReference = new TypeReference<CommonResponse<WechatPayInfo>>(){};
		CommonResponse<WechatPayInfo> hexieResponse = restUtil.exchangeOnUri(requestUrl, prepayRequest, typeReference);
		BaseResult<WechatPayInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		
		//将绑定房屋选项写入缓存，待入账根据选项判断是否帮业主绑定房屋
        String bindHouKey = ModelConstant.KEY_TRADE_BIND_HOU + hexieResponse.getData().getTrade_water_id();
        String bindHouse = prepayRequestDTO.getBindHouse();
        if ("1".equals(bindHouse)) {
        	stringRedisTemplate.opsForValue().setIfAbsent(bindHouKey, bindHouse, 30, TimeUnit.DAYS);
		}
		return baseResult;
		
	}
	
	/**
	 * 专业版缴费
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<WechatPayInfo> getSmsPrePayInfo(PrepayRequestDTO prepayRequestDTO) throws Exception {
		User user = prepayRequestDTO.getUser();
		String appid = user.getAppId();
		String fromSys = sysName;
		if (!StringUtils.isEmpty(appid)) {
			//TODO 下面静态引用以后改注入
			fromSys = SystemConfigServiceImpl.getSysMap().get(appid);
		}
		String requestUrl = requestUtil.getRequestUrl(user, prepayRequestDTO.getRegionName());
		requestUrl += SMS_PAY_URL;
		
		PrepayRequest prepayRequest = new PrepayRequest(prepayRequestDTO);
		prepayRequest.setFromSys(fromSys);
		prepayRequest.setAppid(user.getAppId());
		prepayRequest.setPayee_openid(prepayRequestDTO.getPayee_openid());

		TypeReference<CommonResponse<WechatPayInfo>> typeReference = new TypeReference<CommonResponse<WechatPayInfo>>(){};
		CommonResponse<WechatPayInfo> hexieResponse = restUtil.exchangeOnUri(requestUrl, prepayRequest, typeReference);
		BaseResult<WechatPayInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}

	/**
	 * 获取优惠支付明细
	 * @param discountViewRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<Discounts> getDiscounts(DiscountViewRequestDTO discountViewRequestDTO) throws Exception {
		
		User user = discountViewRequestDTO.getUser();
		String requestUrl = requestUtil.getRequestUrl(user, discountViewRequestDTO.getRegionName());
		requestUrl += DISCOUNT_URL;
		
		DiscountViewRequest discountViewRequest = new DiscountViewRequest(discountViewRequestDTO);
		TypeReference<CommonResponse<Discounts>> typeReference = new TypeReference<CommonResponse<Discounts>>(){};
		CommonResponse<Discounts> hexieResponse = restUtil.exchangeOnUri(requestUrl, discountViewRequest, typeReference);
		BaseResult<Discounts> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}

	/**
	 * 获取优惠支付明细
	 * @param user
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> queryOrder(User user, String orderNo) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_ORDER_URL;
		
		QueryOrderRequest queryOrderRequest = new QueryOrderRequest();
		queryOrderRequest.setOrderNo(orderNo);
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnUri(requestUrl, queryOrderRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}

	/**
	 * 获取优惠支付明细
	 * @param user
	 * @param bankCard
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> getPaySmsCode(User user, BankCard bankCard) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += CARD_PAY_SMS_URL;
		
		PaySmsCodeRequest paySmsCodeRequest = new PaySmsCodeRequest();
		paySmsCodeRequest.setMobile(bankCard.getPhoneNo());
		paySmsCodeRequest.setQuickToken(bankCard.getQuickToken());
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnUri(requestUrl, paySmsCodeRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 其他收入支付
	 * @param otherPayDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<WechatPayInfo> requestOtherPay(OtherPayDTO otherPayDTO) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(otherPayDTO.getUser(), "");
		requestUrl += OTHER_PAY_URL;
		OtherPayRequest otherPayRequest = new OtherPayRequest(otherPayDTO);
		
		TypeReference<CommonResponse<WechatPayInfo>> typeReference = new TypeReference<CommonResponse<WechatPayInfo>>(){};
		CommonResponse<WechatPayInfo> hexieResponse = restUtil.exchangeOnUri(requestUrl, otherPayRequest, typeReference);
		BaseResult<WechatPayInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}
	
	/**
	 * 获取二维码支付服务详情
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<QrCodePayService> getQrCodePayService(User user) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QRCODE_PAY_SERVICE_URL;

		QrCodePayServiceRequest qrCodePayServiceRequest = new QrCodePayServiceRequest();
		qrCodePayServiceRequest.setOpenid(user.getOpenid());
		qrCodePayServiceRequest.setTel(user.getTel());
		
		TypeReference<CommonResponse<QrCodePayService>> typeReference = new TypeReference<CommonResponse<QrCodePayService>>(){};
		CommonResponse<QrCodePayService> hexieResponse = restUtil.exchangeOnUri(requestUrl, qrCodePayServiceRequest, typeReference);
		BaseResult<QrCodePayService> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 获取二维码
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<byte[]> getQrCode(User user, String qrCodeId) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QRCODE_URL;

		QrCodeRequest qrCodeRequest = new QrCodeRequest();
		qrCodeRequest.setQrCodeId(qrCodeId);
		
		TypeReference<CommonResponse<byte[]>> typeReference = new TypeReference<CommonResponse<byte[]>>(){};
		CommonResponse<byte[]> hexieResponse = restUtil.exchange4ResourceOnUri(requestUrl, qrCodeRequest, typeReference);
		BaseResult<byte[]> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 签到签退
	 * @param signInOutDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> signInOut(SignInOutDTO signInOutDTO) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(signInOutDTO.getUser(), "");
		requestUrl += SIGN_IN_OUT_URL;

		SignInOutRequest signInOutRequest = new SignInOutRequest();
		BeanUtils.copyProperties(signInOutDTO, signInOutRequest);
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnUri(requestUrl, signInOutRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	
	/**
	 * 根据ID查询指定类型的合协社区物业信息
	 * @param getCellDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<CellListVO> getMngHeXieList(GetCellDTO getCellDTO) throws Exception{

		String requestUrl = requestUtil.getRequestUrl(getCellDTO.getUser(), "");
		requestUrl += MNG_HEXIE_LIST_URL;
		
		GetCellRequest getCellRequest = new GetCellRequest();
		BeanUtils.copyProperties(getCellDTO, getCellRequest);
		
		TypeReference<CommonResponse<CellListVO>> typeReference = new TypeReference<CommonResponse<CellListVO>>(){};
		CommonResponse<CellListVO> hexieResponse = restUtil.exchangeOnUri(requestUrl, getCellRequest, typeReference);
		BaseResult<CellListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}

	/**
	 * 查询参数配置
	 * @param user
	 * @param type
	 * @param paraName
	 * @return
	 * @throws Exception
	 */
	public BaseResult<HexieConfig> queryServiceCfg(User user, String type, String paraName) throws Exception {

		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += SYNC_SERVICE_CFG_URL;
		
		WuyeParamRequest wuyeParamRequest = new WuyeParamRequest();
		if (ModelConstant.PARA_TYPE_SECT.equals(type)) {
			wuyeParamRequest.setType(type);
			wuyeParamRequest.setInfoId(user.getSectId());
		} else if(ModelConstant.PARA_TYPE_CSP.equals(type)) {
			wuyeParamRequest.setType(type);
			wuyeParamRequest.setInfoId(user.getCspId());
		} else {
			wuyeParamRequest.setType("000"); //TODO 默认给个值，以后有其他参数再改
		}
		wuyeParamRequest.setParaName(paraName);
		
		TypeReference<CommonResponse<HexieConfig>> typeReference = new TypeReference<CommonResponse<HexieConfig>>(){};
		CommonResponse<HexieConfig> hexieResponse = restUtil.exchangeOnUri(requestUrl, wuyeParamRequest, typeReference);
		BaseResult<HexieConfig> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}

	/**
	 * 获取电子凭证
	 * @param user
	 * @param tradeWaterId
	 * @param sysSource
	 * @return
	 * @throws Exception
	 */
	public BaseResult<EReceipt> getEReceipt(User user, String tradeWaterId, String sysSource) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += E_RECEIPT_URL;
		QueryEReceiptRequest request = new QueryEReceiptRequest();
		request.setOrderNo(tradeWaterId);
		request.setSysSource(sysSource);
		TypeReference<CommonResponse<EReceipt>> typeReference = new TypeReference<CommonResponse<EReceipt>>(){};
		CommonResponse<EReceipt> hexieResponse = restUtil.exchangeOnUri(requestUrl, request, typeReference);
		BaseResult<EReceipt> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}

	/**
	 * 推送消息
	 * @param user
	 * @param messageReq
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> sendMessage(User user, MessageReq messageReq) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += MESSAGE_URL;
		MessageRequest messageRequest = new MessageRequest();
		BeanUtils.copyProperties(messageReq, messageRequest);
		messageRequest.setOperId(user.getTel());
		messageRequest.setOperName(user.getName());
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnUri(requestUrl, messageRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}
	
	/**
	 * 根据批次号查询已发送的消息
	 * @param user
	 * @param batchNo
	 * @throws Exception 
	 */
	public BaseResult<Message> getMessage(User user, String batchNo) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_MESSAGE_URL;
		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setBatchNo(batchNo);
		
		TypeReference<CommonResponse<Message>> typeReference = new TypeReference<CommonResponse<Message>>(){};
		CommonResponse<Message> hexieResponse = restUtil.exchangeOnUri(requestUrl, messageRequest, typeReference);
		BaseResult<Message> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}

	/**
	 * 查询发送历史
	 * @param user
	 * @param sectIds
	 * @return
	 * @throws Exception
	 */
	public BaseResult<List<Message>> getMessageHistory(User user, String sectIds) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_MESSAGE_HISTORY_URL;
		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setSectId(sectIds);
		
		TypeReference<CommonResponse<List<Message>>> typeReference = new TypeReference<CommonResponse<List<Message>>>(){};
		CommonResponse<List<Message>> hexieResponse = restUtil.exchangeOnUri(requestUrl, messageRequest, typeReference);
		BaseResult<List<Message>> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}
	
	/**
	 * 根据小区ID查询小区楼栋明细
	 * @param user
	 * @param sectId
	 * @param cellAddr
	 * @return
	 * @throws Exception
	 */
	public BaseResult<CellListVO> queryCellAddr(User user, String sectId, String cellAddr) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_CELL_ADDR_URL;
		QueryCellRequest queryCellRequest = new QueryCellRequest();
		queryCellRequest.setSectId(sectId);
		queryCellRequest.setCellAddr(cellAddr);
		queryCellRequest.setUserId(user.getWuyeId());
		
		TypeReference<CommonResponse<CellListVO>> typeReference = new TypeReference<CommonResponse<CellListVO>>(){};
		CommonResponse<CellListVO> hexieResponse = restUtil.exchangeOnUri(requestUrl, queryCellRequest, typeReference);
		BaseResult<CellListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}

	/**
	 * 根据名称模糊查询合协社区小区列表
	 * @param user
	 * @param sectName
	 * @param regionName
	 * @return
	 * @throws Exception
	 */
	public BaseResult<CellListVO> getVagueSectByName(User user, String sectName, String regionName, String queryAppid) throws Exception{
		
		String requestUrl = requestUtil.getRequestUrl(user, regionName);
		requestUrl += SECT_VAGUE_LIST_URL;
		
		QuerySectRequet querySectRequet = new QuerySectRequet();
		querySectRequet.setSectName(sectName);
		querySectRequet.setOpenid(user.getOpenid());
		querySectRequet.setAppid(user.getAppId());
		querySectRequet.setQueryAppid(queryAppid);
		querySectRequet.setProvince(regionName);
		if (!StringUtils.isEmpty(queryAppid)) {
			String clientType = "0";
			if (queryAppid.equals(user.getMiniAppId())) {
				clientType = "1";
			} else if (queryAppid.equals(user.getAliappid())) {
				clientType = "4";
			}
			querySectRequet.setClientType(clientType);
		}
		TypeReference<CommonResponse<CellListVO>> typeReference = new TypeReference<CommonResponse<CellListVO>>(){};
		CommonResponse<CellListVO> hexieResponse = restUtil.exchangeOnUri(requestUrl, querySectRequet, typeReference);
		BaseResult<CellListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}

	/**
	 * 获取生成公众号动态二维码的必要参数
	 * @param user
	 * @param tradeWaterId
	 * @return
	 * @throws Exception
	 */
	public BaseResult<MpQrCodeParam> queryMpQrCodeParam(User user, String tradeWaterId) throws Exception{
		String requestUrl = requestUtil.getRequestUrl(user, "上海");
		requestUrl += QUERY_MPQRCODE_PARAM_URL;
		Map<String, String> map = new HashMap<>();
		map.put("trade_water_id", tradeWaterId);

		TypeReference<CommonResponse<MpQrCodeParam>> typeReference = new TypeReference<CommonResponse<MpQrCodeParam>>(){};
		CommonResponse<MpQrCodeParam> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<MpQrCodeParam> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}

	/**
	 * 查询当前用户申请过的发票
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<List<InvoiceDetail>> queryInvoiceByUser(User user, String currPage) throws Exception {
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_USER_INVOICE_URL;
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user.getWuyeId());
		map.put("openid", user.getOpenid());
		//TODO for test
		if ("o6pCYtwf0GHxFWOQ3VZ6x0K74QKE".equals(user.getOpenid()) || "oceRY1NWyec-frzqapPOHOB6q8p8".equals(user.getOpenid())) {
			map.put("openid", "oceRY1Is34P879B8KWR6Y-KlJ75k");
		}
		map.put("curr_page", currPage);
		map.put("total_count", "1000");

		TypeReference<CommonResponse<List<InvoiceDetail>>> typeReference = new TypeReference<CommonResponse<List<InvoiceDetail>>>(){};
		CommonResponse<List<InvoiceDetail>> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<List<InvoiceDetail>> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 根据交易ID获取相关的发票
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<List<InvoiceDetail>> queryInvoiceByTrade(User user, String tradeWaterId) throws Exception {
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_TRADE_INVOICE_URL;
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user.getWuyeId());
		map.put("openid", user.getOpenid());
		map.put("trade_water_id", tradeWaterId);

		TypeReference<CommonResponse<List<InvoiceDetail>>> typeReference = new TypeReference<CommonResponse<List<InvoiceDetail>>>(){};
		CommonResponse<List<InvoiceDetail>> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<List<InvoiceDetail>> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 查询当前用户申请过的发票
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<PaymentInfo> getFeeSmsBill(User user, QueryFeeSmsBillReq queryFeeSmsBillReq) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_FEE_SMS_BILL_URL;
		Map<String, String> map = new HashMap<>();
		map.put("batch_no", queryFeeSmsBillReq.getBatchNo());
		map.put("cell_id", queryFeeSmsBillReq.getCellId());
		map.put("appid", queryFeeSmsBillReq.getAppid());
		
		TypeReference<CommonResponse<PaymentInfo>> typeReference = new TypeReference<CommonResponse<PaymentInfo>>(){};
		CommonResponse<PaymentInfo> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<PaymentInfo> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 查询当前用户申请过的发票
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<Discounts> getFeeSmsPayQrCode(User user, QueryFeeSmsBillReq queryFeeSmsBillReq) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += FEE_SMS_PAY_QRCODE;
		Map<String, String> map = new HashMap<>();
		map.put("batch_no", queryFeeSmsBillReq.getBatchNo());
		map.put("cell_id", queryFeeSmsBillReq.getCellId());
		map.put("appid", queryFeeSmsBillReq.getAppid());
		map.put("pay_fee_type", "01");	//01账单支付
		map.put("pay_type", "0");	//非银行卡支付
		map.put("is_qrcode", "1");	//1，二维码支付
		
		TypeReference<CommonResponse<Discounts>> typeReference = new TypeReference<CommonResponse<Discounts>>(){};
		CommonResponse<Discounts> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<Discounts> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	/**
	 * 申请电子收据
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> applyReceipt(User user, ReceiptApplicationReq receiptApplicationReq) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += APPLY_RECEIPT_URL;
		Map<String, String> map = new HashMap<>();
		map.put("trade_water_id", receiptApplicationReq.getTradeWaterId());
		map.put("mobile", receiptApplicationReq.getMobile());
		map.put("openid", receiptApplicationReq.getOpenid());
		map.put("appid", receiptApplicationReq.getAppid());
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
}

	/**
	 * 根据收据ID获取电子收据
	 * @param receiptId
	 * @param sys
	 * @param region
	 * @return
	 * @throws Exception
	 */
	public BaseResult<ReceiptInfo> getReceipt(String receiptId, String sys, String region) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(new User(), region);
		requestUrl += QUERY_RECEIPT_URL;
		Map<String, String> map = new HashMap<>();
		map.put("receipt_id", receiptId);
		map.put("sys", sys);
		
		TypeReference<CommonResponse<ReceiptInfo>> typeReference = new TypeReference<CommonResponse<ReceiptInfo>>(){};
		CommonResponse<ReceiptInfo> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<ReceiptInfo> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 根据收据ID获取电子收据
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<List<Receipt>> getReceiptList(User user, String page) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_RECEIPT_LIST_URL;
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user.getWuyeId());
		map.put("openid", user.getOpenid());
		map.put("curr_page", page);
		map.put("total_count", "1000");
		
		TypeReference<CommonResponse<List<Receipt>>> typeReference = new TypeReference<CommonResponse<List<Receipt>>>(){};
		CommonResponse<List<Receipt>> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<List<Receipt>> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	
	/**
	 * 根据收据ID获取电子收据
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<List<Receipt>> getSectInfo(User user, String page) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_RECEIPT_LIST_URL;
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user.getWuyeId());
		map.put("openid", user.getOpenid());
		map.put("curr_page", page);
		map.put("total_count", "1000");
		
		TypeReference<CommonResponse<List<Receipt>>> typeReference = new TypeReference<CommonResponse<List<Receipt>>>(){};
		CommonResponse<List<Receipt>> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<List<Receipt>> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}

	/**
	 * 用户注册信息传入平台
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> pushUserRegisterUrl(User user) throws Exception {
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += PUSH_USER_REGISTER_URL;
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user.getWuyeId());
		map.put("openid", user.getOpenid());
		map.put("mobile", user.getTel());
		map.put("appid", user.getAppId());
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;

	}
	
	/**
	 * 支付平台h5用户注册登陆
	 * @param aliUserDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<HexieUser> h5UserLogin(H5UserDTO aliUserDTO) throws Exception {
		String requestUrl = requestUtil.getRequestUrl(new User(), "");
		requestUrl += H5_USER_LOGIN_URL;
		TypeReference<CommonResponse<HexieUser>> typeReference = new TypeReference<CommonResponse<HexieUser>>(){};
		CommonResponse<HexieUser> hexieResponse = restUtil.exchangeOnUri(requestUrl, aliUserDTO, typeReference);
		BaseResult<HexieUser> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		baseResult.setResult(hexieResponse.getResult());
		return baseResult;

	}
	
	
	/**
	 * 新朗恩用户绑定房屋
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<List<HexieHouse>> bindHouse4NewLionUser(User user, String mobile) throws Exception {
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += NEWLION_USER_BIND_URL;
		
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("user_id", user.getWuyeId());
		requestMap.put("openid", user.getMiniopenid());	//西部用户取miniopenid
		requestMap.put("appid", user.getMiniAppId());
		requestMap.put("mobile", mobile);
		TypeReference<CommonResponse<List<HexieHouse>>> typeReference = new TypeReference<CommonResponse<List<HexieHouse>>>(){};
		CommonResponse<List<HexieHouse>> hexieResponse = restUtil.exchangeOnUri(requestUrl, requestMap, typeReference);
		BaseResult<List<HexieHouse>> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		baseResult.setResult(hexieResponse.getResult());
		return baseResult;

	}
	
	/**
	 * 新朗恩用户绑定房屋
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<List<HexieHouse>> bindHouse4ChunchuanUser(User user, String mobile, String wuyeIds) throws Exception {
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += CHUNCHUAN_USER_BIND_URL;
		
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("user_id", user.getWuyeId());
		requestMap.put("openid", user.getMiniopenid());	//西部用户取miniopenid
		requestMap.put("appid", user.getMiniAppId());
		requestMap.put("mobile", mobile);
		requestMap.put("wuyeIds", wuyeIds);
		TypeReference<CommonResponse<List<HexieHouse>>> typeReference = new TypeReference<CommonResponse<List<HexieHouse>>>(){};
		CommonResponse<List<HexieHouse>> hexieResponse = restUtil.exchangeOnUri(requestUrl, requestMap, typeReference);
		BaseResult<List<HexieHouse>> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		baseResult.setResult(hexieResponse.getResult());
		return baseResult;

	}
	
	/**
	 * 无账单绑定房产
	 * @param user
	 * @param houseId
	 * @param area
	 * @return
	 * @throws Exception
	 */
	public BaseResult<HexieUser> bindHouseNoStmt(User user, String houseId, String area) throws Exception {
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += ADD_HOUSENOSTMT_URL;
		
		ActiveApp activeApp = systemConfigService.getActiveApp(user);
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("user_id", user.getWuyeId());
		requestMap.put("mng_cell_id", houseId);
		requestMap.put("area", area);
		requestMap.put("appid", activeApp.getActiveAppid());
		requestMap.put("openid", activeApp.getActiveOpenid());
		requestMap.put("mobile", user.getTel());
		
		TypeReference<CommonResponse<HexieUser>> typeReference = new TypeReference<CommonResponse<HexieUser>>(){};
		CommonResponse<HexieUser> hexieResponse = restUtil.exchangeOnUri(requestUrl, requestMap, typeReference);
		BaseResult<HexieUser> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		baseResult.setResult(hexieResponse.getResult());
		return baseResult;
	}

	/**
	 * 根据房屋ID查找房屋
	 * @param user
	 * @param houseId
	 * @return
	 * @throws Exception
	 */
	public BaseResult<HexieUser> queryHouseById(User user, String houseId) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_HOUSE_BY_ID_URL;
		
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("user_id", user.getWuyeId());
		requestMap.put("house_id", houseId);
		
		TypeReference<CommonResponse<HexieUser>> typeReference = new TypeReference<CommonResponse<HexieUser>>(){};
		CommonResponse<HexieUser> hexieResponse = restUtil.exchangeOnUri(requestUrl, requestMap, typeReference);
		BaseResult<HexieUser> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 根据小区ID获取小区信息
	 * @param user
	 * @param sectId
	 * @return
	 * @throws Exception
	 */
	public BaseResult<SectInfo> querySectById(User user, String sectId, String clientType) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_SECT_BY_ID_URL;
		
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("sect_id", sectId);
		requestMap.put("client_type", clientType);
		
		TypeReference<CommonResponse<SectInfo>> typeReference = new TypeReference<CommonResponse<SectInfo>>(){};
		CommonResponse<SectInfo> hexieResponse = restUtil.exchangeOnUri(requestUrl, requestMap, typeReference);
		BaseResult<SectInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 根据小区ID获取小区信息
	 * @param user
	 * @param radiusSectReq 经度,维度
	 * @return
	 * @throws Exception
	 */
	public BaseResult<List<RadiusSect>> querySectNearby(User user, RadiusSectReq radiusSectReq) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_SECT_NEARBY;
		
		Map<String, String> reqMap = new HashMap<>();
		reqMap.put("appid", radiusSectReq.getAppid());
		reqMap.put("coordinate", radiusSectReq.getBdCoordinate());
		
		TypeReference<CommonResponse<List<RadiusSect>>> typeReference = new TypeReference<CommonResponse<List<RadiusSect>>>(){};
		CommonResponse<List<RadiusSect>> hexieResponse = restUtil.exchangeOnUri(requestUrl, reqMap, typeReference);
		BaseResult<List<RadiusSect>> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 获取吱口令优惠资讯
	 * @param user
	 * @param queryAlipayConsultRequest
	 * @return
	 * @throws Exception
	 */
	public BaseResult<AlipayMarketingConsult> queryAlipayMarketingConsult(User user, QueryAlipayConsultRequest queryAlipayConsultRequest) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_MARKETING_CONSULT;
		
		queryAlipayConsultRequest.setAliUserId(user.getWuyeId());	//用合协用户的wuyeId
		TypeReference<CommonResponse<AlipayMarketingConsult>> typeReference = new TypeReference<CommonResponse<AlipayMarketingConsult>>(){};
		CommonResponse<AlipayMarketingConsult> hexieResponse = restUtil.exchangeOnUri(requestUrl, queryAlipayConsultRequest, typeReference);
		BaseResult<AlipayMarketingConsult> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}

	/**
	 * 获取扫二维码缴管理费时，二维码上的参数对照
	 * @param user
	 * @param payKey
	 * @return
	 * @throws Exception
	 */
	public BaseResult<Object> getPayMapping(User user, String payKey) throws Exception {
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_PAY_MAPPING;

		Map<String, String> map = new HashMap<>();
		map.put("payKey", payKey);

		TypeReference<CommonResponse<Object>> typeReference = new TypeReference<CommonResponse<Object>>(){};
		CommonResponse<Object> hexieResponse = restUtil.exchangeOnUri(requestUrl, map, typeReference);
		BaseResult<Object> baseResult = new BaseResult<>();
		baseResult.setResult(hexieResponse.getResult());
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
	}
	
	/**
	 * 获取门禁点信息
	 * @param spotId
	 * @return
	 * @throws Exception
	 */
	public BaseResult<UserAccessSpotResp> getAccessSpot(User user, String spotId) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_ACCESS_SPOT;
		
		Map<String, String> reqMap = new HashMap<>();
		reqMap.put("spotId", spotId);
		
		TypeReference<CommonResponse<UserAccessSpotResp>> typeReference = new TypeReference<CommonResponse<UserAccessSpotResp>>(){};
		CommonResponse<UserAccessSpotResp> hexieResponse = restUtil.exchangeOnUri(requestUrl, reqMap, typeReference);
		BaseResult<UserAccessSpotResp> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
	/**
	 * 保存用户门禁记录
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @param houseId
	 * @param regionName
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> saveUserAccessRecord(User user, UserAccessRecordReq saveAccessRecordReq) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += SAVE_USER_ACCESS_RECORD;
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnBody(requestUrl, saveAccessRecordReq, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		baseResult.setMessage(hexieResponse.getErrMsg());
		return baseResult;
		
	}
	
}
